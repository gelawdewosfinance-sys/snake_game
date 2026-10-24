import 'dart:async';
import 'package:flutter/material.dart';
import '../models/snake_models.dart';

class GameOverDialog extends StatelessWidget {
  final int score;
  final int highScore;
  final VoidCallback onWatchAdToRevive;
  final VoidCallback onPlayAgain;

  const GameOverDialog({
    super.key,
    required this.score,
    required this.highScore,
    required this.onWatchAdToRevive,
    required this.onPlayAgain,
  });

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.transparent,
      insetPadding: const EdgeInsets.symmetric(horizontal: 24),
      child: Container(
        padding: const EdgeInsets.all(24),
        decoration: BoxDecoration(
          color: const Color(0xFF0F172A),
          borderRadius: BorderRadius.circular(24),
          border: Border.all(color: const Color(0xFFE11D48).withOpacity(0.7), width: 1.5),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
              decoration: BoxDecoration(
                color: const Color(0xFFE11D48).withOpacity(0.2),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: const Color(0xFFE11D48)),
              ),
              child: const Text(
                'GAME OVER',
                style: TextStyle(
                  color: Color(0xFFFF4D6D),
                  fontSize: 14,
                  fontWeight: FontWeight.black,
                  letterSpacing: 2,
                ),
              ),
            ),
            const SizedBox(height: 20),

            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Column(
                  children: [
                    const Text('SCORE', style: TextStyle(color: Color(0xFF94A3B8), fontSize: 11, fontWeight: FontWeight.bold)),
                    Text('$score', style: const TextStyle(color: Colors.white, fontSize: 28, fontWeight: FontWeight.black)),
                  ],
                ),
                Container(width: 1, height: 44, color: const Color(0xFF334155)),
                Column(
                  children: [
                    const Text('BEST', style: TextStyle(color: Color(0xFFF59E0B), fontSize: 11, fontWeight: FontWeight.bold)),
                    Text('$highScore', style: const TextStyle(color: Colors.white, fontSize: 28, fontWeight: FontWeight.black)),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 24),

            // Revive / Continue Run Button (Always enabled)
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF10B981),
                shape: RoundedCornerShape(14),
                minimumSize: const Size.fromHeight(56),
              ),
              onPressed: onWatchAdToRevive,
              child: const Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.ondemand_video, color: Colors.black, size: 22),
                  SizedBox(width: 10),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(
                        'Continue Run (+3 Lives)',
                        style: TextStyle(color: Colors.black, fontWeight: FontWeight.black, fontSize: 15),
                      ),
                      Text(
                        'Watch 2 short 10-second video ads to keep your score',
                        style: TextStyle(color: Color(0xFF064E3B), fontSize: 11, fontWeight: FontWeight.w600),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 10),

            // Restart Button
            OutlinedButton.icon(
              style: OutlinedButton.styleFrom(
                side: const BorderSide(color: Color(0xFF475569)),
                shape: RoundedCornerShape(14),
                minimumSize: const Size.fromHeight(48),
              ),
              onPressed: onPlayAgain,
              icon: const Icon(Icons.refresh, color: Colors.white, size: 18),
              label: const Text('Play Again', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
            ),
          ],
        ),
      ),
    );
  }

  RoundedRectangleBorder RoundedCornerShape(double r) =>
      RoundedRectangleBorder(borderRadius: BorderRadius.circular(r));
}

class LifeLostDialog extends StatelessWidget {
  final int remainingLives;
  final int currentScore;
  final VoidCallback onResume;

  const LifeLostDialog({
    super.key,
    required this.remainingLives,
    required this.currentScore,
    required this.onResume,
  });

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: Colors.transparent,
      insetPadding: const EdgeInsets.symmetric(horizontal: 24),
      child: Container(
        padding: const EdgeInsets.all(24),
        decoration: BoxDecoration(
          color: const Color(0xFF0F172A),
          borderRadius: BorderRadius.circular(24),
          border: Border.all(color: const Color(0xFFF59E0B), width: 1.5),
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
              decoration: BoxDecoration(
                color: const Color(0xFFF59E0B).withOpacity(0.2),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: const Color(0xFFF59E0B)),
              ),
              child: const Text(
                'LIFE LOST! (ነፍስ አጥተሃል)',
                style: TextStyle(
                  color: Color(0xFFFBBF24),
                  fontSize: 13,
                  fontWeight: FontWeight.black,
                  letterSpacing: 1,
                ),
              ),
            ),
            const SizedBox(height: 18),
            Container(
              width: 76,
              height: 76,
              decoration: BoxDecoration(
                color: const Color(0xFF1E293B),
                shape: BoxShape.circle,
                border: Border.all(color: const Color(0xFFFF2A42).withOpacity(0.6), width: 2),
              ),
              child: const Icon(Icons.favorite, color: Color(0xFFFF2A42), size: 42),
            ),
            const SizedBox(height: 16),
            Text(
              '$remainingLives Life Remaining',
              style: const TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.black),
            ),
            const SizedBox(height: 6),
            Text(
              'Current Score: $currentScore pts (Saved!)',
              style: const TextStyle(color: Color(0xFF38BDF8), fontSize: 14, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 10),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
              decoration: BoxDecoration(
                color: const Color(0xFF1E293B),
                borderRadius: BorderRadius.circular(8),
              ),
              child: const Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(Icons.security, color: Color(0xFF00E5FF), size: 16),
                  SizedBox(width: 6),
                  Flexible(
                    child: Text(
                      'Respawns in center moving DOWN with grace period',
                      style: TextStyle(color: Color(0xFF94A3B8), fontSize: 11),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 24),
            ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFF10B981),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                minimumSize: const Size.fromHeight(52),
              ),
              onPressed: onResume,
              child: const Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.play_arrow, color: Colors.black, size: 22),
                  SizedBox(width: 8),
                  Text(
                    'Tap to Continue Run (ቀጥል)',
                    style: TextStyle(color: Colors.black, fontWeight: FontWeight.black, fontSize: 15),
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

class RewardedVideoAdDialog extends StatefulWidget {
  final VoidCallback onRewardEarned;

  const RewardedVideoAdDialog({super.key, required this.onRewardEarned});

  @override
  State<RewardedVideoAdDialog> createState() => _RewardedVideoAdDialogState();
}

class _RewardedVideoAdDialogState extends State<RewardedVideoAdDialog> {
  int currentVideoIndex = 1; // 1 or 2
  int secondsLeft = 10;
  bool isCompleted = false;
  Timer? _timer;

  @override
  void initState() {
    super.initState();
    _startCountdown();
  }

  void _startCountdown() {
    _timer?.cancel();
    secondsLeft = 10;
    _timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      if (mounted) {
        setState(() {
          if (secondsLeft > 1) {
            secondsLeft--;
          } else {
            if (currentVideoIndex == 1) {
              currentVideoIndex = 2;
              secondsLeft = 10;
            } else {
              isCompleted = true;
              timer.cancel();
            }
          }
        });
      }
    });
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Dialog.fullscreen(
      backgroundColor: const Color(0xFF030611),
      child: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              // Top bar
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(color: const Color(0xFFE11D48), borderRadius: BorderRadius.circular(6)),
                        child: const Text('Rewarded Video Ad', style: TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold)),
                      ),
                      const SizedBox(width: 6),
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                        decoration: BoxDecoration(color: const Color(0xFF2563EB), borderRadius: BorderRadius.circular(6)),
                        child: Text(
                          !isCompleted ? 'Ad $currentVideoIndex of 2' : '2 of 2 Complete',
                          style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold),
                        ),
                      ),
                    ],
                  ),
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    decoration: BoxDecoration(
                      color: const Color(0xFF1E293B),
                      borderRadius: BorderRadius.circular(12),
                      border: Border.all(color: const Color(0xFF475569)),
                    ),
                    child: Text(
                      isCompleted
                          ? 'Completed!'
                          : currentVideoIndex == 1
                              ? 'Next Ad in ${secondsLeft}s'
                              : 'Reward in ${secondsLeft}s',
                      style: TextStyle(
                        color: isCompleted ? const Color(0xFF4ADE80) : const Color(0xFFFBBF24),
                        fontSize: 12,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),

              // Center Video Player Simulation
              Container(
                height: 310,
                width: double.infinity,
                decoration: BoxDecoration(
                  color: const Color(0xFF0B1327),
                  borderRadius: BorderRadius.circular(20),
                  border: Border.all(color: isCompleted ? const Color(0xFF10B981) : const Color(0xFF3B82F6), width: 1.5),
                ),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Container(
                      width: 80,
                      height: 80,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        gradient: RadialGradient(
                          colors: currentVideoIndex == 1
                              ? [const Color(0xFF3B82F6), const Color(0xFF1D4ED8)]
                              : [const Color(0xFF8B5CF6), const Color(0xFF6D28D9)],
                        ),
                      ),
                      child: Icon(
                        isCompleted ? Icons.favorite : Icons.ondemand_video,
                        color: isCompleted ? const Color(0xFFFF2A42) : Colors.white,
                        size: 44,
                      ),
                    ),
                    const SizedBox(height: 16),
                    Text(
                      isCompleted
                          ? '2 of 2 Videos Watched!'
                          : currentVideoIndex == 1
                              ? 'Video 1: Galaxy Space Arena'
                              : 'Video 2: Cyber Legends Racing',
                      style: const TextStyle(color: Colors.white, fontSize: 18, fontWeight: FontWeight.bold),
                    ),
                    const SizedBox(height: 6),
                    Text(
                      isCompleted
                          ? 'Reward Ready: 3 Extra Lives unlocked!'
                          : 'Watching Video $currentVideoIndex of 2 (10s)... Almost done!',
                      style: const TextStyle(color: Color(0xFF94A3B8), fontSize: 13),
                    ),
                  ],
                ),
              ),

              // Bottom Action
              if (isCompleted)
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFF10B981),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
                    minimumSize: const Size.fromHeight(54),
                  ),
                  onPressed: widget.onRewardEarned,
                  child: const Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.favorite, color: Colors.black, size: 22),
                      SizedBox(width: 8),
                      Text('Claim +3 Lives & Resume Game', style: TextStyle(color: Colors.black, fontWeight: FontWeight.black, fontSize: 15)),
                    ],
                  ),
                )
              else
                Container(
                  padding: const EdgeInsets.all(14),
                  decoration: BoxDecoration(color: const Color(0xFF131D33), borderRadius: BorderRadius.circular(14)),
                  child: const Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.favorite, color: Color(0xFFFF2A42), size: 22),
                      SizedBox(width: 10),
                      Text('Reward: 3 Extra Lives / Continue Run', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 13)),
                    ],
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}

class TutorialDialog extends StatelessWidget {
  final VoidCallback onDismiss;

  const TutorialDialog({super.key, required this.onDismiss});

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: const Color(0xFF0F172A),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('How to Play', style: TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.black)),
                  IconButton(icon: const Icon(Icons.close, color: Color(0xFF94A3B8)), onPressed: onDismiss),
                ],
              ),
              const SizedBox(height: 16),
              _item('Controls & Movement', '• Starts moving downwards automatically.\n• Use D-Pad or swipe directly on the board to steer Up, Down, Left, Right.', Icons.gamepad, const Color(0xFF38BDF8)),
              const SizedBox(height: 10),
              _item('Apples & Bonuses', '• Red Apple: +10 pts and grows snake.\n• Golden Star (+50 pts): Disappears after 7 seconds!', Icons.star, const Color(0xFFFBBF24)),
              const SizedBox(height: 10),
              _item('Invincibility Shield', '• 6s protection against wall crashes and your own tail!', Icons.security, const Color(0xFF00E5FF)),
              const SizedBox(height: 10),
              _item('Lives & Video Ad Revive', '• Starts with 2 Lives.\n• Watch 2 short video ads to get +3 lives and keep your score!', Icons.favorite, const Color(0xFFFF2A42)),
              const SizedBox(height: 16),
              ElevatedButton(
                style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF10B981), minimumSize: const Size.fromHeight(48)),
                onPressed: onDismiss,
                child: const Text('Got it! Let\'s Play', style: TextStyle(color: Colors.black, fontWeight: FontWeight.bold)),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _item(String title, String desc, IconData icon, Color color) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(color: const Color(0xFF1E293B), borderRadius: BorderRadius.circular(12)),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(children: [Icon(icon, color: color, size: 18), const SizedBox(width: 8), Text(title, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold))]),
          const SizedBox(height: 4),
          Text(desc, style: const TextStyle(color: Color(0xFFCBD5E1), fontSize: 12)),
        ],
      ),
    );
  }
}

class SkinPickerDialog extends StatelessWidget {
  final SnakeSkin currentSkin;
  final Set<String> unlockedSkinIds;
  final Function(SnakeSkin) onSelectSkin;
  final Function(SnakeSkin) onWatchAdToUnlock;
  final VoidCallback onDismiss;

  const SkinPickerDialog({
    super.key,
    required this.currentSkin,
    required this.unlockedSkinIds,
    required this.onSelectSkin,
    required this.onWatchAdToUnlock,
    required this.onDismiss,
  });

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: const Color(0xFF0F172A),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
      child: Padding(
        padding: const EdgeInsets.all(20.0),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('Snake Skins', style: TextStyle(color: Colors.white, fontSize: 20, fontWeight: FontWeight.black)),
                IconButton(icon: const Icon(Icons.close, color: Color(0xFF94A3B8)), onPressed: onDismiss),
              ],
            ),
            const SizedBox(height: 14),
            ...SnakeSkin.values.map((skin) {
              final isUnlocked = unlockedSkinIds.contains(skin.id);
              final isSelected = currentSkin.id == skin.id;

              return Container(
                margin: const EdgeInsets.only(bottom: 8),
                padding: const EdgeInsets.all(10),
                decoration: BoxDecoration(
                  color: isSelected ? const Color(0xFF1E293B) : const Color(0xFF131D33),
                  borderRadius: BorderRadius.circular(12),
                  border: Border.all(color: isSelected ? skin.primaryColor : const Color(0xFF1E293B)),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Row(
                      children: [
                        Container(
                          width: 36,
                          height: 36,
                          decoration: BoxDecoration(color: skin.primaryColor, shape: BoxShape.circle),
                        ),
                        const SizedBox(width: 10),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(skin.title, style: const TextStyle(color: Colors.white, fontWeight: FontWeight.bold, fontSize: 13)),
                            Text(skin.unlockScore == 0 ? 'Default' : 'Req: ${skin.unlockScore} pts', style: const TextStyle(color: Color(0xFF94A3B8), fontSize: 11)),
                          ],
                        ),
                      ],
                    ),
                    if (isSelected)
                      const Text('Equipped', style: TextStyle(color: Color(0xFF10B981), fontWeight: FontWeight.bold, fontSize: 12))
                    else if (isUnlocked)
                      TextButton(onPressed: () => onSelectSkin(skin), child: const Text('Use'))
                    else
                      ElevatedButton.icon(
                        style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF2563EB)),
                        onPressed: () => onWatchAdToUnlock(skin),
                        icon: const Icon(Icons.ondemand_video, size: 14),
                        label: const Text('Unlock', style: TextStyle(fontSize: 11)),
                      ),
                  ],
                ),
              );
            }),
            const SizedBox(height: 10),
            ElevatedButton(
              style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFF334155), minimumSize: const Size.fromHeight(44)),
              onPressed: onDismiss,
              child: const Text('Done', style: TextStyle(color: Colors.white)),
            ),
          ],
        ),
      ),
    );
  }
}
