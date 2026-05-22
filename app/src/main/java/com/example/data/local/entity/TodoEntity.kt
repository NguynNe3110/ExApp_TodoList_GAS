package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.TodoItem
import com.example.domain.model.TodoStatus

@Entity(tableName = "todos")
data class TodoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val categoryId: String,
    val status: String, // "ACTIVE", "COMPLETED", "FAILED"
    val createdAt: Long
) {
    fun toDomain() = TodoItem(
        id = id,
        title = title,
        categoryId = categoryId,
        status = TodoStatus.valueOf(status),
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(todo: TodoItem) = TodoEntity(
            id = todo.id,
            title = todo.title,
            categoryId = todo.categoryId,
            status = todo.status.name,
            createdAt = todo.createdAt
        )
    }
}
