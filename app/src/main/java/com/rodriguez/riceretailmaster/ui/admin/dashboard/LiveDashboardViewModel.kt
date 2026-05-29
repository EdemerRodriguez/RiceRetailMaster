package com.rodriguez.riceretailmaster.ui.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.InventoryItem
import com.rodriguez.riceretailmaster.data.repository.InventoryRepository
import com.rodriguez.riceretailmaster.data.repository.MovementRepository
import com.rodriguez.riceretailmaster.ui.components.BarEntry
import com.rodriguez.riceretailmaster.util.Formatters
import com.rodriguez.riceretailmaster.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.roundToInt

data class DashboardUiState(
    val items: List<InventoryItem> = emptyList(),
    val todaysMoves: Int = 0,
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val error: String? = null,
) {
    val totalSacks: Int get() = items.sumOf { it.quantitySacks }.roundToInt()
    val lowStockCount: Int get() = items.count { it.isCritical }
    val bars: List<BarEntry> get() = items.map { BarEntry(abbreviate(it.name), it.quantitySacks) }
}

class LiveDashboardViewModel(
    private val inventoryRepo: InventoryRepository = InventoryRepository(),
    private val movementRepo: MovementRepository = MovementRepository(),
) : ViewModel() {

    private val _ui = MutableStateFlow(DashboardUiState())
    val ui = _ui.asStateFlow()

    init { start() }

    private fun start() {
        if (!SupabaseService.isConfigured) {
            _ui.update { it.copy(loading = false, error = "Supabase isn't configured yet.") }
            return
        }
        refresh()
        viewModelScope.launch { runCatching { inventoryRepo.changes().collect { refresh() } } }
        viewModelScope.launch { runCatching { movementRepo.changes().collect { refresh() } } }
    }

    fun manualRefresh() {
        _ui.update { it.copy(refreshing = true) }
        refresh()
    }

    private fun refresh() {
        viewModelScope.launch {
            runCatching {
                val items = inventoryRepo.items()
                val today = LocalDate.now()
                val moves = movementRepo.items().count { Formatters.localDate(it.createdAt) == today }
                items to moves
            }.onSuccess { (items, moves) ->
                _ui.update {
                    it.copy(
                        items = items,
                        todaysMoves = moves,
                        loading = false,
                        refreshing = false,
                        error = null,
                    )
                }
            }.onFailure { e ->
                _ui.update { it.copy(loading = false, refreshing = false, error = e.userMessage()) }
            }
        }
    }
}

private fun abbreviate(name: String): String = when (name) {
    "Sinandomeng" -> "SND"
    "Jasmine" -> "JAS"
    "Dinorado" -> "DIN"
    "Well-Milled" -> "WML"
    "NFA Rice" -> "NFA"
    "Premium Thai" -> "PTH"
    else -> name.filter { it.isLetter() }.take(3).uppercase()
}
