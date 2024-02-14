package com.github.kitakkun.backintime.evaluation.flux

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
import org.koin.compose.koinInject

@Composable
fun FluxTodoListPage() {
    val store: FluxTodoListStore = koinViewModel()
    val actionCreator: TodoListActionCreator = koinInject()

    LaunchedEffect(store) {
        actionCreator.reloadToDos()
    }

    if (store.editingTodoId.value != null) {
        val item = store.todos.find { it.uuid == store.editingTodoId.value }
        if (item != null) {
            EditTodoDialog(
                item = item,
                onConfirmChange = { label ->
                    actionCreator.updateToDoLabel(item.uuid, label)
                    actionCreator.closeTodoEditDialog()
                },
                onDelete = {
                    actionCreator.deleteToDoItem(item.uuid)
                    actionCreator.closeTodoEditDialog()
                },
                onDismiss = { actionCreator.closeTodoEditDialog() },
            )
        }
    }

    if (store.addingTodo.value) {
        AddTodoDialog(
            onConfirm = { label ->
                actionCreator.addToDoItem(label)
                actionCreator.closeAddTodoDialog()
            },
            onDismiss = { actionCreator.closeAddTodoDialog() },
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { actionCreator.openAddTodoDialog() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
    ) { innerPadding ->
        TodoList(
            items = store.todos,
            onCheckedChange = { id, done -> actionCreator.updateToDoStatus(id, done) },
            onClickItem = { id -> actionCreator.openTodoEditDialog(id) },
            modifier = Modifier.padding(innerPadding),
        )
    }
}
