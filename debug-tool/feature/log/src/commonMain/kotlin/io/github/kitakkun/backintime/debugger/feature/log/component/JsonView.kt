package io.github.kitakkun.backintime.debugger.feature.log.component

import androidx.compose.animation.animateContentSize
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement

sealed interface JsonNode {
    val expanded: Boolean
    val hasChildren: Boolean
        get() = when (this) {
            is Root -> children.isNotEmpty()
            is Inner -> children.isNotEmpty()
            is Leaf -> false
        }

    data class Root(val children: List<JsonNode>, override val expanded: Boolean = false) : JsonNode
    data class Inner(val key: String, val children: List<JsonNode>, override val expanded: Boolean = false) :
        JsonNode

    data class Leaf(val key: String, val value: String) : JsonNode {
        override val expanded: Boolean = true
    }
}

inline fun <reified T> convertToJsonNode(serializableObject: T): JsonNode {
    val jsonElement = Json.encodeToJsonElement(serializableObject)
    return TODO()
}

fun JsonElement.toJsonNode(isRoot: Boolean): JsonNode {
    return when (this) {
        is JsonObject -> {
            val children = this.entries.map { (_, value) ->
                value.toJsonNode(isRoot = false)
            }
            JsonNode.Inner(key = "root", children = children)
        }

        else -> JsonNode.Leaf(key = "root", value = this.toString())
    }
}

@Composable
fun JsonView(
    element: JsonElement,
) {
    when (element) {
        is JsonObject -> {
        }

        is JsonArray -> {
        }

        is JsonPrimitive -> {
        }
    }
}

@Composable
fun JsonView(
    node: JsonNode,
    onClickToggleExpand: (node: JsonNode) -> Unit,
    modifier: Modifier = Modifier,
    indentCharCount: Int = 2,
    textStyle: TextStyle = MaterialTheme.typography.labelSmall,
) {
    val density = LocalDensity.current
    val fontSizeDp = with(density) { textStyle.fontSize.toDp() }
    val indentDp = remember(indentCharCount) { fontSizeDp * indentCharCount }

    CompositionLocalProvider(LocalTextStyle provides textStyle) {
        Column(modifier = modifier) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                when (node) {
                    is JsonNode.Leaf -> Text("${node.key}: ${node.value}")
                    is JsonNode.Root -> Text(if (node.expanded) "{" else "{ ... }")
                    is JsonNode.Inner -> Text(if (node.expanded) "${node.key}: {" else "${node.key}: { ... }")
                }
                if (node.hasChildren) {
                    Icon(
                        imageVector = if (!node.expanded) Icons.AutoMirrored.Filled.ArrowRight else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable(
                                onClick = { onClickToggleExpand(node) },
                            ),
                    )
                }
            }
            if (node.hasChildren && node.expanded) {
                Column(
                    modifier = Modifier
                        .padding(start = indentDp)
                        .animateContentSize(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    when (node) {
                        is JsonNode.Root -> {
                            node.children.forEach { child ->
                                JsonView(
                                    node = child,
                                    onClickToggleExpand = onClickToggleExpand,
                                )
                            }
                        }

                        is JsonNode.Inner -> {
                            node.children.forEach { child ->
                                JsonView(
                                    node = child,
                                    onClickToggleExpand = onClickToggleExpand,
                                )
                            }
                        }

                        is JsonNode.Leaf -> {
                            JsonView(
                                node = node,
                                onClickToggleExpand = onClickToggleExpand,
                            )
                        }
                    }
                }
                Text("}")
            }
        }
    }
}

@Preview
@Composable
private fun JsonViewPreview() {
    JsonView(
        node = JsonNode.Root(
            children = listOf(
                JsonNode.Inner(
                    key = "key1",
                    children = listOf(
                        JsonNode.Leaf("key2", "value2"),
                        JsonNode.Leaf("key3", "value3"),
                    ),
                    expanded = true,
                ),
                JsonNode.Inner(
                    key = "key4",
                    children = listOf(
                        JsonNode.Leaf("key5", "value5"),
                        JsonNode.Leaf("key6", "value6"),
                    ),
                ),
            ),
            expanded = true,
        ),
        onClickToggleExpand = { },
    )
}
