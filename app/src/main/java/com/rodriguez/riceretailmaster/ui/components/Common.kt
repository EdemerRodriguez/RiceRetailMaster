package com.rodriguez.riceretailmaster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rodriguez.riceretailmaster.ui.theme.Background
import com.rodriguez.riceretailmaster.ui.theme.FaintBorder
import com.rodriguez.riceretailmaster.ui.theme.OnSurface
import com.rodriguez.riceretailmaster.ui.theme.OnSurfaceVariant
import com.rodriguez.riceretailmaster.ui.theme.Primary
import com.rodriguez.riceretailmaster.ui.theme.SurfaceTint
import androidx.compose.material3.MaterialTheme

/** Bold screen title, e.g. "Stock Release". */
@Composable
fun ScreenTitle(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = OnSurface,
        textAlign = textAlign,
        modifier = if (textAlign == TextAlign.Center) modifier.fillMaxWidth() else modifier,
    )
}

/** Primary-colored field/section label, e.g. "Rice Variety". */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = Primary,
        modifier = modifier,
    )
}

/** Warm rounded form/card container (SurfaceTint, faint border, 16dp padding). */
@Composable
fun FormCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SurfaceTint)
            .border(1.dp, FaintBorder, shape)
            .padding(16.dp),
        content = content,
    )
}

/** White rounded card with a faint border — used for Dashboard / History sections. */
@Composable
fun SurfaceCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color.White)
            .border(1.dp, FaintBorder, shape)
            .padding(16.dp),
        content = content,
    )
}

/** Centered Primary spinner shown over a screen while loading. */
@Composable
fun LoadingOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = Primary)
    }
}

/** Muted caption used for footers / hints. */
@Composable
fun Caption(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = OnSurfaceVariant,
    textAlign: TextAlign = TextAlign.Start,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        textAlign = textAlign,
        modifier = modifier,
    )
}
