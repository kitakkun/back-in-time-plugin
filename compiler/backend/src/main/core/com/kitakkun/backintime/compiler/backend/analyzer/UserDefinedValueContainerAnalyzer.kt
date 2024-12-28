package com.kitakkun.backintime.compiler.backend.analyzer

import com.kitakkun.backintime.compiler.backend.valuecontainer.CaptureStrategy
import com.kitakkun.backintime.compiler.backend.valuecontainer.ResolvedValueContainer
import com.kitakkun.backintime.compiler.common.BackInTimeAnnotations
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.runtime.structure.classId
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.findAnnotation
import org.jetbrains.kotlin.ir.util.getAnnotationArgumentValue
import org.jetbrains.kotlin.ir.util.getAnnotationValueOrNull
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isGetter
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.simpleFunctions
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.utils.addIfNotNull
import kotlin.reflect.KClass

context(IrPluginContext)
class UserDefinedValueContainerAnalyzer private constructor() : IrElementVisitorVoid {
    companion object {
        context(IrPluginContext)
        fun analyzeAdditionalValueContainerClassInfo(moduleFragment: IrModuleFragment): List<ResolvedValueContainer> {
            with(UserDefinedValueContainerAnalyzer()) {
                moduleFragment.acceptChildrenVoid(this)
                return collectedInfoList
            }
        }
    }

    private val mutableCollectedInfoList = mutableListOf<ResolvedValueContainer>()
    val collectedInfoList: List<ResolvedValueContainer> get() = mutableCollectedInfoList

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitClass(declaration: IrClass) {
        declaration.acceptChildrenVoid(this)

        if (!declaration.hasAnnotation(BackInTimeAnnotations.valueContainerAnnotationFqName)) return

        val containerInfo = declaration.getValueContainerClassInfo()
        mutableCollectedInfoList.addIfNotNull(containerInfo)
    }

    /**
     * To detect value container class defined in external module...
     */
    override fun visitProperty(declaration: IrProperty) {
        declaration.acceptChildrenVoid(this)

        val propertyClass = declaration.getter?.returnType?.classOrNull?.owner ?: return
        val parentClass = declaration.parentClassOrNull ?: return
        if (parentClass.hasAnnotation(BackInTimeAnnotations.backInTimeAnnotationFqName)) {
            val containerInfo = propertyClass.getValueContainerClassInfo()
            mutableCollectedInfoList.addIfNotNull(containerInfo)
        }
    }

    private fun IrClass.getValueContainerClassInfo(): ResolvedValueContainer? {
        val isSelfContained = hasAnnotation(BackInTimeAnnotations.selfContainedValueContainerAnnotationFqName)
        val serializeAs = annotations.findAnnotation(BackInTimeAnnotations.serializeAsAnnotationFqName)?.getAnnotationValueOrNull<KClass<*>>("clazz")?.java?.classId?.let { referenceClass(it) }

        val captureTargets = simpleFunctions()
            .filter { function ->
                function.hasAnnotationOnSelfOrCorrespondingProperty(BackInTimeAnnotations.captureAnnotationFqName)
            }.map { function ->
                val strategy = (function.correspondingPropertySymbol?.owner ?: function)
                    .getAnnotationArgumentValue<com.kitakkun.backintime.core.annotations.CaptureStrategy>(
                        BackInTimeAnnotations.captureAnnotationFqName,
                        "strategy",
                    ) ?: com.kitakkun.backintime.core.annotations.CaptureStrategy.AFTER_CALL
                function.symbol to strategy
            }.map { (function, strategy) ->
                function to when (strategy) {
                    com.kitakkun.backintime.core.annotations.CaptureStrategy.AFTER_CALL -> CaptureStrategy.AfterCall
                    com.kitakkun.backintime.core.annotations.CaptureStrategy.VALUE_ARGUMENT -> CaptureStrategy.ValueArgument() // currently assume the index is 0
                }
            }

        val getter = simpleFunctions()
            .filter { function -> !function.isSetter }
            .firstOrNull { function -> function.hasAnnotationOnSelfOrCorrespondingProperty(BackInTimeAnnotations.getterAnnotationFqName) }
            ?: return null
        val setter = simpleFunctions()
            .filter { function -> !function.isGetter }
            .firstOrNull { function ->
                function.hasAnnotationOnSelfOrCorrespondingProperty(BackInTimeAnnotations.setterAnnotationFqName)
            } ?: return null

        if (captureTargets.isEmpty()) return null

        if (isSelfContained) {
            return ResolvedValueContainer.SelfContained(
                classSymbol = this.symbol,
                setterSymbols = listOf(setter.symbol),
                captureTargetSymbols = captureTargets,
                serializeAs = serializeAs,
            )
        } else {
            return ResolvedValueContainer.Wrapper(
                classSymbol = this.symbol,
                getterSymbol = getter.symbol,
                setterSymbols = listOf(setter.symbol),
                captureTargetSymbols = captureTargets,
                serializeAs = serializeAs,
            )
        }
    }

    private fun IrSimpleFunction.hasAnnotationOnSelfOrCorrespondingProperty(fqName: FqName): Boolean {
        return hasAnnotation(fqName) || correspondingPropertySymbol?.owner?.hasAnnotation(fqName) == true
    }
}
