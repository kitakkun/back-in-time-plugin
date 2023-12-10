package com.github.kitakkun.backintime.test

import com.github.kitakkun.backintime.annotations.DebuggableStateHolder

@DebuggableStateHolder
class PureVarsHolder {
    var string: String = "string value"
    var int: Int = 0
    var long: Long = 0L
    var float: Float = 0f
    var double: Double = 0.0
    var boolean: Boolean = false
    var char: Char = 'a'
    var short: Short = 0
    var byte: Byte = 0
    var nullableString: String? = null
    var nullableInt: Int? = null
    var nullableLong: Long? = null
    var nullableFloat: Float? = null
    var nullableDouble: Double? = null
    var nullableBoolean: Boolean? = null
    var nullableChar: Char? = null
    var nullableShort: Short? = null
    var nullableByte: Byte? = null
    var stringList: List<String> = listOf("string value")
    var intList: List<Int> = listOf(0)
    var longList: List<Long> = listOf(0L)
    var floatList: List<Float> = listOf(0f)
    var doubleList: List<Double> = listOf(0.0)
    var booleanList: List<Boolean> = listOf(false)
}
