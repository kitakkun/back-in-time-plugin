package com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component.InstanceInfoBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component.InstanceInfoView
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component.PropertyInfoBindModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.component.PropertyInfoView
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.filterpopup.ValueFilterPopupPage
import com.github.kitakkun.backintime.debugger.featurecommon.customview.table.TableBodyCell
import com.github.kitakkun.backintime.debugger.featurecommon.customview.table.TableHeadCell
import com.github.kitakkun.backintime.debugger.featurecommon.customview.table.filterpopup.time.TimeFilterPopup
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import com.github.kitakkun.backintime.instance.generated.resources.Res
import com.github.kitakkun.backintime.instance.generated.resources.no_changes
import org.jetbrains.compose.resources.stringResource

@Composable
fun PropertyInspectorLoadedView(
    bindModel: PropertyInspectorBindModel.Loaded,
    onToggleSortWithTime: () -> Unit,
    onToggleSortWithValue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val timeColumnWidth = 160.dp
    val strikeThroughLineColor = DebuggerTheme.colorScheme.error.copy(alpha = 0.5f)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp,
        ),
    ) {
        item {
            InstanceInfoView(bindModel.instanceInfo)
            Spacer(Modifier.height(8.dp))
        }
        item {
            PropertyInfoView(bindModel.propertyInfo)
            Spacer(Modifier.height(8.dp))
        }
        item {
            Text(
                text = "Change History",
                style = DebuggerTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            Spacer(Modifier.height(8.dp))
        }
        item {
            Row {
                TableHeadCell(
                    text = "Time",
                    style = DebuggerTheme.typography.labelMedium,
                    modifier = Modifier.width(timeColumnWidth),
                    isSortActive = bindModel.isSortWithTimeActive,
                    isSortedAscending = bindModel.isSortWithTimeAscending,
                    onClickSort = onToggleSortWithTime,
                    filterPopupDialog = {
                        TimeFilterPopup(it)
                    },
                )
                TableHeadCell(
                    text = "Value",
                    style = DebuggerTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f),
                    isSortActive = bindModel.isSortWithValueActive,
                    isSortedAscending = bindModel.isSortWithValueAscending,
                    onClickSort = onToggleSortWithValue,
                    filterPopupDialog = {
                        ValueFilterPopupPage(it)
                    },
                )
            }
        }
        when {
            bindModel.changesInfo.isEmpty() -> {
                item {
                    Column(
                        modifier = Modifier.fillParentMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(Modifier.weight(1f))
                        Text(
                            text = stringResource(Res.string.no_changes),
                            style = DebuggerTheme.typography.labelMedium,
                            textAlign = TextAlign.Center,
                        )
                        Icon(
                            imageVector = Icons.Default.DataObject,
                            contentDescription = null,
                            modifier = Modifier.drawWithContent {
                                drawContent()
                                drawLine(
                                    color = strikeThroughLineColor,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, size.height),
                                    strokeWidth = 5f,
                                )
                            },
                        )
                        Spacer(Modifier.weight(1f))
                    }
                }
            }

            else -> {
                items(bindModel.changesInfo) {
                    Row {
                        TableBodyCell(
                            text = it.formattedTime,
                            style = DebuggerTheme.typography.labelMedium,
                            modifier = Modifier.width(timeColumnWidth),
                        )
                        TableBodyCell(
                            text = it.newValue,
                            style = DebuggerTheme.typography.labelMedium,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PropertyInspectViewPreview() {
    PropertyInspectorLoadedView(
        PropertyInspectorBindModel.Loaded(
            instanceInfo = InstanceInfoBindModel(
                instanceId = "123",
                instanceClassName = "com.github.kitakkun.backintime.debugger.feature.instance.view.property_inspect.PropertyInspectorLoadedView",
            ),
            propertyInfo = PropertyInfoBindModel(
                propertyName = "property",
                propertyValueType = "kotlin/String",
                propertyType = "androidx/compose/MutableState",
            ),
            changesInfo = emptyList(),
            sortRule = SortRule.CREATED_AT_DESC,
        ),
        onToggleSortWithValue = {},
        onToggleSortWithTime = {},
    )
}
