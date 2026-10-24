import 'package:flutter/material.dart';
import '../models/snake_models.dart';

class SnakeActionBar extends StatelessWidget {
  final bool isPaused;
  final Difficulty difficulty;
  final bool isSoundEnabled;
  final bool isVibrationEnabled;
  final SnakeSkin selectedSkin;
  final VoidCallback onTogglePause;
  final VoidCallback onCycleDifficulty;
  final VoidCallback onToggleSound;
  final VoidCallback onToggleVibration;
  final VoidCallback onOpenSkins;
  final VoidCallback onOpenTutorial;

  const SnakeActionBar({
    super.key,
    required this.isPaused,
    required this.difficulty,
    required this.isSoundEnabled,
    required this.isVibrationEnabled,
    required this.selectedSkin,
    required this.onTogglePause,
    required this.onCycleDifficulty,
    required this.onToggleSound,
    required this.onToggleVibration,
    required this.onOpenSkins,
    required this.onOpenTutorial,
  });

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 4.0, vertical: 4.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          // Pause / Resume Button
          IconButton.filled(
            style: IconButton.styleFrom(
              backgroundColor: isPaused ? const Color(0xFFF59E0B) : const Color(0xFF1E293B),
              side: const BorderSide(color: Color(0xFF475569)),
            ),
            icon: Icon(
              isPaused ? Icons.play_arrow : Icons.pause,
              color: isPaused ? Colors.black : Colors.white,
              size: 20,
            ),
            onPressed: onTogglePause,
          ),

          // Difficulty Selector Chip
          InkWell(
            borderRadius: BorderRadius.circular(16),
            onTap: onCycleDifficulty,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
              decoration: BoxDecoration(
                color: const Color(0xFF1E293B),
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: const Color(0xFF475569)),
              ),
              child: Row(
                children: [
                  Icon(
                    Icons.speed,
                    size: 16,
                    color: difficulty == Difficulty.easy
                        ? const Color(0xFF4ADE80)
                        : difficulty == Difficulty.medium
                            ? const Color(0xFF38BDF8)
                            : const Color(0xFFF87171),
                  ),
                  const SizedBox(width: 4),
                  Text(
                    difficulty.displayName,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ],
              ),
            ),
          ),

          // Skins Button
          IconButton.filled(
            style: IconButton.styleFrom(
              backgroundColor: const Color(0xFF1E293B),
              side: BorderSide(color: selectedSkin.glowColor),
            ),
            icon: Icon(Icons.color_lens, color: selectedSkin.primaryColor, size: 20),
            onPressed: onOpenSkins,
          ),

          // Sound Toggle
          IconButton.filled(
            style: IconButton.styleFrom(
              backgroundColor: const Color(0xFF1E293B),
              side: const BorderSide(color: Color(0xFF475569)),
            ),
            icon: Icon(
              isSoundEnabled ? Icons.volume_up : Icons.volume_off,
              color: isSoundEnabled ? const Color(0xFF38BDF8) : const Color(0xFF64748B),
              size: 20,
            ),
            onPressed: onToggleSound,
          ),

          // Vibration Toggle
          IconButton.filled(
            style: IconButton.styleFrom(
              backgroundColor: const Color(0xFF1E293B),
              side: const BorderSide(color: Color(0xFF475569)),
            ),
            icon: Icon(
              Icons.vibration,
              color: isVibrationEnabled ? const Color(0xFF4ADE80) : const Color(0xFF64748B),
              size: 20,
            ),
            onPressed: onToggleVibration,
          ),

          // Tutorial Button
          IconButton.filled(
            style: IconButton.styleFrom(
              backgroundColor: const Color(0xFF1E293B),
              side: const BorderSide(color: Color(0xFF475569)),
            ),
            icon: const Icon(Icons.help_outline, color: Color(0xFFFBBF24), size: 20),
            onPressed: onOpenTutorial,
          ),
        ],
      ),
    );
  }
}

class SnakeDPadControls extends StatelessWidget {
  final Function(Direction) onDirectionClicked;
  final Color accentColor;

  const SnakeDPadControls({
    super.key,
    required this.onDirectionClicked,
    required this.accentColor,
  });

  @override
  Widget build(BuildContext context) {
    const buttonSize = 58.0;

    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        // UP
        _DPadButton(
          icon: Icons.keyboard_arrow_up,
          size: buttonSize,
          accentColor: accentColor,
          onPressed: () => onDirectionClicked(Direction.up),
        ),
        const SizedBox(height: 4),

        // LEFT - CENTER - RIGHT
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            _DPadButton(
              icon: Icons.keyboard_arrow_left,
              size: buttonSize,
              accentColor: accentColor,
              onPressed: () => onDirectionClicked(Direction.left),
            ),
            const SizedBox(width: 28),
            Container(
              width: 32,
              height: 32,
              decoration: BoxDecoration(
                color: const Color(0xFF0F172A),
                shape: BoxShape.circle,
                border: Border.all(color: const Color(0xFF334155)),
              ),
            ),
            const SizedBox(width: 28),
            _DPadButton(
              icon: Icons.keyboard_arrow_right,
              size: buttonSize,
              accentColor: accentColor,
              onPressed: () => onDirectionClicked(Direction.right),
            ),
          ],
        ),
        const SizedBox(height: 4),

        // DOWN
        _DPadButton(
          icon: Icons.keyboard_arrow_down,
          size: buttonSize,
          accentColor: accentColor,
          onPressed: () => onDirectionClicked(Direction.down),
        ),
      ],
    );
  }
}

class _DPadButton extends StatelessWidget {
  final IconData icon;
  final double size;
  final Color accentColor;
  final VoidCallback onPressed;

  const _DPadButton({
    required this.icon,
    required this.size,
    required this.accentColor,
    required this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: size,
      height: size,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        gradient: const RadialGradient(
          colors: [Color(0xFF1E293B), Color(0xFF0F172A)],
        ),
        border: Border.all(color: accentColor.withOpacity(0.6), width: 1.5),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.4),
            blurRadius: 8,
            offset: const Offset(0, 4),
          ),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          customBorder: const CircleBorder(),
          onTap: onPressed,
          child: Icon(icon, color: Colors.white, size: 36),
        ),
      ),
    );
  }
}
