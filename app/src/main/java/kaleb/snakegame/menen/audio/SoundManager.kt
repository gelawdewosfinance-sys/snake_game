package kaleb.snakegame.menen.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Procedural synthesizer for crisp, zero-latency retro sound effects.
 * Doesn't require any external asset files or network calls.
 */
class SoundManager(private val scope: CoroutineScope) {
  var isSoundEnabled: Boolean = true

  fun playEatSound() {
    if (!isSoundEnabled) return
    scope.launch(Dispatchers.Default) {
      playTone(frequency = 520, durationMs = 60, volume = 0.8f)
      playTone(frequency = 780, durationMs = 60, volume = 0.9f)
    }
  }

  fun playBonusSound() {
    if (!isSoundEnabled) return
    scope.launch(Dispatchers.Default) {
      playTone(frequency = 587, durationMs = 70, volume = 0.9f)
      playTone(frequency = 740, durationMs = 70, volume = 0.9f)
      playTone(frequency = 880, durationMs = 100, volume = 1.0f)
    }
  }

  fun playShieldSound() {
    if (!isSoundEnabled) return
    scope.launch(Dispatchers.Default) {
      playTone(frequency = 440, durationMs = 80, volume = 0.7f)
      playTone(frequency = 660, durationMs = 80, volume = 0.8f)
      playTone(frequency = 990, durationMs = 120, volume = 0.9f)
    }
  }

  fun playCrashSound() {
    if (!isSoundEnabled) return
    scope.launch(Dispatchers.Default) {
      playTone(frequency = 220, durationMs = 120, volume = 0.8f)
      playTone(frequency = 140, durationMs = 180, volume = 0.9f)
    }
  }

  fun playReviveSound() {
    if (!isSoundEnabled) return
    scope.launch(Dispatchers.Default) {
      val notes = intArrayOf(330, 392, 493, 659)
      for (n in notes) {
        playTone(frequency = n, durationMs = 80, volume = 0.85f)
      }
    }
  }

  fun playButtonClick() {
    if (!isSoundEnabled) return
    scope.launch(Dispatchers.Default) {
      playTone(frequency = 800, durationMs = 25, volume = 0.5f)
    }
  }

  private fun playTone(frequency: Int, durationMs: Int, volume: Float) {
    try {
      val sampleRate = 22050
      val numSamples = (sampleRate * (durationMs / 1000.0)).toInt().coerceAtLeast(100)
      val generatedSnd = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val t = i.toDouble() / sampleRate
        // Smooth attack and decay envelope to prevent clicks
        val envelope = when {
          i < 100 -> i / 100.0
          i > numSamples - 100 -> (numSamples - i) / 100.0
          else -> 1.0
        }
        val angle = 2.0 * Math.PI * frequency * t
        val sample = (sin(angle) * Short.MAX_VALUE * volume * envelope).toInt()
        generatedSnd[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
      }

      val bufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      ).coerceAtLeast(numSamples * 2)

      val audioTrack = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(bufferSize)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      audioTrack.write(generatedSnd, 0, numSamples)
      audioTrack.play()

      Thread.sleep((durationMs + 20).toLong())
      audioTrack.stop()
      audioTrack.release()
    } catch (_: Exception) {
      // Ignored gracefully if audio output is unavailable
    }
  }
}
