package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.generateUUIDStringCall
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

context(BackInTimePluginContext)
class BackInTimeDebuggablePropertyGenerationTransformer : IrElementTransformerVoid() {
    private fun shouldGenerateInitializer(declaration: IrProperty): Boolean {
        if (declaration.name != BackInTimeConsts.backInTimeInstanceUUIDName) return false
        if (declaration.parentClassOrNull?.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName) != true) return false
        return true
    }

    override fun visitProperty(declaration: IrProperty): IrStatement {
        if (!shouldGenerateInitializer(declaration)) return declaration

        declaration.backingField?.initializer = with(declaration.irBlockBodyBuilder()) {
            irExprBody(generateUUIDStringCall())
        }

        return declaration
    }
}
