package com.rodriguez.riceretailmaster.data.model
enum class MovementType(val raw: String) {
    DELIVERY("delivery"),
    RELEASE("release");

    companion object {
        fun from(raw: String?) = entries.firstOrNull { it.raw == raw } ?: RELEASE
    }
}

enum class MovementUnit(val raw: String, val label: String) {
    SACK("sack", "Sack (50kg)"),
    KG("kg", "Kilograms");

    companion object {
        fun from(raw: String?) = entries.firstOrNull { it.raw == raw } ?: SACK
    }
}

enum class AlertSeverity(val raw: String) {
    CRITICAL("critical"),
    WARNING("warning");

    companion object {
        fun from(raw: String?) = entries.firstOrNull { it.raw == raw } ?: WARNING
    }
}

enum class UserRole(val raw: String) {
    OWNER("owner"),
    STAFF("staff");

    companion object {
        fun from(raw: String?) = entries.firstOrNull { it.raw == raw } ?: STAFF
    }
}

data class InventoryItem(
    val varietyId: String,
    val name: String,
    val quantitySacks: Double,
    val maxCapacitySacks: Int,
    val threshold: Int,
) {
    val isCritical: Boolean get() = quantitySacks <= threshold
}

data class AlertItem(
    val id: String,
    val varietyName: String,
    val severity: AlertSeverity,
    val message: String,
    val threshold: Int,
    val isRead: Boolean,
    val createdAt: String?,
)

data class MovementItem(
    val id: String,
    val varietyName: String,
    val type: MovementType,
    val quantity: Double,
    val unit: MovementUnit,
    val supplierName: String?,
    val createdAt: String?,
)
