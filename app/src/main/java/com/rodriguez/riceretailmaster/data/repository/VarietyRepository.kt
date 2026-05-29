package com.rodriguez.riceretailmaster.data.repository

import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.InventoryInsert
import com.rodriguez.riceretailmaster.data.model.RiceVariety
import com.rodriguez.riceretailmaster.data.model.RiceVarietyInsert
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order

class VarietyRepository {

    private val db get() = SupabaseService.client.postgrest

    suspend fun all(): List<RiceVariety> =
        db.from("rice_varieties")
            .select { order("created_at", Order.ASCENDING) }
            .decodeList<RiceVariety>()
    suspend fun addVariety(name: String, threshold: Int, initialSacks: Double, maxCapacity: Int) {
        val created = db.from("rice_varieties")
            .insert(RiceVarietyInsert(name = name.trim(), lowStockThreshold = threshold)) { select() }
            .decodeSingle<RiceVariety>()
        db.from("inventory").insert(
            InventoryInsert(
                varietyId = created.id,
                quantitySacks = initialSacks,
                maxCapacitySacks = maxCapacity,
            ),
        )
    }

    suspend fun updateVariety(id: String, name: String, threshold: Int) {
        db.from("rice_varieties").update(
            {
                set("name", name.trim())
                set("low_stock_threshold", threshold)
            },
        ) {
            filter { eq("id", id) }
        }
    }

    suspend fun deleteVariety(id: String) {
        db.from("rice_varieties").delete {
            filter { eq("id", id) }
        }
    }
}
