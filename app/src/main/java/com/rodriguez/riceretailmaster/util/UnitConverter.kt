package com.rodriguez.riceretailmaster.util

import com.rodriguez.riceretailmaster.data.model.MovementUnit
import kotlin.math.roundToLong

object UnitConverter {

    const val KG_PER_SACK = 50.0

    fun toSacks(quantity: Double, unit: MovementUnit): Double =
        if (unit == MovementUnit.KG) quantity / KG_PER_SACK else quantity

    fun sacksToKg(sacks: Double): Double = sacks * KG_PER_SACK

    fun formatSacks(sacks: Double): String {
        val rounded = (sacks * 100).roundToLong() / 100.0
        return if (rounded % 1.0 == 0.0) rounded.toLong().toString()
        else rounded.toString().trimEnd('0').trimEnd('.')
    }

    fun formatKg(sacks: Double): String {
        val kg = sacksToKg(sacks)
        return if (kg % 1.0 == 0.0) "${kg.toLong()} kg" else "$kg kg"
    }
}
