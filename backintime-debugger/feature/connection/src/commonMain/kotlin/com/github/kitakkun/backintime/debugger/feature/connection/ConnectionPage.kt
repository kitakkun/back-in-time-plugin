package com.github.kitakkun.backintime.debugger.feature.connection

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun ConnectionPage() {
    val viewModel: ConnectionViewModel = koinNavViewModel()
    val bindModel by viewModel.bindModel.collectAsState()
    ConnectionView(bindModel)
}
