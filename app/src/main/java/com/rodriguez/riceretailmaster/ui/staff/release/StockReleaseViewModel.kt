package com.rodriguez.riceretailmaster.ui.staff.release

import com.rodriguez.riceretailmaster.data.model.MovementType
import com.rodriguez.riceretailmaster.data.model.MovementUnit
import com.rodriguez.riceretailmaster.ui.staff.MovementFormViewModel

class StockReleaseViewModel : MovementFormViewModel(
    type = MovementType.RELEASE,
    defaultQuantity = 1,
) {
    override fun successMessage(variety: String, quantity: Int, unit: MovementUnit): String =
        "Released $quantity ${unit.label} of $variety."
}
