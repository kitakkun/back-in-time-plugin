package io.github.kitakkun.backintime.debugger.feature.instance

import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.kitakkun.backintime.debugger.feature.instance.component.history.HistoryLoadedView
import io.github.kitakkun.backintime.debugger.feature.instance.component.history.TimelineItemBindModel
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.rememberEventEmitter
import io.github.kitakkun.backintime.debugger.ui.customview.CommonLoadingView
import kotlinx.serialization.Serializable

@Serializable
data class HistoryScreenRoute(
    val sessionId: String,
    val instanceId: String,
)

internal fun NavGraphBuilder.historyScreen(
    onPressBack: () -> Unit,
) {
    composable<HistoryScreenRoute> {
        val route: HistoryScreenRoute = it.toRoute()
        HistoryScreen(
            sessionId = route.sessionId,
            instanceId = route.instanceId,
            onPressBack = onPressBack,
        )
    }
}

fun NavController.navigateToHistoryScreen(sessionId: String, instanceId: String) {
    navigate(HistoryScreenRoute(sessionId = sessionId, instanceId = instanceId))
}

@Composable
fun HistoryScreen(
    sessionId: String,
    instanceId: String,
    eventEmitter: EventEmitter<HistoryScreenEvent> = rememberEventEmitter(),
    uiState: HistoryScreenUiState = historyScreenPresenter(
        eventEmitter = eventEmitter,
        sessionId = sessionId,
        instanceId = instanceId,
    ),
    onPressBack: () -> Unit,
) {
    HistoryScreen(
        uiState = uiState,
        onPressBack = onPressBack,
    )
}

sealed interface HistoryScreenUiState {
    data object Loading : HistoryScreenUiState
    data class Loaded(
        val timelines: List<TimelineItemBindModel>,
    ) : HistoryScreenUiState
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: HistoryScreenUiState,
    onPressBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onPressBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        when (uiState) {
            is HistoryScreenUiState.Loading -> CommonLoadingView()
            is HistoryScreenUiState.Loaded -> HistoryLoadedView(uiState)
        }
    }
}
