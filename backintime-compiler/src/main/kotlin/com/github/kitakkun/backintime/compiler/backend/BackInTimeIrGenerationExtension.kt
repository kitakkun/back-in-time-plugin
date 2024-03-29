package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.configuration.BackInTimeCompilerConfiguration
import com.github.kitakkun.backintime.compiler.backend.transformer.DebuggableStateHolderTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

class BackInTimeIrGenerationExtension(
    private val config: BackInTimeCompilerConfiguration,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val context = BackInTimePluginContext(baseContext = pluginContext, config = config, moduleFragment = moduleFragment)
        with(context) {
            moduleFragment.transformChildrenVoid(DebuggableStateHolderTransformer())
        }
    }
}
