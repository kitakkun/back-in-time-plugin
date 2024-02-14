package com.github.kitakkun.backintime.evaluation.mvvm

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
fun MVVMTodoListPage() {
    val viewModel: MVVMTodoListViewModel = koinViewModel()

    LaunchedEffect(viewModel) {
        viewModel.reload()
    }

    if (viewModel.editingTodoId.value != null) {
        val item = viewModel.todos.find { it.uuid == viewModel.editingTodoId.value }
        if (item != null) {
            EditTodoDialog(
                item = item,
                onConfirmChange = { label ->
                    viewModel.updateTodoLabel(item.uuid, label)
                    viewModel.closeTodoEditDialog()
                },
                onDelete = {
                    viewModel.deleteToDoItem(item.uuid)
                    viewModel.closeTodoEditDialog()
                },
                onDismiss = { viewModel.closeTodoEditDialog() },
            )
        }
    }

    if (viewModel.addingTodo.value) {
        AddTodoDialog(
            onConfirm = { label ->
                viewModel.addToDoItem(label)
                viewModel.closeAddTodoDialog()
            },
            onDismiss = { viewModel.closeAddTodoDialog() },
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openAddTodoDialog() }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
            }
        },
    ) { innerPadding ->
        TodoList(
            items = viewModel.todos,
            onCheckedChange = { id, done -> viewModel.updateTodoStatus(id, done) },
            onClickItem = { id -> viewModel.openTodoEditDialog(id) },
            modifier = Modifier.padding(innerPadding),
        )
    }
}
