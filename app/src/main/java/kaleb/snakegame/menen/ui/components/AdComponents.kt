package kaleb.snakegame.menen.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.OndemandVideo
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kaleb.snakegame.menen.ads.ActiveAd
import kaleb.snakegame.menen.ads.AdMobConfig
import kaleb.snakegame.menen.ads.RewardedAdPurpose
import kotlinx.coroutines.delay

/**
 * Permanent Bottom Banner Ad with AdMob Unit ID attribution and no close button.
 */
@Composable
fun PersistentBannerAdView(modifier: Modifier = Modifier) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(52.dp)
      .background(Color(0xFF0A0F1D))
      .border(1.dp, Color(0xFF1E293B))
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag("persistent_banner_ad"),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF59E0B))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "Ad",
            color = Color.Black,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black
          )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "Top Action Mobile Games 2026",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Play free now • Install today",
            color = Color(0xFF94A3B8),
            fontSize = 10.sp
          )
        }
      }

      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF2563EB))
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Text(
          text = "PLAY",
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

/**
 * App Open Ad dialog triggered on app start.
 */
@Composable
fun AppOpenAdDialog(
  onDismiss: () -> Unit
) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF060913))
        .padding(24.dp)
        .testTag("app_open_ad_dialog"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFF59E0B))
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text("AdMob App Open Ad", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
          modifier = Modifier
            .size(110.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(Color(0xFF10B981), Color(0xFF047857))
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.SmartDisplay,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(56.dp)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Welcome to Snake Game",
          color = Color.White,
          fontSize = 24.sp,
          fontWeight = FontWeight.Black,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Ad ID: ${AdMobConfig.APP_OPEN_AD_ID}",
          color = Color(0xFF64748B),
          fontSize = 10.sp,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        Button(
          onClick = onDismiss,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("app_open_continue_btn")
        ) {
          Text("Continue to Game", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
      }
    }
  }
}

/**
 * Interstitial Ad displayed when a run ends and user restarts.
 */
@Composable
fun InterstitialAdDialog(
  reason: String,
  onDismiss: () -> Unit
) {
  var secondsLeft by remember { mutableIntStateOf(3) }

  LaunchedEffect(Unit) {
    while (secondsLeft > 0) {
      delay(1000L)
      secondsLeft--
    }
  }

  Dialog(
    onDismissRequest = { if (secondsLeft == 0) onDismiss() },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF090D1A))
        .padding(24.dp)
        .testTag("interstitial_ad_dialog")
    ) {
      // Close button available when countdown reaches 0
      if (secondsLeft == 0) {
        IconButton(
          onClick = onDismiss,
          modifier = Modifier
            .align(Alignment.TopEnd)
            .clip(CircleShape)
            .background(Color(0xFF1E293B))
            .testTag("interstitial_close_btn")
        ) {
          Icon(imageVector = Icons.Default.Close, contentDescription = "Close Ad", tint = Color.White)
        }
      } else {
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .clip(CircleShape)
            .background(Color(0xFF1E293B))
            .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
          Text("Skip in ${secondsLeft}s", color = Color.White, fontSize = 12.sp)
        }
      }

      Column(
        modifier = Modifier.align(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFF3B82F6))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text("AdMob Interstitial Ad", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = reason,
          color = Color(0xFFF1F5F9),
          fontSize = 22.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF131C31)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = Color(0xFFF59E0B),
              modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Discover New Challenges!",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Unit ID: ${AdMobConfig.INTERSTITIAL_AD_ID}",
              color = Color(0xFF64748B),
              fontSize = 10.sp,
              textAlign = TextAlign.Center
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (secondsLeft == 0) {
          Button(
            onClick = onDismiss,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
          ) {
            Text("Continue Playing", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

/**
 * Unskippable 2-Video Rewarded Ad Player (2 x 10-second video ads).
 * Plays Video 1 of 2 (10s) then Video 2 of 2 (10s), followed by a Claim Reward button granting 3 lives.
 */
@Composable
fun RewardedVideoAdDialog(
  purpose: RewardedAdPurpose,
  onRewardEarned: (RewardedAdPurpose) -> Unit
) {
  var currentVideoIndex by remember { mutableIntStateOf(1) } // 1 or 2
  var secondsLeftInCurrentVideo by remember { mutableIntStateOf(10) }
  var isBothVideosCompleted by remember { mutableStateOf(false) }
  val progress = remember { Animatable(0f) }

  // Manage animation & countdown for Video 1 and Video 2 (10 seconds each)
  LaunchedEffect(currentVideoIndex) {
    if (!isBothVideosCompleted) {
      secondsLeftInCurrentVideo = 10
      progress.snapTo(0f)
      progress.animateTo(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 10000, easing = LinearEasing)
      )
    }
  }

  LaunchedEffect(currentVideoIndex) {
    if (!isBothVideosCompleted) {
      while (secondsLeftInCurrentVideo > 0) {
        delay(1000L)
        secondsLeftInCurrentVideo--
      }
      if (currentVideoIndex == 1) {
        // Transition to Video 2 of 2
        delay(300L)
        currentVideoIndex = 2
      } else {
        // Completed both videos!
        delay(300L)
        isBothVideosCompleted = true
      }
    }
  }

  Dialog(
    onDismissRequest = { /* Unskippable while video ads are active */ },
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF030611))
        .padding(20.dp)
        .testTag("rewarded_video_dialog")
    ) {
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top info bar
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFE11D48))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text("Rewarded Video Ad", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF2563EB))
                .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Text(
                text = if (!isBothVideosCompleted) "Ad $currentVideoIndex of 2" else "2 of 2 Complete",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF1E293B))
              .border(1.dp, Color(0xFF475569), RoundedCornerShape(12.dp))
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = when {
                isBothVideosCompleted -> "Completed!"
                currentVideoIndex == 1 -> "Next Ad in ${secondsLeftInCurrentVideo}s"
                else -> "Reward in ${secondsLeftInCurrentVideo}s"
              },
              color = if (isBothVideosCompleted) Color(0xFF4ADE80) else Color(0xFFFBBF24),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        // Center Video Player Simulation Screen
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1327)),
          modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .border(
              width = 1.5.dp,
              color = if (isBothVideosCompleted) Color(0xFF10B981) else Color(0xFF3B82F6),
              shape = RoundedCornerShape(20.dp)
            )
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Box(
                modifier = Modifier
                  .size(80.dp)
                  .clip(CircleShape)
                  .background(
                    Brush.radialGradient(
                      if (currentVideoIndex == 1) listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))
                      else listOf(Color(0xFF8B5CF6), Color(0xFF6D28D9))
                    )
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (isBothVideosCompleted) Icons.Default.Favorite else Icons.Default.OndemandVideo,
                  contentDescription = "Video Ad Playback",
                  tint = if (isBothVideosCompleted) Color(0xFFFF2A42) else Color.White,
                  modifier = Modifier.size(44.dp)
                )
              }

              Spacer(modifier = Modifier.height(16.dp))

              Text(
                text = when {
                  isBothVideosCompleted -> "2 of 2 Videos Watched!"
                  currentVideoIndex == 1 -> "Video 1: Galaxy Space Arena"
                  else -> "Video 2: Cyber Legends Racing"
                },
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
              )

              Spacer(modifier = Modifier.height(6.dp))

              Text(
                text = when {
                  isBothVideosCompleted -> "Reward Ready: 3 Extra Lives unlocked!"
                  currentVideoIndex == 1 -> "Watching Video 1 of 2 (10s)..."
                  else -> "Watching Video 2 of 2 (10s)... Almost done!"
                },
                color = Color(0xFF94A3B8),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
              )
            }

            // Live playback video progress bar
            if (!isBothVideosCompleted) {
              LinearProgressIndicator(
                progress = { progress.value },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(6.dp)
                  .align(Alignment.BottomCenter),
                color = Color(0xFF10B981),
                trackColor = Color(0xFF1E293B)
              )
            }
          }
        }

        // Bottom Action Area
        if (isBothVideosCompleted) {
          Button(
            onClick = { onRewardEarned(purpose) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("claim_ad_reward_btn")
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Claim +3 Lives & Resume Game",
                color = Color.Black,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
              )
            }
          }
        } else {
          // Bottom Reward status banner while playing
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(Color(0xFF131D33))
              .padding(14.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color(0xFFFF2A42),
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Reward: 3 Extra Lives / Continue Run",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Text(
                  text = "AdMob Unit: ${AdMobConfig.REWARDED_AD_ID}",
                  color = Color(0xFF64748B),
                  fontSize = 9.sp
                )
              }
            }
          }
        }
      }
    }
  }
}
