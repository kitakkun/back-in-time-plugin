package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeCompilerConfiguration
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.CallableId

class BackInTimeIrGenerationExtension(
    private val config: BackInTimeCompilerConfiguration,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val valueContainerInfo = with(UserDefinedValueContainerAnalyzer()) {
            moduleFragment.acceptChildrenVoid(this)
            resolveIdsToValueContainerInfoList(
                capturedCallableIds = config.capturedCallableIds + collectedCapturedCallableIds,
                valueGetterCallableIds = config.valueGetterCallableIds + collectedGetterCallableIds,
                valueSetterCallableIds = config.valueSetterCallableIds + collectedSetterCallableIds,
            )
        }

        moduleFragment.transformChildrenVoid(BackInTimeCallRegisterOnInitTransformer(pluginContext))
        moduleFragment.transformChildrenVoid(
            BackInTimeIrValueChangeNotifyCodeGenerationExtension(
                pluginContext = pluginContext,
                valueContainerClassInfo = valueContainerInfo,
            )
        )
        moduleFragment.transformChildrenVoid(
            GenerateManipulatorMethodBodyTransformer(
                pluginContext = pluginContext,
                valueContainerClassInfoList = valueContainerInfo,
            )
        )
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
