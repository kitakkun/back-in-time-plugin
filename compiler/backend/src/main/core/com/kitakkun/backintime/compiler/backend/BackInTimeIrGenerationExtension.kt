package com.kitakkun.backintime.compiler.backend

import com.kitakkun.backintime.compiler.backend.api.VersionSpecificAPI
import com.kitakkun.backintime.compiler.backend.api.VersionSpecificAPIImpl
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
        VersionSpecificAPI.INSTANCE = VersionSpecificAPIImpl

        val context = BackInTimePluginContext(
            baseContext = pluginContext,
            moduleFragment = moduleFragment,
            yamlConfiguration = yamlConfiguration,
        )
        with(context) {
            moduleFragment.transformChildrenVoid(BackInTimeEntryPointTransformer(context))
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableConstructorTransformer())
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableCapturePropertyChangesTransformer())
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableImplementTransformer())
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableCaptureLazyDebuggablePropertyAccessTransformer())
        }
    }
}
