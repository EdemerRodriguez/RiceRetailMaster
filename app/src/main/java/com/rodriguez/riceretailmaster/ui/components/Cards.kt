package com.rodriguez.riceretailmaster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.PriorityHigh
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rodriguez.riceretailmaster.data.model.AlertItem
import com.rodriguez.riceretailmaster.data.model.AlertSeverity
import com.rodriguez.riceretailmaster.data.model.InventoryItem
import com.rodriguez.riceretailmaster.data.model.MovementItem
import com.rodriguez.riceretailmaster.data.model.MovementType
import com.rodriguez.riceretailmaster.data.model.MovementUnit
import com.rodriguez.riceretailmaster.ui.theme.CriticalBadgeBg
import com.rodriguez.riceretailmaster.ui.theme.DangerIcon
import com.rodriguez.riceretailmaster.ui.theme.FaintBorder
import com.rodriguez.riceretailmaster.ui.theme.NegativeMove
import com.rodriguez.riceretailmaster.ui.theme.OnSurface
import com.rodriguez.riceretailmaster.ui.theme.OnSurfaceVariant
import com.rodriguez.riceretailmaster.ui.theme.PositiveMove
import com.rodriguez.riceretailmaster.ui.theme.Primary
import com.rodriguez.riceretailmaster.ui.theme.StatPinkBg
import com.rodriguez.riceretailmaster.ui.theme.Surface
import com.rodriguez.riceretailmaster.ui.theme.SurfaceTint
import com.rodriguez.riceretailmaster.ui.theme.UnfilledBar
import com.rodriguez.riceretailmaster.util.Formatters
import com.rodriguez.riceretailmaster.util.UnitConverter

// ---------------------------------------------------------------------
// Inventory list row (Quick-Check Lookup)
// ---------------------------------------------------------------------

@Composable
fun InventoryRow(item: InventoryItem, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceTint)
            .border(1.dp, FaintBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconCircle(Icons.Rounded.Grain, tint = Primary, bg = StatPinkBg)
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(item.name, style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.SemiBold)
            Text(
                "${UnitConverter.formatSacks(item.quantitySacks)} Sacks",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
            )
        }
        StatusBadge(critical = item.isCritical)
    }
}

// ---------------------------------------------------------------------
// Dashboard summary stat card
// ---------------------------------------------------------------------

@Composable
fun StatCard(
    number: String,
    label: String,
    background: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(number, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnSurface)
        Spacer(Modifier.size(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
    }
}

// ---------------------------------------------------------------------
// Dashboard "Stock Levels by Variety" row with progress bar
// ---------------------------------------------------------------------

@Composable
fun StockLevelBar(item: InventoryItem, modifier: Modifier = Modifier) {
    val fraction = if (item.maxCapacitySacks > 0) {
        (item.quantitySacks / item.maxCapacitySacks).toFloat().coerceIn(0f, 1f)
    } else 0f
    val fillColor = if (item.isCritical) DangerIcon else Primary
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(item.name, style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.Medium)
            Text(
                "${UnitConverter.formatSacks(item.quantitySacks)} / ${item.maxCapacitySacks} Sacks",
                style = MaterialTheme.typography.bodySmall,
                color = OnSurfaceVariant,
            )
        }
        Spacer(Modifier.size(6.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(UnfilledBar),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(4.dp))
                    .background(fillColor),
            )
        }
    }
}

// ---------------------------------------------------------------------
// Low-stock alert card
// ---------------------------------------------------------------------

@Composable
fun AlertCard(alert: AlertItem, modifier: Modifier = Modifier) {
    val isCritical = alert.severity == AlertSeverity.CRITICAL
    val icon = if (isCritical) Icons.Rounded.PriorityHigh else Icons.Rounded.WarningAmber
    val iconTint = if (isCritical) DangerIcon else com.rodriguez.riceretailmaster.ui.theme.WarningIcon
    val iconBg = if (isCritical) CriticalBadgeBg else Color(0xFFFFF3D6)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .border(1.dp, FaintBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.Top) {
            IconCircle(icon, tint = iconTint, bg = iconBg)
            Spacer(Modifier.size(12.dp))
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(alert.varietyName, style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.Bold)
                    Text(Formatters.relative(alert.createdAt), style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                }
                Spacer(Modifier.size(4.dp))
                Text(alert.message, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
                Spacer(Modifier.size(10.dp))
                ThresholdBadge(threshold = alert.threshold)
            }
        }
    }
}

// ---------------------------------------------------------------------
// Movement history entry
// ---------------------------------------------------------------------

@Composable
fun MovementRow(item: MovementItem, modifier: Modifier = Modifier) {
    val received = item.type == MovementType.DELIVERY
    val arrow = if (received) Icons.Rounded.ArrowDownward else Icons.Rounded.ArrowUpward
    val arrowColor = if (received) PositiveMove else NegativeMove
    val description = "${if (received) "Received" else "Dispensed"} ${item.varietyName}"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceTint)
            .border(1.dp, FaintBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconCircle(arrow, tint = arrowColor, bg = arrowColor.copy(alpha = 0.12f))
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(description, style = MaterialTheme.typography.bodyMedium, color = OnSurface, fontWeight = FontWeight.Medium)
            if (received && !item.supplierName.isNullOrBlank()) {
                Text(
                    "from ${item.supplierName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                )
            }
            Text(Formatters.time(item.createdAt), style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariant)
        }
        QuantityBadge(text = signedQuantity(item), color = arrowColor)
    }
}

private fun signedQuantity(item: MovementItem): String {
    val sign = if (item.type == MovementType.DELIVERY) "+" else "-"
    val number = UnitConverter.formatSacks(item.quantity)
    val unit = when (item.unit) {
        MovementUnit.SACK -> if (item.quantity == 1.0) "Sack" else "Sacks"
        MovementUnit.KG -> "kg"
    }
    return "$sign$number $unit"
}

// ---------------------------------------------------------------------
// Shared: small circular icon chip
// ---------------------------------------------------------------------

@Composable
private fun IconCircle(icon: ImageVector, tint: Color, bg: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(bg),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
    }
}
