package com.rodriguez.riceretailmaster.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rodriguez.riceretailmaster.ui.theme.OnSurfaceVariant
import com.rodriguez.riceretailmaster.ui.theme.Primary

/** One bar: a short label (e.g. "SND") and its value (sacks). */
data class BarEntry(val label: String, val value: Double)

/** Stock-distribution bar chart, drawn with Compose Canvas (no chart library). */
@Composable
fun DistributionBarChart(
    entries: List<BarEntry>,
    modifier: Modifier = Modifier,
    barColor: Color = Primary,
) {
    val maxValue = (entries.maxOfOrNull { it.value } ?: 0.0).coerceAtLeast(1.0)
    Column(modifier.fillMaxWidth()) {
        Canvas(
            Modifier
                .fillMaxWidth()
                .height(140.dp),
        ) {
            val count = entries.size
            if (count == 0) return@Canvas
            val slot = size.width / count
            val barWidth = slot * 0.5f
            entries.forEachIndexed { i, entry ->
                val barHeight = (entry.value / maxValue).toFloat() * size.height
                val left = i * slot + (slot - barWidth) / 2f
                val top = size.height - barHeight
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 3f, barWidth / 3f),
                )
            }
        }
        Spacer(Modifier.size(6.dp))
        Row(Modifier.fillMaxWidth()) {
            entries.forEach { entry ->
                Text(
                    text = entry.label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                )
            }
        }
    }
}
