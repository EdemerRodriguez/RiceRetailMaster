package com.rodriguez.riceretailmaster.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodriguez.riceretailmaster.data.repository.AuthRepository
import com.rodriguez.riceretailmaster.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ResetPasswordState(
    val password: String = "",
    val confirm: String = "",
    val submitting: Boolean = false,
    val error: String? = null,
    val done: Boolean = false,
)

class ResetPasswordViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
) : ViewModel() {

    private val _ui = MutableStateFlow(ResetPasswordState())
    val ui = _ui.asStateFlow()

    fun onPasswordChange(value: String) = _ui.update { it.copy(password = value, error = null) }
    fun onConfirmChange(value: String) = _ui.update { it.copy(confirm = value, error = null) }

    fun submit() {
        val state = _ui.value
        if (state.submitting) return
        if (state.password.length < 6) {
            _ui.update { it.copy(error = "Password must be at least 6 characters.") }
            return
        }
        if (state.password != state.confirm) {
            _ui.update { it.copy(error = "Passwords don't match.") }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(submitting = true, error = null) }
            runCatching { authRepo.updatePassword(state.password) }
                .onSuccess {
                    authRepo.signOut()
                    _ui.update { it.copy(submitting = false, done = true) }
                }
                .onFailure { e -> _ui.update { it.copy(submitting = false, error = resetError(e)) } }
        }
    }

    private fun resetError(e: Throwable): String {
        val msg = (e.message ?: "").lowercase()
        return if ("session" in msg || "expired" in msg || "invalid" in msg || "missing" in msg) {
            "Your reset link has expired or is invalid. Request a new one from the login screen."
        } else {
            e.userMessage()
        }
    }
}
