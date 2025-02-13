package com.kitakkun.backintime.compiler.backend.transformer.capture

import com.kitakkun.backintime.compiler.backend.analyzer.ValueContainerStateChangeInsideFunctionAnalyzer
import com.kitakkun.backintime.compiler.backend.api.VersionSpecificAPI
import com.kitakkun.backintime.compiler.backend.utils.generateCaptureValueCallForValueContainer
import com.kitakkun.backintime.compiler.backend.utils.generateUUIDVariable
import com.kitakkun.backintime.compiler.backend.utils.getCorrespondingProperty
import com.kitakkun.backintime.compiler.backend.utils.getRelevantLambdaExpressions
import com.kitakkun.backintime.compiler.backend.utils.getSerializerType
import com.kitakkun.backintime.compiler.backend.utils.isBackInTimeDebuggable
import com.kitakkun.backintime.compiler.backend.utils.isBackInTimeGenerated
import com.kitakkun.backintime.compiler.backend.utils.isIndirectValueContainerSetterCall
import com.kitakkun.backintime.compiler.backend.utils.isLambdaFunctionRelevantCall
import com.kitakkun.backintime.compiler.backend.utils.isValueContainerSetterCall
import com.kitakkun.backintime.compiler.backend.utils.signatureForBackInTimeDebugger
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.isLocal
import org.jetbrains.kotlin.ir.util.isPropertyAccessor
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.parentDeclarationsWithSelf
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.Name

context(com.kitakkun.backintime.compiler.backend.BackInTimePluginContext)
class BackInTimeDebuggableCapturePropertyChangesTransformer : IrElementTransformerVoidWithContext() {
    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        reportMethodInvocationIfNeeded(declaration)
        return super.visitFunctionNew(declaration)
    }

    private fun reportMethodInvocationIfNeeded(declaration: IrFunction) {
        if (declaration.isBackInTimeGenerated) return
        if (declaration.isPropertyAccessor) return

        val parentClass = declaration.parentClassOrNull ?: return
        if (!parentClass.isBackInTimeDebuggable) return

        val parentClassDispatchReceiver = declaration.dispatchReceiverParameter ?: return

        with(irBuiltIns.createIrBuilder(declaration.symbol)) {
            val uuidVariable = generateUUIDVariable()

            val notifyMethodCallFunctionCall = irCall(reportMethodInvocationFunctionSymbol).apply {
                putValueArgument(0, irGet(parentClassDispatchReceiver))
                putValueArgument(1, irGet(uuidVariable))
                putValueArgument(2, irString(declaration.signatureForBackInTimeDebugger()))
            }

            (declaration.body as? IrBlockBody)?.statements?.addAll(0, listOf(uuidVariable, notifyMethodCallFunctionCall))
        }
    }

    override fun visitCall(expression: IrCall): IrExpression {
        when {
            expression.isPureSetterCall() -> {
                expression.transformPureSetterCall()
            }

            expression.isValueContainerRelevantCall() -> {
                super.visitCall(expression)
                return expression.transformValueContainerRelevantCall()
            }
        }

        return super.visitCall(expression)
    }

    private fun IrCall.isPureSetterCall(): Boolean {
        val propertySymbol = this.symbol.owner.correspondingPropertySymbol ?: return false
        val propertyOwnerClass = propertySymbol.owner.parentClassOrNull ?: return false
        val isSetter = this.symbol.owner.isSetter
        return isSetter && propertyOwnerClass.isBackInTimeDebuggable
    }

    private fun IrCall.transformPureSetterCall() {
        val function = currentClosestBackInTimeDebuggableOwnerFunction() ?: return
        val uuidVariable = function.getLocalMethodInvocationIdVariable() ?: return
        val property = this.symbol.owner.correspondingPropertySymbol?.owner ?: return
        val classDispatchReceiverParameter = function.dispatchReceiverParameter ?: return
        val value = this.valueArguments.first()

        this.putValueArgument(
            index = 0,
            valueArgument = with(irBuiltIns.createIrBuilder(symbol)) {
                irCall(captureThenReturnValueFunctionSymbol).apply {
                    putValueArgument(0, irGet(classDispatchReceiverParameter))
                    putValueArgument(1, irGet(uuidVariable))
                    putValueArgument(2, irString(property.signatureForBackInTimeDebugger()))
                    putValueArgument(3, value)
                    putTypeArgument(0, property.getter!!.returnType.getSerializerType())
                }
            },
        )
    }

    private fun IrFunction.getLocalMethodInvocationIdVariable(): IrVariable? {
        var variable: IrVariable? = null
        this.acceptVoid(
            object : IrElementVisitorVoid {
                override fun visitElement(element: IrElement) {
                    element.acceptChildrenVoid(this)
                }

                override fun visitVariable(declaration: IrVariable) {
                    if (declaration.isBackInTimeGenerated && declaration.name == Name.identifier("backInTimeUUID")) {
                        variable = declaration
                    }
                }
            },
        )
        return variable
    }

    private fun IrCall.isValueContainerRelevantCall(): Boolean {
        return VersionSpecificAPI.INSTANCE.getReceiverAndArgs(this)
            .mapNotNull { it.getCorrespondingProperty() }
            .any { property ->
                property.parentClassOrNull?.isBackInTimeDebuggable == true &&
                    valueContainerClassInfoList.any { it.classSymbol == property.getter?.returnType?.classOrNull }
            }
    }

    private fun IrCall.transformValueContainerRelevantCall(): IrExpression {
        if (isLambdaFunctionRelevantCall()) {
            transformInsideRelevantLambdaFunctions()
        }

        if (isValueContainerSetterCall()) {
            val function = currentClosestBackInTimeDebuggableOwnerFunction()

            val uuidVariable = function?.getLocalMethodInvocationIdVariable()
            val classDispatchReceiverParameter = function?.dispatchReceiverParameter

            return captureIfNeeded(
                classDispatchReceiverParameter = classDispatchReceiverParameter ?: return this,
                uuidVariable = uuidVariable ?: return this,
            ) ?: this
        }

        if (isIndirectValueContainerSetterCall()) {
            return transformIndirectValueContainerSetterCall()
        }

        return this
    }

    private fun IrCall.transformInsideRelevantLambdaFunctions() {
        val involvingLambdas = getRelevantLambdaExpressions()

        val function = currentClosestBackInTimeDebuggableOwnerFunction()
        val uuidVariable = function?.getLocalMethodInvocationIdVariable()
        val parentClassSymbol = function?.parentClassOrNull?.symbol
        val classDispatchReceiverParameter = function?.dispatchReceiverParameter

        val passedProperties = VersionSpecificAPI.INSTANCE.getReceiverAndArgs(this)
            .mapNotNull { it.getCorrespondingProperty() }
            .filter { it.parentClassOrNull?.symbol == parentClassSymbol }
            .toSet()

        involvingLambdas.forEach { lambda ->
            lambda.transformChildrenVoid(
                LambdaArgumentBodyTransformer(
                    passedProperties = passedProperties,
                    classDispatchReceiverParameter = classDispatchReceiverParameter ?: return,
                    uuidVariable = uuidVariable ?: return,
                ),
            )
        }
    }

    private fun IrCall.transformIndirectValueContainerSetterCall(): IrExpression {
        val propertiesShouldBeCapturedAfterCall = ValueContainerStateChangeInsideFunctionAnalyzer.analyzePropertiesShouldBeCaptured(this)

        val function = currentClosestBackInTimeDebuggableOwnerFunction()
        val uuidVariable = function?.getLocalMethodInvocationIdVariable()
        val classDispatchReceiverParameter = function?.dispatchReceiverParameter

        with(irBuiltIns.createIrBuilder(symbol)) {
            val propertyCaptureCalls = propertiesShouldBeCapturedAfterCall.mapNotNull { property ->
                property.generateCaptureValueCallForValueContainer(
                    instanceParameter = classDispatchReceiverParameter ?: return this@transformIndirectValueContainerSetterCall,
                    uuidVariable = uuidVariable ?: return this@transformIndirectValueContainerSetterCall,
                )
            }

            if (propertyCaptureCalls.isEmpty()) return this@transformIndirectValueContainerSetterCall

            return irComposite {
                +this@transformIndirectValueContainerSetterCall
                +propertyCaptureCalls
            }
        }
    }

    private fun currentClosestBackInTimeDebuggableOwnerFunction(): IrFunction? {
        return (currentFunction?.irElement as? IrFunction)
            ?.parentDeclarationsWithSelf
            ?.filterIsInstance<IrFunction>()
            ?.firstOrNull {
                !it.isLocal && it.parentClassOrNull?.isBackInTimeDebuggable == true
            }
    }
}
