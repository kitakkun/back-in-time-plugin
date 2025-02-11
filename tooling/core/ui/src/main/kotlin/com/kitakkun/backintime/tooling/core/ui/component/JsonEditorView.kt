package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun JsonEditorView(
    initialJsonString: String,
    modifier: Modifier = Modifier,
) {
    val textFieldState = rememberTextFieldState(initialText = initialJsonString)
    val jsonElement = remember(initialJsonString) { Json.parseToJsonElement(initialJsonString) }

    val currentJsonElement by remember {
        derivedStateOf {
            try {
                Json.parseToJsonElement(textFieldState.text.toString())
            } catch (e: Throwable) {
                null
            }
        }
    }

    Column {
        TextField(
            state = rememberTextFieldState(initialText = initialJsonString),
        )
        if (currentJsonElement == null) {
            Text(text = "Invalid JSON Format")
        }
    }
}

@Composable
fun EditableView(
    jsonElement: JsonElement,
    key: String? = null,
    indentLevel: Int = 0,
) {
    val letterDp = with(LocalDensity.current) { LocalTextStyle.current.fontSize.toDp() }
    val indentDp = (letterDp * 4) * indentLevel

    Column(
        modifier = Modifier.padding(start = indentDp)
    ) {
        Row {
            key?.let { KeyTextField(it) }
            Text(text = ":")
            when (jsonElement) {
                is JsonArray -> Text(text = "{")
                is JsonObject -> TODO()
                is JsonPrimitive -> TODO()
                JsonNull -> TODO()
            }
        }
        when (jsonElement) {
            is JsonObject -> {
                Column {
                    Text("{")

                    Text("}")
                }
            }

            is JsonArray -> {

            }

            is JsonPrimitive -> {

            }

            JsonNull -> {
                UnsafeValueTextField(
                    initialValue = "null",
                )
            }
        }
    }
}

@Composable
private fun KeyTextField(
    key: String,
) {
    val textFieldState = rememberTextFieldState(initialText = key)
    TextField(
        state = textFieldState,
        outputTransformation = {
            this.insert(this.asCharSequence().length, "\"")
            this.insert(0, "\"")
        }
    )
}

@Composable
private fun UnsafeValueTextField(
    initialValue: String,
    modifier: Modifier = Modifier,
) {
    val textFieldState = rememberTextFieldState(initialValue)
    TextField(
        state = textFieldState,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun KeyTextFieldPreview() {
    PreviewContainer {
        KeyTextField(
            key = "key"
        )
    }
}