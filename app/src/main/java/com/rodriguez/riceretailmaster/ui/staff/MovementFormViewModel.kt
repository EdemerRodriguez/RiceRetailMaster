package com.rodriguez.riceretailmaster.ui.staff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.MovementType
import com.rodriguez.riceretailmaster.data.model.MovementUnit
import com.rodriguez.riceretailmaster.data.model.RiceVariety
import com.rodriguez.riceretailmaster.data.repository.MovementRepository
import com.rodriguez.riceretailmaster.data.repository.VarietyRepository
import com.rodriguez.riceretailmaster.util.Validators
import com.rodriguez.riceretailmaster.util.userMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovementFormState(
    val varieties: List<RiceVariety> = emptyList(),
    val selectedVarietyName: String = "",
    val quantity: Int = 1,
    val unit: MovementUnit = MovementUnit.SACK,
    val supplierName: String = "",
    val loadingVarieties: Boolean = true,
    val submitting: Boolean = false,
    val error: String? = null,
    val success: String? = null,
) {
    val varietyNames: List<String> get() = varieties.map { it.name }
}

abstract class MovementFormViewModel(
    private val type: MovementType,
    private val defaultQuantity: Int,
    private val varietyRepo: VarietyRepository = VarietyRepository(),
    private val movementRepo: MovementRepository = MovementRepository(),
) : ViewModel() {

    protected val _ui = MutableStateFlow(MovementFormState(quantity = defaultQuantity))
    val ui = _ui.asStateFlow()

    init {
        loadVarieties()
    }

    private fun loadVarieties() {
        if (!SupabaseService.isConfigured) {
            _ui.update {
                it.copy(
                    loadingVarieties = false,
                    error = "Supabase isn't configured yet.",
                )
            }
            return
        }
        viewModelScope.launch {
            runCatching { varietyRepo.all() }
                .onSuccess { list ->
                    _ui.update {
                        it.copy(
                            varieties = list,
                            selectedVarietyName = it.selectedVarietyName.ifBlank { list.firstOrNull()?.name.orEmpty() },
                            loadingVarieties = false,
                        )
                    }
                }
                .onFailure { e -> _ui.update { it.copy(loadingVarieties = false, error = e.userMessage()) } }
        }
    }

    fun onVarietySelected(name: String) = _ui.update { it.copy(selectedVarietyName = name) }
    fun onQuantityChange(quantity: Int) = _ui.update { it.copy(quantity = quantity.coerceIn(1, 999)) }
    fun onUnitSelected(unit: MovementUnit) = _ui.update { it.copy(unit = unit) }
    fun onSupplierChange(value: String) =
        _ui.update { it.copy(supplierName = value.take(Validators.MAX_SUPPLIER_LENGTH)) }
    fun consumeMessages() = _ui.update { it.copy(error = null, success = null) }

    fun submit() {
        val state = _ui.value
        if (state.submitting) return

        val variety = state.varieties.firstOrNull { it.name == state.selectedVarietyName }
        if (variety == null) {
            _ui.update { it.copy(error = "Select a rice variety.") }
            return
        }
        if (state.quantity <= 0) {
            _ui.update { it.copy(error = "Quantity must be greater than zero.") }
            return
        }
        if (type == MovementType.DELIVERY && state.supplierName.isBlank()) {
            _ui.update { it.copy(error = "Enter the supplier name.") }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(submitting = true, error = null, success = null) }
            runCatching {
                movementRepo.log(
                    varietyId = variety.id,
                    type = type,
                    quantity = state.quantity.toDouble(),
                    unit = state.unit,
                    supplierName = if (type == MovementType.DELIVERY) state.supplierName.trim() else null,
                )
            }.onSuccess {
                _ui.update {
                    it.copy(
                        submitting = false,
                        success = successMessage(variety.name, state.quantity, state.unit),
                        quantity = defaultQuantity,
                        supplierName = "",
                    )
                }
            }.onFailure { e ->
                _ui.update { it.copy(submitting = false, error = e.userMessage()) }
            }
        }
    }

    protected abstract fun successMessage(variety: String, quantity: Int, unit: MovementUnit): String
}
