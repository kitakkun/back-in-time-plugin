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
    data object This : CallableSignature

    @Serializable
    sealed interface PropertyAccessor : CallableSignature {
        val propertyName: String

        @Serializable
        data class Getter(override val propertyName: String) : PropertyAccessor

        @Serializable
        data class Setter(override val propertyName: String) : PropertyAccessor
    }

    @Serializable
    sealed interface NamedFunction : CallableSignature {
        val name: String

        data class Member(override val name: String) : NamedFunction
        data class TopLevel(val packageFqName: String, override val name: String) : NamedFunction
    }
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
        } else if (value == "<this>") {
            CallableSignature.This
        } else {
            if (value.contains("/")) {
                CallableSignature.NamedFunction.TopLevel(
                    packageFqName = value.split("/").dropLast(1).joinToString("."),
                    name = value.split("/").lastOrNull() ?: "",
                )
            } else {
                CallableSignature.NamedFunction.Member(name = value)
            }
        }
    }

    override fun serialize(encoder: Encoder, value: CallableSignature) {
    }
}
