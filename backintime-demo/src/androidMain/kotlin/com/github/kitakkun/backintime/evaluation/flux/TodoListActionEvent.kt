package com.github.kitakkun.backintime.evaluation.flux

import com.github.kitakkun.backintime.evaluation.data.Todo
import com.github.kitakkun.backintime.evaluation.flux.architecture.ActionEvent

sealed interface TodoListActionEvent : ActionEvent {
    data class ReloadToDoSucceeded(val todos: List<Todo>) : TodoListActionEvent
    data class TodoLabelUpdated(val uuid: String, val label: String) : TodoListActionEvent
    data class TodoStatusUpdated(val uuid: String, val done: Boolean) : TodoListActionEvent
    data class OpenTodoEditDialogRequested(val uuid: String) : TodoListActionEvent
    data object CloseTodoEditDialogRequested : TodoListActionEvent
    data object OpenAddTodoDialogRequested : TodoListActionEvent
    data object CloseAddTodoDialogRequested : TodoListActionEvent
    data class AddTodoItemSucceeded(val todo: Todo) : TodoListActionEvent
    data class DeleteTodoItemSucceeded(val uuid: String) : TodoListActionEvent
}
