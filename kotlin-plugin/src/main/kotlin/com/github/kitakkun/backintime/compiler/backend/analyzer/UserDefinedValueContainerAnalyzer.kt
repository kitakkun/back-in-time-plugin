package com.github.kitakkun.backintime.compiler.backend.analyzer

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.backend.ValueContainerClassInfo
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.isGetter
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.simpleFunctions
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName

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
        if (!declaration.hasAnnotation(BackInTimeAnnotations.valueContainerAnnotationFqName)) return

        val classId = declaration.classId ?: return

        val captures = declaration.simpleFunctions().filter { function -> function.hasAnnotationOnSelfOrCorrespondingProperty(BackInTimeAnnotations.captureAnnotationFqName) }.toList()
        val getter = declaration.simpleFunctions()
            .filter { function -> !function.isSetter }
            .firstOrNull { function -> function.hasAnnotationOnSelfOrCorrespondingProperty(BackInTimeAnnotations.getterAnnotationFqName) }
            ?: return
        val setter = declaration
            .simpleFunctions()
            .filter { function -> !function.isGetter }
            .firstOrNull { function ->
                function.hasAnnotationOnSelfOrCorrespondingProperty(BackInTimeAnnotations.setterAnnotationFqName)
            } ?: return

        if (captures.isEmpty()) return

        val containerInfo = ValueContainerClassInfo(
            classId = classId,
            capturedCallableIds = captures.map { CallableId(classId, it.name) },
            valueGetter = CallableId(classId, getter.name),
            valueSetter = CallableId(classId, setter.name),
        )

        mutableCollectedInfoList.add(containerInfo)
    }

    private fun IrSimpleFunction.hasAnnotationOnSelfOrCorrespondingProperty(fqName: FqName): Boolean {
        return hasAnnotation(fqName) || correspondingPropertySymbol?.owner?.hasAnnotation(fqName) == true
    }
}
