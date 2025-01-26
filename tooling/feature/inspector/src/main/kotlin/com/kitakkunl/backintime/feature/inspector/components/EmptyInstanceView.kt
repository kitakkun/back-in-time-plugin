package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.ui.component.Text

@Composable
fun EmptyInstanceView(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "No instance is registered.")
    }
}

@Preview
@Composable
private fun EmptyInstanceViewPreview() {
    PreviewContainer {
        EmptyInstanceView()
    }
}
