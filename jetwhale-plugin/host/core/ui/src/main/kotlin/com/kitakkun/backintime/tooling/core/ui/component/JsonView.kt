package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwCodeBlock
import com.kitakkun.jetwhale.host.ui.JwTheme
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull

/**
 * A JSON document, syntax-coloured, in the host's code block.
 *
 * [JwCodeBlock] gives it the monospace style, the tinted box and the copy button every other code
 * view in the host has; the colouring below is this view's own, because a JSON scheme needs more
 * distinct hues than [JwTone][com.kitakkun.jetwhale.host.ui.JwTone] has semantics for.
 */
@Composable
fun JsonView(
    jsonString: String,
    modifier: Modifier = Modifier,
    // The host's dark flag, not the OS's: a user running a light host on a dark desktop still gets
    // the light scheme.
    colorStyle: JsonColorStyle = if (JwTheme.isDark) JsonColorStyle.Dark else JsonColorStyle.Light,
    copyLabel: String? = "Copy JSON",
) {
    val jsonElement = remember(jsonString) { Json.parseToJsonElement(jsonString) }

    JwCodeBlock(
        text = remember(colorStyle, jsonElement) {
            buildAnnotatedString {
                appendJsonString(
                    rootJsonElement = jsonElement,
                    printTrailingComma = false,
                    colorStyle = colorStyle,
                )
            }
        },
        copyLabel = copyLabel,
        modifier = modifier,
    )
}

sealed interface JsonColorStyle {
    val keySpanStyle: SpanStyle
    val stringSpanStyle: SpanStyle
    val numberSpanStyle: SpanStyle
    val booleanSpanStyle: SpanStyle
    val nullSpanStyle: SpanStyle

    data object Light : JsonColorStyle {
        override val keySpanStyle = SpanStyle(color = Color(0xFF1565C0))
        override val stringSpanStyle = SpanStyle(color = Color(0xFF2E7D32))
        override val numberSpanStyle = SpanStyle(color = Color(0xFFEF6C00))
        override val booleanSpanStyle = SpanStyle(color = Color(0xFFD84315))
        override val nullSpanStyle = SpanStyle(color = Color(0xFF6A1B9A))
    }

    data object Dark : JsonColorStyle {
        override val keySpanStyle = SpanStyle(color = Color(0xFFBC77B1))
        override val stringSpanStyle = SpanStyle(color = Color(0xFF6AAB73))
        override val numberSpanStyle = SpanStyle(color = Color(0xFF2CABB8))
        override val booleanSpanStyle = SpanStyle(color = Color(0xFFCE8E6D))
        override val nullSpanStyle = SpanStyle(color = Color(0xFFCE8E6D))
    }
}

fun AnnotatedString.Builder.appendJsonString(
    rootJsonElement: JsonElement,
    key: String? = null,
    currentIndentLevel: Int = 0,
    printTrailingComma: Boolean = false,
    colorStyle: JsonColorStyle = JsonColorStyle.Light,
) {
    append(" ".repeat(4 * currentIndentLevel))

    key?.let {
        withStyle(colorStyle.keySpanStyle) {
            append(text = "\"$key\": ")
        }
    }

    when (rootJsonElement) {
        is JsonObject -> {
            appendLine("{")
            rootJsonElement.entries.forEachIndexed { index, (key, jsonElement) ->
                appendJsonString(
                    rootJsonElement = jsonElement,
                    key = key,
                    currentIndentLevel = currentIndentLevel + 1,
                    printTrailingComma = index != rootJsonElement.entries.size - 1,
                    colorStyle = colorStyle,
                )
            }
            appendWithIndent(indentLevel = currentIndentLevel, text = "}")
        }

        is JsonArray -> {
            appendLine("[")
            rootJsonElement.forEachIndexed { index, jsonElement ->
                appendJsonString(
                    rootJsonElement = jsonElement,
                    key = null,
                    currentIndentLevel = currentIndentLevel + 1,
                    printTrailingComma = index != rootJsonElement.size - 1,
                    colorStyle = colorStyle,
                )
            }
            appendWithIndent(indentLevel = currentIndentLevel, text = "]")
        }

        is JsonPrimitive -> {
            val style = when {
                rootJsonElement.isString -> colorStyle.stringSpanStyle
                rootJsonElement.booleanOrNull != null -> colorStyle.booleanSpanStyle
                else -> colorStyle.numberSpanStyle
            }
            withStyle(style) {
                if (rootJsonElement.isString) append("\"")
                append(rootJsonElement.content)
                if (rootJsonElement.isString) append("\"")
            }
        }

        JsonNull -> {
            withStyle(colorStyle.nullSpanStyle) {
                append("null")
            }
        }
    }

    if (printTrailingComma) {
        appendLine(",")
    } else {
        appendLine()
    }
}

private fun AnnotatedString.Builder.appendWithIndent(
    indentLevel: Int,
    text: String,
    indentSpaces: Int = 4,
) {
    append(" ".repeat(indentSpaces * indentLevel))
    append(text)
}

@Preview
@Composable
private fun JsonViewPreview() {
    PreviewContainer {
        JsonView(
            jsonString =
                """
                    {
                        "key1": "String",
                        "key2": 0,
                        "key3": {
                            "nested_key1": "String",
                            "nested_key2": 0,
                            "nested_key3": 1.0,
                            "nested_key4": [
                                "string1",
                                "string2",
                                "string3",
                                "string4"
                            ]
                        },
                        "key4": [
                            "string1",
                            "string2",
                            "string3",
                            "string4"
                        ]
                    }
                """.trimIndent()
        )
    }
}