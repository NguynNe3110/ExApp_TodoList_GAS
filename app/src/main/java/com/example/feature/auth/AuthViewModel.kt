package com.example.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.core.persistence.AuthSessionStore
import com.example.core.MyApp
import com.example.data.repository.FirebaseAuthRepository
import com.example.domain.repository.SyncResult
import com.example.domain.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: TodoRepository,
    private val authSessionStore: AuthSessionStore,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authSessionStore.session.collect { session ->
                _uiState.update {
                    it.copy(
                        username = session.username,
                        password = session.password,
                        linkedEmail = session.linkedEmail,
                        loggedInUser = session.username.ifBlank { null }
                    )
                }

                if (session.username.isNotBlank() && session.password.isNotBlank()) {
                    repository.setCloudSyncCredentials(session.username, session.password)
                }
            }
        }
    }

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
        persistSessionDraft()
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
        persistSessionDraft()
    }

    fun updateLinkedEmail(email: String) {
        _uiState.update { it.copy(linkedEmail = email) }
        persistSessionDraft()
    }

    fun saveSession() {
        persistSessionDraft()
    }

    private fun persistSessionDraft() {
        val state = _uiState.value
        viewModelScope.launch {
            authSessionStore.saveSession(state.username, state.password, state.linkedEmail)
        }
    }

    fun resetState() {
        _uiState.update { it.copy(screenState = AuthScreenState.Idle) }
    }

    fun backupCloud() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(screenState = AuthScreenState.Error("Vui lòng nhập đầy đủ thông tin!")) }
            return
        }

        _uiState.update { it.copy(screenState = AuthScreenState.Loading) }

        viewModelScope.launch {
            val result = repository.backupToFirestore(
                username = state.username.trim().lowercase(),
                password = state.password
            )

            if (result is SyncResult.Success) {
                repository.setCloudSyncCredentials(state.username.trim().lowercase(), state.password)
                authSessionStore.saveSession(state.username, state.password, state.linkedEmail)
            }

            handleResult(result, "Đã sao lưu (đồng bộ lên) Cloud thành công!")
        }
    }

    fun restoreCloud() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(screenState = AuthScreenState.Error("Vui lòng nhập đầy đủ thông tin!")) }
            return
        }

        _uiState.update { it.copy(screenState = AuthScreenState.Loading) }

        viewModelScope.launch {
            val result = repository.restoreFromFirestore(
                username = state.username.trim().lowercase(),
                password = state.password
            )

            if (result is SyncResult.Success) {
                repository.setCloudSyncCredentials(state.username.trim().lowercase(), state.password)
                authSessionStore.saveSession(state.username, state.password, state.linkedEmail)
            }

            handleResult(result, "Đã khôi phục (tải về) dữ liệu từ Cloud thành công!")
        }
    }

    fun registerAccount() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(screenState = AuthScreenState.Error("Vui lòng điền đầy đủ username và password!")) }
            return
        }

        _uiState.update { it.copy(screenState = AuthScreenState.Loading) }

        viewModelScope.launch {
            val result = repository.registerAndBackup(
                username = state.username.trim().lowercase(),
                password = state.password
            )

            if (result is SyncResult.Success) {
                repository.setCloudSyncCredentials(state.username.trim().lowercase(), state.password)
                authSessionStore.saveSession(state.username, state.password, state.linkedEmail)
                if (state.linkedEmail.isNotBlank()) {
                    firebaseAuthRepository.ensureRecoveryAccount(state.linkedEmail, state.password)
                }
            }

            handleResult(result, "Đăng ký thành công và đồng bộ dữ liệu hiện tại lên Cloud!")
        }
    }

    fun sendPasswordResetEmail() {
        val state = _uiState.value
        val email = state.linkedEmail.trim()
        if (email.isBlank()) {
            _uiState.update { it.copy(screenState = AuthScreenState.Error("Hãy nhập email Gmail liên kết để nhận link khôi phục!")) }
            return
        }

        _uiState.update { it.copy(screenState = AuthScreenState.Loading) }

        viewModelScope.launch {
            val recoverySetup = if (state.password.isNotBlank()) {
                firebaseAuthRepository.ensureRecoveryAccount(email, state.password)
            } else {
                Result.failure(IllegalStateException("Bạn cần lưu password hiện tại trước khi tạo link khôi phục."))
            }

            if (recoverySetup.isFailure) {
                val message = recoverySetup.exceptionOrNull()?.localizedMessage ?: "Không thể tạo tài khoản khôi phục."
                _uiState.update { it.copy(screenState = AuthScreenState.Error(message)) }
                return@launch
            }

            val result = firebaseAuthRepository.sendPasswordResetEmail(email)
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        screenState = AuthScreenState.Success("Link khôi phục mật khẩu đã được gửi tới $email. Hãy kiểm tra Inbox hoặc Spam."),
                        linkedEmail = email
                    )
                }
            } else {
                val message = result.exceptionOrNull()?.localizedMessage ?: "Không thể gửi email khôi phục."
                _uiState.update { it.copy(screenState = AuthScreenState.Error(message)) }
            }
        }
    }

    private fun handleResult(result: SyncResult, successMsg: String) {
        when (result) {
            is SyncResult.Success -> {
                _uiState.update {
                    it.copy(
                        screenState = AuthScreenState.Success(successMsg),
                        loggedInUser = it.username
                    )
                }
            }
            is SyncResult.InvalidPassword -> {
                _uiState.update { it.copy(screenState = AuthScreenState.Error("Mật khẩu không chính xác!")) }
            }
            is SyncResult.UserNotFound -> {
                _uiState.update { it.copy(screenState = AuthScreenState.Error("Không tìm thấy tài khoản! Vui lòng Đăng Ký trước.")) }
            }
            is SyncResult.Error -> {
                _uiState.update { it.copy(screenState = AuthScreenState.Error("Lỗi: ${result.message}")) }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[APPLICATION_KEY]) as MyApp
                return AuthViewModel(
                    application.container.todoRepository,
                    application.container.authSessionStore,
                    application.container.firebaseAuthRepository
                ) as T
            }
        }
    }
}
