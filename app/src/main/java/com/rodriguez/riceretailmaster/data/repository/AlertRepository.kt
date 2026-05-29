package com.rodriguez.riceretailmaster.data.repository

import com.rodriguez.riceretailmaster.data.SupabaseService
import com.rodriguez.riceretailmaster.data.model.AlertItem
import com.rodriguez.riceretailmaster.data.model.AlertRow
import com.rodriguez.riceretailmaster.data.model.AlertSeverity
import com.rodriguez.riceretailmaster.data.tableChanges
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.flow.Flow

class AlertRepository(
    private val varietyRepo: VarietyRepository = VarietyRepository(),
) {
    private val db get() = SupabaseService.client.postgrest

    suspend fun items(): List<AlertItem> {
        val byId = varietyRepo.all().associateBy { it.id }
        return db.from("alerts")
            .select {
                filter { eq("is_read", false) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<AlertRow>()
            .map { r ->
                val v = byId[r.varietyId]
                AlertItem(
                    id = r.id,
                    varietyName = v?.name ?: "Unknown",
                    severity = AlertSeverity.from(r.severity),
                    message = r.message,
                    threshold = v?.lowStockThreshold ?: 0,
                    isRead = r.isRead,
                    createdAt = r.createdAt,
                )
            }
    }

    suspend fun markAllRead() {
        db.from("alerts").update({
            set("is_read", true)
        }) {
            filter { eq("is_read", false) }
        }
    }

    fun changes(): Flow<Unit> = SupabaseService.client.tableChanges("alerts")
}
