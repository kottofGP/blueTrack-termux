package com.bluestar.bluetrack.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bluestar.bluetrack.domain.model.Session
import com.bluestar.bluetrack.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class LoginState(
    val isLoading: Boolean = false,
    val session: Session? = null,
    val error: String? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _state.value = LoginState(error = "Ingresa tu usuario y contraseña")
            return
        }

        viewModelScope.launch {
            _state.value = LoginState(isLoading = true)
            delay(450)
            val session = authRepository.login(username.trim(), password)
            _state.value = if (session == null) {
                LoginState(error = "No pudimos validar tus credenciales")
            } else {
                LoginState(session = session)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = LoginState()
        }
    }
}
