package com.kitakkun.backintime.compiler.backend.transformer.capture

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.analyzer.TrackableStateHolderStateChangeInsideFunctionAnalyzer
import com.kitakkun.backintime.compiler.backend.trackablestateholder.ResolvedTrackableStateHolder
import com.kitakkun.backintime.compiler.backend.utils.captureIfNeeded
import com.kitakkun.backintime.compiler.backend.utils.captureThenReturn
import com.kitakkun.backintime.compiler.backend.utils.captureValueArgument
import com.kitakkun.backintime.compiler.backend.utils.generateCaptureValueCallForTrackableStateHolder
import com.kitakkun.backintime.compiler.backend.utils.generateUUIDVariable
import com.kitakkun.backintime.compiler.backend.utils.getCorrespondingProperty
import com.kitakkun.backintime.compiler.backend.utils.getRelevantLambdaExpressions
import com.kitakkun.backintime.compiler.backend.utils.getSerializerType
import com.kitakkun.backintime.compiler.backend.utils.isBackInTimeDebuggable
import com.kitakkun.backintime.compiler.backend.utils.isBackInTimeGenerated
import com.kitakkun.backintime.compiler.backend.utils.isIndirectTrackableStateHolderSetterCall
import com.kitakkun.backintime.compiler.backend.utils.isLambdaFunctionRelevantCall
import com.kitakkun.backintime.compiler.backend.utils.isTrackableStateHolderSetterCall
import com.kitakkun.backintime.compiler.backend.utils.putRegularArgument
import com.kitakkun.backintime.compiler.backend.utils.signatureForBackInTimeDebugger
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrConstructor
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
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.receiverAndArgs
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.acceptVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.Name

class BackInTimeDebuggableCapturePropertyChangesTransformer(
    private val irContext: BackInTimePluginContext,
) : IrElementTransformerVoidWithContext() {
    override fun visitConstructor(declaration: IrConstructor): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return super.visitConstructor(declaration)

        if (!parentClass.isBackInTimeDebuggable) return super.visitConstructor(declaration)

        val irBuilder = irContext.irBuiltIns.createIrBuilder(declaration.symbol)

        val uuidVariable = irBuilder.generateUUIDVariable(irContext)

        val methodInvocationCall = irBuilder.run {
            irCall(irContext.reportMethodInvocationFunctionSymbol).apply {
                putRegularArgument(0, irGet(parentClass.thisReceiver!!))
                putRegularArgument(1, irGet(uuidVariable))
                putRegularArgument(2, irString(declaration.signatureForBackInTimeDebugger()))
            }
        }

        val captureCalls = parentClass.properties
            .filter { !it.isBackInTimeGenerated }
            .mapNotNull { property ->
                val backingField = property.backingField ?: return@mapNotNull null

                val fieldType = backingField.type
                val trackableStateHolderDefinition = irContext.trackableStateHolderClassInfoList.find { it.classSymbol == fieldType.classOrNull }

                val thisReceiver = parentClass.thisReceiver ?: return@mapNotNull null

                val getFieldCall = irBuilder.run {
                    irGetField(
                        receiver = irGet(thisReceiver),
                        field = backingField,
                    )
                }

                val getFieldValueCall = when (trackableStateHolderDefinition) {
                    is ResolvedTrackableStateHolder.SelfContained -> getFieldCall
                    is ResolvedTrackableStateHolder.Wrapper ->
                        irBuilder.irCall(trackableStateHolderDefinition.getterSymbol).apply {
                            dispatchReceiver = getFieldCall
                        }

                    else -> {
                        if (property.isVar) getFieldCall else null
                    }
                }

                getFieldValueCall?.let {
                    captureThenReturn(
                        irContext = irContext,
                        irBuilder = irBuilder,
                        instanceParameter = thisReceiver,
                        uuidVariable = uuidVariable,
                        signature = property.signatureForBackInTimeDebugger(),
                        value = it,
                        serializerType = fieldType.getSerializerType(irContext)
                    )
                }
            }

        (declaration.body as? IrBlockBody)?.statements?.let {
            it.add(uuidVariable)
            it.add(methodInvocationCall)
            it.addAll(captureCalls)
        }

        return super.visitConstructor(declaration)
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        reportMethodInvocationIfNeeded(declaration)
        return super.visitFunctionNew(declaration)
    }

    private fun reportMethodInvocationIfNeeded(declaration: IrFunction) {
        if (declaration.isBackInTimeGenerated) return
        if (declaration.isPropertyAccessor) return

        val parentClass = declaration.parentClassOrNull ?: return
        if (!parentClass.isBackInTimeDebuggable) return

        // When the function is a constructor, the dispatch receiver is null.
        val parentClassDispatchReceiver = declaration.dispatchReceiverParameter ?: return

        with(irContext.irBuiltIns.createIrBuilder(declaration.symbol)) {
            val uuidVariable = generateUUIDVariable(irContext)

            val notifyMethodCallFunctionCall = irCall(irContext.reportMethodInvocationFunctionSymbol).apply {
                putRegularArgument(0, irGet(parentClassDispatchReceiver))
                putRegularArgument(1, irGet(uuidVariable))
                putRegularArgument(2, irString(declaration.signatureForBackInTimeDebugger()))
            }

            (declaration.body as? IrBlockBody)?.statements?.addAll(0, listOf(uuidVariable, notifyMethodCallFunctionCall))
        }
    }

    override fun visitCall(expression: IrCall): IrExpression {
        when {
            expression.isPureSetterCall() -> {
                expression.transformPureSetterCall()
            }

            expression.isTrackableStateHolderRelevantCall() -> {
                super.visitCall(expression)
                return expression.transformTrackableStateHolderRelevantCall()
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

        this.captureValueArgument(
            irContext = irContext,
            index = 0,
            classDispatchReceiverParameter = classDispatchReceiverParameter,
            uuidVariable = uuidVariable,
            propertySignature = property.signatureForBackInTimeDebugger(),
        )
    }

    private fun IrFunction.getLocalMethodInvocationIdVariable(): IrVariable? {
        var variable: IrVariable? = null
        this.acceptVoid(
            object : IrVisitorVoid() {
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

    private fun IrCall.isTrackableStateHolderRelevantCall(): Boolean {
        return receiverAndArgs()
            .mapNotNull { it.getCorrespondingProperty() }
            .any { property ->
                property.parentClassOrNull?.isBackInTimeDebuggable == true &&
                    irContext.trackableStateHolderClassInfoList.any {
                        it.classSymbol == property.getter?.returnType?.classOrNull ||
                            it.classSymbol == property.backingField?.type?.classOrNull
                    }
            }
    }

    private fun IrCall.transformTrackableStateHolderRelevantCall(): IrExpression {
        if (isLambdaFunctionRelevantCall()) {
            transformInsideRelevantLambdaFunctions()
        }

        if (isTrackableStateHolderSetterCall(irContext)) {
            val function = currentClosestBackInTimeDebuggableOwnerFunction()

            val uuidVariable = function?.getLocalMethodInvocationIdVariable()
            val classDispatchReceiverParameter = function?.dispatchReceiverParameter

            return captureIfNeeded(
                irContext = irContext,
                classDispatchReceiverParameter = classDispatchReceiverParameter ?: return this,
                uuidVariable = uuidVariable ?: return this,
            ) ?: this
        }

        if (isIndirectTrackableStateHolderSetterCall(irContext)) {
            return transformIndirectTrackableStateHolderSetterCall()
        }

        return this
    }

    private fun IrCall.transformInsideRelevantLambdaFunctions() {
        val involvingLambdas = getRelevantLambdaExpressions()

        val function = currentClosestBackInTimeDebuggableOwnerFunction()
        val uuidVariable = function?.getLocalMethodInvocationIdVariable()
        val parentClassSymbol = function?.parentClassOrNull?.symbol
        val classDispatchReceiverParameter = function?.dispatchReceiverParameter

        val passedProperties = receiverAndArgs()
            .mapNotNull { it.getCorrespondingProperty() }
            .filter { it.parentClassOrNull?.symbol == parentClassSymbol }
            .toSet()

        involvingLambdas.forEach { lambda ->
            lambda.transformChildrenVoid(
                LambdaArgumentBodyTransformer(
                    irContext = irContext,
                    passedProperties = passedProperties,
                    classDispatchReceiverParameter = classDispatchReceiverParameter ?: return,
                    uuidVariable = uuidVariable ?: return,
                ),
            )
        }
    }

    private fun IrCall.transformIndirectTrackableStateHolderSetterCall(): IrExpression {
        val propertiesShouldBeCapturedAfterCall = TrackableStateHolderStateChangeInsideFunctionAnalyzer.analyzePropertiesShouldBeCaptured(irContext, this)

        val function = currentClosestBackInTimeDebuggableOwnerFunction()
        val uuidVariable = function?.getLocalMethodInvocationIdVariable()
        val classDispatchReceiverParameter = function?.dispatchReceiverParameter

        with(irContext.irBuiltIns.createIrBuilder(symbol)) {
            val propertyCaptureCalls = propertiesShouldBeCapturedAfterCall.mapNotNull { property ->
                property.generateCaptureValueCallForTrackableStateHolder(
                    irContext = irContext,
                    irBuilder = this,
                    instanceParameter = classDispatchReceiverParameter ?: return this@transformIndirectTrackableStateHolderSetterCall,
                    uuidVariable = uuidVariable ?: return this@transformIndirectTrackableStateHolderSetterCall,
                )
            }

            if (propertyCaptureCalls.isEmpty()) return this@transformIndirectTrackableStateHolderSetterCall

            return irComposite {
                +this@transformIndirectTrackableStateHolderSetterCall
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
