package io.github.kitakkun.backintime.debugger.feature.instance

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.dialog
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.rememberEventEmitter
import kotlinx.serialization.Serializable

@Serializable
data object InstanceSettingsScreen

fun NavGraphBuilder.instanceSettingsScreen(onPressBack: () -> Unit) {
    dialog<InstanceSettingsScreen>(
        dialogProperties = DialogProperties(
            usePlatformDefaultWidth = false,
        )
    ) {
        InstanceSettingsScreen(onPressBack = onPressBack)
    }
}

fun NavController.navigateToInstanceSettingsScreen() {
    navigate(InstanceSettingsScreen)
}

@Composable
fun InstanceSettingsScreen(
    eventEmitter: EventEmitter<InstanceSettingsScreenEvent> = rememberEventEmitter(),
    uiState: InstanceSettingsUiState = instanceSettingsPresenter(eventEmitter),
    onPressBack: () -> Unit,
) {
    InstanceSettingsScreen(
        uiState = uiState,
        onPressBack = onPressBack,
    )
}

data class InstanceSettingsUiState(
    val showOnlyActiveInstances: Boolean,
    val showOnlyDebuggableProperties: Boolean,
    val showOnlyPropertiesWithStateChanges: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InstanceSettingsScreen(
    uiState: InstanceSettingsUiState,
    onPressBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Instance Tab Settings") },
                actions = {
                    IconButton(onClick = onPressBack) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            )
        },
        modifier = Modifier
            .widthIn(max = 500.dp)
            .heightIn(max = 600.dp)
            .clip(MaterialTheme.shapes.medium),
    ) {
        LazyColumn(
            modifier = Modifier.padding(it),
            contentPadding = PaddingValues(32.dp),
        ) {
            item {
                Row {
                    Text("Show only active instances:")
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = uiState.showOnlyActiveInstances,
                        onCheckedChange = {}
                    )
                }
            }
            item {
                Row {
                    Text("Show only debuggable properties:")
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = uiState.showOnlyDebuggableProperties,
                        onCheckedChange = {}
                    )
                }
            }
            item {
                Row {
                    Text("Show only properties with state changes:")
                    Spacer(Modifier.weight(1f))
                    Switch(
                        checked = uiState.showOnlyPropertiesWithStateChanges,
                        onCheckedChange = {}
                    )
                }
            }
        }
    }
}
