package com.example.data.remote.api

import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface FirestoreApi {

    @GET("projects/{projectId}/databases/(default)/documents/users/{username}")
    suspend fun getUserDocument(
        @Path("projectId") projectId: String,
        @Path("username") username: String,
        @Query("key") apiKey: String? = null
    ): Response<FirestoreDocument>

    @PATCH("projects/{projectId}/databases/(default)/documents/users/{username}")
    suspend fun saveUserDocument(
        @Path("projectId") projectId: String,
        @Path("username") username: String,
        @Body document: FirestoreDocument,
        @Query("key") apiKey: String? = null
    ): Response<FirestoreDocument>
}

@JsonClass(generateAdapter = true)
data class FirestoreValue(
    val stringValue: String? = null
)

@JsonClass(generateAdapter = true)
data class FirestoreFields(
    val password: FirestoreValue? = null,
    val todosJson: FirestoreValue? = null,
    val categoriesJson: FirestoreValue? = null
)

@JsonClass(generateAdapter = true)
data class FirestoreDocument(
    val fields: FirestoreFields
)
