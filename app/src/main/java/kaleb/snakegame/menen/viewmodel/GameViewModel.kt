package kaleb.snakegame.menen.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kaleb.snakegame.menen.ads.ActiveAd
import kaleb.snakegame.menen.ads.RewardedAdPurpose
import kaleb.snakegame.menen.audio.SoundManager
import kaleb.snakegame.menen.audio.VibrationManager
import kaleb.snakegame.menen.model.Difficulty
import kaleb.snakegame.menen.model.Direction
import kaleb.snakegame.menen.model.FoodItem
import kaleb.snakegame.menen.model.FoodType
import kaleb.snakegame.menen.model.Point2D
import kaleb.snakegame.menen.model.SnakeSkin
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameUiState(
  val gridWidth: Int = 18,
  val gridHeight: Int = 22,
  val snake: List<Point2D> = emptyList(),
  val currentDirection: Direction = Direction.DOWN,
  val nextDirection: Direction = Direction.DOWN,
  val foods: List<FoodItem> = emptyList(),
  val score: Int = 0,
  val highScore: Int = 0,
  val lives: Int = 2,
  val isGameOver: Boolean = false,
  val isPaused: Boolean = false,
  val showLifeLostDialog: Boolean = false,
  val difficulty: Difficulty = Difficulty.MEDIUM,
  val selectedSkin: SnakeSkin = SnakeSkin.EMERALD_VIPER,
  val unlockedSkinIds: Set<String> = setOf(SnakeSkin.EMERALD_VIPER.skinId),
  val isShieldActive: Boolean = false,
  val shieldRemainingSeconds: Float = 0f,
  val invulnerabilityTicks: Int = 0,
  val gameOverCountdown: Int = 5,
  val activeAd: ActiveAd = ActiveAd.None,
  val showTutorial: Boolean = false,
  val showSkinPicker: Boolean = false,
  val isSoundEnabled: Boolean = true,
  val isVibrationEnabled: Boolean = true
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

  private val prefs = application.getSharedPreferences("snake_game_prefs", Context.MODE_PRIVATE)
  val soundManager = SoundManager(viewModelScope)
  val vibrationManager = VibrationManager(application)

  private val _uiState = MutableStateFlow(GameUiState())
  val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

  private var gameLoopJob: Job? = null
  private var bonusSpawnerJob: Job? = null
  private var countdownJob: Job? = null
  private var foodEatenCounter = 0

  init {
    loadSavedData()
    resetGame(startPaused = false)
    // Trigger App Open Ad on launch per specification
    _uiState.update { it.copy(activeAd = ActiveAd.AppOpen("Welcome to Snake Game!")) }
  }

  private fun loadSavedData() {
    val savedHighScore = prefs.getInt("high_score", 0)
    val savedSkinId = prefs.getString("selected_skin", SnakeSkin.EMERALD_VIPER.skinId) ?: SnakeSkin.EMERALD_VIPER.skinId
    val savedUnlocked = prefs.getStringSet("unlocked_skins", setOf(SnakeSkin.EMERALD_VIPER.skinId)) ?: setOf(SnakeSkin.EMERALD_VIPER.skinId)
    val sound = prefs.getBoolean("sound_enabled", true)
    val vibration = prefs.getBoolean("vibration_enabled", true)

    val currentSkin = SnakeSkin.entries.find { it.skinId == savedSkinId } ?: SnakeSkin.EMERALD_VIPER
    soundManager.isSoundEnabled = sound
    vibrationManager.isVibrationEnabled = vibration

    _uiState.update {
      it.copy(
        highScore = savedHighScore,
        selectedSkin = currentSkin,
        unlockedSkinIds = savedUnlocked,
        isSoundEnabled = sound,
        isVibrationEnabled = vibration
      )
    }
  }

  fun dismissAd() {
    val previousAd = _uiState.value.activeAd
    _uiState.update { it.copy(activeAd = ActiveAd.None) }
    // If was App Open ad or interstitial, ensure game can run smoothly
    if (previousAd is ActiveAd.AppOpen && !_uiState.value.isGameOver) {
      startGameLoop()
    }
  }

  fun resetGame(startPaused: Boolean = false) {
    gameLoopJob?.cancel()
    bonusSpawnerJob?.cancel()
    countdownJob?.cancel()

    val width = _uiState.value.gridWidth
    val height = _uiState.value.gridHeight

    // Snake starts moving downwards from top-center
    val startX = width / 2
    val startY = 5
    val initialSnake = listOf(
      Point2D(startX, startY),
      Point2D(startX, startY - 1),
      Point2D(startX, startY - 2)
    )

    foodEatenCounter = 0

    val initialFood = spawnFood(initialSnake, emptyList(), FoodType.NORMAL)

    _uiState.update {
      it.copy(
        snake = initialSnake,
        currentDirection = Direction.DOWN,
        nextDirection = Direction.DOWN,
        foods = listOf(initialFood),
        score = 0,
        lives = 2,
        isGameOver = false,
        isPaused = startPaused,
        isShieldActive = false,
        shieldRemainingSeconds = 0f,
        invulnerabilityTicks = 8,
        gameOverCountdown = 5
      )
    }

    if (!startPaused && _uiState.value.activeAd == ActiveAd.None) {
      startGameLoop()
      startBonusSpawner()
    }
  }

  fun changeDirection(newDir: Direction) {
    if (_uiState.value.isGameOver || _uiState.value.isPaused || _uiState.value.activeAd !is ActiveAd.None) return
    val current = _uiState.value.currentDirection
    if (!current.isOpposite(newDir)) {
      _uiState.update { it.copy(nextDirection = newDir) }
      vibrationManager.vibrateClick()
    }
  }

  fun togglePause() {
    if (_uiState.value.isGameOver) return
    val willPause = !_uiState.value.isPaused
    _uiState.update { it.copy(isPaused = willPause) }
    soundManager.playButtonClick()
    if (willPause) {
      gameLoopJob?.cancel()
    } else {
      startGameLoop()
    }
  }

  fun setDifficulty(difficulty: Difficulty) {
    _uiState.update { it.copy(difficulty = difficulty) }
    soundManager.playButtonClick()
  }

  fun toggleSound() {
    val newState = !_uiState.value.isSoundEnabled
    _uiState.update { it.copy(isSoundEnabled = newState) }
    soundManager.isSoundEnabled = newState
    prefs.edit().putBoolean("sound_enabled", newState).apply()
    if (newState) soundManager.playButtonClick()
  }

  fun toggleVibration() {
    val newState = !_uiState.value.isVibrationEnabled
    _uiState.update { it.copy(isVibrationEnabled = newState) }
    vibrationManager.isVibrationEnabled = newState
    prefs.edit().putBoolean("vibration_enabled", newState).apply()
    if (newState) vibrationManager.vibrateEat()
  }

  fun openTutorial() {
    _uiState.update { it.copy(showTutorial = true, isPaused = true) }
    gameLoopJob?.cancel()
  }

  fun closeTutorial() {
    _uiState.update { it.copy(showTutorial = false, isPaused = false) }
    if (!_uiState.value.isGameOver && _uiState.value.activeAd is ActiveAd.None) {
      startGameLoop()
    }
  }

  fun openSkinPicker() {
    _uiState.update { it.copy(showSkinPicker = true, isPaused = true) }
    gameLoopJob?.cancel()
  }

  fun closeSkinPicker() {
    _uiState.update { it.copy(showSkinPicker = false, isPaused = false) }
    if (!_uiState.value.isGameOver && _uiState.value.activeAd is ActiveAd.None) {
      startGameLoop()
    }
  }

  fun selectSkin(skin: SnakeSkin) {
    val unlocked = _uiState.value.unlockedSkinIds
    if (unlocked.contains(skin.skinId)) {
      _uiState.update { it.copy(selectedSkin = skin) }
      prefs.edit().putString("selected_skin", skin.skinId).apply()
      soundManager.playButtonClick()
    }
  }

  fun watchAdToUnlockSkin(skin: SnakeSkin) {
    // Show unskippable Rewarded Video Ad (2 x 5-second videos)
    _uiState.update {
      it.copy(
        activeAd = ActiveAd.RewardedVideo(
          purpose = RewardedAdPurpose.UnlockSkin(skin.skinId)
        ),
        isPaused = true
      )
    }
    gameLoopJob?.cancel()
  }

  fun watchAdToRevive() {
    countdownJob?.cancel()
    _uiState.update {
      it.copy(
        activeAd = ActiveAd.RewardedVideo(
          purpose = RewardedAdPurpose.ReviveThreeLives
        ),
        isPaused = true
      )
    }
  }

  fun onRewardedAdCompleted(purpose: RewardedAdPurpose) {
    when (purpose) {
      is RewardedAdPurpose.ReviveThreeLives -> {
        // Player receives 3 lives and resumes playing with current score!
        soundManager.playReviveSound()
        vibrationManager.vibrateBonus()

        val width = _uiState.value.gridWidth
        val startX = width / 2
        val startY = 6
        val revivedSnake = listOf(
          Point2D(startX, startY),
          Point2D(startX, startY - 1),
          Point2D(startX, startY - 2)
        )

        _uiState.update {
          it.copy(
            lives = 3,
            isGameOver = false,
            isPaused = false,
            activeAd = ActiveAd.None,
            snake = revivedSnake,
            currentDirection = Direction.DOWN,
            nextDirection = Direction.DOWN,
            invulnerabilityTicks = 12
          )
        }
        startGameLoop()
      }
      is RewardedAdPurpose.UnlockSkin -> {
        soundManager.playBonusSound()
        val updated = _uiState.value.unlockedSkinIds + purpose.skinId
        val chosenSkin = SnakeSkin.entries.find { it.skinId == purpose.skinId } ?: _uiState.value.selectedSkin
        _uiState.update {
          it.copy(
            unlockedSkinIds = updated,
            selectedSkin = chosenSkin,
            activeAd = ActiveAd.None
          )
        }
        prefs.edit().putStringSet("unlocked_skins", updated).apply()
        prefs.edit().putString("selected_skin", chosenSkin.skinId).apply()
      }
    }
  }

  fun playAgainAfterGameOver() {
    countdownJob?.cancel()
    // Show Interstitial ad on game over as requested
    _uiState.update {
      it.copy(activeAd = ActiveAd.Interstitial("Game Over - Great Run!"))
    }
    resetGame(startPaused = false)
  }

  private fun startGameLoop() {
    gameLoopJob?.cancel()
    gameLoopJob = viewModelScope.launch {
      while (isActive && !_uiState.value.isGameOver && !_uiState.value.isPaused && _uiState.value.activeAd is ActiveAd.None) {
        val delayMs = _uiState.value.difficulty.tickDelayMs
        delay(delayMs)
        tick()
      }
    }
  }

  private fun startBonusSpawner() {
    bonusSpawnerJob?.cancel()
    bonusSpawnerJob = viewModelScope.launch {
      while (isActive) {
        delay(18000L) // Every 18-20 seconds check to spawn bonus food
        if (!_uiState.value.isGameOver && !_uiState.value.isPaused) {
          spawnBonusOrShield()
        }
      }
    }
  }

  private fun spawnBonusOrShield() {
    val currentFoods = _uiState.value.foods.filterNot { it.isExpired }
    val hasSpecial = currentFoods.any { it.type != FoodType.NORMAL }
    if (!hasSpecial) {
      val type = if (Random.nextBoolean()) FoodType.BONUS_GOLDEN else FoodType.SHIELD_POWERUP
      val food = spawnFood(_uiState.value.snake, currentFoods, type)
      _uiState.update { it.copy(foods = currentFoods + food) }
    }
  }

  private fun tick() {
    val state = _uiState.value
    if (state.isGameOver || state.isPaused) return

    val dir = state.nextDirection
    val head = state.snake.firstOrNull() ?: return

    // Calculate new head
    var nextX = head.x
    var nextY = head.y
    when (dir) {
      Direction.UP -> nextY -= 1
      Direction.DOWN -> nextY += 1
      Direction.LEFT -> nextX -= 1
      Direction.RIGHT -> nextX += 1
    }

    val isShielded = state.isShieldActive
    val isInvulnerable = state.invulnerabilityTicks > 0

    // Check Wall Collisions
    val hitWall = nextX < 0 || nextX >= state.gridWidth || nextY < 0 || nextY >= state.gridHeight

    if (hitWall) {
      if (isShielded) {
        // Wrap around borders safely when shield is active!
        nextX = (nextX + state.gridWidth) % state.gridWidth
        nextY = (nextY + state.gridHeight) % state.gridHeight
      } else if (!isInvulnerable) {
        handleCrash()
        return
      } else {
        // Prevent moving out of bounds during invulnerability ticks
        nextX = nextX.coerceIn(0, state.gridWidth - 1)
        nextY = nextY.coerceIn(0, state.gridHeight - 1)
      }
    }

    val nextHead = Point2D(nextX, nextY)

    // Check Self Collisions
    val hitSelf = state.snake.contains(nextHead)
    if (hitSelf && !isShielded && !isInvulnerable) {
      handleCrash()
      return
    }

    // Check Food Consumption
    val cleanFoods = state.foods.filterNot { it.isExpired }
    val eatenFood = cleanFoods.find { it.position == nextHead }

    val newSnake = mutableListOf(nextHead)
    var newScore = state.score
    var newFoods = cleanFoods.toMutableList()
    var shieldDuration = state.shieldRemainingSeconds
    var shieldActive = state.isShieldActive

    if (eatenFood != null) {
      newFoods.remove(eatenFood)
      foodEatenCounter++
      val pts = eatenFood.type.points * state.difficulty.pointMultiplier
      newScore += pts

      // Sound and vibration
      when (eatenFood.type) {
        FoodType.NORMAL -> {
          soundManager.playEatSound()
          vibrationManager.vibrateEat()
        }
        FoodType.BONUS_GOLDEN -> {
          soundManager.playBonusSound()
          vibrationManager.vibrateBonus()
        }
        FoodType.SHIELD_POWERUP -> {
          soundManager.playShieldSound()
          vibrationManager.vibrateBonus()
          shieldActive = true
          shieldDuration = 6.0f
        }
      }

      // Snake grows
      newSnake.addAll(state.snake)

      // Always ensure normal food exists
      if (newFoods.none { it.type == FoodType.NORMAL }) {
        newFoods.add(spawnFood(newSnake, newFoods, FoodType.NORMAL))
      }

      // Check skin unlocking based on score
      checkUnlockSkins(newScore)
    } else {
      // Regular move without eating: keep length
      newSnake.addAll(state.snake.dropLast(1))
    }

    // Update Shield Timer
    if (shieldActive) {
      val deltaSec = state.difficulty.tickDelayMs / 1000f
      shieldDuration -= deltaSec
      if (shieldDuration <= 0f) {
        shieldActive = false
        shieldDuration = 0f
      }
    }

    // High Score update
    val currentHigh = maxOf(state.highScore, newScore)
    if (currentHigh > state.highScore) {
      prefs.edit().putInt("high_score", currentHigh).apply()
    }

    val nextInvulTicks = (state.invulnerabilityTicks - 1).coerceAtLeast(0)

    _uiState.update {
      it.copy(
        snake = newSnake,
        currentDirection = dir,
        foods = newFoods,
        score = newScore,
        highScore = currentHigh,
        isShieldActive = shieldActive,
        shieldRemainingSeconds = shieldDuration,
        invulnerabilityTicks = nextInvulTicks
      )
    }
  }

  private fun handleCrash() {
    soundManager.playCrashSound()
    vibrationManager.vibrateCrash()

    val currentLives = _uiState.value.lives
    if (currentLives > 1) {
      // Pause immediately so player is in control and lives don't slip away without consent!
      gameLoopJob?.cancel()
      val startX = _uiState.value.gridWidth / 2
      val startY = 6
      val resetSnake = listOf(
        Point2D(startX, startY),
        Point2D(startX, startY - 1),
        Point2D(startX, startY - 2)
      )
      _uiState.update {
        it.copy(
          lives = currentLives - 1,
          snake = resetSnake,
          currentDirection = Direction.DOWN,
          nextDirection = Direction.DOWN,
          isPaused = true,
          showLifeLostDialog = true,
          invulnerabilityTicks = 14
        )
      }
    } else {
      // 0 lives left -> Game Over!
      gameLoopJob?.cancel()
      _uiState.update {
        it.copy(
          lives = 0,
          isGameOver = true,
          showLifeLostDialog = false,
          gameOverCountdown = 5
        )
      }
      startGameOverCountdown()
    }
  }

  fun resumeAfterLifeLost() {
    _uiState.update {
      it.copy(
        showLifeLostDialog = false,
        isPaused = false,
        invulnerabilityTicks = 14
      )
    }
    soundManager.playButtonClick()
    startGameLoop()
  }

  private fun startGameOverCountdown() {
    countdownJob?.cancel()
    countdownJob = viewModelScope.launch {
      for (second in 5 downTo 0) {
        _uiState.update { it.copy(gameOverCountdown = second) }
        delay(1000L)
      }
    }
  }

  private fun checkUnlockSkins(currentScore: Int) {
    val unlocked = _uiState.value.unlockedSkinIds.toMutableSet()
    var hasNew = false
    SnakeSkin.entries.forEach { skin ->
      if (!unlocked.contains(skin.skinId) && currentScore >= skin.unlockScoreReq) {
        unlocked.add(skin.skinId)
        hasNew = true
      }
    }
    if (hasNew) {
      _uiState.update { it.copy(unlockedSkinIds = unlocked) }
      prefs.edit().putStringSet("unlocked_skins", unlocked).apply()
    }
  }

  private fun spawnFood(snake: List<Point2D>, existingFoods: List<FoodItem>, type: FoodType): FoodItem {
    val width = _uiState.value.gridWidth
    val height = _uiState.value.gridHeight
    val occupied = (snake + existingFoods.map { it.position }).toSet()

    var target: Point2D
    var attempts = 0
    do {
      target = Point2D(Random.nextInt(width), Random.nextInt(height))
      attempts++
    } while (occupied.contains(target) && attempts < 200)

    return FoodItem(position = target, type = type)
  }
}
