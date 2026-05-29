package com.rodriguez.riceretailmaster.ui.staff.delivery

import com.rodriguez.riceretailmaster.data.model.MovementType
import com.rodriguez.riceretailmaster.data.model.MovementUnit
import com.rodriguez.riceretailmaster.ui.staff.MovementFormViewModel

class DeliveryLogViewModel : MovementFormViewModel(
    type = MovementType.DELIVERY,
    defaultQuantity = 5,
) {
    override fun successMessage(variety: String, quantity: Int, unit: MovementUnit): String =
        "Logged delivery of $quantity ${unit.label} of $variety."
}
