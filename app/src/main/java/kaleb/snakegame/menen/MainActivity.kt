package kaleb.snakegame.menen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kaleb.snakegame.menen.ui.screens.SnakeGameScreen
import kaleb.snakegame.menen.ui.theme.MyApplicationTheme
import kaleb.snakegame.menen.viewmodel.GameViewModel

import com.google.android.gms.ads.MobileAds

class MainActivity : ComponentActivity() {

  private val viewModel: GameViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize official Google Mobile Ads SDK
    try {
      MobileAds.initialize(this) {}
    } catch (_: Exception) {}

    setContent {
      MyApplicationTheme(darkTheme = true) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = Color(0xFF060913)
        ) {
          SnakeGameScreen(viewModel = viewModel)
        }
      }
    }
  }

  override fun onPause() {
    super.onPause()
    // Auto-pause game when leaving app
    if (!viewModel.uiState.value.isGameOver && !viewModel.uiState.value.isPaused) {
      viewModel.togglePause()
    }
  }
}
