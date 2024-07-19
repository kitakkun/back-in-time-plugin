package io.github.kitakkun.backintime.compiler.backend.transformer.init

import io.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import io.github.kitakkun.backintime.compiler.consts.BackInTimeAnnotations
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irInt
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.getAnnotationArgumentValue
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

context(BackInTimePluginContext)
class BackInTimeEntryPointTransformer : IrElementTransformerVoid() {
    override fun visitFunction(declaration: IrFunction): IrStatement {
        val hostName = declaration.getAnnotationArgumentValue<String>(BackInTimeAnnotations.BACK_IN_TIME_ENTRY_POINT_FQ_NAME, "host")
        val port = declaration.getAnnotationArgumentValue<Int>(BackInTimeAnnotations.BACK_IN_TIME_ENTRY_POINT_FQ_NAME, "port")

        if (hostName == null || port == null) return super.visitFunction(declaration)

        val irBuilder = irBuiltIns.createIrBuilder(declaration.symbol)

        // TODO: IrExprBodyの時にバグりそう
        val originalStatements = declaration.body?.statements.orEmpty()
        declaration.body = irBuilder.irBlockBody {
            +irCall(startBackInTimeDebugServiceFunctionSymbol).apply {
                putValueArgument(0, irString(hostName))
                putValueArgument(1, irInt(port))
            }
            +originalStatements
        }

        return super.visitFunction(declaration)
    }
}
