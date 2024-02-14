package com.github.kitakkun.backintime.evaluation.mvi

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.github.kitakkun.backintime.evaluation.ui.AddTodoDialog
import com.github.kitakkun.backintime.evaluation.ui.EditTodoDialog
import com.github.kitakkun.backintime.evaluation.ui.TodoList
import org.koin.androidx.compose.koinViewModel

@Composable
fun MVITodoListPage() {
    val store: MVITodoListStore = koinViewModel()

    LaunchedEffect(store) {
        store.processIntent(TodoListIntent.ReloadTodoList)
    }

    if (store.editingTodoId.value != null) {
        val item = store.todos.find { it.uuid == store.editingTodoId.value }
        if (item != null) {
            EditTodoDialog(
                item = item,
                onConfirmChange = { label ->
                    store.processIntent(TodoListIntent.UpdateTodoLabel(item.uuid, label))
                    store.processIntent(TodoListIntent.CloseTodoEditDialog)
                },
                onDelete = {
                    store.processIntent(TodoListIntent.DeleteTodoItem(item.uuid))
                    store.processIntent(TodoListIntent.CloseTodoEditDialog)
                },
                onDismiss = { store.processIntent(TodoListIntent.CloseTodoEditDialog) },
            )
        }
    }

    if (store.addingTodo.value) {
        AddTodoDialog(
            onConfirm = { label ->
                store.processIntent(TodoListIntent.AddTodoItem(label))
                store.processIntent(TodoListIntent.CloseAddTodoDialog)
            },
            onDismiss = { store.processIntent(TodoListIntent.CloseAddTodoDialog) },
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { store.processIntent(TodoListIntent.OpenAddTodoDialog) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
    ) { innerPadding ->
        TodoList(
            items = store.todos,
            onCheckedChange = { id, done -> store.processIntent(TodoListIntent.UpdateTodoStatus(id, done)) },
            onClickItem = { id -> store.processIntent(TodoListIntent.OpenTodoEditDialog(id)) },
            modifier = Modifier.padding(innerPadding),
        )
    }
}
