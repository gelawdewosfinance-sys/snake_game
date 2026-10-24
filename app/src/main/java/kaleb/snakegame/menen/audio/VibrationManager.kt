package kaleb.snakegame.menen.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Handles tactile haptic feedback for game events (eating, dying, powerup, buttons).
 */
class VibrationManager(context: Context) {
  var isVibrationEnabled: Boolean = true

  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  fun vibrateEat() {
    if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(35L, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(35L)
      }
    } catch (_: Exception) {}
  }

  fun vibrateBonus() {
    if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val pattern = longArrayOf(0, 40, 50, 70)
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(longArrayOf(0, 40, 50, 70), -1)
      }
    } catch (_: Exception) {}
  }

  fun vibrateCrash() {
    if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val pattern = longArrayOf(0, 90, 60, 160)
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(longArrayOf(0, 90, 60, 160), -1)
      }
    } catch (_: Exception) {}
  }

  fun vibrateClick() {
    if (!isVibrationEnabled || vibrator == null || !vibrator.hasVibrator()) return
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(18L, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(18L)
      }
    } catch (_: Exception) {}
  }
}
