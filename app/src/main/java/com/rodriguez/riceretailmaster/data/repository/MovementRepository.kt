package com.rodriguez.riceretailmaster.data.repository

import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.InventoryInsert
import com.rodriguez.riceretailmaster.data.model.InventoryRow
import com.rodriguez.riceretailmaster.data.model.MovementItem
import com.rodriguez.riceretailmaster.data.model.MovementType
import com.rodriguez.riceretailmaster.data.model.MovementUnit
import com.rodriguez.riceretailmaster.data.model.StockMovementInsert
import com.rodriguez.riceretailmaster.data.model.StockMovementRow
import com.rodriguez.riceretailmaster.data.tableChanges
import com.rodriguez.riceretailmaster.util.UnitConverter
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

class MovementRepository(
    private val varietyRepo: VarietyRepository = VarietyRepository(),
) {
    private val db get() = SupabaseService.client.postgrest
    private val auth get() = SupabaseService.client.auth

    suspend fun log(
        varietyId: String,
        type: MovementType,
        quantity: Double,
        unit: MovementUnit,
        supplierName: String? = null,
    ) {
        require(quantity > 0) { "Quantity must be greater than zero." }
        val sacks = UnitConverter.toSacks(quantity, unit)

        val current = db.from("inventory")
            .select { filter { eq("variety_id", varietyId) } }
            .decodeSingleOrNull<InventoryRow>()
        val currentQty = current?.quantitySacks ?: 0.0

        if (type == MovementType.RELEASE && sacks > currentQty + 1e-9) {
            val name = varietyRepo.all().firstOrNull { it.id == varietyId }?.name ?: "This variety"
            throw IllegalStateException(
                if (currentQty <= 0.0) {
                    "$name is out of stock — there's nothing to release."
                } else {
                    "Not enough stock: only ${UnitConverter.formatSacks(currentQty)} sack(s) of $name available."
                },
            )
        }

        val newQty = (if (type == MovementType.DELIVERY) currentQty + sacks else currentQty - sacks)
            .coerceAtLeast(0.0)

        if (current == null) {
            db.from("inventory").insert(
                InventoryInsert(varietyId = varietyId, quantitySacks = newQty),
            )
        } else {
            db.from("inventory").update({
                set("quantity_sacks", newQty)
            }) {
                filter { eq("variety_id", varietyId) }
            }
        }

        db.from("stock_movements").insert(
            StockMovementInsert(
                varietyId = varietyId,
                type = type.raw,
                quantity = quantity,
                unit = unit.raw,
                supplierName = supplierName?.takeIf { it.isNotBlank() },
                recordedBy = auth.currentUserOrNull()?.id,
            ),
        )
    }

    suspend fun items(): List<MovementItem> {
        val byId = varietyRepo.all().associateBy { it.id }
        return db.from("stock_movements")
            .select { order("created_at", Order.DESCENDING) }
            .decodeList<StockMovementRow>()
            .map { r ->
                MovementItem(
                    id = r.id,
                    varietyName = byId[r.varietyId]?.name ?: "Unknown",
                    type = MovementType.from(r.type),
                    quantity = r.quantity,
                    unit = MovementUnit.from(r.unit),
                    supplierName = r.supplierName?.takeIf { it.isNotBlank() },
                    createdAt = r.createdAt,
                )
            }
    }

    fun changes(): Flow<Unit> = SupabaseService.client.tableChanges("stock_movements")
}
