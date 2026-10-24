package kaleb.snakegame.menen.model

import androidx.compose.ui.graphics.Color

/**
 * Direction enum representing snake movement on grid.
 */
enum class Direction {
  UP,
  DOWN,
  LEFT,
  RIGHT;

  fun isOpposite(other: Direction): Boolean {
    return (this == UP && other == DOWN) ||
        (this == DOWN && other == UP) ||
        (this == LEFT && other == RIGHT) ||
        (this == RIGHT && other == LEFT)
  }
}

/**
 * Difficulty levels adjusting tick speeds.
 */
enum class Difficulty(val displayName: String, val tickDelayMs: Long, val pointMultiplier: Int) {
  EASY("Easy", 220L, 1),
  MEDIUM("Medium", 150L, 2),
  HARD("Hard", 90L, 3)
}

/**
 * Coordinate position on the grid.
 */
data class Point2D(val x: Int, val y: Int)

/**
 * Types of food items appearing on board.
 */
enum class FoodType(val points: Int, val isTimed: Boolean, val durationSeconds: Int) {
  NORMAL(10, false, 0),
  BONUS_GOLDEN(50, true, 7),
  SHIELD_POWERUP(25, true, 6)
}

/**
 * Food item currently placed on the board.
 */
data class FoodItem(
  val position: Point2D,
  val type: FoodType,
  val spawnTimestamp: Long = System.currentTimeMillis(),
  val durationMs: Long = type.durationSeconds * 1000L
) {
  val remainingRatio: Float
    get() {
      if (!type.isTimed || durationMs <= 0L) return 1f
      val elapsed = System.currentTimeMillis() - spawnTimestamp
      val rem = (durationMs - elapsed).coerceAtLeast(0L)
      return rem.toFloat() / durationMs.toFloat()
    }

  val isExpired: Boolean
    get() {
      if (!type.isTimed) return false
      return (System.currentTimeMillis() - spawnTimestamp) >= durationMs
    }
}

/**
 * Customizable Snake Skins with distinct glowing aesthetics.
 */
enum class SnakeSkin(
  val skinId: String,
  val title: String,
  val primaryColor: Color,
  val secondaryColor: Color,
  val glowColor: Color,
  val eyeColor: Color,
  val unlockScoreReq: Int
) {
  EMERALD_VIPER(
    skinId = "emerald",
    title = "Emerald Viper",
    primaryColor = Color(0xFF10B981),
    secondaryColor = Color(0xFF34D399),
    glowColor = Color(0xFF059669),
    eyeColor = Color(0xFFFACC15),
    unlockScoreReq = 0
  ),
  CYBER_NEON(
    skinId = "cyber",
    title = "Cyber Neon",
    primaryColor = Color(0xFF06B6D4),
    secondaryColor = Color(0xFF22D3EE),
    glowColor = Color(0xFF0891B2),
    eyeColor = Color(0xFFF43F5E),
    unlockScoreReq = 150
  ),
  GOLDEN_DRAGON(
    skinId = "golden",
    title = "Golden Dragon",
    primaryColor = Color(0xFFF59E0B),
    secondaryColor = Color(0xFFFCD34D),
    glowColor = Color(0xFFD97706),
    eyeColor = Color(0xFFEF4444),
    unlockScoreReq = 300
  ),
  LAVA_SERPENT(
    skinId = "lava",
    title = "Lava Serpent",
    primaryColor = Color(0xFFEF4444),
    secondaryColor = Color(0xFFF87171),
    glowColor = Color(0xFFB91C1C),
    eyeColor = Color(0xFFFBBF24),
    unlockScoreReq = 500
  ),
  COSMIC_AMETHYST(
    skinId = "cosmic",
    title = "Cosmic Amethyst",
    primaryColor = Color(0xFFA855F7),
    secondaryColor = Color(0xFFC084FC),
    glowColor = Color(0xFF7E22CE),
    eyeColor = Color(0xFF38BDF8),
    unlockScoreReq = 800
  )
}
