package com.rodriguez.riceretailmaster.ui.admin.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodriguez.riceretailmaster.ui.components.LabeledInputField
import com.rodriguez.riceretailmaster.ui.components.QuantityStepper
import com.rodriguez.riceretailmaster.ui.components.SectionLabel
import com.rodriguez.riceretailmaster.ui.theme.DangerIcon
import com.rodriguez.riceretailmaster.ui.theme.OnSurface
import com.rodriguez.riceretailmaster.ui.theme.OnSurfaceVariant
import com.rodriguez.riceretailmaster.ui.theme.Primary
import com.rodriguez.riceretailmaster.ui.theme.Surface

@Composable
fun AddVarietyDialog(
    onDismiss: () -> Unit,
    onAdded: (String) -> Unit,
    viewModel: AddVarietyViewModel = viewModel(),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.reset() }
    LaunchedEffect(viewModel) {
        viewModel.added.collect { name -> onAdded(name) }
    }

    AlertDialog(
        onDismissRequest = { if (!ui.submitting) onDismiss() },
        containerColor = Surface,
        title = { Text("Add Rice Variety", color = OnSurface) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                SectionLabel("Variety Name")
                Spacer(Modifier.height(8.dp))
                LabeledInputField(
                    label = "",
                    value = ui.name,
                    onValueChange = viewModel::onNameChange,
                    placeholder = "e.g. Sinandomeng",
                    leadingIcon = Icons.Rounded.Grain,
                )

                Spacer(Modifier.height(16.dp))
                SectionLabel("Initial Stock (sacks)")
                Spacer(Modifier.height(8.dp))
                QuantityStepper(value = ui.initialStock, onValueChange = viewModel::onStockChange, min = 0)

                Spacer(Modifier.height(16.dp))
                SectionLabel("Low-Stock Threshold (sacks)")
                Spacer(Modifier.height(8.dp))
                QuantityStepper(value = ui.threshold, onValueChange = viewModel::onThresholdChange, min = 1, max = 99)

                Spacer(Modifier.height(16.dp))
                SectionLabel("Max Capacity (sacks)")
                Spacer(Modifier.height(8.dp))
                QuantityStepper(value = ui.maxCapacity, onValueChange = viewModel::onCapacityChange, min = 1)

                ui.error?.let { message ->
                    Spacer(Modifier.height(12.dp))
                    Text(message, color = DangerIcon, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = viewModel::submit, enabled = !ui.submitting) {
                Text(if (ui.submitting) "Adding…" else "Add", color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !ui.submitting) {
                Text("Cancel", color = OnSurfaceVariant)
            }
        },
    )
}
