package com.rodriguez.riceretailmaster.data.repository

import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.InventoryItem
import com.rodriguez.riceretailmaster.data.model.InventoryRow
import com.rodriguez.riceretailmaster.data.tableChanges
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow

class InventoryRepository(
    private val varietyRepo: VarietyRepository = VarietyRepository(),
) {
    private val db get() = SupabaseService.client.postgrest

    suspend fun items(): List<InventoryItem> {
        val varieties = varietyRepo.all()
        val rowsByVariety = db.from("inventory")
            .select()
            .decodeList<InventoryRow>()
            .associateBy { it.varietyId }

        return varieties.mapNotNull { v ->
            val row = rowsByVariety[v.id] ?: return@mapNotNull null
            InventoryItem(
                varietyId = v.id,
                name = v.name,
                quantitySacks = row.quantitySacks,
                maxCapacitySacks = row.maxCapacitySacks,
                threshold = v.lowStockThreshold,
            )
        }
    }

    fun changes(): Flow<Unit> = SupabaseService.client.tableChanges("inventory")

    suspend fun updateMaxCapacity(varietyId: String, maxCapacitySacks: Int) {
        db.from("inventory").update(
            { set("max_capacity_sacks", maxCapacitySacks) },
        ) {
            filter { eq("variety_id", varietyId) }
        }
    }
}
