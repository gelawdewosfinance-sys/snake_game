package kaleb.snakegame.menen.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaleb.snakegame.menen.ads.ActiveAd
import kaleb.snakegame.menen.model.Difficulty
import kaleb.snakegame.menen.ui.components.AppOpenAdDialog
import kaleb.snakegame.menen.ui.components.GameOverDialog
import kaleb.snakegame.menen.ui.components.InterstitialAdDialog
import kaleb.snakegame.menen.ui.components.LifeLostDialog
import kaleb.snakegame.menen.ui.components.PersistentBannerAdView
import kaleb.snakegame.menen.ui.components.RewardedVideoAdDialog
import kaleb.snakegame.menen.ui.components.SkinPickerDialog
import kaleb.snakegame.menen.ui.components.SnakeActionBar
import kaleb.snakegame.menen.ui.components.SnakeDPadControls
import kaleb.snakegame.menen.ui.components.SnakeGameBoard
import kaleb.snakegame.menen.ui.components.SnakeHud
import kaleb.snakegame.menen.ui.components.TutorialDialog
import kaleb.snakegame.menen.viewmodel.GameViewModel

@Composable
fun SnakeGameScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(Color(0xFF060913), Color(0xFF0B132B), Color(0xFF070C1A))
        )
      )
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding(),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Top Area: Title & HUD
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 500.dp)
          .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // App Title Banner
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(uiState.selectedSkin.primaryColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "SNAKE GAME",
              color = Color.White,
              fontSize = 17.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = 2.sp
            )
          }

          Text(
            text = uiState.selectedSkin.title,
            color = uiState.selectedSkin.secondaryColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // HUD: Score, Lives Hearts, High Score, Shield
        SnakeHud(
          score = uiState.score,
          highScore = uiState.highScore,
          lives = uiState.lives,
          isShieldActive = uiState.isShieldActive,
          shieldSeconds = uiState.shieldRemainingSeconds,
          skin = uiState.selectedSkin
        )

        // Action Bar: Pause, Difficulty, Sound, Vibration, Skin, Tutorial
        SnakeActionBar(
          isPaused = uiState.isPaused,
          difficulty = uiState.difficulty,
          isSoundEnabled = uiState.isSoundEnabled,
          isVibrationEnabled = uiState.isVibrationEnabled,
          selectedSkin = uiState.selectedSkin,
          onTogglePause = { viewModel.togglePause() },
          onCycleDifficulty = {
            val nextDiff = when (uiState.difficulty) {
              Difficulty.EASY -> Difficulty.MEDIUM
              Difficulty.MEDIUM -> Difficulty.HARD
              Difficulty.HARD -> Difficulty.EASY
            }
            viewModel.setDifficulty(nextDiff)
          },
          onToggleSound = { viewModel.toggleSound() },
          onToggleVibration = { viewModel.toggleVibration() },
          onOpenSkins = { viewModel.openSkinPicker() },
          onOpenTutorial = { viewModel.openTutorial() }
        )
      }

      // Middle Area: Snake Game Board (Edge-to-edge from side to side to phone edges)
      Box(
        modifier = Modifier
          .weight(1f, fill = false)
          .fillMaxWidth()
          .padding(horizontal = 2.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
      ) {
        SnakeGameBoard(
          gridWidth = uiState.gridWidth,
          gridHeight = uiState.gridHeight,
          snake = uiState.snake,
          currentDirection = uiState.currentDirection,
          foods = uiState.foods,
          skin = uiState.selectedSkin,
          isShieldActive = uiState.isShieldActive,
          shieldRemainingRatio = uiState.shieldRemainingSeconds / 6f,
          isInvulnerable = uiState.invulnerabilityTicks > 0,
          onDirectionChanged = { dir -> viewModel.changeDirection(dir) }
        )

        // Paused Indicator Overlay
        if (uiState.isPaused && !uiState.isGameOver) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(16.dp))
              .background(Color(0xDD0B1224))
              .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(16.dp))
              .padding(horizontal = 24.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Pause, contentDescription = null, tint = Color(0xFF38BDF8))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "GAME PAUSED",
                color = Color.White,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                fontSize = 15.sp
              )
            }
          }
        }
      }

      // Bottom Area: Directional D-Pad Controls
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .widthIn(max = 500.dp)
          .padding(bottom = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        SnakeDPadControls(
          onDirectionClicked = { dir -> viewModel.changeDirection(dir) },
          accentColor = uiState.selectedSkin.primaryColor
        )
      }

      // Persistent Bottom Banner Ad (No close button, per requirement)
      PersistentBannerAdView(modifier = Modifier.fillMaxWidth())
    }

    // Modal Overlays
    when (val ad = uiState.activeAd) {
      is ActiveAd.AppOpen -> {
        AppOpenAdDialog(onDismiss = { viewModel.dismissAd() })
      }
      is ActiveAd.Interstitial -> {
        InterstitialAdDialog(reason = ad.reason, onDismiss = { viewModel.dismissAd() })
      }
      is ActiveAd.RewardedVideo -> {
        RewardedVideoAdDialog(
          purpose = ad.purpose,
          onRewardEarned = { purpose -> viewModel.onRewardedAdCompleted(purpose) }
        )
      }
      is ActiveAd.None -> {
        // No ad
      }
    }

    // Life Lost Dialog (prevents accidental consecutive deaths, gives full control to resume)
    if (uiState.showLifeLostDialog && !uiState.isGameOver && uiState.activeAd is ActiveAd.None) {
      LifeLostDialog(
        remainingLives = uiState.lives,
        currentScore = uiState.score,
        onResume = { viewModel.resumeAfterLifeLost() }
      )
    }

    // Game Over Dialog
    if (uiState.isGameOver && uiState.activeAd is ActiveAd.None) {
      GameOverDialog(
        score = uiState.score,
        highScore = uiState.highScore,
        countdown = uiState.gameOverCountdown,
        onWatchAdToRevive = { viewModel.watchAdToRevive() },
        onPlayAgain = { viewModel.playAgainAfterGameOver() }
      )
    }

    // Tutorial Dialog
    if (uiState.showTutorial) {
      TutorialDialog(onDismiss = { viewModel.closeTutorial() })
    }

    // Skin Picker Dialog
    if (uiState.showSkinPicker) {
      SkinPickerDialog(
        currentSkin = uiState.selectedSkin,
        unlockedSkinIds = uiState.unlockedSkinIds,
        highScore = uiState.highScore,
        onSelectSkin = { skin -> viewModel.selectSkin(skin) },
        onWatchAdToUnlock = { skin -> viewModel.watchAdToUnlockSkin(skin) },
        onDismiss = { viewModel.closeSkinPicker() }
      )
    }
  }
}
