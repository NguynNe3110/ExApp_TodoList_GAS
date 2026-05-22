package com.example.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.core.MyApp
import com.example.domain.repository.SyncResult
import com.example.domain.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun updateProjectId(projectId: String) {
        _uiState.update { it.copy(projectId = projectId) }
    }

    fun resetState() {
        _uiState.update { it.copy(screenState = AuthScreenState.Idle) }
    }

    fun backupCloud() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank() || state.projectId.isBlank()) {
            _uiState.update { it.copy(screenState = AuthScreenState.Error("Vui lòng nhập đầy đủ thông tin!")) }
            return
        }

        _uiState.update { it.copy(screenState = AuthScreenState.Loading) }

        viewModelScope.launch {
            val result = repository.backupToFirestore(
                projectId = state.projectId.trim(),
                username = state.username.trim().lowercase(),
                password = state.password
            )

            handleResult(result, "Đã sao lưu (đồng bộ lên) Cloud thành công!")
        }
    }

    fun restoreCloud() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank() || state.projectId.isBlank()) {
            _uiState.update { it.copy(screenState = AuthScreenState.Error("Vui lòng nhập đầy đủ thông tin!")) }
            return
        }

        _uiState.update { it.copy(screenState = AuthScreenState.Loading) }

        viewModelScope.launch {
            val result = repository.restoreFromFirestore(
                projectId = state.projectId.trim(),
                username = state.username.trim().lowercase(),
                password = state.password
            )

            handleResult(result, "Đã khôi phục (tải về) dữ liệu từ Cloud thành công!")
        }
    }

    fun registerAccount() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank() || state.projectId.isBlank()) {
            _uiState.update { it.copy(screenState = AuthScreenState.Error("Vui lòng điền đầy đủ username và password!")) }
            return
        }

        _uiState.update { it.copy(screenState = AuthScreenState.Loading) }

        viewModelScope.launch {
            val result = repository.registerAndBackup(
                projectId = state.projectId.trim(),
                username = state.username.trim().lowercase(),
                password = state.password
            )

            handleResult(result, "Đăng ký thành công và đồng bộ dữ liệu hiện tại lên Cloud!")
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
                return AuthViewModel(application.container.todoRepository) as T
            }
        }
    }
}
