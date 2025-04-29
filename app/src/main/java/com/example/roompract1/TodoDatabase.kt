package com.example.roompract1

import android.content.Context
import androidx.room.Database
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase

//step 3: todo database
//tododatabase.kt roomdatabase class

@Database(entities = [Todo::class], version = 2)
abstract class TodoDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

//    companion object {
//        @Volatile
//        private var INSTANCE : TodoDatabase? = null
//
//        fun getDatabase(context: Context): TodoDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    TodoDatabase::class.java,
//                    "todo_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//
//    }

    override fun createInvalidationTracker(): InvalidationTracker {
        TODO("Not yet implemented")
    }

    override fun clearAllTables() {
        TODO("Not yet implemented")
    }
}