package kaleb.snakegame.menen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OndemandVideo
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kaleb.snakegame.menen.model.SnakeSkin

@Composable
fun SkinPickerDialog(
  currentSkin: SnakeSkin,
  unlockedSkinIds: Set<String>,
  highScore: Int,
  onSelectSkin: (SnakeSkin) -> Unit,
  onWatchAdToUnlock: (SnakeSkin) -> Unit,
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
        .testTag("skin_picker_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Snake Skins",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Black
          )

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(32.dp).testTag("skin_picker_close_btn")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF94A3B8))
          }
        }

        Text(
          text = "Unlock skins by reaching high scores or watching a short video ad!",
          color = Color(0xFF94A3B8),
          fontSize = 12.sp,
          modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        LazyColumn(
          verticalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(SnakeSkin.entries) { skin ->
            val isUnlocked = unlockedSkinIds.contains(skin.skinId)
            val isSelected = currentSkin.skinId == skin.skinId

            SkinItemRow(
              skin = skin,
              isUnlocked = isUnlocked,
              isSelected = isSelected,
              onEquip = { onSelectSkin(skin) },
              onWatchAd = { onWatchAdToUnlock(skin) }
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun SkinItemRow(
  skin: SnakeSkin,
  isUnlocked: Boolean,
  isSelected: Boolean,
  onEquip: () -> Unit,
  onWatchAd: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF131D33))
      .border(
        width = if (isSelected) 2.dp else 1.dp,
        color = if (isSelected) skin.primaryColor else Color(0xFF1E293B),
        shape = RoundedCornerShape(14.dp)
      )
      .clickable(enabled = isUnlocked, onClick = onEquip)
      .padding(12.dp)
      .testTag("skin_item_${skin.skinId}")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Color preview orb
        Box(
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                colors = listOf(skin.secondaryColor, skin.primaryColor)
              )
            )
            .border(2.dp, skin.glowColor, CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Text(
            text = skin.title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = if (skin.unlockScoreReq == 0) "Default Skin" else "Score req: ${skin.unlockScoreReq} pts",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
          )
        }
      }

      if (isSelected) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(skin.primaryColor.copy(alpha = 0.2f))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Check, contentDescription = null, tint = skin.primaryColor, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Equipped", color = skin.primaryColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      } else if (isUnlocked) {
        Button(
          onClick = onEquip,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(36.dp)
        ) {
          Text("Use", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      } else {
        Button(
          onClick = onWatchAd,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.height(36.dp)
        ) {
          Icon(Icons.Default.OndemandVideo, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Unlock (Ad)", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
