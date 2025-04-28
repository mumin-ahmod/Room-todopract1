package com.example.roompract1

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


//step1: todo model
//todo data model
@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false
)