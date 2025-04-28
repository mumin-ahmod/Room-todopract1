package com.example.roompract1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


//step 5: Viewmodel with stateflow
class TodoViewModel (private val repository: TodoRepository) : ViewModel() {


    //1. we took a changeable state and its accessible ui state
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos

    private val _uiState = MutableStateFlow<UiState>(UiState.Empty)
    val uiState: StateFlow<UiState> = _uiState



    //initializing the viewmodel
    init {
//        val todoDao = TodoDatabase.getDatabase(application).todoDao()
        loadTodos()
    }

    fun loadTodos() {

    viewModelScope.launch {
            repository.allTodos.collectLatest { todos ->
                _todos.value = todos
                _uiState.value = if (todos.isEmpty()) UiState.Empty else UiState.Success
            }
        }
    }

    fun addTodo(title: String, description: String) {
        if (title.isBlank()) {
            _uiState.value = UiState.Error("Title cannot be empty")
            return
        }

        viewModelScope.launch {
            val todo = Todo(title = title, description = description)
            repository.insert(todo)
            _uiState.value = UiState.Success
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            repository.update(todo)
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.delete(todo)
        }
    }

    sealed class UiState {
        object Empty : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }
}
