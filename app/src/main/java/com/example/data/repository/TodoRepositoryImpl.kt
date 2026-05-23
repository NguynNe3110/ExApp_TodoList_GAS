package com.example.data.repository

import com.example.data.local.dao.TodoDao
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TodoEntity
import com.example.domain.model.Category
import com.example.domain.model.TodoItem
import com.example.domain.repository.TodoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
) : TodoRepository {

    init {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val current = todoDao.getAllCategoriesFlow().first()
                if (current.isEmpty()) {
                    val defaults = Category.DEFAULT_CATEGORIES
                    todoDao.insertCategories(defaults.map { CategoryEntity.fromDomain(it) })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override val allTodos: Flow<List<TodoItem>> = todoDao.getAllTodosFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    override val allCategories: Flow<List<Category>> = todoDao.getAllCategoriesFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    override suspend fun insertTodo(todoItem: TodoItem) {
        todoDao.insertTodo(TodoEntity.fromDomain(todoItem))
    }

    override suspend fun deleteTodoById(id: String) {
        todoDao.deleteTodoById(id)
    }

    override suspend fun insertCategory(category: Category) {
        todoDao.insertCategory(CategoryEntity.fromDomain(category))
    }

    override suspend fun deleteCategoryById(id: String) {
        todoDao.deleteCategoryById(id)
    }
}
