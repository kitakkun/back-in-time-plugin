package io.github.kitakkun.backintime.compiler.backend

import io.github.kitakkun.backintime.compiler.backend.transformer.capture.BackInTimeDebuggableCaptureLazyDebuggablePropertyAccessTransformer
import io.github.kitakkun.backintime.compiler.backend.transformer.capture.BackInTimeDebuggableCapturePropertyChangesTransformer
import io.github.kitakkun.backintime.compiler.backend.transformer.capture.BackInTimeDebuggableConstructorTransformer
import io.github.kitakkun.backintime.compiler.backend.transformer.implement.BackInTimeDebuggableImplementTransformer
import io.github.kitakkun.backintime.compiler.configuration.BackInTimeCompilerConfiguration
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
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableConstructorTransformer())
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableCapturePropertyChangesTransformer())
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableImplementTransformer())
            moduleFragment.transformChildrenVoid(BackInTimeDebuggableCaptureLazyDebuggablePropertyAccessTransformer())
        }
    }
}
