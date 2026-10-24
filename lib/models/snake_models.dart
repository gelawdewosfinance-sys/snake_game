import 'package:flutter/material.dart';

enum Direction {
  up,
  down,
  left,
  right;

  bool isOpposite(Direction other) {
    return (this == Direction.up && other == Direction.down) ||
        (this == Direction.down && other == Direction.up) ||
        (this == Direction.left && other == Direction.right) ||
        (this == Direction.right && other == Direction.left);
  }
}

enum Difficulty {
  easy('Easy', Duration(milliseconds: 220), 1),
  medium('Medium', Duration(milliseconds: 150), 2),
  hard('Hard', Duration(milliseconds: 90), 3);

  final String displayName;
  final Duration tickDelay;
  final int multiplier;

  const Difficulty(this.displayName, this.tickDelay, this.multiplier);
}

class Point2D {
  final int x;
  final int y;

  const Point2D(this.x, this.y);

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is Point2D && runtimeType == other.runtimeType && x == other.x && y == other.y;

  @override
  int get hashCode => x.hashCode ^ y.hashCode;
}

enum FoodType {
  normal(10, false, 0),
  bonusGolden(50, true, 7),
  shieldPowerup(25, true, 6);

  final int points;
  final bool isTimed;
  final int durationSeconds;

  const FoodType(this.points, this.isTimed, this.durationSeconds);
}

class FoodItem {
  final Point2D position;
  final FoodType type;
  final DateTime spawnTime;
  final Duration duration;

  FoodItem({
    required this.position,
    required this.type,
    DateTime? spawnTime,
  })  : spawnTime = spawnTime ?? DateTime.now(),
        duration = Duration(seconds: type.durationSeconds);

  double get remainingRatio {
    if (!type.isTimed || duration.inMilliseconds <= 0) return 1.0;
    final elapsed = DateTime.now().difference(spawnTime).inMilliseconds;
    final remaining = (duration.inMilliseconds - elapsed).clamp(0, duration.inMilliseconds);
    return remaining / duration.inMilliseconds;
  }

  bool get isExpired {
    if (!type.isTimed) return false;
    return DateTime.now().difference(spawnTime) >= duration;
  }
}

enum SnakeSkin {
  emerald(
    id: 'emerald',
    title: 'Emerald Viper',
    primaryColor: Color(0xFF10B981),
    secondaryColor: Color(0xFF34D399),
    glowColor: Color(0xFF059669),
    eyeColor: Color(0xFFFACC15),
    unlockScore: 0,
  ),
  cyber(
    id: 'cyber',
    title: 'Cyber Neon',
    primaryColor: Color(0xFF06B6D4),
    secondaryColor: Color(0xFF22D3EE),
    glowColor: Color(0xFF0891B2),
    eyeColor: Color(0xFFF43F5E),
    unlockScore: 150,
  ),
  golden(
    id: 'golden',
    title: 'Golden Dragon',
    primaryColor: Color(0xFFF59E0B),
    secondaryColor: Color(0xFFFCD34D),
    glowColor: Color(0xFFD97706),
    eyeColor: Color(0xFFEF4444),
    unlockScore: 300,
  ),
  lava(
    id: 'lava',
    title: 'Lava Serpent',
    primaryColor: Color(0xFFEF4444),
    secondaryColor: Color(0xFFF87171),
    glowColor: Color(0xFFB91C1C),
    eyeColor: Color(0xFFFBBF24),
    unlockScore: 500,
  ),
  cosmic(
    id: 'cosmic',
    title: 'Cosmic Amethyst',
    primaryColor: Color(0xFFA855F7),
    secondaryColor: Color(0xFFC084FC),
    glowColor: Color(0xFF7E22CE),
    eyeColor: Color(0xFF38BDF8),
    unlockScore: 800,
  );

  final String id;
  final String title;
  final Color primaryColor;
  final Color secondaryColor;
  final Color glowColor;
  final Color eyeColor;
  final int unlockScore;

  const SnakeSkin({
    required this.id,
    required this.title,
    required this.primaryColor,
    required this.secondaryColor,
    required this.glowColor,
    required this.eyeColor,
    required this.unlockScore,
  });
}
