package com.github.kitakkun.backintime.evaluation.mvi

sealed interface TodoListIntent {
    data object ReloadTodoList : TodoListIntent
    data class UpdateTodoLabel(val uuid: String, val label: String) : TodoListIntent
    data class UpdateTodoStatus(val uuid: String, val done: Boolean) : TodoListIntent
    data class OpenTodoEditDialog(val uuid: String) : TodoListIntent
    data object CloseTodoEditDialog : TodoListIntent
    data object OpenAddTodoDialog : TodoListIntent
    data object CloseAddTodoDialog : TodoListIntent
    data class AddTodoItem(val label: String) : TodoListIntent
    data class DeleteTodoItem(val uuid: String) : TodoListIntent
}
