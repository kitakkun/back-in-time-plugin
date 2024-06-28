package com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.featurecommon.customview.table.TableBodyCell
import com.github.kitakkun.backintime.debugger.featurecommon.customview.table.TableHeadCell
import com.github.kitakkun.backintime.debugger.featurecommon.customview.table.filterpopup.kind.EventKind
import com.github.kitakkun.backintime.debugger.featurecommon.customview.table.filterpopup.kind.KindFilterPopup
import com.github.kitakkun.backintime.debugger.featurecommon.customview.table.filterpopup.time.TimeFilterPopup
import com.github.kitakkun.backintime.debugger.featurecommon.util.formatEpochSecondsToDateTimeText
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import com.github.kitakkun.backintime.log.generated.resources.Res
import com.github.kitakkun.backintime.log.generated.resources.table_column_kind
import com.github.kitakkun.backintime.log.generated.resources.table_column_payload
import com.github.kitakkun.backintime.log.generated.resources.table_column_time
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.stringResource

data class LogItemBindModel(
    val payload: BackInTimeDebugServiceEvent,
    val createdAt: Long,
) {
    @OptIn(ExperimentalSerializationApi::class)
    private val prettyPrintJson = Json {
        explicitNulls = true
        prettyPrint = true
        classDiscriminatorMode = ClassDiscriminatorMode.NONE
    }

    private val simplifyJson = Json {
        classDiscriminatorMode = ClassDiscriminatorMode.NONE
    }

    val kind: EventKind = EventKind.fromEvent(payload)

    val formattedCreatedAt: String = formatEpochSecondsToDateTimeText(createdAt)

    val simplifiedPayload: String = simplifyJson.encodeToString(payload)
    val formattedPayload: String = prettyPrintJson.encodeToString(payload)
}

@Composable
fun SessionLogContentLoadedView(
    bindModel: SessionLogContentBindModel.Loaded,
    onToggleSortWithTime: () -> Unit,
    onToggleSortWithKind: () -> Unit,
    onUpdateVisibleKinds: (Set<EventKind>) -> Unit,
    onSelectLogItem: (LogItemBindModel) -> Unit,
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
                        isSortActive = bindModel.sortRule == SortRule.CREATED_AT_ASC || bindModel.sortRule == SortRule.CREATED_AT_DESC,
                        isSortedAscending = bindModel.sortRule == SortRule.CREATED_AT_ASC,
                        filterPopupDialog = { TimeFilterPopup(it) },
                        onClickSort = onToggleSortWithTime,
                        modifier = Modifier.width(timeColumnWidth),
                    )
                    TableHeadCell(
                        text = stringResource(Res.string.table_column_kind),
                        style = DebuggerTheme.typography.bodyMedium,
                        isSortActive = bindModel.sortRule == SortRule.KIND_ASC || bindModel.sortRule == SortRule.KIND_DESC,
                        isSortedAscending = bindModel.sortRule == SortRule.KIND_ASC,
                        onClickSort = onToggleSortWithKind,
                        filterPopupDialog = { dismiss ->
                            KindFilterPopup(
                                selectedKinds = bindModel.visibleKinds,
                                onSelectedKindsUpdate = onUpdateVisibleKinds,
                                onDismissRequest = dismiss,
                            )
                        },
                        modifier = Modifier.width(kindColumnWidth),
                    )
                    TableHeadCell(
                        text = stringResource(Res.string.table_column_payload),
                        style = DebuggerTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            items(bindModel.logs) { logBindModel ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable {
                        onSelectLogItem(logBindModel)
                    },
                ) {
                    TableBodyCell(
                        text = logBindModel.formattedCreatedAt,
                        style = DebuggerTheme.typography.bodyMedium,
                        modifier = Modifier.width(timeColumnWidth),
                    )
                    TableBodyCell(
                        text = logBindModel.kind.label,
                        style = DebuggerTheme.typography.bodyMedium,
                        modifier = Modifier.width(kindColumnWidth),
                    )
                    TableBodyCell(
                        text = logBindModel.simplifiedPayload,
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
    SessionLogContentLoadedView(
        bindModel = SessionLogContentBindModel.Loaded(
            logs = listOf(
                LogItemBindModel(
                    createdAt = 0L,
                    payload = BackInTimeDebugServiceEvent.Ping,
                ),
            ),
            sortRule = SortRule.CREATED_AT_DESC,
            visibleKinds = emptySet(),
        ),
        onToggleSortWithKind = {},
        onToggleSortWithTime = {},
        onUpdateVisibleKinds = {},
        onSelectLogItem = {},
    )
}
