package com.github.kitakkun.backintime.compiler.backend.analyzer

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.BackInTimeCompilerConfiguration
import com.github.kitakkun.backintime.compiler.backend.ValueContainerClassInfo
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.name.CallableId

class UserDefinedValueContainerAnalyzer private constructor() : IrElementVisitorVoid {
    companion object {
        fun analyzeAdditionalValueContainerClassInfo(config: BackInTimeCompilerConfiguration, moduleFragment: IrModuleFragment): List<ValueContainerClassInfo> {
            with(UserDefinedValueContainerAnalyzer()) {
                moduleFragment.acceptChildrenVoid(this)
                return resolveIdsToValueContainerInfoList(
                    capturedCallableIds = config.capturedCallableIds + collectedCapturedCallableIds,
                    valueGetterCallableIds = config.valueGetterCallableIds + collectedGetterCallableIds,
                    valueSetterCallableIds = config.valueSetterCallableIds + collectedSetterCallableIds,
                )
            }
        }

        private fun resolveIdsToValueContainerInfoList(
            capturedCallableIds: Set<CallableId>,
            valueGetterCallableIds: Set<CallableId>,
            valueSetterCallableIds: Set<CallableId>,
        ): List<ValueContainerClassInfo> {
            return capturedCallableIds
                .mapNotNull { it.classId }
                .mapNotNull { classId ->
                    ValueContainerClassInfo(
                        classId = classId,
                        capturedCallableIds = capturedCallableIds.filter { it.classId == classId },
                        valueGetter = valueGetterCallableIds.firstOrNull { it.classId == classId } ?: return@mapNotNull null,
                        valueSetter = valueSetterCallableIds.firstOrNull { it.classId == classId } ?: return@mapNotNull null,
                    )
                }
        }
    }

    private val mutableCapturedCallableIds = mutableSetOf<CallableId>()
    private val mutableGetterCallableIds = mutableSetOf<CallableId>()
    private val mutableSetterCallableIds = mutableSetOf<CallableId>()
    val collectedCapturedCallableIds: Set<CallableId> = mutableCapturedCallableIds
    val collectedGetterCallableIds: Set<CallableId> = mutableGetterCallableIds
    val collectedSetterCallableIds: Set<CallableId> = mutableSetterCallableIds

    // need this to execute visitSimpleFunction
    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction) {
        val name = declaration.name
        val classId = declaration.parentClassOrNull?.classId ?: return

        if (declaration.hasAnnotation(BackInTimeAnnotations.captureAnnotationFqName)) {
            mutableCapturedCallableIds.add(CallableId(classId, name))
        }

        if (declaration.hasAnnotation(BackInTimeAnnotations.getterAnnotationFqName)) {
            mutableGetterCallableIds.add(CallableId(classId, name))
        }

        if (declaration.hasAnnotation(BackInTimeAnnotations.setterAnnotationFqName)) {
            mutableSetterCallableIds.add(CallableId(classId, name))
        }
    }

    override fun visitProperty(declaration: IrProperty) {
        val classId = declaration.parentClassOrNull?.classId ?: return

        if (declaration.hasAnnotation(BackInTimeAnnotations.captureAnnotationFqName)) {
            mutableCapturedCallableIds.add(CallableId(classId, declaration.setter!!.name))
        }

        if (declaration.hasAnnotation(BackInTimeAnnotations.getterAnnotationFqName)) {
            mutableGetterCallableIds.add(CallableId(classId, declaration.getter!!.name))
        }

        if (declaration.hasAnnotation(BackInTimeAnnotations.setterAnnotationFqName)) {
            mutableSetterCallableIds.add(CallableId(classId, declaration.setter!!.name))
        }
    }
}
