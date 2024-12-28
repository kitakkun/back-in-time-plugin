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
        val valueParameters: ParametersSignature

        data class Member(
            override val name: String,
            override val valueParameters: ParametersSignature,
        ) : NamedFunction

        data class TopLevel(
            val receiverClassId: String,
            val packageFqName: String,
            override val name: String,
            override val valueParameters: ParametersSignature,
        ) : NamedFunction
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
            // com/example/ReceiverClass com/example/hoge(*, kotlin/Int)
            // -> com/example/ReceiverClass com/example/hoge
            val signature = value.takeWhile { it != '(' }
            val receiverAndFunction = signature.split(" ")

            val receiverPart: String
            val functionPart: String
            when (receiverAndFunction.size) {
                1 -> {
                    receiverPart = ""
                    functionPart = receiverAndFunction.single()
                }

                2 -> {
                    receiverPart = receiverAndFunction[0]
                    functionPart = receiverAndFunction[1]
                }

                else -> error("Error!")
            }

            val functionName = functionPart.takeLastWhile { it != '.' && it != '/' }
            val functionPackage = functionPart.removeSuffix(functionName)
                .replace("/", ".")
                .removeSuffix(".")

            val parameters = if (!value.contains("(")) {
                ParametersSignature.Any
            } else {
                ParametersSignature.Specified(
                    value.dropWhile { it != '(' }.dropLastWhile { it != ')' }
                        .split(",")
                        .map { it.trim() }
                        .map {
                            if (it == "*") TypeSignature.Any
                            else if (it.toIntOrNull() != null) TypeSignature.Generic(it.toInt())
                            else TypeSignature.Class(it)
                        }
                )
            }

            if (receiverPart.isEmpty() && functionPackage.isEmpty()) {
                CallableSignature.NamedFunction.Member(
                    name = functionName,
                    valueParameters = parameters,
                )
            } else {
                CallableSignature.NamedFunction.TopLevel(
                    receiverClassId = receiverPart,
                    packageFqName = functionPackage,
                    name = functionName,
                    valueParameters = parameters,
                )
            }
        }
    }

    override fun serialize(encoder: Encoder, value: CallableSignature) {
    }
}
