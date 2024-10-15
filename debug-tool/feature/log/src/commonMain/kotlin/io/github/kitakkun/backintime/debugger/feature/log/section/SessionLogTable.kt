package io.github.kitakkun.backintime.debugger.feature.log.section

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backintime.debug_tool.feature.log.generated.resources.Res
import backintime.debug_tool.feature.log.generated.resources.table_column_kind
import backintime.debug_tool.feature.log.generated.resources.table_column_payload
import backintime.debug_tool.feature.log.generated.resources.table_column_time
import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import io.github.kitakkun.backintime.debugger.featurecommon.component.EventKind
import io.github.kitakkun.backintime.debugger.featurecommon.component.KindFilterPopup
import io.github.kitakkun.backintime.debugger.featurecommon.component.TableBodyCell
import io.github.kitakkun.backintime.debugger.featurecommon.component.TableHeadCell
import io.github.kitakkun.backintime.debugger.featurecommon.component.TimeFilterPopup
import io.github.kitakkun.backintime.debugger.featurecommon.util.formatEpochSecondsToDateTimeText
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource

data class LogItemUiState(
    val id: String,
    val payload: BackInTimeDebugServiceEvent,
    val createdAt: Long,
    val selected: Boolean,
) {
    @OptIn(ExperimentalSerializationApi::class)
    private val prettyPrintJson = Json {
        explicitNulls = true
        prettyPrint = true
        classDiscriminatorMode = ClassDiscriminatorMode.NONE
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val simplifyJson = Json {
        classDiscriminatorMode = ClassDiscriminatorMode.NONE
    }

    val kind: EventKind = EventKind.fromEvent(payload)

    val formattedCreatedAt: String = formatEpochSecondsToDateTimeText(createdAt)

    val simplifiedPayload: String = simplifyJson.encodeToString(payload)
    val formattedPayload: String = prettyPrintJson.encodeToString(payload)
}

data class SessionLogTableUiState(
    val logs: List<LogItemUiState>,
    val sortRule: SortRule,
    val visibleKinds: Set<EventKind>,
) {
    enum class SortRule {
        CREATED_AT_ASC,
        CREATED_AT_DESC,
        KIND_ASC,
        KIND_DESC,
    }

    val selectedItem: LogItemUiState? get() = logs.firstOrNull { it.selected }
}

@Composable
fun SessionLogTable(
    uiState: SessionLogTableUiState,
    onToggleSortWithTime: () -> Unit,
    onToggleSortWithKind: () -> Unit,
    onUpdateVisibleKinds: (Set<EventKind>) -> Unit,
    onSelectLogItem: (LogItemUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val timeColumnWidth = 160.dp
    val kindColumnWidth = 200.dp
    val lazyListState = rememberLazyListState()

    Box(modifier) {
        LazyColumn(
            state = lazyListState,
            modifier = modifier,
        ) {
            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TableHeadCell(
                        text = stringResource(Res.string.table_column_time),
                        style = DebuggerTheme.typography.bodyMedium,
                        isSortActive = uiState.sortRule == SessionLogTableUiState.SortRule.CREATED_AT_ASC || uiState.sortRule == SessionLogTableUiState.SortRule.CREATED_AT_DESC,
                        isSortedAscending = uiState.sortRule == SessionLogTableUiState.SortRule.CREATED_AT_ASC,
                        filterPopupDialog = { TimeFilterPopup(it) },
                        onClickSort = onToggleSortWithTime,
                        modifier = Modifier.width(timeColumnWidth),
                    )
                    TableHeadCell(
                        text = stringResource(Res.string.table_column_kind),
                        style = DebuggerTheme.typography.bodyMedium,
                        isSortActive = uiState.sortRule == SessionLogTableUiState.SortRule.KIND_ASC || uiState.sortRule == SessionLogTableUiState.SortRule.KIND_DESC,
                        isSortedAscending = uiState.sortRule == SessionLogTableUiState.SortRule.KIND_ASC,
                        onClickSort = onToggleSortWithKind,
                        filterPopupDialog = { dismiss ->
                            KindFilterPopup(
                                selectedKinds = uiState.visibleKinds,
                                onSelectedKindsUpdate = onUpdateVisibleKinds,
                                onDismissRequest = dismiss,
                            )
                        },
                        modifier = Modifier.width(kindColumnWidth),
                    )
                    // TODO: 検索機能
                    TableHeadCell(
                        text = stringResource(Res.string.table_column_payload),
                        style = DebuggerTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            items(uiState.logs) { itemUiState ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .then(
                            if (itemUiState.selected) {
                                Modifier.background(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = MaterialTheme.shapes.small,
                                )
                            } else {
                                Modifier
                            }
                        )
                        .clickable {
                            onSelectLogItem(itemUiState)
                        },
                ) {
                    TableBodyCell(
                        text = itemUiState.formattedCreatedAt,
                        style = DebuggerTheme.typography.bodyMedium,
                        modifier = Modifier.width(timeColumnWidth),
                    )
                    TableBodyCell(
                        text = itemUiState.kind.label,
                        style = DebuggerTheme.typography.bodyMedium,
                        modifier = Modifier.width(kindColumnWidth),
                    )
                    TableBodyCell(
                        text = itemUiState.simplifiedPayload,
                        style = DebuggerTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState = lazyListState),
            style = defaultScrollbarStyle().copy(
                hoverColor = DebuggerTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                unhoverColor = DebuggerTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            ),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
        )
    }
}

@Preview
@Composable
fun SessionLogLoadedViewPreview() {
    SessionLogTable(
        uiState = SessionLogTableUiState(
            logs = listOf(
                LogItemUiState(
                    id = "",
                    createdAt = 0L,
                    payload = BackInTimeDebugServiceEvent.Ping,
                    selected = false,
                ),
            ),
            sortRule = SessionLogTableUiState.SortRule.CREATED_AT_DESC,
            visibleKinds = emptySet(),
        ),
        onToggleSortWithKind = {},
        onToggleSortWithTime = {},
        onUpdateVisibleKinds = {},
        onSelectLogItem = {},
    )
}
