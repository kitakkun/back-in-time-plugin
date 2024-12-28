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

    @Serializable(TypeSignatureClassSerializer::class)
    data class Class(val classId: String) : TypeSignature
}

private class TypeSignatureSerializer : KSerializer<TypeSignature> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName = "TypeSignature", kind = PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TypeSignature {
        val value = decoder.decodeString()
        return value.toIntOrNull()?.let { TypeSignature.Generic(it) } ?: TypeSignature.Class(value)
    }

    override fun serialize(encoder: Encoder, value: TypeSignature) {
    }
}

private class TypeSignatureClassSerializer : KSerializer<TypeSignature.Class> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName = "TypeSignature.Class", kind = PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): TypeSignature.Class {
        val value = decoder.decodeString()
        return TypeSignature.Class(value)
    }

    override fun serialize(encoder: Encoder, value: TypeSignature.Class) {
    }
}