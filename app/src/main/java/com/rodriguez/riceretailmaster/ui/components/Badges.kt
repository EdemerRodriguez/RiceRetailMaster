package com.rodriguez.riceretailmaster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rodriguez.riceretailmaster.ui.theme.CriticalBadgeBg
import com.rodriguez.riceretailmaster.ui.theme.CriticalBadgeText
import com.rodriguez.riceretailmaster.ui.theme.InStockBadgeBg
import com.rodriguez.riceretailmaster.ui.theme.InStockBadgeText
import com.rodriguez.riceretailmaster.ui.theme.Primary
import com.rodriguez.riceretailmaster.ui.theme.StatPinkBg

/** "IN STOCK" (green) / "CRITICAL" (red) status pill. */
@Composable
fun StatusBadge(critical: Boolean, modifier: Modifier = Modifier) {
    val text = if (critical) "CRITICAL" else "IN STOCK"
    val fg = if (critical) CriticalBadgeText else InStockBadgeText
    val bg = if (critical) CriticalBadgeBg else InStockBadgeBg
    Pill(text = text, fg = fg, bg = bg, modifier = modifier)
}

/** Pink "Threshold: X sacks" badge on alert cards. */
@Composable
fun ThresholdBadge(threshold: Int, modifier: Modifier = Modifier) {
    Pill(
        text = "Threshold: $threshold sacks",
        fg = Primary,
        bg = StatPinkBg,
        modifier = modifier,
    )
}

/** Quantity badge for movement history, e.g. "+10 Sacks" / "-25 kg". */
@Composable
fun QuantityBadge(text: String, color: Color, modifier: Modifier = Modifier) {
    Pill(text = text, fg = color, bg = color.copy(alpha = 0.12f), modifier = modifier, bold = true)
}

/** Outlined summary badge with a leading icon, e.g. "⊕ Deliveries +3". */
@Composable
fun OutlineSummaryBadge(
    text: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, color, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
        Text(text, color = color, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun Pill(
    text: String,
    fg: Color,
    bg: Color,
    modifier: Modifier = Modifier,
    bold: Boolean = false,
) {
    Text(
        text = text,
        color = fg,
        style = if (bold) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelSmall,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 5.dp),
    )
}
