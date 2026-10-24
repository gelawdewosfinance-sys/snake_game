import 'package:flutter_test/flutter_test.dart';
import 'package:snake_game/models/snake_models.dart';

void main() {
  test('Initial movement is down', () {
    expect(Direction.down, isNotNull);
    expect(Direction.down.isOpposite(Direction.up), isTrue);
  });

  test('Default skin is Emerald Viper', () {
    expect(SnakeSkin.emerald.id, equals('emerald'));
    expect(SnakeSkin.emerald.title, equals('Emerald Viper'));
  });

  test('Difficulty tick speeds', () {
    expect(Difficulty.easy.tickDelay.inMilliseconds, equals(220));
    expect(Difficulty.medium.tickDelay.inMilliseconds, equals(150));
    expect(Difficulty.hard.tickDelay.inMilliseconds, equals(90));
  });
}
