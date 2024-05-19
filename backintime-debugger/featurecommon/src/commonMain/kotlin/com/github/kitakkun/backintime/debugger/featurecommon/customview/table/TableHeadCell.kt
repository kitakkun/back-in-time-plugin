package com.github.kitakkun.backintime.debugger.featurecommon.customview.table

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TableHeadCell(
    text: String,
    modifier: Modifier = Modifier,
    isSortActive: Boolean = false,
    isSortedAscending: Boolean = true,
    style: TextStyle = LocalTextStyle.current,
    onClickSort: (() -> Unit)? = null,
    filterPopupDialog: @Composable ((dismiss: () -> Unit) -> Unit)? = null,
) {
    val filterOptionAvailable: Boolean = filterPopupDialog != null
    val sortOptionAvailable: Boolean = onClickSort != null

    var hoveringOnFilterButton by remember { mutableStateOf(false) }
    var hoveringOnSortButton by remember { mutableStateOf(false) }
    var showFilterPopupWindow by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = style,
        )
        Spacer(Modifier.weight(1f))
        if (filterOptionAvailable) {
            Box {
                Icon(
                    imageVector = Icons.Default.FilterAlt,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable {
                            showFilterPopupWindow = true
                        }
                        .size(24.dp)
                        .alpha(if (hoveringOnFilterButton) 1f else 0.5f)
                        .onPointerEvent(PointerEventType.Enter) {
                            hoveringOnFilterButton = true
                        }
                        .onPointerEvent(PointerEventType.Exit) {
                            hoveringOnFilterButton = false
                        },
                )
                if (showFilterPopupWindow && filterPopupDialog != null) {
                    filterPopupDialog {
                        showFilterPopupWindow = false
                    }
                }
            }
        }
        if (sortOptionAvailable) {
            Icon(
                imageVector = if (isSortedAscending) Icons.Default.ArrowDropDown else Icons.Default.ArrowDropUp,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        onClickSort?.invoke()
                    }
                    .alpha(if (hoveringOnSortButton) 1f else 0.5f)
                    .onPointerEvent(PointerEventType.Enter) {
                        hoveringOnSortButton = true
                    }
                    .onPointerEvent(PointerEventType.Exit) {
                        hoveringOnSortButton = false
                    },
                tint = if (isSortActive) DebuggerTheme.colorScheme.primary else LocalTextStyle.current.color,
            )
        }
    }
}

@Preview
@Composable
private fun TableHeadCellPreview_Ascending() {
    TableHeadCell(
        text = "Time",
        filterPopupDialog = {},
        isSortedAscending = true,
        onClickSort = {},
    )
}

@Preview
@Composable
private fun TableHeadCellPreview_Descending() {
    TableHeadCell(
        text = "Time",
        filterPopupDialog = {},
        isSortedAscending = false,
        onClickSort = {},
    )
}
