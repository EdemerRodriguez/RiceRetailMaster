package com.rodriguez.riceretailmaster.ui.admin.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.AlertItem
import com.rodriguez.riceretailmaster.data.repository.AlertRepository
import com.rodriguez.riceretailmaster.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlertsUiState(
    val alerts: List<AlertItem> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
)

class LowStockAlertsViewModel(
    private val repo: AlertRepository = AlertRepository(),
) : ViewModel() {

    private val _ui = MutableStateFlow(AlertsUiState())
    val ui = _ui.asStateFlow()

    init { start() }

    private fun start() {
        if (!SupabaseService.isConfigured) {
            _ui.update { it.copy(loading = false, error = "Supabase isn't configured yet (see docs/SUPABASE_SETUP.md).") }
            return
        }
        refresh()
        viewModelScope.launch { runCatching { repo.changes().collect { refresh() } } }
    }

    private fun refresh() {
        viewModelScope.launch {
            runCatching { repo.items() }
                .onSuccess { list -> _ui.update { it.copy(alerts = list, loading = false, error = null) } }
                .onFailure { e -> _ui.update { it.copy(loading = false, error = e.userMessage()) } }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            runCatching { repo.markAllRead() }
                .onSuccess { refresh() }
                .onFailure { e -> _ui.update { it.copy(error = e.userMessage()) } }
        }
    }
}
