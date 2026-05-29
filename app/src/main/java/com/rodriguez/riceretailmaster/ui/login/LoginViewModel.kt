package com.rodriguez.riceretailmaster.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.UserRole
import com.rodriguez.riceretailmaster.data.repository.AuthRepository
import com.rodriguez.riceretailmaster.util.Validators
import com.rodriguez.riceretailmaster.util.loginMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val notice: String? = null,
    val resetDialogVisible: Boolean = false,
    val resetEmail: String = "",
    val resetSending: Boolean = false,
)

class LoginViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
) : ViewModel() {

    private val _ui = MutableStateFlow(LoginUiState())
    val ui = _ui.asStateFlow()

    private val _loginSuccess = MutableSharedFlow<UserRole>()
    val loginSuccess = _loginSuccess.asSharedFlow()

    fun onEmailChange(value: String) = _ui.update { it.copy(email = value, error = null) }
    fun onPasswordChange(value: String) = _ui.update { it.copy(password = value, error = null) }
    fun consumeNotice() = _ui.update { it.copy(notice = null) }

    fun onForgotPasswordClick() =
        _ui.update { it.copy(resetDialogVisible = true, resetEmail = it.email.trim(), error = null) }

    fun onResetEmailChange(value: String) = _ui.update { it.copy(resetEmail = value) }
    fun dismissResetDialog() = _ui.update { it.copy(resetDialogVisible = false, resetSending = false) }

    fun sendPasswordReset() {
        val state = _ui.value
        if (state.resetSending) return
        val email = state.resetEmail.trim()
        if (!Validators.isValidEmail(email)) {
            _ui.update { it.copy(error = "Enter a valid email address.") }
            return
        }
        if (!SupabaseService.isConfigured) {
            _ui.update { it.copy(error = "Supabase isn't configured yet (see docs/SUPABASE_SETUP.md).") }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(resetSending = true, error = null, notice = null) }
            runCatching { authRepo.sendPasswordReset(email) }
                .onSuccess {
                    _ui.update {
                        it.copy(
                            resetSending = false,
                            resetDialogVisible = false,
                            notice = "Password reset link sent to $email. Check your inbox.",
                        )
                    }
                }
                .onFailure { e -> _ui.update { it.copy(resetSending = false, error = e.loginMessage()) } }
        }
    }

    fun signIn() {
        val state = _ui.value
        if (state.loading) return

        val email = state.email.trim()
        if (email.isBlank() || state.password.isBlank()) {
            _ui.update { it.copy(error = "Enter your email and password.") }
            return
        }
        if (!Validators.isValidEmail(email)) {
            _ui.update { it.copy(error = "Enter a valid email address.") }
            return
        }
        if (!SupabaseService.isConfigured) {
            _ui.update {
                it.copy(error = "Supabase isn't configured yet.")
            }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }
            runCatching { authRepo.signIn(email, state.password) }
                .onSuccess { role ->
                    _ui.update { it.copy(loading = false) }
                    _loginSuccess.emit(role)
                }
                .onFailure { e ->
                    _ui.update { it.copy(loading = false, password = "", error = e.loginMessage()) }
                }
        }
    }
}
