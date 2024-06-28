package com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.kitakkun.backintime.debugger.ui.customview.CommonLoadingView

@Composable
fun PropertyInspectorView(
    bindModel: PropertyInspectorBindModel,
    onToggleSortWithTime: () -> Unit,
    onToggleSortWithValue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (bindModel) {
        is PropertyInspectorBindModel.Loading -> CommonLoadingView(modifier)
        is PropertyInspectorBindModel.Error -> PropertyInspectErrorView(bindModel, modifier)
        is PropertyInspectorBindModel.Loaded -> PropertyInspectorLoadedView(
            bindModel = bindModel,
            onToggleSortWithTime = onToggleSortWithTime,
            onToggleSortWithValue = onToggleSortWithValue,
            modifier = modifier,
        )
    }
}
