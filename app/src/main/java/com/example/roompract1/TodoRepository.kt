package com.example.roompract1

import kotlinx.coroutines.flow.Flow



//step 4: repository: a bridge between ViewModel and Database.

//todorepository.kt medium to access the dao
class TodoRepository(private val todoDao: TodoDao) {
    val allTodos: Flow<List<Todo>> = todoDao.getAllTodos()

    suspend fun insert(todo: Todo) {
        todoDao.insertTodo(todo)
    }

    suspend fun update(todo: Todo) {
        todoDao.updateTodo(todo)
    }

    suspend fun delete(todo: Todo) {
        todoDao.deleteTodo(todo)
    }
}