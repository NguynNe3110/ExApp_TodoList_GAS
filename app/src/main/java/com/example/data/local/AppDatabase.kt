package com.example.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.TodoDao
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TodoEntity

@Database(entities = [TodoEntity::class, CategoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
