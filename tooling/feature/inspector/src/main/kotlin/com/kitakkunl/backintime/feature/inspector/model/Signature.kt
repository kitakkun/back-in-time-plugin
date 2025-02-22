package com.kitakkunl.backintime.feature.inspector.model

sealed interface Signature {
    val signature: String
    val packageFqName: String

    fun asString(): String = signature

    @JvmInline
    value class Class(override val signature: String) : Signature {
        override val packageFqName: String get() = signature.substringBeforeLast("/")
        val className: String get() = signature.substringAfterLast("/")
    }

    @JvmInline
    value class Function(override val signature: String) : Signature {
        override val packageFqName: String
            get() = signature
                .split(" ")
                .last()
                .substringBefore("(")
                .substringBeforeLast("/")
        val functionName: String
            get() = signature
                .substringBefore("(")
                .substringAfterLast("/")
                .substringAfterLast(".")

        val parentClassFqName: String?
            get() = signature
                .split(" ")
                .last()
                .substringBeforeLast("(")
                .let {
                    if (it.contains(".")) {
                        it.substringBeforeLast(".")
                    } else {
                        null
                    }
                }
        val parentClassName: String?
            get() = parentClassFqName?.substringAfterLast("/")

        val extensionReceiverClassFqName: String?
            get() = signature.split(" ")
                .let {
                    if (it.size == 2) {
                        it.first().substringBeforeLast(".")
                    } else {
                        null
                    }
                }

        val extensionReceiverClassName: String?
            get() = extensionReceiverClassFqName?.substringAfterLast("/")
    }

    @JvmInline
    value class Property(override val signature: String) : Signature {
        override val packageFqName: String get() = signature.substringBeforeLast("/")
        val propertyName: String get() = signature.substringAfterLast("/").substringAfterLast(".")
    }
}

fun String.toFunctionSignature(): Signature.Function = Signature.Function(this)
fun String.toClassSignature(): Signature.Class = Signature.Class(this)
fun String.toPropertySignature(): Signature.Property = Signature.Property(this)
