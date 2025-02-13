package com.kitakkun.backintime.compiler.yaml

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(TypeSignatureSerializer::class)
sealed interface TypeSignature {
    @Serializable
    data class Generic(val index: Int) : TypeSignature

    @Serializable
    data class Class(val classId: String) : TypeSignature

    @Serializable
    data object Any : TypeSignature
}

private class TypeSignatureSerializer : KSerializer<TypeSignature> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName = "TypeSignature", kind = PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TypeSignature {
        val value = decoder.decodeString()
        if (value == "*") return TypeSignature.Any
        return value.toIntOrNull()?.let { TypeSignature.Generic(it) } ?: TypeSignature.Class(value)
    }

    override fun serialize(encoder: Encoder, value: TypeSignature) {
        val stringValue = when (value) {
            is TypeSignature.Any -> "*"
            is TypeSignature.Class -> value.classId
            is TypeSignature.Generic -> value.index.toString()
        }
        encoder.encodeString(stringValue)
    }
}