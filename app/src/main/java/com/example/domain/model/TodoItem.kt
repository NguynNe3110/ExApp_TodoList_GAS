package com.example.domain.model

enum class TodoStatus {
    ACTIVE,       // Đang làm
    COMPLETED,    // Đã hoàn thành
    FAILED        // Không hoàn thành (marked on long press of active item)
}

data class TodoItem(
    val id: String,
    val title: String,
    val categoryId: String, // Folder association
    val status: TodoStatus = TodoStatus.ACTIVE,
    val createdAt: Long = System.currentTimeMillis()
)
