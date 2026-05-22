package com.example.domain.repository

import com.example.domain.model.Category
import com.example.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

sealed class SyncResult {
    data object Success : SyncResult()
    data class Error(val message: String) : SyncResult()
    data object InvalidPassword : SyncResult()
    data object UserNotFound : SyncResult()
}

interface TodoRepository {
    val allTodos: Flow<List<TodoItem>>
    val allCategories: Flow<List<Category>>

    suspend fun insertTodo(todo: TodoItem)
    suspend fun deleteTodoById(id: String)
    suspend fun insertCategory(category: Category)
    suspend fun deleteCategoryById(id: String)
    
    suspend fun backupToFirestore(username: String, password: String): SyncResult
    suspend fun restoreFromFirestore(username: String, password: String): SyncResult
    suspend fun registerAndBackup(username: String, password: String): SyncResult
}
