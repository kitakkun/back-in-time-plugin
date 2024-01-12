package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.BackInTimeAnnotations
import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.hasAnnotation
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

context(BackInTimePluginContext)
class DebuggableStateHolderTransformer : IrElementTransformerVoid() {
    override fun visitElement(element: IrElement): IrElement {
        element.transformChildrenVoid()
        return element
    }

    override fun visitClass(declaration: IrClass): IrStatement {
        if (declaration.hasAnnotation(BackInTimeAnnotations.debuggableStateHolderAnnotationFqName)) {
            declaration.apply {
                transformChildrenVoid(RegisterOnInitTransformer())
                transformChildrenVoid(CaptureValueChangeInsideMethodTransformer())
                transformChildrenVoid(BackInTimeDebuggableMethodBodyGenerationTransformer())
                transformChildrenVoid(BackInTimeDebuggablePropertyGenerationTransformer())
            }
        }
        return declaration
    }
}
