package com.rodriguez.riceretailmaster.ui.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.repository.VarietyRepository
import com.rodriguez.riceretailmaster.util.userMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddVarietyState(
    val name: String = "",
    val initialStock: Int = 0,
    val threshold: Int = 3,
    val maxCapacity: Int = 20,
    val submitting: Boolean = false,
    val error: String? = null,
)

class AddVarietyViewModel(
    private val repo: VarietyRepository = VarietyRepository(),
) : ViewModel() {

    private val _ui = MutableStateFlow(AddVarietyState())
    val ui = _ui.asStateFlow()

    private val _added = MutableSharedFlow<String>()
    val added = _added.asSharedFlow()

    fun onNameChange(value: String) = _ui.update { it.copy(name = value.take(40), error = null) }
    fun onStockChange(value: Int) = _ui.update { it.copy(initialStock = value.coerceIn(0, 999)) }
    fun onThresholdChange(value: Int) = _ui.update { it.copy(threshold = value.coerceIn(1, 99)) }
    fun onCapacityChange(value: Int) = _ui.update { it.copy(maxCapacity = value.coerceIn(1, 999)) }

    fun reset() = _ui.update { AddVarietyState() }

    fun submit() {
        val state = _ui.value
        if (state.submitting) return

        val name = state.name.trim()
        if (name.isBlank()) {
            _ui.update { it.copy(error = "Enter a variety name.") }
            return
        }
        if (state.initialStock > state.maxCapacity) {
            _ui.update { it.copy(error = "Initial stock can't exceed max capacity.") }
            return
        }
        if (!SupabaseService.isConfigured) {
            _ui.update { it.copy(error = "Supabase isn't configured yet (see docs/SUPABASE_SETUP.md).") }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(submitting = true, error = null) }
            val duplicate = runCatching {
                repo.all().any { existing -> existing.name.equals(name, ignoreCase = true) }
            }.getOrDefault(false)
            if (duplicate) {
                _ui.update {
                    it.copy(
                        submitting = false,
                        error = "A rice variety named \"$name\" already exists.",
                    )
                }
                return@launch
            }

            runCatching {
                repo.addVariety(name, state.threshold, state.initialStock.toDouble(), state.maxCapacity)
            }.onSuccess {
                _added.emit(name)
                _ui.update { AddVarietyState() }
            }.onFailure { e ->
                _ui.update { it.copy(submitting = false, error = addErrorMessage(e)) }
            }
        }
    }

    private fun addErrorMessage(e: Throwable): String {
        val msg = (e.message ?: "").lowercase()
        return if ("duplicate" in msg || "unique" in msg || "23505" in msg || "already exists" in msg) {
            "A rice variety with that name already exists."
        } else {
            e.userMessage()
        }
    }
}
