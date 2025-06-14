package com.kitakkun.backintime.evaluation.flux

import com.kitakkun.backintime.evaluation.data.Todo
import com.kitakkun.backintime.evaluation.data.TodoDao
import com.kitakkun.backintime.evaluation.flux.architecture.ActionCreator
import com.kitakkun.backintime.evaluation.flux.architecture.Dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TodoListActionCreator(
    private val dispatcher: Dispatcher,
    private val todoDao: TodoDao,
) : ActionCreator, CoroutineScope by IOScope() {
    fun reloadToDos() {
        launch {
            val loadedTodos = todoDao.getAll()
            dispatcher.dispatch(TodoListActionEvent.ReloadToDoSucceeded(loadedTodos))
        }
    }

    fun updateToDoLabel(uuid: String, label: String) {
        launch {
            todoDao.updateLabel(uuid, label)
            dispatcher.dispatch(TodoListActionEvent.TodoLabelUpdated(uuid, label))
        }
    }

    fun updateToDoStatus(uuid: String, done: Boolean) {
        launch {
            todoDao.updateStatus(uuid, done)
            dispatcher.dispatch(TodoListActionEvent.TodoStatusUpdated(uuid, done))
        }
    }

    fun openTodoEditDialog(uuid: String) {
        dispatcher.dispatch(TodoListActionEvent.OpenTodoEditDialogRequested(uuid))
    }

    fun closeTodoEditDialog() {
        dispatcher.dispatch(TodoListActionEvent.CloseTodoEditDialogRequested)
    }

    fun deleteToDoItem(uuid: String) {
        launch {
            todoDao.deleteByUuid(uuid)
            dispatcher.dispatch(TodoListActionEvent.DeleteTodoItemSucceeded(uuid))
        }
    }

    fun openAddTodoDialog() {
        dispatcher.dispatch(TodoListActionEvent.OpenAddTodoDialogRequested)
    }

    fun closeAddTodoDialog() {
        dispatcher.dispatch(TodoListActionEvent.CloseAddTodoDialogRequested)
    }

    fun addToDoItem(label: String) {
        launch {
            val todo = Todo(label = label, done = false)
            todoDao.insertAll(todo)
            dispatcher.dispatch(TodoListActionEvent.AddTodoItemSucceeded(todo))
        }
    }
}
