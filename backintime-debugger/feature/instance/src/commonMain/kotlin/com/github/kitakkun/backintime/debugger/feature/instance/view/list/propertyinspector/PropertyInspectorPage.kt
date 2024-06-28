package com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun PropertyInspectorPage(
    params: PropertyInspectorArgs,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<PropertyInspectorViewModel>(key = params.toString()) { parametersOf(params) }
    val bindModel by viewModel.bindModel.collectAsState()

    PropertyInspectorView(
        bindModel = bindModel,
        onToggleSortWithTime = viewModel::onToggleSortWithTime,
        onToggleSortWithValue = viewModel::onToggleSortWithValue,
        modifier = modifier,
    )
}
