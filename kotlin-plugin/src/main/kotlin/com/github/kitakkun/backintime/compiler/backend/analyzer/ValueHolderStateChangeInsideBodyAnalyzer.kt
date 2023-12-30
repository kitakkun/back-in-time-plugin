package com.github.kitakkun.backintime.compiler.backend.analyzer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.isValueContainerSetterCall
import com.github.kitakkun.backintime.compiler.backend.utils.receiver
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.util.allParameters
import org.jetbrains.kotlin.ir.util.getAllArgumentsWithIr
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

/**
 * 外部関数のbody内で値を変更する関数呼び出しが発生しているかチェックし，呼び出し後にキャプチャが必要なプロパティを返す
 */
context(BackInTimePluginContext)
class ValueHolderStateChangeInsideBodyAnalyzer private constructor(
    targetExpression: IrCall
) : IrElementVisitorVoid {
    companion object {
        context(BackInTimePluginContext)
        fun analyzePropertiesShouldBeCaptured(expression: IrCall): Set<IrProperty> {
            with(ValueHolderStateChangeInsideBodyAnalyzer(expression)) {
                expression.symbol.owner.acceptChildrenVoid(this)
                return modifiedProperties
            }
        }
    }

    private val mutableModifiedProperties = mutableSetOf<IrProperty>()
    val modifiedProperties: Set<IrProperty> = mutableModifiedProperties

    private val parameters = targetExpression.symbol.owner.allParameters
    private val allArgumentsWithIr = targetExpression.getAllArgumentsWithIr()

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitCall(expression: IrCall) {
        if (!expression.isValueContainerSetterCall()) return

        val receiverAsIrGetValue = expression.receiver as? IrGetValue ?: return
        val receiverParameter = receiverAsIrGetValue.symbol.owner as? IrValueParameter ?: return
        if (receiverParameter !in parameters) return

        val (_, correspondingArgument) = allArgumentsWithIr.find { (parameter, _) -> parameter == receiverParameter } ?: return
        val correspondingProperty = (correspondingArgument as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner ?: return
        mutableModifiedProperties += correspondingProperty
    }
}
