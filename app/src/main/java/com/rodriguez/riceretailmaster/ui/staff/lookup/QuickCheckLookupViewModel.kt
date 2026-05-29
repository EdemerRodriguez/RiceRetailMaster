package com.rodriguez.riceretailmaster.ui.staff.lookup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.InventoryItem
import com.rodriguez.riceretailmaster.data.repository.InventoryRepository
import com.rodriguez.riceretailmaster.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LookupUiState(
    val items: List<InventoryItem> = emptyList(),
    val query: String = "",
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val error: String? = null,
) {
    val filtered: List<InventoryItem>
        get() = if (query.isBlank()) items
        else items.filter { it.name.contains(query.trim(), ignoreCase = true) }
}

class QuickCheckLookupViewModel(
    private val repo: InventoryRepository = InventoryRepository(),
) : ViewModel() {

    private val _ui = MutableStateFlow(LookupUiState())
    val ui = _ui.asStateFlow()

    init { start() }

    fun onQueryChange(value: String) = _ui.update { it.copy(query = value) }

    fun manualRefresh() {
        _ui.update { it.copy(refreshing = true) }
        refresh()
    }

    private fun start() {
        if (!SupabaseService.isConfigured) {
            _ui.update { it.copy(loading = false, error = "Supabase isn't configured yet.") }
            return
        }
        refresh()
        viewModelScope.launch {
            runCatching { repo.changes().collect { refresh() } }
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            runCatching { repo.items() }
                .onSuccess { list ->
                    _ui.update { it.copy(items = list, loading = false, refreshing = false, error = null) }
                }
                .onFailure { e ->
                    _ui.update { it.copy(loading = false, refreshing = false, error = e.userMessage()) }
                }
        }
    }
}
