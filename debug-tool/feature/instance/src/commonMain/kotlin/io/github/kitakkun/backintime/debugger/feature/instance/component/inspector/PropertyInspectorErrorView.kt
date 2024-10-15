package io.github.kitakkun.backintime.debugger.feature.instance.component.inspector

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.kitakkun.backintime.debugger.feature.instance.PropertyInspectorScreenUiState

@Composable
fun PropertyInspectErrorView(
    bindModel: PropertyInspectorScreenUiState.Error,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text("Error: ${bindModel.message}")
    }
}
