package com.example.data.repository

import com.example.data.local.dao.TodoDao
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.TodoEntity
import com.example.data.remote.api.FirestoreApi
import com.example.data.remote.api.FirestoreDocument
import com.example.data.remote.api.FirestoreFields
import com.example.data.remote.api.FirestoreValue
import com.example.domain.model.Category
import com.example.domain.model.TodoItem
import com.example.domain.model.TodoStatus
import com.example.domain.repository.SyncResult
import com.example.domain.repository.TodoRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TodoRepositoryImpl(
    private val todoDao: TodoDao,
    private val firestoreApi: FirestoreApi
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

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    // Inline types for simple Json serialization
    private val todoListType = Types.newParameterizedType(List::class.java, TodoJsonModel::class.java)
    private val todoAdapter = moshi.adapter<List<TodoJsonModel>>(todoListType)

    private val categoryListType = Types.newParameterizedType(List::class.java, CategoryJsonModel::class.java)
    private val categoryAdapter = moshi.adapter<List<CategoryJsonModel>>(categoryListType)

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

    override suspend fun backupToFirestore(
        projectId: String,
        username: String,
        password: String
    ): SyncResult {
        return try {
            val response = firestoreApi.getUserDocument(projectId, username)
            if (response.isSuccessful && response.body() != null) {
                val cloudDoc = response.body()!!
                val cloudPassword = cloudDoc.fields.password?.stringValue
                if (cloudPassword != null && cloudPassword != password) {
                    return SyncResult.InvalidPassword
                }
            } else if (response.code() == 404) {
                return SyncResult.UserNotFound
            }

            // Upload data
            performUpload(projectId, username, password)
        } catch (e: Exception) {
            SyncResult.Error(e.localizedMessage ?: "Unknown network error during backup")
        }
    }

    override suspend fun restoreFromFirestore(
        projectId: String,
        username: String,
        password: String
    ): SyncResult {
        return try {
            val response = firestoreApi.getUserDocument(projectId, username)
            if (response.isSuccessful && response.body() != null) {
                val cloudDoc = response.body()!!
                val cloudPassword = cloudDoc.fields.password?.stringValue
                if (cloudPassword != null && cloudPassword != password) {
                    return SyncResult.InvalidPassword
                }

                // Restore local items
                val todosJson = cloudDoc.fields.todosJson?.stringValue
                val categoriesJson = cloudDoc.fields.categoriesJson?.stringValue

                if (!todosJson.isNullOrEmpty()) {
                    val list = todoAdapter.fromJson(todosJson) ?: emptyList()
                    todoDao.clearAllTodos()
                    todoDao.insertTodos(list.map {
                        TodoEntity(
                            id = it.id,
                            title = it.title,
                            categoryId = it.categoryId,
                            status = it.status,
                            createdAt = it.createdAt
                        )
                    })
                }

                if (!categoriesJson.isNullOrEmpty()) {
                    val list = categoryAdapter.fromJson(categoriesJson) ?: emptyList()
                    todoDao.clearAllCategories()
                    todoDao.insertCategories(list.map {
                        CategoryEntity(
                            id = it.id,
                            name = it.name,
                            colorHex = it.colorHex,
                            iconName = it.iconName
                        )
                    })
                }

                SyncResult.Success
            } else if (response.code() == 404) {
                SyncResult.UserNotFound
            } else {
                SyncResult.Error("Fetch failed with code: ${response.code()}")
            }
        } catch (e: Exception) {
            SyncResult.Error(e.localizedMessage ?: "Unknown network error during restore")
        }
    }

    override suspend fun registerAndBackup(
        projectId: String,
        username: String,
        password: String
    ): SyncResult {
        return try {
            val response = firestoreApi.getUserDocument(projectId, username)
            if (response.isSuccessful && response.body() != null) {
                // User already exists in firestore, verify credentials
                val cloudDoc = response.body()!!
                val cloudPassword = cloudDoc.fields.password?.stringValue
                if (cloudPassword != password) {
                    return SyncResult.InvalidPassword
                }
                // If password matches, perform normal backup of any local data or let them know it's fine!
                return performUpload(projectId, username, password)
            } else if (response.code() == 404) {
                // Correct path! Brand new user registration!
                return performUpload(projectId, username, password)
            } else {
                SyncResult.Error("Registration failed with network code: ${response.code()}")
            }
        } catch (e: Exception) {
            SyncResult.Error(e.localizedMessage ?: "Registration connection failed")
        }
    }

    private suspend fun performUpload(
        projectId: String,
        username: String,
        password: String
    ): SyncResult {
        // Collect current local data
        val currentTodos = allTodos.first().map {
            TodoJsonModel(it.id, it.title, it.categoryId, it.status.name, it.createdAt)
        }
        val currentCategories = allCategories.first().map {
            CategoryJsonModel(it.id, it.name, it.colorHex, it.iconName)
        }

        val todosJson = todoAdapter.toJson(currentTodos)
        val categoriesJson = categoryAdapter.toJson(currentCategories)

        val doc = FirestoreDocument(
            fields = FirestoreFields(
                password = FirestoreValue(stringValue = password),
                todosJson = FirestoreValue(stringValue = todosJson),
                categoriesJson = FirestoreValue(stringValue = categoriesJson)
            )
        )

        val writeResponse = firestoreApi.saveUserDocument(projectId, username, doc)
        return if (writeResponse.isSuccessful) {
            SyncResult.Success
        } else {
            SyncResult.Error("Data save failure: ${writeResponse.code()}")
        }
    }
}

// Simple JSON helper classes to prevent complex serialization of entity tags
data class TodoJsonModel(
    val id: String,
    val title: String,
    val categoryId: String,
    val status: String,
    val createdAt: Long
)

data class CategoryJsonModel(
    val id: String,
    val name: String,
    val colorHex: String,
    val iconName: String
)
