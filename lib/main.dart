import 'dart:async';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:google_mobile_ads/google_mobile_ads.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'models/snake_models.dart';
import 'services/ad_service.dart';
import 'services/vibration_service.dart';
import 'widgets/dialogs.dart';
import 'widgets/dpad_controls.dart';
import 'widgets/game_board.dart';
import 'widgets/game_hud.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await AdService.initialize();
  runApp(const SnakeGameApp());
}

class SnakeGameApp extends StatelessWidget {
  const SnakeGameApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Snake Game',
      debugShowCheckedModeBanner: false,
      theme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: const Color(0xFF060913),
        colorScheme: const ColorScheme.dark(
          primary: Color(0xFF10B981),
          secondary: Color(0xFF06B6D4),
          surface: Color(0xFF0F172A),
        ),
      ),
      home: const SnakeGameScreen(),
    );
  }
}

class SnakeGameScreen extends StatefulWidget {
  const SnakeGameScreen({super.key});

  @override
  State<SnakeGameScreen> createState() => _SnakeGameScreenState();
}

class _SnakeGameScreenState extends State<SnakeGameScreen> with WidgetsBindingObserver {
  static const int gridWidth = 18;
  static const int gridHeight = 22;

  List<Point2D> snake = [];
  Direction currentDirection = Direction.down;
  Direction nextDirection = Direction.down;
  List<FoodItem> foods = [];
  int score = 0;
  int highScore = 0;
  int lives = 2; // Default 2 lives
  bool isGameOver = false;
  bool isPaused = false;
  Difficulty difficulty = Difficulty.medium;
  SnakeSkin selectedSkin = SnakeSkin.emerald;
  Set<String> unlockedSkinIds = {SnakeSkin.emerald.id};

  bool isShieldActive = false;
  double shieldRemainingSeconds = 0.0;
  int invulnerabilityTicks = 0;

  bool isSoundEnabled = true;
  bool isVibrationEnabled = true;

  Timer? _gameLoopTimer;
  Timer? _bonusSpawnerTimer;
  Timer? _shieldTimer;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _loadPreferences();
    _resetGame();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _gameLoopTimer?.cancel();
    _bonusSpawnerTimer?.cancel();
    _shieldTimer?.cancel();
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.paused && !isGameOver && !isPaused) {
      _togglePause();
    }
  }

  Future<void> _loadPreferences() async {
    final prefs = await SharedPreferences.getInstance();
    setState(() {
      highScore = prefs.getInt('high_score') ?? 0;
      final savedSkinId = prefs.getString('selected_skin') ?? SnakeSkin.emerald.id;
      selectedSkin = SnakeSkin.values.firstWhere((s) => s.id == savedSkinId, orElse: () => SnakeSkin.emerald);
      final unlockedList = prefs.getStringList('unlocked_skins') ?? [SnakeSkin.emerald.id];
      unlockedSkinIds = unlockedList.toSet();
      isSoundEnabled = prefs.getBool('sound_enabled') ?? true;
      isVibrationEnabled = prefs.getBool('vibration_enabled') ?? true;
      VibrationService.isEnabled = isVibrationEnabled;
    });
  }

  void _resetGame({bool startPaused = false}) {
    _gameLoopTimer?.cancel();
    _bonusSpawnerTimer?.cancel();
    _shieldTimer?.cancel();

    const startX = gridWidth ~/ 2;
    const startY = 5;

    // Starts moving downwards from top to bottom
    snake = [
      const Point2D(startX, startY),
      const Point2D(startX, startY - 1),
      const Point2D(startX, startY - 2),
    ];
    currentDirection = Direction.down;
    nextDirection = Direction.down;
    score = 0;
    lives = 2; // Default 2 lives
    isGameOver = false;
    isPaused = startPaused;
    isShieldActive = false;
    shieldRemainingSeconds = 0.0;
    invulnerabilityTicks = 8;

    foods = [_spawnFood(FoodType.normal)];

    setState(() {});

    if (!startPaused) {
      _startGameLoop();
      _startBonusSpawner();
    }
  }

  void _startGameLoop() {
    _gameLoopTimer?.cancel();
    _gameLoopTimer = Timer.periodic(difficulty.tickDelay, (timer) {
      if (!isGameOver && !isPaused) {
        _tick();
      }
    });
  }

  void _startBonusSpawner() {
    _bonusSpawnerTimer?.cancel();
    _bonusSpawnerTimer = Timer.periodic(const Duration(seconds: 18), (timer) {
      if (!isGameOver && !isPaused) {
        final hasSpecial = foods.any((f) => f.type != FoodType.normal && !f.isExpired);
        if (!hasSpecial) {
          final type = Random().nextBool() ? FoodType.bonusGolden : FoodType.shieldPowerup;
          setState(() {
            foods.add(_spawnFood(type));
          });
        }
      }
    });
  }

  FoodItem _spawnFood(FoodType type) {
    final occupied = (snake + foods.map((f) => f.position)).toSet();
    Point2D target;
    int attempts = 0;
    do {
      target = Point2D(Random().nextInt(gridWidth), Random().nextInt(gridHeight));
      attempts++;
    } while (occupied.contains(target) && attempts < 200);

    return FoodItem(position: target, type: type);
  }

  void _tick() {
    currentDirection = nextDirection;
    final head = snake.first;

    int nextX = head.x;
    int nextY = head.y;

    switch (currentDirection) {
      case Direction.up:
        nextY -= 1;
        break;
      case Direction.down:
        nextY += 1;
        break;
      case Direction.left:
        nextX -= 1;
        break;
      case Direction.right:
        nextX += 1;
        break;
    }

    final hitWall = nextX < 0 || nextX >= gridWidth || nextY < 0 || nextY >= gridHeight;

    if (hitWall) {
      if (isShieldActive) {
        nextX = (nextX + gridWidth) % gridWidth;
        nextY = (nextY + gridHeight) % gridHeight;
      } else if (invulnerabilityTicks <= 0) {
        _handleCrash();
        return;
      } else {
        nextX = nextX.clamp(0, gridWidth - 1);
        nextY = nextY.clamp(0, gridHeight - 1);
      }
    }

    final nextHead = Point2D(nextX, nextY);

    if (snake.contains(nextHead) && !isShieldActive && invulnerabilityTicks <= 0) {
      _handleCrash();
      return;
    }

    // Clean expired timed foods
    foods.removeWhere((f) => f.isExpired);

    FoodItem? eatenFood;
    for (final f in foods) {
      if (f.position == nextHead) {
        eatenFood = f;
        break;
      }
    }

    final newSnake = [nextHead];

    if (eatenFood != null) {
      foods.remove(eatenFood);
      score += eatenFood.type.points * difficulty.multiplier;

      switch (eatenFood.type) {
        case FoodType.normal:
          VibrationService.eat();
          break;
        case FoodType.bonusGolden:
          VibrationService.bonus();
          break;
        case FoodType.shieldPowerup:
          VibrationService.bonus();
          isShieldActive = true;
          shieldRemainingSeconds = 6.0;
          _startShieldTimer();
          break;
      }

      newSnake.addAll(snake);

      if (!foods.any((f) => f.type == FoodType.normal)) {
        foods.add(_spawnFood(FoodType.normal));
      }

      if (score > highScore) {
        highScore = score;
        SharedPreferences.getInstance().then((prefs) => prefs.setInt('high_score', highScore));
      }
    } else {
      newSnake.addAll(snake.sublist(0, snake.length - 1));
    }

    if (invulnerabilityTicks > 0) {
      invulnerabilityTicks--;
    }

    setState(() {
      snake = newSnake;
    });
  }

  void _startShieldTimer() {
    _shieldTimer?.cancel();
    _shieldTimer = Timer.periodic(const Duration(milliseconds: 100), (timer) {
      if (!isPaused && isShieldActive) {
        setState(() {
          shieldRemainingSeconds -= 0.1;
          if (shieldRemainingSeconds <= 0) {
            isShieldActive = false;
            shieldRemainingSeconds = 0;
            timer.cancel();
          }
        });
      }
    });
  }

  void _handleCrash() {
    VibrationService.crash();
    if (lives > 1) {
      // Pause immediately so player is in control and lives don't slip away without consent!
      _gameLoopTimer?.cancel();
      setState(() {
        lives--;
        const startX = gridWidth ~/ 2;
        const startY = 6;
        snake = [
          const Point2D(startX, startY),
          const Point2D(startX, startY - 1),
          const Point2D(startX, startY - 2),
        ];
        currentDirection = Direction.down;
        nextDirection = Direction.down;
        isPaused = true;
        invulnerabilityTicks = 14;
      });
      _showLifeLostDialog();
    } else {
      // 0 lives left -> Game Over!
      _gameLoopTimer?.cancel();
      setState(() {
        lives = 0;
        isGameOver = true;
      });
      _showGameOverDialog();
    }
  }

  void _showLifeLostDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (ctx) => LifeLostDialog(
        remainingLives: lives,
        currentScore: score,
        onResume: () {
          Navigator.of(ctx).pop();
          setState(() {
            isPaused = false;
            invulnerabilityTicks = 14;
          });
          _startGameLoop();
        },
      ),
    );
  }

  void _changeDirection(Direction newDir) {
    if (isGameOver || isPaused) return;
    if (!currentDirection.isOpposite(newDir)) {
      nextDirection = newDir;
      VibrationService.click();
    }
  }

  void _togglePause() {
    if (isGameOver) return;
    setState(() {
      isPaused = !isPaused;
    });
    VibrationService.click();
    if (!isPaused) {
      _startGameLoop();
    }
  }

  void _cycleDifficulty() {
    setState(() {
      difficulty = difficulty == Difficulty.easy
          ? Difficulty.medium
          : difficulty == Difficulty.medium
              ? Difficulty.hard
              : Difficulty.easy;
    });
    VibrationService.click();
    if (!isPaused && !isGameOver) {
      _startGameLoop();
    }
  }

  void _showGameOverDialog() {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (ctx) => GameOverDialog(
        score: score,
        highScore: highScore,
        onWatchAdToRevive: () {
          Navigator.of(ctx).pop();
          _showRewardedVideoAd();
        },
        onPlayAgain: () {
          Navigator.of(ctx).pop();
          AdService.showInterstitial(onDismissed: () {
            _resetGame();
          });
        },
      ),
    );
  }

  void _showRewardedVideoAd() {
    // 2-Video Rewarded Ad popup (Ad 1 of 2, then Ad 2 of 2)
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (ctx) => RewardedVideoAdDialog(
        onRewardEarned: () {
          Navigator.of(ctx).pop();
          // Player receives 3 lives and continues the current score run!
          setState(() {
            lives = 3;
            isGameOver = false;
            isPaused = false;
            const startX = gridWidth ~/ 2;
            const startY = 6;
            snake = [
              const Point2D(startX, startY),
              const Point2D(startX, startY - 1),
              const Point2D(startX, startY - 2),
            ];
            currentDirection = Direction.down;
            nextDirection = Direction.down;
            invulnerabilityTicks = 12;
          });
          _startGameLoop();
        },
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: SafeArea(
        child: Column(
          children: [
            // Header Banner
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 6.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: [
                      Container(
                        width: 10,
                        height: 10,
                        decoration: BoxDecoration(color: selectedSkin.primaryColor, shape: BoxShape.circle),
                      ),
                      const SizedBox(width: 8),
                      const Text(
                        'SNAKE GAME',
                        style: TextStyle(color: Colors.white, fontSize: 17, fontWeight: FontWeight.black, letterSpacing: 2),
                      ),
                    ],
                  ),
                  Text(
                    selectedSkin.title,
                    style: TextStyle(color: selectedSkin.secondaryColor, fontSize: 11, fontWeight: FontWeight.bold),
                  ),
                ],
              ),
            ),

            // Top HUD
            SnakeHud(
              score: score,
              highScore: highScore,
              lives: lives,
              isShieldActive: isShieldActive,
              shieldSeconds: shieldRemainingSeconds,
              skin: selectedSkin,
            ),

            // Action Bar
            SnakeActionBar(
              isPaused: isPaused,
              difficulty: difficulty,
              isSoundEnabled: isSoundEnabled,
              isVibrationEnabled: isVibrationEnabled,
              selectedSkin: selectedSkin,
              onTogglePause: _togglePause,
              onCycleDifficulty: _cycleDifficulty,
              onToggleSound: () {
                setState(() => isSoundEnabled = !isSoundEnabled);
                SharedPreferences.getInstance().then((p) => p.setBool('sound_enabled', isSoundEnabled));
              },
              onToggleVibration: () {
                setState(() {
                  isVibrationEnabled = !isVibrationEnabled;
                  VibrationService.isEnabled = isVibrationEnabled;
                });
                SharedPreferences.getInstance().then((p) => p.setBool('vibration_enabled', isVibrationEnabled));
              },
              onOpenSkins: () {
                showDialog(
                  context: context,
                  builder: (ctx) => SkinPickerDialog(
                    currentSkin: selectedSkin,
                    unlockedSkinIds: unlockedSkinIds,
                    onSelectSkin: (skin) {
                      setState(() => selectedSkin = skin);
                      SharedPreferences.getInstance().then((p) => p.setString('selected_skin', skin.id));
                    },
                    onWatchAdToUnlock: (skin) {
                      Navigator.of(ctx).pop();
                      _showRewardedVideoAd();
                      setState(() {
                        unlockedSkinIds.add(skin.id);
                        selectedSkin = skin;
                      });
                      SharedPreferences.getInstance().then((p) => p.setStringList('unlocked_skins', unlockedSkinIds.toList()));
                    },
                    onDismiss: () => Navigator.of(ctx).pop(),
                  ),
                );
              },
              onOpenTutorial: () {
                showDialog(context: context, builder: (ctx) => TutorialDialog(onDismiss: () => Navigator.of(ctx).pop()));
              },
            ),

            // Game Board (Edge-to-edge from side to side to phone edges)
            Expanded(
              child: Center(
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 2.0),
                  child: Stack(
                    alignment: Alignment.center,
                    children: [
                      SnakeGameBoard(
                        gridWidth: gridWidth,
                        gridHeight: gridHeight,
                        snake: snake,
                        currentDirection: currentDirection,
                        foods: foods,
                        skin: selectedSkin,
                        isShieldActive: isShieldActive,
                        shieldRemainingRatio: shieldRemainingSeconds / 6.0,
                        isInvulnerable: invulnerabilityTicks > 0,
                        onDirectionChanged: _changeDirection,
                      ),
                      if (isPaused && !isGameOver)
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
                          decoration: BoxDecoration(
                            color: const Color(0xDD0B1224),
                            borderRadius: BorderRadius.circular(16),
                            border: Border.all(color: const Color(0xFF38BDF8)),
                          ),
                          child: const Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              Icon(Icons.pause, color: Color(0xFF38BDF8)),
                              SizedBox(width: 8),
                              Text('GAME PAUSED', style: TextStyle(color: Colors.white, fontWeight: FontWeight.black, letterSpacing: 1)),
                            ],
                          ),
                        ),
                    ],
                  ),
                ),
              ),
            ),

            // D-Pad Controls
            Padding(
              padding: const EdgeInsets.only(bottom: 6.0),
              child: SnakeDPadControls(
                onDirectionClicked: _changeDirection,
                accentColor: selectedSkin.primaryColor,
              ),
            ),

            // Persistent Banner Ad (No close button)
            Container(
              height: 50,
              width: double.infinity,
              color: const Color(0xFF0A0F1D),
              padding: const EdgeInsets.symmetric(horizontal: 12),
              child: AdService.bannerAd != null
                  ? AdWidget(ad: AdService.bannerAd!)
                  : Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Row(
                          children: [
                            Container(
                              padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                              decoration: BoxDecoration(color: const Color(0xFFF59E0B), borderRadius: BorderRadius.circular(4)),
                              child: const Text('Ad', style: TextStyle(color: Colors.black, fontSize: 11, fontWeight: FontWeight.bold)),
                            ),
                            const SizedBox(width: 8),
                            const Text('Top Action Games 2026 • Play Free', style: TextStyle(color: Colors.white, fontSize: 12)),
                          ],
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                          decoration: BoxDecoration(color: const Color(0xFF2563EB), borderRadius: BorderRadius.circular(12)),
                          child: const Text('PLAY', style: TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold)),
                        ),
                      ],
                    ),
            ),
          ],
        ),
      ),
    );
  }
}
