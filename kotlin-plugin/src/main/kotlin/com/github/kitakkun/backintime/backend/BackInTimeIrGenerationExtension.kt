package com.github.kitakkun.backintime.backend

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid
import org.jetbrains.kotlin.name.CallableId

class BackInTimeIrGenerationExtension(
    private val capturedCallableIds: List<CallableId>,
    private val valueGetterCallableIds: List<CallableId>,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transformChildrenVoid(BackInTimeCallRegisterOnInitTransformer(pluginContext))
//        moduleFragment.transformChildrenVoid(BackInTimePureVarPropertySetterTransformer(pluginContext))
        moduleFragment.transformChildrenVoid(
            BackInTimeIrValueChangeNotifyCodeGenerationExtension(
                pluginContext = pluginContext,
                capturedCallableIds = capturedCallableIds,
                valueGetterCallableIds = valueGetterCallableIds,
            )
        )
        moduleFragment.transformChildrenVoid(BackInTimeForceSetPropertyValueGenerateTransformer(pluginContext))
    }
}
