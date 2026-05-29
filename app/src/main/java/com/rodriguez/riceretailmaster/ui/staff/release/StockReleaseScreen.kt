package com.rodriguez.riceretailmaster.ui.staff.release

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rodriguez.riceretailmaster.ui.components.FormCard
import com.rodriguez.riceretailmaster.ui.components.GradientButton
import com.rodriguez.riceretailmaster.ui.components.QuantityStepper
import com.rodriguez.riceretailmaster.ui.components.ScreenHeader
import com.rodriguez.riceretailmaster.ui.components.SectionLabel
import com.rodriguez.riceretailmaster.ui.components.UnitToggle
import com.rodriguez.riceretailmaster.ui.components.VarietyDropdown

@Composable
fun StockReleaseScreen(
    snackbar: SnackbarHostState,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: StockReleaseViewModel = viewModel(),
) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()

    LaunchedEffect(ui.success, ui.error) {
        (ui.success ?: ui.error)?.let {
            snackbar.showSnackbar(it)
            viewModel.consumeMessages()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(8.dp))
        ScreenHeader("Stock Release", onSignOut)
        Spacer(Modifier.height(16.dp))

        FormCard {
            SectionLabel("Rice Variety")
            Spacer(Modifier.height(8.dp))
            VarietyDropdown(
                options = ui.varietyNames,
                selected = ui.selectedVarietyName,
                onSelect = viewModel::onVarietySelected,
            )

            Spacer(Modifier.height(20.dp))
            SectionLabel("Quantity")
            Spacer(Modifier.height(12.dp))
            QuantityStepper(value = ui.quantity, onValueChange = viewModel::onQuantityChange)

            Spacer(Modifier.height(20.dp))
            SectionLabel("Unit of Measurement")
            Spacer(Modifier.height(8.dp))
            UnitToggle(selected = ui.unit, onSelect = viewModel::onUnitSelected)

            Spacer(Modifier.height(24.dp))
            GradientButton(
                text = "Confirm Release",
                onClick = viewModel::submit,
                leadingIcon = Icons.Rounded.Check,
                enabled = !ui.submitting && !ui.loadingVarieties,
            )
        }
        Spacer(Modifier.height(16.dp))
    }
}
