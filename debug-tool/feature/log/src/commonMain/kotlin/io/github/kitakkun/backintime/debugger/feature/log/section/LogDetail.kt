package io.github.kitakkun.backintime.debugger.feature.log.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backintime.debug_tool.feature.log.generated.resources.Res
import backintime.debug_tool.feature.log.generated.resources.table_column_kind
import backintime.debug_tool.feature.log.generated.resources.table_column_payload
import backintime.debug_tool.feature.log.generated.resources.table_column_time
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun LogDetail(
    selectedLogItem: LogItemUiState?,
) {
    selectedLogItem?.let {
        SelectionContainer {
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
