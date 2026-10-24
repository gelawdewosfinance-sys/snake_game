package kaleb.snakegame.menen.ui.components

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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

/**
 * Dialog displayed whenever the player loses a life, giving full control
 * to resume when ready rather than automatically losing all remaining lives.
 */
@Composable
fun LifeLostDialog(
  remainingLives: Int,
  currentScore: Int,
  onResume: () -> Unit
) {
  Dialog(
    onDismissRequest = onResume,
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
  ) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFF59E0B)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp)
        .testTag("life_lost_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Warning Badge
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF59E0B).copy(alpha = 0.2f))
            .border(1.dp, Color(0xFFF59E0B), RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text(
            text = "LIFE LOST! (ነፍስ አጥተሃል)",
            color = Color(0xFFFBBF24),
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Heart Icon Graphic
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E293B))
            .border(2.dp, Color(0xFFFF2A42).copy(alpha = 0.6f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            tint = Color(0xFFFF2A42),
            modifier = Modifier.size(42.dp)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "$remainingLives Life Remaining",
          color = Color.White,
          fontSize = 20.sp,
          fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Current Score: $currentScore pts (Saved!)",
          color = Color(0xFF38BDF8),
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1E293B))
            .padding(horizontal = 10.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Respawns in center moving DOWN with grace period",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp,
            textAlign = TextAlign.Center
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue Button
        Button(
          onClick = onResume,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("resume_run_btn")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Tap to Continue Run (ቀጥል)",
              color = Color.Black,
              fontWeight = FontWeight.Black,
              fontSize = 15.sp
            )
          }
        }
      }
    }
  }
}
