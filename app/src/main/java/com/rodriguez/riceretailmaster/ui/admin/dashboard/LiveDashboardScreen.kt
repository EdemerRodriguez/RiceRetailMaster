package com.rodriguez.riceretailmaster.ui.admin.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodriguez.riceretailmaster.data.model.InventoryItem
import com.rodriguez.riceretailmaster.ui.components.DistributionBarChart
import com.rodriguez.riceretailmaster.ui.components.ScreenHeader
import com.rodriguez.riceretailmaster.ui.components.StatCard
import com.rodriguez.riceretailmaster.ui.components.StockLevelBar
import com.rodriguez.riceretailmaster.ui.components.SurfaceCard
import com.rodriguez.riceretailmaster.ui.theme.OnSurface
import com.rodriguez.riceretailmaster.ui.theme.OnSurfaceVariant
import com.rodriguez.riceretailmaster.ui.theme.Primary
import com.rodriguez.riceretailmaster.ui.theme.StatBlueBg
import com.rodriguez.riceretailmaster.ui.theme.StatGreenBg
import com.rodriguez.riceretailmaster.ui.theme.StatPinkBg
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveDashboardScreen(
    snackbar: SnackbarHostState,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LiveDashboardViewModel = viewModel(),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var showAddVariety by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<InventoryItem?>(null) }

    LaunchedEffect(ui.error) {
        ui.error?.let { snackbar.showSnackbar(it) }
    }

    if (showAddVariety) {
        AddVarietyDialog(
            onDismiss = { showAddVariety = false },
            onAdded = { name ->
                showAddVariety = false
                viewModel.manualRefresh()
                scope.launch { snackbar.showSnackbar("Added $name.") }
            },
        )
    }

    editingItem?.let { item ->
        EditVarietyDialog(
            item = item,
            onDismiss = { editingItem = null },
            onUpdated = { name ->
                editingItem = null
                viewModel.manualRefresh()
                scope.launch { snackbar.showSnackbar("Updated $name.") }
            },
            onDeleted = { name ->
                editingItem = null
                viewModel.manualRefresh()
                scope.launch { snackbar.showSnackbar("Deleted $name.") }
            },
        )
    }

    if (ui.loading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    PullToRefreshBox(
        isRefreshing = ui.refreshing,
        onRefresh = { viewModel.manualRefresh() },
        modifier = modifier.fillMaxSize(),
    ) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        ScreenHeader(
            title = "Live Dashboard",
            onSignOut = onSignOut,
            trailing = {
                IconButton(onClick = { showAddVariety = true }) {
                    Icon(Icons.Rounded.Add, contentDescription = "Add rice variety", tint = Primary)
                }
            },
        )
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(ui.totalSacks.toString(), "Total Sacks", StatPinkBg, Modifier.weight(1f))
            StatCard(ui.lowStockCount.toString(), "Low Stock", StatBlueBg, Modifier.weight(1f))
            StatCard(ui.todaysMoves.toString(), "Today's Moves", StatGreenBg, Modifier.weight(1f))
        }

        Spacer(Modifier.height(16.dp))
        SurfaceCard {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Stock Distribution", style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Rounded.BarChart, contentDescription = null, tint = Primary)
            }
            Spacer(Modifier.height(16.dp))
            if (ui.bars.isEmpty()) {
                Text("No data yet.", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
            } else {
                DistributionBarChart(entries = ui.bars)
            }
        }

        Spacer(Modifier.height(16.dp))
        SurfaceCard {
            Text("Stock Levels by Variety", style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(16.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ui.items.forEach { item ->
                    Box(modifier = Modifier.clickable { editingItem = item }) {
                        StockLevelBar(item)
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
    }
    }
}
