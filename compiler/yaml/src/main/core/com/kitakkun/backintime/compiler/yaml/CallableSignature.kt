package com.kitakkun.backintime.compiler.yaml

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(CallableSignatureSerializer::class)
sealed interface CallableSignature {
    @Serializable
    sealed interface PropertyAccessor : CallableSignature {
        val propertyName: String

        @Serializable
        data class Getter(override val propertyName: String) : PropertyAccessor

        @Serializable
        data class Setter(override val propertyName: String) : PropertyAccessor
    }

    @Serializable
    data class NamedFunction(
        val name: String,
        val typeParameters: List<String>?,
        val parameters: List<String>?,
    ) : CallableSignature
}

private class CallableSignatureSerializer : KSerializer<CallableSignature> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName = "CallableSignature", kind = PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): CallableSignature {
        val value = decoder.decodeString()
        return if (value.startsWith("<get-") && value.endsWith(">")) {
            val propertyName = value.removePrefix("<get-").removeSuffix(">")
            CallableSignature.PropertyAccessor.Getter(propertyName)
        } else if (value.startsWith("<set-") && value.endsWith(">")) {
            val propertyName = value.removePrefix("<set-").removeSuffix(">")
            CallableSignature.PropertyAccessor.Setter(propertyName)
        } else {
            error("Unexpected pattern")
        }
    }

    override fun serialize(encoder: Encoder, value: CallableSignature) {
    }
}
