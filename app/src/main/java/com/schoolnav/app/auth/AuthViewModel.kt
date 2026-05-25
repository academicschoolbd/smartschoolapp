package com.schoolnav.app.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Holds the auth UI state for the whole app. Owned by the activity so that
 * the login screen and every other screen share the same instance — when
 * the login screen flips the state to `SignedIn`, the navigation layer
 * automatically unblocks the gated destinations.
 */
class AuthViewModel(application: Application) : AndroidViewModel(application) {

    val sessionStore: SessionStore = SessionStore(application)
    val auth: AuthRepository = AuthRepository(sessionStore)

    val session: StateFlow<SessionState> = sessionStore.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = SessionState.SignedOut,
        )

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui.asStateFlow()

    init {
        // Re-hydrate cookies into the system CookieManager on first launch.
        viewModelScope.launch {
            sessionStore.state.collect { state ->
                if (state is SessionState.SignedIn) {
                    auth.primeCookieManagerFromSavedSession(state)
                }
            }
        }
    }

    fun onUsernameChange(v: String) { _ui.value = _ui.value.copy(username = v, error = null) }
    fun onPasswordChange(v: String) { _ui.value = _ui.value.copy(password = v, error = null) }
    fun setPasswordVisible(v: Boolean) { _ui.value = _ui.value.copy(passwordVisible = v) }

    fun submit(onSuccess: () -> Unit) {
        val state = _ui.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _ui.value = state.copy(error = "Enter your username/email and password")
            return
        }
        _ui.value = state.copy(submitting = true, error = null)
        viewModelScope.launch {
            when (val result = auth.signIn(state.username.trim(), state.password)) {
                is AuthRepository.Result.Success -> {
                    _ui.value = LoginUiState()
                    onSuccess()
                }
                is AuthRepository.Result.Failure -> {
                    _ui.value = _ui.value.copy(submitting = false, error = result.message)
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch { auth.signOut() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = checkNotNull(this[APPLICATION_KEY])
                AuthViewModel(app)
            }
        }
    }
}

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val submitting: Boolean = false,
    val error: String? = null,
)

@androidx.compose.runtime.Composable
fun rememberAuthViewModel(): AuthViewModel = viewModel(factory = AuthViewModel.Factory)
