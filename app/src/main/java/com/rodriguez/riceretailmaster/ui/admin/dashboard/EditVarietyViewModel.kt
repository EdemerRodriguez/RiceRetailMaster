package com.rodriguez.riceretailmaster.ui.admin.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.InventoryItem
import com.rodriguez.riceretailmaster.data.repository.InventoryRepository
import com.rodriguez.riceretailmaster.data.repository.VarietyRepository
import com.rodriguez.riceretailmaster.util.userMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditVarietyState(
    val varietyId: String = "",
    val originalName: String = "",
    val name: String = "",
    val threshold: Int = 3,
    val maxCapacity: Int = 20,
    val submitting: Boolean = false,
    val confirmingDelete: Boolean = false,
    val error: String? = null,
)

sealed interface EditResult {
    data class Updated(val name: String) : EditResult
    data class Deleted(val name: String) : EditResult
}

class EditVarietyViewModel(
    private val varietyRepo: VarietyRepository = VarietyRepository(),
    private val inventoryRepo: InventoryRepository = InventoryRepository(),
) : ViewModel() {

    private val _ui = MutableStateFlow(EditVarietyState())
    val ui = _ui.asStateFlow()

    private val _result = MutableSharedFlow<EditResult>()
    val result = _result.asSharedFlow()

    fun load(item: InventoryItem) {
        _ui.value = EditVarietyState(
            varietyId = item.varietyId,
            originalName = item.name,
            name = item.name,
            threshold = item.threshold,
            maxCapacity = item.maxCapacitySacks,
        )
    }

    fun onNameChange(value: String) =
        _ui.update { it.copy(name = value.take(40), error = null, confirmingDelete = false) }

    fun onThresholdChange(value: Int) =
        _ui.update { it.copy(threshold = value.coerceIn(1, 99), confirmingDelete = false) }

    fun onCapacityChange(value: Int) =
        _ui.update { it.copy(maxCapacity = value.coerceIn(1, 999), confirmingDelete = false) }

    fun requestDelete() = _ui.update { it.copy(confirmingDelete = true, error = null) }

    fun cancelDelete() = _ui.update { it.copy(confirmingDelete = false) }

    fun submit() {
        val state = _ui.value
        if (state.submitting) return

        val name = state.name.trim()
        if (name.isBlank()) {
            _ui.update { it.copy(error = "Enter a variety name.") }
            return
        }
        if (!SupabaseService.isConfigured) {
            _ui.update { it.copy(error = "Supabase isn't configured yet (see docs/SUPABASE_SETUP.md).") }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(submitting = true, error = null) }

            val duplicate = runCatching {
                varietyRepo.all().any { v ->
                    v.id != state.varietyId && v.name.equals(name, ignoreCase = true)
                }
            }.getOrDefault(false)
            if (duplicate) {
                _ui.update {
                    it.copy(
                        submitting = false,
                        error = "Another rice variety named \"$name\" already exists.",
                    )
                }
                return@launch
            }

            runCatching {
                varietyRepo.updateVariety(state.varietyId, name, state.threshold)
                inventoryRepo.updateMaxCapacity(state.varietyId, state.maxCapacity)
            }.onSuccess {
                _result.emit(EditResult.Updated(name))
            }.onFailure { e ->
                _ui.update { it.copy(submitting = false, error = updateErrorMessage(e)) }
            }
        }
    }

    fun confirmDelete() {
        val state = _ui.value
        if (state.submitting || !state.confirmingDelete) return
        if (!SupabaseService.isConfigured) {
            _ui.update { it.copy(error = "Supabase isn't configured yet (see docs/SUPABASE_SETUP.md).") }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(submitting = true, error = null) }
            runCatching { varietyRepo.deleteVariety(state.varietyId) }
                .onSuccess { _result.emit(EditResult.Deleted(state.originalName)) }
                .onFailure { e -> _ui.update { it.copy(submitting = false, error = e.userMessage()) } }
        }
    }

    private fun updateErrorMessage(e: Throwable): String {
        val msg = (e.message ?: "").lowercase()
        return if ("duplicate" in msg || "unique" in msg || "23505" in msg || "already exists" in msg) {
            "Another rice variety with that name already exists."
        } else {
            e.userMessage()
        }
    }
}
