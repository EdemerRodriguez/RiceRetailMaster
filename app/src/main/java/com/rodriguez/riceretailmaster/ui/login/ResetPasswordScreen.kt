package com.rodriguez.riceretailmaster.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LockReset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodriguez.riceretailmaster.ui.components.Caption
import com.rodriguez.riceretailmaster.ui.components.GradientButton
import com.rodriguez.riceretailmaster.ui.components.LabeledInputField
import com.rodriguez.riceretailmaster.ui.components.LoadingOverlay
import com.rodriguez.riceretailmaster.ui.theme.Background
import com.rodriguez.riceretailmaster.ui.theme.OnSurface
import com.rodriguez.riceretailmaster.ui.theme.Primary
import com.rodriguez.riceretailmaster.ui.theme.StatPinkBg

@Composable
fun ResetPasswordScreen(
    onDone: () -> Unit,
    viewModel: ResetPasswordViewModel = viewModel(),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(ui.error) {
        ui.error?.let { snackbar.showSnackbar(it) }
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
                    Icon(Icons.Rounded.LockReset, contentDescription = null, tint = Primary, modifier = Modifier.size(32.dp))
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    if (ui.done) "Password Updated" else "Set New Password",
                    style = MaterialTheme.typography.headlineMedium,
                    color = OnSurface,
                )
                Spacer(Modifier.height(4.dp))
                Caption(
                    if (ui.done) "You can now sign in with your new password."
                    else "Enter a new password for your account.",
                )

                Spacer(Modifier.height(36.dp))

                if (ui.done) {
                    GradientButton(text = "Back to Sign In", onClick = onDone)
                } else {
                    LabeledInputField(
                        label = "New Password",
                        value = ui.password,
                        onValueChange = viewModel::onPasswordChange,
                        placeholder = "Enter new password",
                        leadingIcon = Icons.Rounded.LockReset,
                        isPassword = true,
                        keyboardType = KeyboardType.Password,
                    )
                    Spacer(Modifier.height(16.dp))
                    LabeledInputField(
                        label = "Confirm Password",
                        value = ui.confirm,
                        onValueChange = viewModel::onConfirmChange,
                        placeholder = "Re-enter new password",
                        leadingIcon = Icons.Rounded.LockReset,
                        isPassword = true,
                        keyboardType = KeyboardType.Password,
                    )
                    Spacer(Modifier.height(28.dp))
                    GradientButton(
                        text = "Update Password",
                        onClick = viewModel::submit,
                        enabled = !ui.submitting,
                    )
                }
            }

            if (ui.submitting) LoadingOverlay()
        }
    }
}
