package com.github.kitakkun.backintime.compiler.backend.analyzer

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.backend.ValueContainerClassInfo
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isGetter
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.simpleFunctions
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.utils.addIfNotNull

class UserDefinedValueContainerAnalyzer private constructor() : IrElementVisitorVoid {
    companion object {
        fun analyzeAdditionalValueContainerClassInfo(moduleFragment: IrModuleFragment): List<ValueContainerClassInfo> {
            with(UserDefinedValueContainerAnalyzer()) {
                moduleFragment.acceptChildrenVoid(this)
                return collectedInfoList
            }
        }
    }

    private val mutableCollectedInfoList = mutableListOf<ValueContainerClassInfo>()
    val collectedInfoList: List<ValueContainerClassInfo> get() = mutableCollectedInfoList

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
        if (parentClass.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName)) {
            val containerInfo = propertyClass.getValueContainerClassInfo()
            mutableCollectedInfoList.addIfNotNull(containerInfo)
        }
    }

    private fun IrClass.getValueContainerClassInfo(): ValueContainerClassInfo? {
        val classId = classId ?: return null

        val captures = simpleFunctions().filter { function -> function.hasAnnotationOnSelfOrCorrespondingProperty(BackInTimeAnnotations.captureAnnotationFqName) }.toList()
        val getter = simpleFunctions()
            .filter { function -> !function.isSetter }
            .firstOrNull { function -> function.hasAnnotationOnSelfOrCorrespondingProperty(BackInTimeAnnotations.getterAnnotationFqName) }
            ?: return null
        val setter = simpleFunctions()
            .filter { function -> !function.isGetter }
            .firstOrNull { function ->
                function.hasAnnotationOnSelfOrCorrespondingProperty(BackInTimeAnnotations.setterAnnotationFqName)
            } ?: return null

        if (captures.isEmpty()) return null

        return ValueContainerClassInfo(
            classId = classId,
            capturedFunctionNames = captures.map { it.name },
            getterFunctionName = getter.name,
            preSetterFunctionNames = emptyList(),
            setterFunctionName = setter.name,
            serializeItSelf = false, // FIXME: doesn't support yet
            serializeAs = null, // FIXME: doesn't support yet
        )
    }

    private fun IrSimpleFunction.hasAnnotationOnSelfOrCorrespondingProperty(fqName: FqName): Boolean {
        return hasAnnotation(fqName) || correspondingPropertySymbol?.owner?.hasAnnotation(fqName) == true
    }
}
