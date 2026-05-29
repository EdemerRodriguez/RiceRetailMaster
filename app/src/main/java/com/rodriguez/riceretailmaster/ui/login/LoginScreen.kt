package com.rodriguez.riceretailmaster.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Grass
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodriguez.riceretailmaster.data.model.UserRole
import com.rodriguez.riceretailmaster.ui.components.Caption
import com.rodriguez.riceretailmaster.ui.components.GradientButton
import com.rodriguez.riceretailmaster.ui.components.LabeledInputField
import com.rodriguez.riceretailmaster.ui.components.LoadingOverlay
import com.rodriguez.riceretailmaster.ui.theme.Background
import com.rodriguez.riceretailmaster.ui.theme.ForgotPassword
import com.rodriguez.riceretailmaster.ui.theme.OnSurface
import com.rodriguez.riceretailmaster.ui.theme.OnSurfaceVariant
import com.rodriguez.riceretailmaster.ui.theme.Primary
import com.rodriguez.riceretailmaster.ui.theme.StatPinkBg
import com.rodriguez.riceretailmaster.ui.theme.Surface

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    viewModel: LoginViewModel = viewModel(),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedSuccess(viewModel, onLoginSuccess)

    LaunchedEffect(ui.error) {
        ui.error?.let { snackbar.showSnackbar(it) }
    }
    LaunchedEffect(ui.notice) {
        ui.notice?.let {
            snackbar.showSnackbar(it)
            viewModel.consumeNotice()
        }
    }

    if (ui.resetDialogVisible) {
        ForgotPasswordDialog(
            email = ui.resetEmail,
            sending = ui.resetSending,
            onEmailChange = viewModel::onResetEmailChange,
            onSend = viewModel::sendPasswordReset,
            onDismiss = viewModel::dismissResetDialog,
        )
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.height(48.dp))

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(StatPinkBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.Grass, contentDescription = null, tint = Primary, modifier = Modifier.size(32.dp))
                }

                Spacer(Modifier.height(16.dp))
                Text("Rice Retail Master", style = MaterialTheme.typography.headlineMedium, color = OnSurface)
                Spacer(Modifier.height(4.dp))
                Caption("Balayan, Batangas")

                Spacer(Modifier.height(36.dp))

                LabeledInputField(
                    label = "Email Address",
                    value = ui.email,
                    onValueChange = viewModel::onEmailChange,
                    placeholder = "Enter your email",
                    leadingIcon = Icons.Rounded.Email,
                    keyboardType = KeyboardType.Email,
                )
                Spacer(Modifier.height(16.dp))
                LabeledInputField(
                    label = "Password",
                    value = ui.password,
                    onValueChange = viewModel::onPasswordChange,
                    placeholder = "Enter your password",
                    leadingIcon = Icons.Rounded.Lock,
                    isPassword = true,
                    keyboardType = KeyboardType.Password,
                )

                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Forgot Password?",
                    style = MaterialTheme.typography.titleSmall,
                    color = ForgotPassword,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clickable { viewModel.onForgotPasswordClick() },
                )

                Spacer(Modifier.height(28.dp))
                GradientButton(text = "Sign In", onClick = viewModel::signIn, enabled = !ui.loading)
            }

            if (ui.loading) LoadingOverlay()
        }
    }
}

@Composable
private fun LaunchedSuccess(viewModel: LoginViewModel, onLoginSuccess: (UserRole) -> Unit) {
    LaunchedEffect(viewModel) {
        viewModel.loginSuccess.collect { role -> onLoginSuccess(role) }
    }
}

@Composable
private fun ForgotPasswordDialog(
    email: String,
    sending: Boolean,
    onEmailChange: (String) -> Unit,
    onSend: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = { Text("Reset password", color = OnSurface) },
        text = {
            Column {
                Text(
                    "Enter your account email and we'll send you a password reset link.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                LabeledInputField(
                    label = "",
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = "Enter your email",
                    leadingIcon = Icons.Rounded.Email,
                    keyboardType = KeyboardType.Email,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSend, enabled = !sending) {
                Text(if (sending) "Sending…" else "Send link", color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        },
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginScreenPreview() {
    MaterialTheme {
        LoginScreen(onLoginSuccess = {})
    }
}
