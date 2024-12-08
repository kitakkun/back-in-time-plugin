package com.kitakkun.backintime.compiler.valuecontainer.filter.type

import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.name.ClassId

class NormalTypeMatcher(val classId: ClassId) : TypeMatcher() {
    override fun matches(type: IrType): Boolean {
        return type.classOrNull?.owner?.classId == classId
    }
}
