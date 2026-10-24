package kaleb.snakegame.menen.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun GameOverDialog(
  score: Int,
  highScore: Int,
  countdown: Int,
  onWatchAdToRevive: () -> Unit,
  onPlayAgain: () -> Unit
) {
  val isNewHigh = score > 0 && score >= highScore
  val countdownRatio = (countdown.toFloat() / 5f).coerceIn(0f, 1f)

  Dialog(
    onDismissRequest = { /* Require deliberate button choice */ },
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
  ) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE11D48).copy(alpha = 0.7f)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .testTag("game_over_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Game Over Banner
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE11D48).copy(alpha = 0.2f))
            .border(1.dp, Color(0xFFE11D48), RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "GAME OVER",
            color = Color(0xFFFF4D6D),
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Countdown Timer Dial for Ad Revive
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier.size(76.dp)
        ) {
          CircularProgressIndicator(
            progress = { countdownRatio },
            modifier = Modifier.size(76.dp),
            color = if (countdown > 2) Color(0xFFFBBF24) else Color(0xFFEF4444),
            trackColor = Color(0xFF1E293B),
            strokeWidth = 6.dp
          )
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "$countdown",
              color = Color.White,
              fontSize = 26.sp,
              fontWeight = FontWeight.Black
            )
            Text(
              text = "sec",
              color = Color(0xFF94A3B8),
              fontSize = 9.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Score summary
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("SCORE", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("$score", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
          }

          Box(
            modifier = Modifier
              .width(1.dp)
              .height(44.dp)
              .background(Color(0xFF334155))
          )

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("BEST", color = Color(0xFFF59E0B), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("$highScore", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
          }
        }

        if (isNewHigh) {
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF854D0E).copy(alpha = 0.3f))
              .padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("NEW HIGH SCORE!", color = Color(0xFFFBBF24), fontSize = 11.sp, fontWeight = FontWeight.Black)
          }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Revive / Continue Run Button (Always enabled to allow continuing anytime)
        Button(
          onClick = onWatchAdToRevive,
          enabled = true,
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF10B981)
          ),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("revive_ad_button")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.OndemandVideo,
              contentDescription = null,
              tint = Color.Black,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Continue Run (+3 Lives)",
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
              )
              Text(
                text = "Watch 2 short 10-second video ads to keep your score",
                color = Color(0xFF064E3B),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Play Again Button (starts new game, shows interstitial)
        OutlinedButton(
          onClick = onPlayAgain,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF475569)),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("play_again_button")
        ) {
          Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Play Again", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }
    }
  }
}
