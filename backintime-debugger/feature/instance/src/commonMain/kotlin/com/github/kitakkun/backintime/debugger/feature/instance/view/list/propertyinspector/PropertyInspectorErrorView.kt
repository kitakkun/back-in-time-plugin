package com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PropertyInspectErrorView(
    bindModel: PropertyInspectorBindModel.Error,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Text("Error: ${bindModel.message}")
    }
}
