package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeCompilerConfiguration
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.CallableId

class BackInTimeIrGenerationExtension(
    config: BackInTimeCompilerConfiguration,
) : IrGenerationExtension {
    private val mutableCapturedCallableIds = config.capturedCallableIds.toMutableSet()
    private val mutableValueGetterCallableIds = config.valueGetterCallableIds.toMutableSet()
    private val mutableValueSetterCallableIds = config.valueSetterCallableIds.toMutableSet()
    private val capturedCallableIds: Set<CallableId> = mutableCapturedCallableIds
    private val valueGetterCallableIds: Set<CallableId> = mutableValueGetterCallableIds
    private val valueSetterCallableIds: Set<CallableId> = mutableValueSetterCallableIds

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        with(UserDefinedValueContainerAnalyzer()) {
            moduleFragment.acceptChildrenVoid(this)
            mutableCapturedCallableIds.addAll(collectedCapturedCallableIds)
            mutableValueGetterCallableIds.addAll(collectedGetterCallableIds)
            mutableValueSetterCallableIds.addAll(collectedSetterCallableIds)
        }

        moduleFragment.transformChildrenVoid(BackInTimeCallRegisterOnInitTransformer(pluginContext))
        moduleFragment.transformChildrenVoid(
            BackInTimeIrValueChangeNotifyCodeGenerationExtension(
                pluginContext = pluginContext,
                capturedCallableIds = capturedCallableIds,
                valueGetterCallableIds = valueGetterCallableIds,
            )
        )
        moduleFragment.transformChildrenVoid(
            GenerateManipulatorMethodBodyTransformer(
                pluginContext = pluginContext,
                valueSetterCallableIds = valueSetterCallableIds,
            )
        )
    }
}
