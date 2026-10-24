import 'package:flutter/services.dart';

class VibrationService {
  static bool isEnabled = true;

  static void eat() {
    if (!isEnabled) return;
    HapticFeedback.lightImpact();
  }

  static void bonus() {
    if (!isEnabled) return;
    HapticFeedback.mediumImpact();
  }

  static void crash() {
    if (!isEnabled) return;
    HapticFeedback.heavyImpact();
  }

  static void click() {
    if (!isEnabled) return;
    HapticFeedback.selectionClick();
  }
}
