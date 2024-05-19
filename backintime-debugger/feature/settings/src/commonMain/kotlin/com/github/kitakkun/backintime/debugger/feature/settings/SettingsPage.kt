package com.github.kitakkun.backintime.debugger.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SettingsPage() {
    val viewModel: SettingsViewModel = koinNavViewModel()
    val bindModel by viewModel.bindModel.collectAsState()

    SettingsView(
        bindModel = bindModel,
        onChangeWebSocketPort = viewModel::updateWebSocketPort,
        onChangeDeleteSessionDataOnDisconnect = viewModel::updateDeleteSessionDataOnDisconnect,
    )
}
