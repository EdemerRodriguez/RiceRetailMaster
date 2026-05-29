package com.rodriguez.riceretailmaster.ui.admin.alerts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodriguez.riceretailmaster.ui.components.AlertCard
import com.rodriguez.riceretailmaster.ui.components.ScreenHeader
import com.rodriguez.riceretailmaster.ui.theme.NavInactive
import com.rodriguez.riceretailmaster.ui.theme.OnSurface
import com.rodriguez.riceretailmaster.ui.theme.OnSurfaceVariant
import com.rodriguez.riceretailmaster.ui.theme.Primary

@Composable
fun LowStockAlertsScreen(
    snackbar: SnackbarHostState,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LowStockAlertsViewModel = viewModel(),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    LaunchedEffect(ui.error) {
        ui.error?.let { snackbar.showSnackbar(it) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        ScreenHeader("Low-Stock Alerts", onSignOut)
        Spacer(Modifier.height(16.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Recent Notifications", style = MaterialTheme.typography.titleSmall, color = OnSurface)
            Text(
                "Mark all as read",
                style = MaterialTheme.typography.titleSmall,
                color = Primary,
                modifier = Modifier.clickable(enabled = ui.alerts.isNotEmpty()) { viewModel.markAllRead() },
            )
        }
        Spacer(Modifier.height(12.dp))

        when {
            ui.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
            ) {
                items(ui.alerts, key = { it.id }) { alert -> AlertCard(alert) }
                item { EmptyFooter() }
            }
        }
    }
}

@Composable
private fun EmptyFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            Icons.Rounded.NotificationsNone,
            contentDescription = null,
            tint = NavInactive,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.height(6.dp))
        Text("No other alerts", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
    }
}
