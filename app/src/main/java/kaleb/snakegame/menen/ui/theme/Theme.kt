package kaleb.snakegame.menen.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = EmeraldPrimary,
  secondary = CyberCyan,
  tertiary = GoldenAmber,
  background = DarkBackground,
  surface = DarkSurface,
  onPrimary = Color.Black,
  onSecondary = Color.Black,
  onBackground = Color.White,
  onSurface = Color.White
)

private val LightColorScheme = DarkColorScheme // Game is styled with luxury dark gaming palette

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColor: Boolean = false, // Keep consistent cyber theme for game immersion
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = Typography,
    content = content
  )
}
