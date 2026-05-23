package com.example.core.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.TodoRepositoryImpl
import com.example.domain.repository.TodoRepository

class AppContainer(private val context: Context) {

    private val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "todo_database.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    val todoRepository: TodoRepository by lazy {
        TodoRepositoryImpl(
            todoDao = database.todoDao()
        )
    }
}
