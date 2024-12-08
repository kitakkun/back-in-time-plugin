package com.kitakkun.backintime.compiler.valuecontainer.filter.type

import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.name.ClassId

class GenericTypeMatcher(
    private val classId: ClassId,
    private val args: List<TypeMatcher>,
) : TypeMatcher() {
    override fun matches(type: IrType): Boolean {
        if (type.classOrNull?.owner?.classId != classId) return false
        if (type !is IrSimpleType) return false
        if (type.arguments.size != args.size) return false
        return type.arguments.zip(args).all { (irTypeArgument, arg) ->
            arg.matches(irTypeArgument.typeOrFail)
        }
    }
}
