package com.rodriguez.riceretailmaster.ui.admin.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.MovementItem
import com.rodriguez.riceretailmaster.data.model.MovementType
import com.rodriguez.riceretailmaster.data.repository.MovementRepository
import com.rodriguez.riceretailmaster.util.Formatters
import com.rodriguez.riceretailmaster.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HistoryUiState(
    val all: List<MovementItem> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val typeFilter: MovementType? = null,
    val loading: Boolean = true,
    val error: String? = null,
) {
    val visible: List<MovementItem>
        get() = all
            .filter { Formatters.localDate(it.createdAt) == selectedDate }
            .filter { typeFilter == null || it.type == typeFilter }

    val deliveries: Int get() = visible.count { it.type == MovementType.DELIVERY }
    val releases: Int get() = visible.count { it.type == MovementType.RELEASE }
    val net: Int get() = deliveries - releases
    val isFiltered: Boolean get() = typeFilter != null
}

class MovementHistoryViewModel(
    private val repo: MovementRepository = MovementRepository(),
) : ViewModel() {

    private val _ui = MutableStateFlow(HistoryUiState())
    val ui = _ui.asStateFlow()

    init { start() }

    fun onDateSelected(date: LocalDate) = _ui.update { it.copy(selectedDate = date) }

    fun onTypeFilterChange(type: MovementType?) = _ui.update { it.copy(typeFilter = type) }

    private fun start() {
        if (!SupabaseService.isConfigured) {
            _ui.update { it.copy(loading = false, error = "Supabase isn't configured yet.") }
            return
        }
        refresh()
        viewModelScope.launch { runCatching { repo.changes().collect { refresh() } } }
    }

    private fun refresh() {
        viewModelScope.launch {
            runCatching { repo.items() }
                .onSuccess { list -> _ui.update { it.copy(all = list, loading = false, error = null) } }
                .onFailure { e -> _ui.update { it.copy(loading = false, error = e.userMessage()) } }
        }
    }
}
