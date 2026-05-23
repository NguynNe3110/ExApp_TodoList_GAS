package com.example.domain.repository

import com.example.domain.model.Category
import com.example.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    val allTodos: Flow<List<TodoItem>>
    val allCategories: Flow<List<Category>>

    suspend fun insertTodo(todo: TodoItem)
    suspend fun deleteTodoById(id: String)
    suspend fun insertCategory(category: Category)
    suspend fun deleteCategoryById(id: String)
}
