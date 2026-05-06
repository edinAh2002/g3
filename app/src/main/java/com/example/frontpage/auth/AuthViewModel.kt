package com.example.frontpage.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.frontpage.auth.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val username: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val currentUsername: String? = null,
    val currentUserId: Long? = null,
    val isGuest: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun hasSavedUser(): Boolean {
        return authRepository.getCurrentUserId() != null
    }

    fun getCurrentUserId(): Long? {
        return authRepository.getCurrentUserId()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()

            _uiState.value = _uiState.value.copy(
                currentUsername = user?.username,
                currentUserId = user?.id,
                isGuest = user?.isGuest ?: false
            )
        }
    }

    fun onUsernameChange(username: String) {
        _uiState.value = _uiState.value.copy(
            username = username,
            errorMessage = null
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun logIn(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = authRepository.logIn(_uiState.value.username)

            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState()
                    onSuccess()
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

    fun signUp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = authRepository.signUp(_uiState.value.username)

            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState()
                    onSuccess()
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

    fun continueAsGuest(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result = authRepository.continueAsGuest()

            result.fold(
                onSuccess = {
                    _uiState.value = AuthUiState()
                    onSuccess()
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