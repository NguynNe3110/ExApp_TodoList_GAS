package com.example.core.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.remote.api.FirestoreApi
import com.example.data.repository.TodoRepositoryImpl
import com.example.domain.repository.TodoRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

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
        
        // Logging interceptor for debugging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        
        // Add API key to requests
        val apiKey = "AIzaSyAY7KgNeNXXuxrz2SSUwRjX20GQzWTuN4k"
        val authInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val url = original.url.newBuilder()
                .addQueryParameter("key", apiKey)
                .build()
            val request = original.newBuilder()
                .url(url)
                .build()
            chain.proceed(request)
        }
        
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
            
        Retrofit.Builder()
            .baseUrl("https://firestore.googleapis.com/v1/")
            .client(client)
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
