package com.rodriguez.riceretailmaster.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val RrmColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Surface,
    secondary = PrimaryLight,
    onSecondary = OnSurface,
    background = Background,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceTint,
    onSurfaceVariant = OnSurfaceVariant,
    error = DangerIcon,
    onError = Surface,
    outline = InputBorder,
)

@Composable
fun RiceRetailMasterTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = RrmColorScheme,
        typography = Typography,
        content = content,
    )
}
