package com.rodriguez.riceretailmaster.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rodriguez.riceretailmaster.ui.theme.OnSurfaceVariant

/**
 * Screen title row with a small sign-out icon at the end. Optionally centers
 * the title (Delivery Log) and/or shows an extra trailing icon before the
 * sign-out button (Movement History's filter).
 */
@Composable
fun ScreenHeader(
    title: String,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Balance the sign-out button so a centered title stays visually centered.
        if (centered) Spacer(Modifier.size(48.dp))

        ScreenTitle(
            text = title,
            textAlign = if (centered) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.weight(1f),
        )

        trailing?.invoke()

        IconButton(onClick = onSignOut) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Logout,
                contentDescription = "Sign out",
                tint = OnSurfaceVariant,
            )
        }
    }
}
