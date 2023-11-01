package com.github.kitakkun.backintime.backend

import com.github.kitakkun.backintime.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class BackInTimePropertySetterTransformer(
    private val pluginContext: IrPluginContext,
) : IrElementTransformerVoid() {
    override fun visitProperty(declaration: IrProperty): IrStatement {
        val ownerClass = declaration.parentClassOrNull ?: return super.visitProperty(declaration)
        if (ownerClass.superTypes.none { it.classFqName == BackInTimeConsts.debuggableStateHolderManipulatorFqName }) return super.visitProperty(declaration)
        val backingField = declaration.backingField ?: return super.visitProperty(declaration)
        val backingFieldType = backingField.type
        val backingFieldClass = backingField.type.classOrNull ?: return super.visitProperty(declaration)

        if (backingFieldType.isKotlinPrimitiveType() || backingFieldType.isPrimitiveType()) {
            val setter = declaration.setter ?: return super.visitProperty(declaration)
            val setterBody = setter.body as? IrBlockBody ?: return super.visitProperty(declaration)
            // IMPORTANT: If this line is removed, property access inside a class is not executed via generated setter.
            setter.origin = IrDeclarationOrigin.DEFINED
            setter.body = IrBlockBodyBuilder(
                context = pluginContext,
                startOffset = setter.startOffset,
                endOffset = setter.endOffset,
                scope = Scope(declaration.symbol),
            ).blockBody {
                +setterBody.statements
                +notifyValueChangeToBackInTimeDebugService(declaration)
            }
            return super.visitProperty(declaration)
        }
        when (backingFieldClass.owner.fqNameWhenAvailable) {
            BackInTimeConsts.mutableLiveDataFqName -> {
                // TODO: generate code for MutableLiveData
//                val typeArguments = backingFieldType as? IrSimpleType
//                val genericTypeArgument = typeArguments?.arguments?.firstOrNull() as? IrTypeProjection ?: return irBlock { }
//                val genericType = genericTypeArgument.type.classOrNull ?: return irBlock { }
            }

            BackInTimeConsts.mutableStateFlowFqName -> {
                // TODO: generate code for MutableStateFlow
            }

            BackInTimeConsts.mutableStateFqName -> {
                // TODO: generate code for MutableState
            }
        }
        return super.visitProperty(declaration)
    }

    private fun IrBuilderWithScope.notifyValueChangeToBackInTimeDebugService(property: IrProperty): List<IrStatement> {
        // add BackInTimeDebugService.notifyPropertyChanged(this, "propertyName", value, value::class.qualifiedName)
        val parentClassReceiver = property.setter!!.dispatchReceiverParameter!!
        val propertyName = irString(property.name.identifier)
        val value = irGet(property.setter!!.valueParameters[0], type = pluginContext.irBuiltIns.anyNType)
        val valueTypeFqName = irString(property.backingField!!.type.classFqName!!.asString())

        val debugServiceClass = pluginContext.referenceClass(BackInTimeConsts.backInTimeDebugServiceClassId) ?: return emptyList()
        val notifyPropertyChangedFunction = debugServiceClass.getSimpleFunction(BackInTimeConsts.notifyPropertyChanged) ?: return emptyList()

        val notifyPropertyChangedCall = irCall(
            callee = notifyPropertyChangedFunction,
        ).apply {
            dispatchReceiver = irGetObject(debugServiceClass)
            putValueArgument(0, irGet(parentClassReceiver))
            putValueArgument(1, propertyName)
            putValueArgument(2, value)
            putValueArgument(3, valueTypeFqName)
        }

        return listOf(notifyPropertyChangedCall)
    }
}
