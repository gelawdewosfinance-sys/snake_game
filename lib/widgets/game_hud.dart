import 'package:flutter/material.dart';
import '../models/snake_models.dart';

class SnakeHud extends StatelessWidget {
  final int score;
  final int highScore;
  final int lives;
  final int maxLives;
  final bool isShieldActive;
  final double shieldSeconds;
  final SnakeSkin skin;

  const SnakeHud({
    super.key,
    required this.score,
    required this.highScore,
    required this.lives,
    this.maxLives = 3,
    required this.isShieldActive,
    required this.shieldSeconds,
    required this.skin,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 4.0, vertical: 2.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          // Current Score
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
            decoration: BoxDecoration(
              color: const Color(0xFF0F172A),
              borderRadius: BorderRadius.circular(12),
              border: Border.all(color: skin.glowColor.withOpacity(0.5)),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  'SCORE',
                  style: TextStyle(
                    color: Color(0xFF94A3B8),
                    fontSize: 10,
                    fontWeight: FontWeight.bold,
                    letterSpacing: 1,
                  ),
                ),
                Text(
                  '$score',
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.black,
                  ),
                ),
              ],
            ),
          ),

          // Shield status
          if (isShieldActive)
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
              decoration: BoxDecoration(
                color: const Color(0xFF00363A),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: const Color(0xFF00E5FF)),
              ),
              child: Row(
                children: [
                  const Icon(Icons.shield, color: Color(0xFF00E5FF), size: 16),
                  const SizedBox(width: 4),
                  Text(
                    '${shieldSeconds.toStringAsFixed(1)}s',
                    style: const TextStyle(
                      color: Color(0xFF00E5FF),
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),

          // Lives Hearts (Default 2 lives)
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
            decoration: BoxDecoration(
              color: const Color(0xFF0F172A),
              borderRadius: BorderRadius.circular(12),
              border: Border.all(color: const Color(0xFF334155)),
            ),
            child: Row(
              children: List.generate(maxLives, (index) {
                final hasLife = index < lives;
                return Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 2.0),
                  child: Icon(
                    hasLife ? Icons.favorite : Icons.favorite_border,
                    color: hasLife ? const Color(0xFFFF2A42) : const Color(0xFF475569),
                    size: 18,
                  ),
                );
              }),
            ),
          ),

          // High Score
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
            decoration: BoxDecoration(
              color: const Color(0xFF0F172A),
              borderRadius: BorderRadius.circular(12),
              border: Border.all(color: const Color(0xFFF59E0B).withOpacity(0.5)),
            ),
            child: Row(
              children: [
                const Icon(Icons.star, color: Color(0xFFF59E0B), size: 16),
                const SizedBox(width: 6),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text(
                      'BEST',
                      style: TextStyle(
                        color: Color(0xFFF59E0B),
                        fontSize: 9,
                        fontWeight: FontWeight.bold,
                        letterSpacing: 1,
                      ),
                    ),
                    Text(
                      '$highScore',
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 18,
                        fontWeight: FontWeight.black,
                      ),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
