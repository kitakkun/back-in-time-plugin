package com.github.kitakkun.backintime.evaluation.mvi

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.evaluation.data.Todo
import com.github.kitakkun.backintime.evaluation.data.TodoDao
import com.github.kitakkun.backintime.evaluation.mvi.architecture.Store
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@DebuggableStateHolder
class MVITodoListStore(
    private val todoDao: TodoDao,
) : Store, ViewModel() {
    private val mutableTodos = mutableStateListOf<Todo>()
    val todos: List<Todo> = mutableTodos

    private val mutableEditingTodoId = mutableStateOf<String?>(null)
    val editingTodoId: State<String?> = mutableEditingTodoId

    private val mutableAddingTodo = mutableStateOf(false)
    val addingTodo: State<Boolean> = mutableAddingTodo

    fun processIntent(intent: TodoListIntent) {
        when (intent) {
            is TodoListIntent.ReloadTodoList -> onReload(intent)
            is TodoListIntent.UpdateTodoLabel -> onUpdateTodoLabel(intent)
            is TodoListIntent.UpdateTodoStatus -> onUpdateTodoStatus(intent)
            is TodoListIntent.OpenTodoEditDialog -> onOpenTodoEditDialog(intent)
            is TodoListIntent.CloseTodoEditDialog -> onCloseTodoEditDialog(intent)
            is TodoListIntent.OpenAddTodoDialog -> onOpenAddTodoDialog(intent)
            is TodoListIntent.CloseAddTodoDialog -> onCloseAddTodoDialog(intent)
            is TodoListIntent.AddTodoItem -> onAddTodoItem(intent)
            is TodoListIntent.DeleteTodoItem -> onDeleteTodoItem(intent)
        }
    }

    private fun onReload(intent: TodoListIntent.ReloadTodoList) {
        viewModelScope.launch(Dispatchers.IO) {
            val todos = todoDao.getAll()
            mutableTodos.clear()
            mutableTodos.addAll(todos)
        }
    }

    private fun onUpdateTodoLabel(intent: TodoListIntent.UpdateTodoLabel) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.updateLabel(intent.uuid, intent.label)
            mutableTodos.replaceAll {
                if (it.uuid == intent.uuid) {
                    it.copy(label = intent.label)
                } else {
                    it
                }
            }
        }
    }

    private fun onUpdateTodoStatus(intent: TodoListIntent.UpdateTodoStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.updateStatus(intent.uuid, intent.done)
            mutableTodos.replaceAll {
                if (it.uuid == intent.uuid) {
                    it.copy(done = intent.done)
                } else {
                    it
                }
            }
        }
    }

    private fun onOpenTodoEditDialog(intent: TodoListIntent.OpenTodoEditDialog) {
        mutableEditingTodoId.value = intent.uuid
    }

    private fun onCloseTodoEditDialog(intent: TodoListIntent.CloseTodoEditDialog) {
        mutableEditingTodoId.value = null
    }

    private fun onOpenAddTodoDialog(intent: TodoListIntent.OpenAddTodoDialog) {
        mutableAddingTodo.value = true
    }

    private fun onCloseAddTodoDialog(intent: TodoListIntent.CloseAddTodoDialog) {
        mutableAddingTodo.value = false
    }

    private fun onAddTodoItem(intent: TodoListIntent.AddTodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val todo = Todo(label = intent.label, done = false)
            todoDao.insertAll(todo)
            mutableTodos.add(todo)
        }
    }

    private fun onDeleteTodoItem(intent: TodoListIntent.DeleteTodoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteByUuid(intent.uuid)
            mutableTodos.removeAll { it.uuid == intent.uuid }
        }
    }
}
