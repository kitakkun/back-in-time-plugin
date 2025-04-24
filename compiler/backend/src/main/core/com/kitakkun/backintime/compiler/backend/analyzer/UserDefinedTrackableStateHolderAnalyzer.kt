package com.kitakkun.backintime.compiler.backend.analyzer

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.trackablestateholder.CaptureStrategy
import com.kitakkun.backintime.compiler.backend.trackablestateholder.ResolvedTrackableStateHolder
import com.kitakkun.backintime.compiler.common.BackInTimeAnnotations
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

class UserDefinedTrackableStateHolderAnalyzer private constructor(
    private val irContext: BackInTimePluginContext,
) : IrElementVisitorVoid {
    companion object {
        fun analyze(
            irPluginContext: BackInTimePluginContext,
            moduleFragment: IrModuleFragment,
        ): List<ResolvedTrackableStateHolder> {
            with(UserDefinedTrackableStateHolderAnalyzer(irPluginContext)) {
                moduleFragment.acceptChildrenVoid(this)
                return collectedInfoList
            }
        }
    }

    private val mutableCollectedInfoList = mutableListOf<ResolvedTrackableStateHolder>()
    val collectedInfoList: List<ResolvedTrackableStateHolder> get() = mutableCollectedInfoList

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitClass(declaration: IrClass) {
        declaration.acceptChildrenVoid(this)

        if (!declaration.hasAnnotation(BackInTimeAnnotations.trackableStateHolderAnnotationFqName)) return

        val containerInfo = declaration.getTrackableStateHolderClassInfo()
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
            val trackableStateHolderInfo = propertyClass.getTrackableStateHolderClassInfo()
            mutableCollectedInfoList.addIfNotNull(trackableStateHolderInfo)
        }
    }

    private fun IrClass.getTrackableStateHolderClassInfo(): ResolvedTrackableStateHolder? {
        val isSelfContained = hasAnnotation(BackInTimeAnnotations.selfContainedTrackableStateHolderAnnotationFqName)
        val serializeAs = annotations.findAnnotation(BackInTimeAnnotations.serializeAsAnnotationFqName)
            ?.getAnnotationValueOrNull<KClass<*>>("clazz")?.java?.classId?.let { irContext.referenceClass(it) }

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
            return ResolvedTrackableStateHolder.SelfContained(
                classSymbol = this.symbol,
                setterSymbols = listOf(setter.symbol),
                captureTargetSymbols = captureTargets,
                serializeAs = serializeAs,
            )
        } else {
            return ResolvedTrackableStateHolder.Wrapper(
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
