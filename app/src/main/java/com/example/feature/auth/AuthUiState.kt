package com.example.feature.auth

sealed class AuthScreenState {
    data object Idle : AuthScreenState()
    data object Loading : AuthScreenState()
    data class Success(val message: String) : AuthScreenState()
    data class Error(val message: String) : AuthScreenState()
}

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val projectId: String = "todolist-cloud-sync", // Safe default Firebase Demo Project ID
    val apiKey: String = "", // Firebase Web API Key for advanced configurations or custom databases
    val screenState: AuthScreenState = AuthScreenState.Idle,
    val loggedInUser: String? = null,
    val hasSyncedInitially: Boolean = false
)
