package com.example.core.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.remote.api.FirestoreApi
import com.example.data.repository.TodoRepositoryImpl
import com.example.domain.repository.TodoRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

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

    private val firestoreApi: FirestoreApi by lazy {
        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
        Retrofit.Builder()
            .baseUrl("https://firestore.googleapis.com/v1/")
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FirestoreApi::class.java)
    }

    val todoRepository: TodoRepository by lazy {
        TodoRepositoryImpl(
            todoDao = database.todoDao(),
            firestoreApi = firestoreApi
        )
    }
}
