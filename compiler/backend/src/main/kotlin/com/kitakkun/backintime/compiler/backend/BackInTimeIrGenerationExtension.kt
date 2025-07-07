package com.kitakkun.backintime.compiler.backend

import com.kitakkun.backintime.compiler.backend.transformer.capture.BackInTimeDebuggableCaptureLazyDebuggablePropertyAccessTransformer
import com.kitakkun.backintime.compiler.backend.transformer.capture.BackInTimeDebuggableCapturePropertyChangesTransformer
import com.kitakkun.backintime.compiler.backend.transformer.capture.BackInTimeDebuggableConstructorTransformer
import com.kitakkun.backintime.compiler.backend.transformer.implement.BackInTimeDebuggableImplementTransformer
import com.kitakkun.backintime.compiler.backend.transformer.implement.BackInTimeEntryPointTransformer
import com.kitakkun.backintime.compiler.yaml.BackInTimeYamlConfiguration
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

class BackInTimeIrGenerationExtension(
    private val yamlConfiguration: BackInTimeYamlConfiguration,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val irContext = BackInTimePluginContext(
            baseContext = pluginContext,
            moduleFragment = moduleFragment,
            yamlConfiguration = yamlConfiguration,
        )
        with(irContext) {
            moduleFragment.transformChildrenVoid(BackInTimeEntryPointTransformer(irContext))
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableConstructorTransformer(irContext))
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableCapturePropertyChangesTransformer(irContext))
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableImplementTransformer(irContext))
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableCaptureLazyDebuggablePropertyAccessTransformer(irContext))
        }
    }
}
