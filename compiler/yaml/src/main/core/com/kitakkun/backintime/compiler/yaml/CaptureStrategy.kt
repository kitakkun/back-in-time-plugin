package com.kitakkun.backintime.compiler.yaml

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(CaptureStrategySerializer::class)
sealed interface CaptureStrategy {
    @Serializable
    data class ValueArgument(val index: Int) : CaptureStrategy

    @Serializable
    data object AfterCall : CaptureStrategy
}

private class CaptureStrategySerializer : KSerializer<CaptureStrategy> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(serialName = "CaptureStrategy", kind = PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): CaptureStrategy {
        val value = decoder.decodeString()
        return when {
            value.startsWith("arg") -> CaptureStrategy.ValueArgument(value.removePrefix("arg").toInt())
            value == "afterCall" -> CaptureStrategy.AfterCall
            else -> error("Unexpected pattern for CaptureStrategy: $value")
        }
    }

    override fun serialize(encoder: Encoder, value: CaptureStrategy) {
        val encodedValue = when (value) {
            is CaptureStrategy.ValueArgument -> "arg${value.index}"
            is CaptureStrategy.AfterCall -> "afterCall"
        }
        encoder.encodeString(encodedValue)
    }
}

