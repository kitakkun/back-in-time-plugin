package com.github.kitakkun.backintime.debugger.feature.instance.view.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.github.kitakkun.backintime.debugger.feature.instance.navigation.navigateToInstanceHistory
import com.github.kitakkun.backintime.debugger.feature.instance.view.InstanceSharedViewModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.component.PropertyBindModel
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf

@OptIn(KoinExperimentalAPI::class)
@Composable
fun SessionInstancePage(
    sessionId: String,
    navController: NavController,
) {
    val sharedViewModel: InstanceSharedViewModel = koinNavViewModel()
    val viewModel: SessionInstanceViewModel = koinViewModel(key = sessionId) { parametersOf(sessionId) }
    val bindModel by viewModel.bindModel.collectAsState()

    SessionInstanceView(
        bindModel = bindModel,
        onTogglePropertiesExpanded = viewModel::onTogglePropertiesExpanded,
        onClickProperty = { instance, property ->
            val propertyOwnerClassName = when (property) {
                is PropertyBindModel.Super -> property.parentClassName
                else -> instance.className
            }
            sharedViewModel.selectProperty(
                sessionId = sessionId,
                instanceId = instance.uuid,
                propertyName = property.name,
                propertyOwnerClassName = propertyOwnerClassName,
            )
        },
        onClickHistory = { bindModel ->
            navController.navigateToInstanceHistory(
                sessionId = sessionId,
                instanceId = bindModel.uuid,
            )
        },
    )
}
