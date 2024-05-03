package com.github.kitakkun.backintime.debugger.feature.connection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.kitakkun.backintime.debugger.featurecommon.lifecycle.GlobalViewModelStoreOwner

@Composable
fun ConnectionPage() {
    val viewModel: ConnectionViewModel = viewModel(GlobalViewModelStoreOwner)
    val bindModel by viewModel.bindModel.collectAsState()
    ConnectionView(bindModel)
}
