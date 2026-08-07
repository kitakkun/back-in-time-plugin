package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer

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
