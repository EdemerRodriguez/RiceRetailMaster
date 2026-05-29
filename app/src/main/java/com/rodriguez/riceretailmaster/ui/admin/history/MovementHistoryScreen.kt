package com.rodriguez.riceretailmaster.ui.admin.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodriguez.riceretailmaster.data.model.MovementType
import com.rodriguez.riceretailmaster.ui.components.MovementRow
import com.rodriguez.riceretailmaster.ui.components.OutlineSummaryBadge
import com.rodriguez.riceretailmaster.ui.components.ScreenHeader
import com.rodriguez.riceretailmaster.ui.components.SurfaceCard
import com.rodriguez.riceretailmaster.ui.login.LoginScreen
import com.rodriguez.riceretailmaster.ui.theme.FaintBorder
import com.rodriguez.riceretailmaster.ui.theme.NavInactive
import com.rodriguez.riceretailmaster.ui.theme.OnSurface
import com.rodriguez.riceretailmaster.ui.theme.OnSurfaceVariant
import com.rodriguez.riceretailmaster.ui.theme.PositiveMove
import com.rodriguez.riceretailmaster.ui.theme.Primary
import com.rodriguez.riceretailmaster.ui.theme.Surface
import com.rodriguez.riceretailmaster.util.Formatters
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementHistoryScreen(
    snackbar: SnackbarHostState,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovementHistoryViewModel = viewModel(),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    var showPicker by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    LaunchedEffect(ui.error) {
        ui.error?.let { snackbar.showSnackbar(it) }
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = ui.selectedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        viewModel.onDateSelected(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    showPicker = false
                }) { Text("OK", color = Primary) }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Cancel", color = OnSurfaceVariant) }
            },
        ) {
            DatePicker(state = pickerState)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        ScreenHeader(
            title = "Movement History",
            onSignOut = onSignOut,
            trailing = {
                Box {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            Icons.Rounded.Tune,
                            contentDescription = "Filter movements",
                            tint = if (ui.isFiltered) Primary else NavInactive,
                        )
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false },
                    ) {
                        FilterMenuItem(
                            label = "All movements",
                            selected = ui.typeFilter == null,
                            onClick = {
                                viewModel.onTypeFilterChange(null)
                                showFilterMenu = false
                            },
                        )
                        FilterMenuItem(
                            label = "Deliveries only",
                            selected = ui.typeFilter == MovementType.DELIVERY,
                            onClick = {
                                viewModel.onTypeFilterChange(MovementType.DELIVERY)
                                showFilterMenu = false
                            },
                        )
                        FilterMenuItem(
                            label = "Releases only",
                            selected = ui.typeFilter == MovementType.RELEASE,
                            onClick = {
                                viewModel.onTypeFilterChange(MovementType.RELEASE)
                                showFilterMenu = false
                            },
                        )
                    }
                }
            },
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Surface)
                .border(1.dp, FaintBorder, RoundedCornerShape(12.dp))
                .clickable { showPicker = true }
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Rounded.CalendarMonth, contentDescription = null, tint = Primary)
            Spacer(Modifier.weight(0.05f))
            Text(
                Formatters.dateLong(ui.selectedDate),
                modifier = Modifier.weight(1f).padding(start = 10.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurface,
            )
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = OnSurfaceVariant)
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = if (ui.selectedDate == LocalDate.now()) "TODAY" else Formatters.dateLong(ui.selectedDate).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = OnSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        Box(Modifier.weight(1f).fillMaxWidth()) {
            when {
                ui.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }

                ui.visible.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No movements on this date.",
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariant,
                    )
                }

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(ui.visible, key = { it.id }) { item -> MovementRow(item) }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        DailySummaryBar(deliveries = ui.deliveries, releases = ui.releases, net = ui.net)
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun FilterMenuItem(label: String, selected: Boolean, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Text(
                label,
                color = if (selected) Primary else OnSurface,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
        },
        onClick = onClick,
    )
}

@Composable
private fun DailySummaryBar(deliveries: Int, releases: Int, net: Int) {
    val sign = if (net > 0) "+" else ""
    val moveWord = if (abs(net) == 1) "Move" else "Moves"
    SurfaceCard {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Daily Summary", style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)
            Text("Net: $sign$net $moveWord", style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            OutlineSummaryBadge("Deliveries +$deliveries", Icons.Rounded.AddCircleOutline, PositiveMove)
            OutlineSummaryBadge("Releases -$releases", Icons.Rounded.RemoveCircleOutline, Primary)
        }
    }
}
