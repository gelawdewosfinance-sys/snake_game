package kaleb.snakegame.menen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaleb.snakegame.menen.model.Difficulty
import kaleb.snakegame.menen.model.Direction
import kaleb.snakegame.menen.model.SnakeSkin

@Composable
fun SnakeActionBar(
  isPaused: Boolean,
  difficulty: Difficulty,
  isSoundEnabled: Boolean,
  isVibrationEnabled: Boolean,
  selectedSkin: SnakeSkin,
  onTogglePause: () -> Unit,
  onCycleDifficulty: () -> Unit,
  onToggleSound: () -> Unit,
  onToggleVibration: () -> Unit,
  onOpenSkins: () -> Unit,
  onOpenTutorial: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Pause / Play Button
    IconButton(
      onClick = onTogglePause,
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(if (isPaused) Color(0xFFF59E0B) else Color(0xFF1E293B))
        .border(1.dp, Color(0xFF475569), CircleShape)
        .testTag("action_pause_resume")
    ) {
      Icon(
        imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
        contentDescription = if (isPaused) "Resume Game" else "Pause Game",
        tint = if (isPaused) Color.Black else Color.White,
        modifier = Modifier.size(22.dp)
      )
    }

    // Difficulty Selector Pill
    Surface(
      onClick = onCycleDifficulty,
      shape = RoundedCornerShape(16.dp),
      color = Color(0xFF1E293B),
      border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
      modifier = Modifier.testTag("difficulty_button")
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Speed,
          contentDescription = "Difficulty",
          tint = when (difficulty) {
            Difficulty.EASY -> Color(0xFF4ADE80)
            Difficulty.MEDIUM -> Color(0xFF38BDF8)
            Difficulty.HARD -> Color(0xFFF87171)
          },
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = difficulty.displayName,
          color = Color.White,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // Skins Picker Button
    IconButton(
      onClick = onOpenSkins,
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(Color(0xFF1E293B))
        .border(1.dp, selectedSkin.glowColor.copy(alpha = 0.8f), CircleShape)
        .testTag("action_open_skins")
    ) {
      Icon(
        imageVector = Icons.Default.ColorLens,
        contentDescription = "Snake Skins",
        tint = selectedSkin.primaryColor,
        modifier = Modifier.size(20.dp)
      )
    }

    // Sound Toggle
    IconButton(
      onClick = onToggleSound,
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(Color(0xFF1E293B))
        .border(1.dp, Color(0xFF475569), CircleShape)
        .testTag("action_toggle_sound")
    ) {
      Icon(
        imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
        contentDescription = if (isSoundEnabled) "Mute Sound" else "Enable Sound",
        tint = if (isSoundEnabled) Color(0xFF38BDF8) else Color(0xFF64748B),
        modifier = Modifier.size(20.dp)
      )
    }

    // Vibration Toggle
    IconButton(
      onClick = onToggleVibration,
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(Color(0xFF1E293B))
        .border(1.dp, Color(0xFF475569), CircleShape)
        .testTag("action_toggle_vibration")
    ) {
      Icon(
        imageVector = Icons.Default.Vibration,
        contentDescription = "Toggle Vibration",
        tint = if (isVibrationEnabled) Color(0xFF4ADE80) else Color(0xFF64748B),
        modifier = Modifier.size(20.dp)
      )
    }

    // How to Play / Tutorial
    IconButton(
      onClick = onOpenTutorial,
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(Color(0xFF1E293B))
        .border(1.dp, Color(0xFF475569), CircleShape)
        .testTag("action_open_tutorial")
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.Help,
        contentDescription = "How to Play Tutorial",
        tint = Color(0xFFFBBF24),
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
fun SnakeDPadControls(
  onDirectionClicked: (Direction) -> Unit,
  accentColor: Color,
  modifier: Modifier = Modifier
) {
  val buttonSize = 58.dp

  Column(
    modifier = modifier.fillMaxWidth(),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // UP button
    DPadButton(
      icon = Icons.Default.KeyboardArrowUp,
      contentDescription = "Move Up",
      testTag = "btn_dir_up",
      size = buttonSize,
      accentColor = accentColor,
      onClick = { onDirectionClicked(Direction.UP) }
    )

    Spacer(modifier = Modifier.height(4.dp))

    // LEFT - CENTER - RIGHT
    Row(
      horizontalArrangement = Arrangement.spacedBy(28.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      DPadButton(
        icon = Icons.Default.KeyboardArrowLeft,
        contentDescription = "Move Left",
        testTag = "btn_dir_left",
        size = buttonSize,
        accentColor = accentColor,
        onClick = { onDirectionClicked(Direction.LEFT) }
      )

      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(Color(0xFF0F172A))
          .border(1.dp, Color(0xFF334155), CircleShape)
      )

      DPadButton(
        icon = Icons.Default.KeyboardArrowRight,
        contentDescription = "Move Right",
        testTag = "btn_dir_right",
        size = buttonSize,
        accentColor = accentColor,
        onClick = { onDirectionClicked(Direction.RIGHT) }
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    // DOWN button
    DPadButton(
      icon = Icons.Default.KeyboardArrowDown,
      contentDescription = "Move Down",
      testTag = "btn_dir_down",
      size = buttonSize,
      accentColor = accentColor,
      onClick = { onDirectionClicked(Direction.DOWN) }
    )
  }
}

@Composable
private fun DPadButton(
  icon: ImageVector,
  contentDescription: String,
  testTag: String,
  size: androidx.compose.ui.unit.Dp,
  accentColor: Color,
  onClick: () -> Unit
) {
  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier = Modifier
      .size(size)
      .shadow(elevation = 8.dp, shape = CircleShape)
      .clip(CircleShape)
      .background(
        Brush.radialGradient(
          colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
        )
      )
      .border(1.5.dp, accentColor.copy(alpha = 0.6f), CircleShape)
      .clickable(
        interactionSource = interactionSource,
        indication = ripple(bounded = true, color = accentColor),
        onClick = onClick
      )
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Icon(
      imageVector = icon,
      contentDescription = contentDescription,
      tint = Color.White,
      modifier = Modifier.size(36.dp)
    )
  }
}
