package com.github.kitakkun.backintime.evaluation.flux

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.evaluation.data.Todo
import com.github.kitakkun.backintime.evaluation.flux.architecture.ActionEvent
import com.github.kitakkun.backintime.evaluation.flux.architecture.Dispatcher
import com.github.kitakkun.backintime.evaluation.flux.architecture.Store

@DebuggableStateHolder
class FluxTodoListStore(
    private val dispatcher: Dispatcher,
) : Store, ViewModel() {
    private val mutableTodos = mutableStateListOf<Todo>()
    val todos: List<Todo> = mutableTodos

    private val mutableEditingTodoId = mutableStateOf<String?>(null)
    val editingTodoId: State<String?> = mutableEditingTodoId

    private val mutableAddingTodo = mutableStateOf(false)
    val addingTodo: State<Boolean> = mutableAddingTodo

    init {
        dispatcher.register(this)
    }

    override fun reduce(event: ActionEvent) {
        when (event) {
            is TodoListActionEvent.ReloadToDoSucceeded -> onReload(event)
            is TodoListActionEvent.TodoLabelUpdated -> onTodoLabelUpdated(event)
            is TodoListActionEvent.TodoStatusUpdated -> onTodoStatusUpdated(event)
            is TodoListActionEvent.OpenTodoEditDialogRequested -> onOpenTodoEditDialog(event)
            is TodoListActionEvent.CloseTodoEditDialogRequested -> onCloseTodoEditDialog(event)
            is TodoListActionEvent.OpenAddTodoDialogRequested -> onOpenAddTodoDialog(event)
            is TodoListActionEvent.CloseAddTodoDialogRequested -> onCloseAddTodoDialog(event)
            is TodoListActionEvent.AddTodoItemSucceeded -> onAddTodoItemSucceeded(event)
            is TodoListActionEvent.DeleteTodoItemSucceeded -> onDeleteTodoItemSucceeded(event)
        }
    }

    private fun onReload(event: TodoListActionEvent.ReloadToDoSucceeded) {
        mutableTodos.clear()
        mutableTodos.addAll(event.todos)
    }

    private fun onTodoLabelUpdated(event: TodoListActionEvent.TodoLabelUpdated) {
        mutableTodos.replaceAll {
            if (it.uuid == event.uuid) {
                it.copy(label = event.label)
            } else {
                it
            }
        }
    }

    private fun onTodoStatusUpdated(event: TodoListActionEvent.TodoStatusUpdated) {
        mutableTodos.replaceAll {
            if (it.uuid == event.uuid) {
                it.copy(done = event.done)
            } else {
                it
            }
        }
    }

    private fun onOpenTodoEditDialog(event: TodoListActionEvent.OpenTodoEditDialogRequested) {
        mutableEditingTodoId.value = event.uuid
    }

    private fun onCloseTodoEditDialog(event: TodoListActionEvent.CloseTodoEditDialogRequested) {
        mutableEditingTodoId.value = null
    }

    private fun onOpenAddTodoDialog(event: TodoListActionEvent.OpenAddTodoDialogRequested) {
        mutableAddingTodo.value = true
    }

    private fun onCloseAddTodoDialog(event: TodoListActionEvent.CloseAddTodoDialogRequested) {
        mutableAddingTodo.value = false
    }

    private fun onAddTodoItemSucceeded(event: TodoListActionEvent.AddTodoItemSucceeded) {
        mutableTodos.add(event.todo)
    }

    private fun onDeleteTodoItemSucceeded(event: TodoListActionEvent.DeleteTodoItemSucceeded) {
        mutableTodos.removeAll { it.uuid == event.uuid }
    }

    override fun onCleared() {
        super.onCleared()
        dispatcher.unregister(this)
    }
}
