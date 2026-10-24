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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun TutorialDialog(
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
      border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF334155)),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 4.dp)
        .testTag("tutorial_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981).copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Gamepad,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "How to Play",
              color = Color.White,
              fontSize = 20.sp,
              fontWeight = FontWeight.Black
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("tutorial_close_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Tutorial",
              tint = Color(0xFF94A3B8)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Movement Section
        TutorialSection(
          icon = Icons.Default.Gamepad,
          iconColor = Color(0xFF38BDF8),
          title = "Controls & Starting Movement",
          description = "• At the start of the game, the snake moves downwards automatically.\n• Use the on-screen D-Pad buttons or swipe directly on the board to steer Up, Down, Left, or Right.\n• Tap the Pause button anytime to pause."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Food & Bonuses
        TutorialSection(
          icon = Icons.Default.Star,
          iconColor = Color(0xFFFBBF24),
          title = "Apples & Timed Bonuses",
          description = "• Red Apple: +10 points and extends snake length.\n• Golden Star (+50 points): Limited bonus item! It disappears after 7 seconds, so be quick!"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Powerups
        TutorialSection(
          icon = Icons.Default.Security,
          iconColor = Color(0xFF00E5FF),
          title = "Invincibility Shield",
          description = "• Blue Shield: Grants 6 seconds of temporary invincibility.\n• Pass safely through your own tail or wrap across walls without losing a life!"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Lives & Revive
        TutorialSection(
          icon = Icons.Default.Favorite,
          iconColor = Color(0xFFFF2A42),
          title = "Lives & Video Ad Revive",
          description = "• You start with 2 Lives.\n• When all lives are lost, a 5-second countdown gives you the chance to watch a short video ad to instantly revive with 3 extra lives and keep your score!"
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("tutorial_got_it_btn")
        ) {
          Text("Got it! Let's Play", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }
    }
  }
}

@Composable
private fun TutorialSection(
  icon: ImageVector,
  iconColor: Color,
  title: String,
  description: String
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Color(0xFF1E293B))
      .padding(14.dp)
  ) {
    Column {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = title,
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = description,
        color = Color(0xFFCBD5E1),
        fontSize = 12.sp,
        lineHeight = 17.sp
      )
    }
  }
}
