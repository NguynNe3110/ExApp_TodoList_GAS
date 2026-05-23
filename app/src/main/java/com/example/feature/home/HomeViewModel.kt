package com.example.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.core.MyApp
import com.example.domain.model.Category
import com.example.domain.model.TodoItem
import com.example.domain.model.TodoStatus
import com.example.domain.repository.TodoRepository
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        repository.allTodos,
        repository.allCategories,
        _selectedCategoryId,
        _error
    ) { todos, categories, selectedId, error ->
        HomeUiState(
            isLoading = false,
            categories = categories,
            todos = todos,
            selectedCategoryId = selectedId,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun addTodo(title: String, categoryId: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            val newItem = TodoItem(
                id = UUID.randomUUID().toString(),
                title = title.trim(),
                categoryId = categoryId,
                status = TodoStatus.ACTIVE,
                createdAt = System.currentTimeMillis()
            )
            repository.insertTodo(newItem)
        }
    }

    fun updateTodo(todoId: String, title: String, categoryId: String, status: TodoStatus) {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isBlank()) return

        viewModelScope.launch {
            val item = uiState.value.todos.find { it.id == todoId } ?: return@launch
            repository.updateTodo(
                item.copy(
                    title = trimmedTitle,
                    categoryId = categoryId,
                    status = status
                )
            )
        }
    }

    fun toggleTodoCompleted(todoId: String) {
        viewModelScope.launch {
            val todos = uiState.value.todos
            val item = todos.find { it.id == todoId } ?: return@launch
            val updatedStatus = if (item.status == TodoStatus.ACTIVE) {
                TodoStatus.COMPLETED
            } else {
                TodoStatus.ACTIVE // Toggle back to active
            }
            repository.insertTodo(item.copy(status = updatedStatus))
        }
    }

    fun markTodoFailed(todoId: String) {
        viewModelScope.launch {
            val todos = uiState.value.todos
            val item = todos.find { it.id == todoId } ?: return@launch
            if (item.status == TodoStatus.ACTIVE) {
                repository.insertTodo(item.copy(status = TodoStatus.FAILED))
            } else {
                repository.insertTodo(item.copy(status = TodoStatus.ACTIVE)) // Toggle back
            }
        }
    }

    fun addCategory(name: String, colorHex: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val newCategory = Category(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                colorHex = colorHex,
                iconName = "folder"
            )
            repository.insertCategory(newCategory)
        }
    }

    fun renameCategory(categoryId: String, newName: String) {
        val trimmedName = newName.trim()
        if (trimmedName.isBlank()) return

        viewModelScope.launch {
            val category = uiState.value.categories.find { it.id == categoryId } ?: return@launch
            repository.insertCategory(category.copy(name = trimmedName))
        }
    }

    fun deleteTodo(todoId: String) {
        viewModelScope.launch {
            repository.deleteTodoById(todoId)
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            repository.deleteCategoryById(categoryId)
            if (_selectedCategoryId.value == categoryId) {
                _selectedCategoryId.value = null
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApp
                return HomeViewModel(application.container.todoRepository) as T
            }
        }
    }
}
