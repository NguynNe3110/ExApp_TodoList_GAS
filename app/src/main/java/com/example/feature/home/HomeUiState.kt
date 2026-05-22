package com.example.feature.home

import com.example.domain.model.Category
import com.example.domain.model.TodoItem

data class HomeUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val todos: List<TodoItem> = emptyList(),
    val selectedCategoryId: String? = null, // null means "All" / "Tất cả" folder
    val isAddingTask: Boolean = false,
    val isAddingCategory: Boolean = false,
    val error: String? = null
)
