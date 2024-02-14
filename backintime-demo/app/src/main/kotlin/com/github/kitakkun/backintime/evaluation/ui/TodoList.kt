package com.github.kitakkun.backintime.evaluation.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.kitakkun.backintime.evaluation.data.Todo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TodoList(
    items: List<Todo>,
    onCheckedChange: (id: String, done: Boolean) -> Unit,
    onClickItem: (id: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(items.sortedBy { it.label }.sortedBy { it.done }, key = { it.uuid }) { item ->
            TodoItemView(
                item = item,
                onCheckedChange = { onCheckedChange(item.uuid, it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClickItem(item.uuid) }
                    .animateItemPlacement(),
            )
        }
    }
}
