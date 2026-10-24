package kaleb.snakegame.menen.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaleb.snakegame.menen.model.SnakeSkin

@Composable
fun SnakeHud(
  score: Int,
  highScore: Int,
  lives: Int,
  maxLives: Int = 3,
  isShieldActive: Boolean,
  shieldSeconds: Float,
  skin: SnakeSkin,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 4.dp, vertical = 2.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Current Score Box
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF0F172A))
        .border(1.dp, skin.glowColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        .padding(horizontal = 14.dp, vertical = 6.dp)
        .testTag("score_display")
    ) {
      Column(horizontalAlignment = Alignment.Start) {
        Text(
          text = "SCORE",
          color = Color(0xFF94A3B8),
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.sp
        )
        Text(
          text = "$score",
          color = Color.White,
          fontSize = 20.sp,
          fontWeight = FontWeight.Black
        )
      }
    }

    // Shield status if active
    AnimatedVisibility(
      visible = isShieldActive,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF00363A))
          .border(1.dp, Color(0xFF00E5FF), RoundedCornerShape(12.dp))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.Shield,
          contentDescription = "Shield Active",
          tint = Color(0xFF00E5FF),
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${String.format("%.1f", shieldSeconds)}s",
          color = Color(0xFF00E5FF),
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Lives hearts
    Row(
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF0F172A))
        .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
        .padding(horizontal = 10.dp, vertical = 8.dp)
        .testTag("lives_container"),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      for (i in 1..maxLives) {
        val hasLife = i <= lives
        Icon(
          imageVector = if (hasLife) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
          contentDescription = if (hasLife) "Active Life" else "Empty Life",
          tint = if (hasLife) Color(0xFFFF2A42) else Color(0xFF475569),
          modifier = Modifier.size(18.dp)
        )
      }
    }

    // High Score Box
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF0F172A))
        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        .padding(horizontal = 14.dp, vertical = 6.dp)
        .testTag("high_score_display")
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Star,
          contentDescription = "High Score",
          tint = Color(0xFFF59E0B),
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column(horizontalAlignment = Alignment.Start) {
          Text(
            text = "BEST",
            color = Color(0xFFF59E0B),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Text(
            text = "$highScore",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black
          )
        }
      }
    }
  }
}
