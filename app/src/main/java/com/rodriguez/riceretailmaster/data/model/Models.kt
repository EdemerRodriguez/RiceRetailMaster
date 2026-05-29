package com.rodriguez.riceretailmaster.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class RiceVariety(
    val id: String = "",
    val name: String = "",
    @SerialName("low_stock_threshold") val lowStockThreshold: Int = 3,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class InventoryRow(
    val id: String = "",
    @SerialName("variety_id") val varietyId: String = "",
    @SerialName("quantity_sacks") val quantitySacks: Double = 0.0,
    @SerialName("max_capacity_sacks") val maxCapacitySacks: Int = 20,
    @SerialName("updated_at") val updatedAt: String? = null,
)

@Serializable
data class StockMovementRow(
    val id: String = "",
    @SerialName("variety_id") val varietyId: String = "",
    val type: String = "",
    val quantity: Double = 0.0,
    val unit: String = "",
    @SerialName("supplier_name") val supplierName: String? = null,
    @SerialName("recorded_by") val recordedBy: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class AlertRow(
    val id: String = "",
    @SerialName("variety_id") val varietyId: String = "",
    val severity: String = "",
    val message: String = "",
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class StockMovementInsert(
    @SerialName("variety_id") val varietyId: String,
    val type: String,
    val quantity: Double,
    val unit: String,
    @SerialName("supplier_name") val supplierName: String? = null,
    @SerialName("recorded_by") val recordedBy: String? = null,
)

@Serializable
data class RiceVarietyInsert(
    val name: String,
    @SerialName("low_stock_threshold") val lowStockThreshold: Int,
)

@Serializable
data class InventoryInsert(
    @SerialName("variety_id") val varietyId: String,
    @SerialName("quantity_sacks") val quantitySacks: Double,
    @SerialName("max_capacity_sacks") val maxCapacitySacks: Int = 20,
)

@Serializable
data class AlertInsert(
    @SerialName("variety_id") val varietyId: String,
    val severity: String,
    val message: String,
)
