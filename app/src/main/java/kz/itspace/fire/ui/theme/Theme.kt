package kz.itspace.fire.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFFC62828),
    primaryContainer = Color(0xFFFFDAD6),
    secondary = Color(0xFFE65100),
    tertiary = Color(0xFF2E7D32),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB4AB),
    primaryContainer = Color(0xFF93000A),
    secondary = Color(0xFFFFB74D),
    tertiary = Color(0xFF81C784),
)

@Composable
fun FireTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        content = content,
    )
}
