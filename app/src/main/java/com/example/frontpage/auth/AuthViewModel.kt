package com.example.frontpage.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontpage.auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface AuthEvent {
    object Authenticated : AuthEvent
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _authEvents = MutableSharedFlow<AuthEvent>()
    val authEvents: SharedFlow<AuthEvent> = _authEvents.asSharedFlow()

    fun hasSavedUser(): Boolean {
        return authRepository.getCurrentUserId() != null
    }

    fun getCurrentUserId(): Long? {
        return authRepository.getCurrentUserId()
    }

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(
            username = username,
            errorMessage = null
        )
    }

    fun onPasswordChange(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(
            confirmPassword = confirmPassword,
            errorMessage = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }

    fun resetForm() {
        _uiState.value = AuthUiState()
    }

    fun logIn() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = authRepository.logIn(
                username = _uiState.value.username,
                password = _uiState.value.password
            )

            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState()
                    _authEvents.emit(AuthEvent.Authenticated)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login failed."
                    )
                }
            )
        }
    }

    fun signUp() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = authRepository.signUp(
                username = _uiState.value.username,
                password = _uiState.value.password,
                confirmPassword = _uiState.value.confirmPassword
            )

            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState()
                    _authEvents.emit(AuthEvent.Authenticated)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Sign up failed."
                    )
                }
            )
        }
    }

    fun continueAsGuest() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = authRepository.continueAsGuest()

            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState()
                    _authEvents.emit(AuthEvent.Authenticated)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Could not continue as guest."
                    )
                }
            )
        }
    }

    fun logOut() {
        authRepository.logOut()
        _uiState.value = AuthUiState()
    }

    companion object {
        fun factory(authRepository: AuthRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                        return AuthViewModel(authRepository) as T
                    }

                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}