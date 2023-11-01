package com.github.kitakkun.backintime.backend

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

class BackInTimeIrGenerationExtension : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        moduleFragment.transformChildrenVoid(BackInTimeForceSetPropertyValueGenerateTransformer(pluginContext))
        moduleFragment.transformChildrenVoid(BackInTimePropertySetterTransformer(pluginContext))
    }
}
