package com.github.kitakkun.backintime.debugger.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.kitakkun.backintime.debugger.featurecommon.lifecycle.GlobalViewModelStoreOwner

@Composable
fun SettingsPage() {
    val viewModel: SettingsViewModel = viewModel(GlobalViewModelStoreOwner)
    val bindModel by viewModel.bindModel.collectAsState()

    SettingsView(
        bindModel = bindModel,
        onChangeWebSocketPort = viewModel::updateWebSocketPort,
        onChangeDeleteSessionDataOnDisconnect = viewModel::updateDeleteSessionDataOnDisconnect,
    )
}
