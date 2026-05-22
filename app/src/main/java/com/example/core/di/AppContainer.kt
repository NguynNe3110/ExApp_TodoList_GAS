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
        
        // Auth interceptor to add API key or access token
        val authInterceptor = okhttp3.Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            
            // Add API Key if available (for Firebase/Firestore)
            // Replace with your actual API key or use OAuth token
            val apiKey = BuildConfig.FIREBASE_API_KEY.takeIf { it.isNotEmpty() }
            if (apiKey != null) {
                requestBuilder.addHeader("X-Goog-Api-Key", apiKey)
            }
            
            // Or add Bearer token for OAuth2
            // val token = getAccessToken()
            // if (token != null) {
            //     requestBuilder.addHeader("Authorization", "Bearer $token")
            // }
            
            requestBuilder
                .addHeader("Content-Type", "application/json")
                .method(original.method, original.body)
            
            chain.proceed(requestBuilder.build())
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
        
        Retrofit.Builder()
            .baseUrl("https://firestore.googleapis.com/v1/")
            .client(okHttpClient)
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
