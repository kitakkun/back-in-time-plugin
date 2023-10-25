package com.github.kitakkun.back_in_time

import com.github.kitakkun.back_in_time.annotations.DebuggableProperty
import com.github.kitakkun.back_in_time.annotations.DebuggableStateHolder
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.runtime.structure.classId
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.impl.IrVariableImpl
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.impl.IrVariableSymbolImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.isNullableAny
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class DebuggableStateHolderIrGenerationExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transformChildrenVoid(DebuggableStateHolderIrGenerationTransformer(pluginContext))
    }
}

class DebuggableStateHolderIrGenerationTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    override fun visitClass(declaration: IrClass): IrStatement {
        val annotation = declaration.getAnnotation(FqName(DebuggableStateHolder::class.java.name)) ?: return super.visitClass(declaration)
        val applyToAllProperties = (annotation.valueArguments.first() as? IrConst<*>)?.value as? Boolean ?: true
        if (applyToAllProperties) {
            // 全てのプロパティにアノテーションを付与する
            val propertyAnnotation = pluginContext.referenceConstructors(DebuggableProperty::class.java.classId).first()
            declaration.properties.forEach {
                it.annotations += listOf(
                    IrConstructorCallImpl(
                        startOffset = declaration.startOffset,
                        endOffset = declaration.endOffset,
                        type = pluginContext.irBuiltIns.unitType,
                        symbol = propertyAnnotation,
                        typeArgumentsCount = 0,
                        valueArgumentsCount = 0,
                        constructorTypeArgumentsCount = 0,
                    )
                )
            }
        }
        return super.visitClass(declaration)
    }

    override fun visitProperty(declaration: IrProperty): IrStatement {
        if (!declaration.hasAnnotation(FqName(DebuggableProperty::class.java.name))) return super.visitProperty(declaration)

        val throwableClass = pluginContext.irBuiltIns.throwableClass
        val throwableClassConstructor = throwableClass.constructors.first { it.owner.valueParameters.isEmpty() }
        val stackTraceParameter = throwableClass.getSimpleFunction("getStackTrace") ?: return super.visitProperty(declaration)
        val arrayGetFunction = pluginContext.irBuiltIns.arrayClass.getSimpleFunction("get") ?: return super.visitProperty(declaration)
        val stackTraceElementClass = pluginContext.referenceClass(java.lang.StackTraceElement::class.java.classId) ?: return super.visitProperty(declaration)

        val setterBody = declaration.setter?.body as? IrBlockBody ?: IrBlockBodyImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
        ).also {
            declaration.setter?.body = it
        }

        val instantiateThrowable = IrConstructorCallImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            type = throwableClass.defaultType,
            symbol = throwableClassConstructor,
            typeArgumentsCount = 0,
            valueArgumentsCount = 0,
            constructorTypeArgumentsCount = 0,
        )

        val getStackTraceOfThrowable = IrCallImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            type = stackTraceParameter.owner.returnType,
            symbol = stackTraceParameter,
            typeArgumentsCount = 0,
            valueArgumentsCount = 0,
            superQualifierSymbol = null,
        ).apply {
            dispatchReceiver = instantiateThrowable
        }

        val getStackTraceFirstElement = IrCallImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            symbol = arrayGetFunction,
            type = stackTraceElementClass.defaultType,
            typeArgumentsCount = 0,
            valueArgumentsCount = 1,
            superQualifierSymbol = null,
        ).apply {
            dispatchReceiver = getStackTraceOfThrowable
            putValueArgument(0, IrConstImpl.int(startOffset, endOffset, pluginContext.irBuiltIns.intType, 0))
        }


        val stackTraceVar = IrVariableImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            type = stackTraceElementClass.defaultType,
            symbol = IrVariableSymbolImpl(),
            origin = declaration.origin,
            name = declaration.name,
            isVar = false,
            isConst = false,
            isLateinit = false,
        ).apply {
            initializer = getStackTraceFirstElement
            parent = declaration.parent
        }

        val getStackTraceVarValue = IrGetValueImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            type = stackTraceElementClass.defaultType,
            symbol = stackTraceVar.symbol,
        )

        val println = pluginContext.referenceFunctions(
            CallableId(
                packageName = FqName("kotlin.io"),
                callableName = Name.identifier("println")
            )
        ).first {
            it.owner.valueParameters.size == 1 && it.owner.valueParameters.single().type.isNullableAny()
        }
        val printlnCall = IrCallImpl(
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            type = pluginContext.irBuiltIns.unitType,
            symbol = println,
            typeArgumentsCount = 0,
            valueArgumentsCount = 1,
            superQualifierSymbol = null,
        ).apply {
            putValueArgument(0, getStackTraceVarValue)
        }

        setterBody.statements.addAll(
            index = 0,
            elements = listOf(
                stackTraceVar,
                printlnCall,
            )
        )

        return super.visitProperty(declaration)
    }
}

