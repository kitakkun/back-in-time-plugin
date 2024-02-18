package com.github.kitakkun.backintime.evaluation.mvvm

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kitakkun.backintime.annotations.DebuggableStateHolder
import com.github.kitakkun.backintime.evaluation.data.Todo
import com.github.kitakkun.backintime.evaluation.data.TodoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@DebuggableStateHolder
class MVVMTodoListViewModel(
    private val todoDao: TodoDao,
) : ViewModel() {
    private val mutableTodos = mutableStateListOf<Todo>()
    val todos: List<Todo> = mutableTodos

    private val mutableEditingTodoId = mutableStateOf<String?>(null)
    val editingTodoId: State<String?> = mutableEditingTodoId

    private val mutableAddingTodo = mutableStateOf(false)
    val addingTodo: State<Boolean> = mutableAddingTodo

    fun reload() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableTodos.clear()
            mutableTodos.addAll(todoDao.getAll())
        }
    }

    fun updateTodoLabel(uuid: String, label: String) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.updateLabel(uuid, label)
            mutableTodos.replaceAll {
                if (it.uuid == uuid) {
                    it.copy(label = label)
                } else {
                    it
                }
            }
        }
    }

    fun updateTodoStatus(uuid: String, done: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.updateStatus(uuid, done)
            mutableTodos.replaceAll {
                if (it.uuid == uuid) {
                    it.copy(done = done)
                } else {
                    it
                }
            }
        }
    }

    fun openTodoEditDialog(uuid: String) {
        mutableEditingTodoId.value = uuid
    }

    fun closeTodoEditDialog() {
        mutableEditingTodoId.value = null
    }

    fun openAddTodoDialog() {
        mutableAddingTodo.value = true
    }

    fun closeAddTodoDialog() {
        mutableAddingTodo.value = false
    }

    fun addToDoItem(label: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val todo = Todo(
                label = label,
                done = false,
            )
            todoDao.insertAll(todo)
            mutableTodos.add(todo)
        }
    }

    fun deleteToDoItem(uuid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            todoDao.deleteByUuid(uuid)
            mutableTodos.removeAll { it.uuid == uuid }
        }
    }
}
