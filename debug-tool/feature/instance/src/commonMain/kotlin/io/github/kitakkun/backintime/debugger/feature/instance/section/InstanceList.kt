package io.github.kitakkun.backintime.debugger.feature.instance.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backintime.debug_tool.feature.instance.generated.resources.Res
import backintime.debug_tool.feature.instance.generated.resources.msg_no_instance_registered
import io.github.kitakkun.backintime.debugger.feature.instance.component.list.InstanceItemView
import io.github.kitakkun.backintime.debugger.feature.instance.component.list.InstanceUiState
import io.github.kitakkun.backintime.debugger.feature.instance.component.list.PropertyUiState
import org.jetbrains.compose.resources.stringResource

sealed interface InstanceListUiState {
    val items: List<InstanceUiState>

    data object Empty : InstanceListUiState {
        override val items: List<InstanceUiState> = emptyList()
    }

    data class Loaded(override val items: List<InstanceUiState>) : InstanceListUiState
}

@Composable
fun InstanceList(
    uiState: InstanceListUiState,
    onClickHistory: (InstanceUiState) -> Unit,
    onClickProperty: (InstanceUiState, PropertyUiState) -> Unit,
    onClickExpand: (InstanceUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is InstanceListUiState.Empty -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(Res.string.msg_no_instance_registered),
                )
            }
        }

        is InstanceListUiState.Loaded -> {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(4.dp),
            ) {
                items(uiState.items) { itemUiState ->
                    InstanceItemView(
                        uiState = itemUiState,
                        onClickHistory = { onClickHistory(itemUiState) },
                        onClickProperty = { onClickProperty(itemUiState, it) },
                        onClickExpand = { onClickExpand(itemUiState) },
                    )
                }
            }
        }
    }
}
