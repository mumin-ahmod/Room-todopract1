package com.example.roompract1

import android.app.Application
import androidx.room.Room

class TodoApplication : Application() {

    val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            TodoDatabase::class.java,
            "todo-db"
        ).build()
    }

    val repository by lazy { TodoRepository(database.todoDao()) }

}