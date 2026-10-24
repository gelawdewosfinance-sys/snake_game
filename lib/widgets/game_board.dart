import 'dart:math' as math;
import 'package:flutter/material.dart';
import '../models/snake_models.dart';

class SnakeGameBoard extends StatelessWidget {
  final int gridWidth;
  final int gridHeight;
  final List<Point2D> snake;
  final Direction currentDirection;
  final List<FoodItem> foods;
  final SnakeSkin skin;
  final bool isShieldActive;
  final double shieldRemainingRatio;
  final bool isInvulnerable;
  final Function(Direction) onDirectionChanged;

  const SnakeGameBoard({
    super.key,
    required this.gridWidth,
    required this.gridHeight,
    required this.snake,
    required this.currentDirection,
    required this.foods,
    required this.skin,
    required this.isShieldActive,
    required this.shieldRemainingRatio,
    required this.isInvulnerable,
    required this.onDirectionChanged,
  });

  @override
  Widget build(BuildContext context) {
    return AspectRatio(
      aspectRatio: gridWidth / gridHeight,
      child: Container(
        decoration: BoxDecoration(
          color: const Color(0xFF090E1C),
          borderRadius: BorderRadius.circular(16),
          boxShadow: [
            BoxShadow(
              color: skin.glowColor.withOpacity(0.35),
              blurRadius: 16,
              spreadRadius: 1,
            ),
          ],
        ),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(16),
          child: GestureDetector(
            behavior: HitTestBehavior.opaque,
            onPanEnd: (details) {
              final velocity = details.velocity.pixelsPerSecond;
              final dx = velocity.dx;
              final dy = velocity.dy;

              if (dx.abs() > dy.abs()) {
                if (dx > 0) {
                  onDirectionChanged(Direction.right);
                } else {
                  onDirectionChanged(Direction.left);
                }
              } else {
                if (dy > 0) {
                  onDirectionChanged(Direction.down);
                } else {
                  onDirectionChanged(Direction.up);
                }
              }
            },
            child: CustomPaint(
              painter: _SnakeBoardPainter(
                gridWidth: gridWidth,
                gridHeight: gridHeight,
                snake: snake,
                direction: currentDirection,
                foods: foods,
                skin: skin,
                isShieldActive: isShieldActive,
                shieldRemainingRatio: shieldRemainingRatio,
                isInvulnerable: isInvulnerable,
              ),
              child: const SizedBox.expand(),
            ),
          ),
        ),
      ),
    );
  }
}

class _SnakeBoardPainter extends CustomPainter {
  final int gridWidth;
  final int gridHeight;
  final List<Point2D> snake;
  final Direction direction;
  final List<FoodItem> foods;
  final SnakeSkin skin;
  final bool isShieldActive;
  final double shieldRemainingRatio;
  final bool isInvulnerable;

  _SnakeBoardPainter({
    required this.gridWidth,
    required this.gridHeight,
    required this.snake,
    required this.direction,
    required this.foods,
    required this.skin,
    required this.isShieldActive,
    required this.shieldRemainingRatio,
    required this.isInvulnerable,
  });

  @override
  void paint(Canvas canvas, Size size) {
    final cellW = size.width / gridWidth;
    final cellH = size.height / gridHeight;

    // 1. Grid lines
    final gridPaint = Paint()
      ..color = const Color(0xFF141F38)
      ..strokeWidth = 0.8;
    for (int c = 1; c < gridWidth; c++) {
      canvas.drawLine(Offset(c * cellW, 0), Offset(c * cellW, size.height), gridPaint);
    }
    for (int r = 1; r < gridHeight; r++) {
      canvas.drawLine(Offset(0, r * cellH), Offset(size.width, r * cellH), gridPaint);
    }

    // 2. Border
    final borderPaint = Paint()
      ..color = isShieldActive ? const Color(0xFF00E5FF) : skin.glowColor.withOpacity(0.6)
      ..style = PaintingStyle.stroke
      ..strokeWidth = isShieldActive ? 4.0 : 2.5;
    canvas.drawRRect(
      RRect.fromRectAndRadius(
        Rect.fromLTWH(1, 1, size.width - 2, size.height - 2),
        const Radius.circular(16),
      ),
      borderPaint,
    );

    // 3. Foods
    for (final food in foods) {
      _drawFood(canvas, food, cellW, cellH);
    }

    // 4. Snake
    if (snake.isNotEmpty) {
      _drawSnake(canvas, cellW, cellH);
    }
  }

  void _drawFood(Canvas canvas, FoodItem food, double cellW, double cellH) {
    final cx = (food.position.x + 0.5) * cellW;
    final centerY = (food.position.y + 0.5) * cellH;
    final radius = math.min(cellW, cellH) * 0.42;

    switch (food.type) {
      case FoodType.normal:
        // Glowing Red Apple
        final glowPaint = Paint()..color = const Color(0xFFFF2A42).withOpacity(0.3);
        canvas.drawCircle(Offset(cx, centerY), radius * 1.3, glowPaint);

        final applePaint = Paint()
          ..shader = RadialGradient(
            colors: const [Color(0xFFFF5252), Color(0xFFD50000)],
          ).createShader(Rect.fromCircle(center: Offset(cx, centerY), radius: radius));
        canvas.drawCircle(Offset(cx, centerY), radius, applePaint);

        // Highlight
        final shinePaint = Paint()..color = Colors.white.withOpacity(0.7);
        canvas.drawCircle(Offset(cx - radius * 0.35, centerY - radius * 0.35), radius * 0.22, shinePaint);
        break;

      case FoodType.bonusGolden:
        // Timed Golden Star
        final starPaint = Paint()
          ..shader = const LinearGradient(
            colors: [Color(0xFFFFF176), Color(0xFFFFB300), Color(0xFFFF8F00)],
          ).createShader(Rect.fromCircle(center: Offset(cx, centerY), radius: radius));
        canvas.drawCircle(Offset(cx, centerY), radius * 1.1, starPaint);

        // Timer arc
        final timerPaint = Paint()
          ..color = const Color(0xFFFFD54F)
          ..style = PaintingStyle.stroke
          ..strokeCap = StrokeCap.round
          ..strokeWidth = 3;
        canvas.drawArc(
          Rect.fromCircle(center: Offset(cx, centerY), radius: radius * 1.35),
          -math.pi / 2,
          2 * math.pi * food.remainingRatio,
          false,
          timerPaint,
        );
        break;

      case FoodType.shieldPowerup:
        // Shield Orb
        final shieldPaint = Paint()
          ..shader = RadialGradient(
            colors: const [Color(0xFFE0F7FA), Color(0xFF00B0FF), Color(0xFF0059B2)],
          ).createShader(Rect.fromCircle(center: Offset(cx, centerY), radius: radius));
        canvas.drawCircle(Offset(cx, centerY), radius, shieldPaint);
        break;
    }
  }

  void _drawSnake(Canvas canvas, double cellW, double cellH) {
    final baseRadius = math.min(cellW, cellH) * 0.46;

    // Blink if invulnerable grace period
    if (isInvulnerable && (DateTime.now().millisecondsSinceEpoch ~/ 150) % 2 == 0) {
      return;
    }

    for (int i = snake.length - 1; i >= 0; i--) {
      final seg = snake[i];
      final cx = (seg.x + 0.5) * cellW;
      final cy = (seg.y + 0.5) * cellH;

      if (i == 0) {
        // Head
        final headPaint = Paint()
          ..shader = RadialGradient(
            colors: [skin.secondaryColor, skin.primaryColor],
          ).createShader(Rect.fromCircle(center: Offset(cx, cy), radius: baseRadius));
        canvas.drawCircle(Offset(cx, cy), baseRadius, headPaint);

        // Eyes
        final eyeDist = baseRadius * 0.55;
        final eyeSize = baseRadius * 0.32;
        Offset eye1, eye2;

        switch (direction) {
          case Direction.up:
            eye1 = Offset(cx - eyeDist, cy - eyeDist * 0.4);
            eye2 = Offset(cx + eyeDist, cy - eyeDist * 0.4);
            break;
          case Direction.down:
            eye1 = Offset(cx - eyeDist, cy + eyeDist * 0.4);
            eye2 = Offset(cx + eyeDist, cy + eyeDist * 0.4);
            break;
          case Direction.left:
            eye1 = Offset(cx - eyeDist * 0.4, cy - eyeDist);
            eye2 = Offset(cx - eyeDist * 0.4, cy + eyeDist);
            break;
          case Direction.right:
            eye1 = Offset(cx + eyeDist * 0.4, cy - eyeDist);
            eye2 = Offset(cx + eyeDist * 0.4, cy + eyeDist);
            break;
        }

        final eyePaint = Paint()..color = skin.eyeColor;
        canvas.drawCircle(eye1, eyeSize, eyePaint);
        canvas.drawCircle(eye2, eyeSize, eyePaint);

        final pupilPaint = Paint()..color = Colors.black;
        canvas.drawCircle(eye1, eyeSize * 0.5, pupilPaint);
        canvas.drawCircle(eye2, eyeSize * 0.5, pupilPaint);
      } else {
        // Body segment
        final color = (i % 2 == 0) ? skin.primaryColor : skin.secondaryColor;
        final segPaint = Paint()..color = color;
        canvas.drawCircle(Offset(cx, cy), baseRadius * 0.9, segPaint);
      }
    }

    // Shield Aura
    if (isShieldActive) {
      final head = snake.first;
      final cx = (head.x + 0.5) * cellW;
      final cy = (head.y + 0.5) * cellH;
      final shieldAuraPaint = Paint()
        ..color = const Color(0xFF00E5FF)
        ..style = PaintingStyle.stroke
        ..strokeWidth = 3.5;
      canvas.drawCircle(Offset(cx, cy), baseRadius * 1.6, shieldAuraPaint);
    }
  }

  @override
  bool shouldRepaint(covariant _SnakeBoardPainter oldDelegate) => true;
}
