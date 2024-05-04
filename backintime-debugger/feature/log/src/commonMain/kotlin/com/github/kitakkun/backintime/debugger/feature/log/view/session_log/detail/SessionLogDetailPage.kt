package com.github.kitakkun.backintime.debugger.feature.log.view.session_log.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.kitakkun.backintime.debugger.feature.log.view.session_log.SessionLogViewModel
import com.github.kitakkun.backintime.debugger.featurecommon.lifecycle.GlobalViewModelStoreOwner
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import com.github.kitakkun.backintime.log.generated.resources.Res
import com.github.kitakkun.backintime.log.generated.resources.table_column_kind
import com.github.kitakkun.backintime.log.generated.resources.table_column_payload
import com.github.kitakkun.backintime.log.generated.resources.table_column_time
import org.jetbrains.compose.resources.stringResource

@Composable
fun SessionLogDetailPage() {
    val sharedViewModel: SessionLogViewModel = viewModel(GlobalViewModelStoreOwner)
    val selectedLogItemBindModel by sharedViewModel.selectedLogItem.collectAsState()

    selectedLogItemBindModel?.let {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                Text(
                    text = stringResource(Res.string.table_column_time),
                    style = DebuggerTheme.typography.labelLarge,
                )
                Text(
                    text = "${it.formattedCreatedAt} (${it.createdAt})",
                    style = DebuggerTheme.typography.labelMedium,
                )
            }
            item {
                Text(
                    text = stringResource(Res.string.table_column_kind),
                    style = DebuggerTheme.typography.labelLarge,
                )
                Text(
                    text = it.kind.label,
                    style = DebuggerTheme.typography.labelMedium,
                )
            }
            item {
                Text(
                    text = stringResource(Res.string.table_column_payload),
                    style = DebuggerTheme.typography.labelLarge,
                )
                Text(
                    text = it.formattedPayload,
                    style = DebuggerTheme.typography.labelMedium,
                )
            }
        }
    } ?: Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text("No item selected")
    }
}
