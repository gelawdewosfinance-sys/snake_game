package kaleb.snakegame.menen.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kaleb.snakegame.menen.model.Direction
import kaleb.snakegame.menen.model.FoodItem
import kaleb.snakegame.menen.model.FoodType
import kaleb.snakegame.menen.model.Point2D
import kaleb.snakegame.menen.model.SnakeSkin
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SnakeGameBoard(
  gridWidth: Int,
  gridHeight: Int,
  snake: List<Point2D>,
  currentDirection: Direction,
  foods: List<FoodItem>,
  skin: SnakeSkin,
  isShieldActive: Boolean,
  shieldRemainingRatio: Float,
  isInvulnerable: Boolean,
  onDirectionChanged: (Direction) -> Unit,
  modifier: Modifier = Modifier
) {
  var totalDragX by remember { mutableFloatStateOf(0f) }
  var totalDragY by remember { mutableFloatStateOf(0f) }
  val dragThreshold = 35f

  // Pulse animation for bonus items and shield
  val infiniteTransition = rememberInfiniteTransition(label = "game_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(600, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "star_rotation"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(gridWidth.toFloat() / gridHeight.toFloat())
      .clip(RoundedCornerShape(16.dp))
      .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp))
      .background(Color(0xFF090E1C))
      .pointerInput(Unit) {
        detectDragGestures(
          onDragStart = {
            totalDragX = 0f
            totalDragY = 0f
          },
          onDrag = { change, dragAmount ->
            change.consume()
            totalDragX += dragAmount.x
            totalDragY += dragAmount.y

            if (abs(totalDragX) > dragThreshold || abs(totalDragY) > dragThreshold) {
              if (abs(totalDragX) > abs(totalDragY)) {
                if (totalDragX > 0) {
                  onDirectionChanged(Direction.RIGHT)
                } else {
                  onDirectionChanged(Direction.LEFT)
                }
              } else {
                if (totalDragY > 0) {
                  onDirectionChanged(Direction.DOWN)
                } else {
                  onDirectionChanged(Direction.UP)
                }
              }
              totalDragX = 0f
              totalDragY = 0f
            }
          }
        )
      }
  ) {
    Canvas(modifier = Modifier.matchParentSize()) {
      val cellW = size.width / gridWidth
      val cellH = size.height / gridHeight

      // 1. Draw subtle background grid
      drawGrid(gridWidth, gridHeight, cellW, cellH)

      // 2. Draw border frame with glowing tint
      val borderColor = if (isShieldActive) Color(0xFF00E5FF) else skin.glowColor.copy(alpha = 0.6f)
      drawRoundRect(
        color = borderColor,
        topLeft = Offset(1.5f, 1.5f),
        size = Size(size.width - 3f, size.height - 3f),
        cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
        style = Stroke(width = if (isShieldActive) 4f else 2.5f)
      )

      // 3. Draw Foods
      for (food in foods) {
        drawFoodItem(food, cellW, cellH, pulseScale, rotationAngle)
      }

      // 4. Draw Snake
      if (snake.isNotEmpty()) {
        drawSnake(
          snake = snake,
          direction = currentDirection,
          skin = skin,
          cellW = cellW,
          cellH = cellH,
          isShieldActive = isShieldActive,
          shieldRemainingRatio = shieldRemainingRatio,
          isInvulnerable = isInvulnerable,
          pulseScale = pulseScale
        )
      }
    }
  }
}

private fun DrawScope.drawGrid(cols: Int, rows: Int, cellW: Float, cellH: Float) {
  val gridColor = Color(0xFF141F38)
  for (c in 1 until cols) {
    val x = c * cellW
    drawLine(color = gridColor, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = 0.8f)
  }
  for (r in 1 until rows) {
    val y = r * cellH
    drawLine(color = gridColor, start = Offset(0f, y), end = Offset(size.width, y), strokeWidth = 0.8f)
  }
}

private fun DrawScope.drawFoodItem(
  food: FoodItem,
  cellW: Float,
  cellH: Float,
  pulseScale: Float,
  rotationAngle: Float
) {
  val centerX = (food.position.x + 0.5f) * cellW
  val centerY = (food.position.y + 0.5f) * cellH
  val radius = (minOf(cellW, cellH) * 0.42f)

  when (food.type) {
    FoodType.NORMAL -> {
      // Radiant Apple with green leaf and highlight
      drawCircle(
        color = Color(0xFFFF2A42).copy(alpha = 0.25f),
        radius = radius * 1.4f,
        center = Offset(centerX, centerY)
      )
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFFFF5252), Color(0xFFD50000)),
          center = Offset(centerX - radius * 0.3f, centerY - radius * 0.3f),
          radius = radius
        ),
        radius = radius,
        center = Offset(centerX, centerY)
      )
      // Highlight shine
      drawCircle(
        color = Color.White.copy(alpha = 0.7f),
        radius = radius * 0.22f,
        center = Offset(centerX - radius * 0.35f, centerY - radius * 0.35f)
      )
      // Leaf
      val leafPath = Path().apply {
        moveTo(centerX, centerY - radius)
        quadraticTo(centerX + radius * 0.5f, centerY - radius * 1.5f, centerX + radius * 0.8f, centerY - radius * 1.2f)
        quadraticTo(centerX + radius * 0.2f, centerY - radius * 0.8f, centerX, centerY - radius)
        close()
      }
      drawPath(leafPath, color = Color(0xFF4CAF50))
    }

    FoodType.BONUS_GOLDEN -> {
      // Glowing timed golden star / diamond with timer ring
      val currentRadius = radius * pulseScale
      drawCircle(
        color = Color(0xFFFFD700).copy(alpha = 0.3f),
        radius = currentRadius * 1.5f,
        center = Offset(centerX, centerY)
      )
      // Draw spinning 4-point star
      val starPath = Path()
      val points = 8
      for (i in 0 until points) {
        val angle = Math.toRadians((rotationAngle + i * (360f / points)).toDouble())
        val r = if (i % 2 == 0) currentRadius * 1.1f else currentRadius * 0.45f
        val px = centerX + (cos(angle) * r).toFloat()
        val py = centerY + (sin(angle) * r).toFloat()
        if (i == 0) starPath.moveTo(px, py) else starPath.lineTo(px, py)
      }
      starPath.close()

      drawPath(
        path = starPath,
        brush = Brush.linearGradient(
          listOf(Color(0xFFFFF176), Color(0xFFFFB300), Color(0xFFFF8F00))
        )
      )

      // Remaining time arc indicator
      val ratio = food.remainingRatio
      drawArc(
        color = Color(0xFFFFD54F),
        startAngle = -90f,
        sweepAngle = 360f * ratio,
        useCenter = false,
        topLeft = Offset(centerX - currentRadius * 1.35f, centerY - currentRadius * 1.35f),
        size = Size(currentRadius * 2.7f, currentRadius * 2.7f),
        style = Stroke(width = 3f, cap = StrokeCap.Round)
      )
    }

    FoodType.SHIELD_POWERUP -> {
      // Glowing Cyan Shield power-up
      val shieldRadius = radius * pulseScale
      drawCircle(
        color = Color(0xFF00E5FF).copy(alpha = 0.35f),
        radius = shieldRadius * 1.45f,
        center = Offset(centerX, centerY)
      )
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(Color(0xFFE0F7FA), Color(0xFF00B0FF), Color(0xFF0059B2)),
          center = Offset(centerX, centerY)
        ),
        radius = shieldRadius,
        center = Offset(centerX, centerY)
      )
      // Shield emblem
      val crest = Path().apply {
        moveTo(centerX, centerY - shieldRadius * 0.6f)
        lineTo(centerX + shieldRadius * 0.5f, centerY - shieldRadius * 0.3f)
        lineTo(centerX + shieldRadius * 0.35f, centerY + shieldRadius * 0.4f)
        lineTo(centerX, centerY + shieldRadius * 0.65f)
        lineTo(centerX - shieldRadius * 0.35f, centerY + shieldRadius * 0.4f)
        lineTo(centerX - shieldRadius * 0.5f, centerY - shieldRadius * 0.3f)
        close()
      }
      drawPath(crest, color = Color.White)
    }
  }
}

private fun DrawScope.drawSnake(
  snake: List<Point2D>,
  direction: Direction,
  skin: SnakeSkin,
  cellW: Float,
  cellH: Float,
  isShieldActive: Boolean,
  shieldRemainingRatio: Float,
  isInvulnerable: Boolean,
  pulseScale: Float
) {
  val baseRadius = minOf(cellW, cellH) * 0.46f

  // If invulnerable (grace period after crash), blink effect
  if (isInvulnerable && (System.currentTimeMillis() / 150) % 2 == 0L) {
    return
  }

  // 1. Draw Body segments (from tail to neck)
  for (i in snake.indices.reversed()) {
    val seg = snake[i]
    val cx = (seg.x + 0.5f) * cellW
    val cy = (seg.y + 0.5f) * cellH

    if (i == 0) {
      // Head
      drawSnakeHead(
        cx = cx,
        cy = cy,
        direction = direction,
        radius = baseRadius,
        skin = skin,
        isShieldActive = isShieldActive,
        pulseScale = pulseScale
      )
    } else {
      // Body Segment
      val progress = i.toFloat() / snake.size.toFloat()
      val segmentColor = if (i % 2 == 0) skin.primaryColor else skin.secondaryColor
      val segRadius = baseRadius * (1f - (progress * 0.22f)) // slight taper toward tail

      // Glow behind segment
      drawCircle(
        color = skin.glowColor.copy(alpha = 0.2f),
        radius = segRadius * 1.25f,
        center = Offset(cx, cy)
      )

      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(segmentColor, skin.glowColor),
          center = Offset(cx - segRadius * 0.25f, cy - segRadius * 0.25f),
          radius = segRadius
        ),
        radius = segRadius,
        center = Offset(cx, cy)
      )

      // Spine / Scale dot accent
      drawCircle(
        color = Color.White.copy(alpha = 0.25f),
        radius = segRadius * 0.22f,
        center = Offset(cx, cy)
      )
    }
  }

  // 2. Draw active shield bubble aura if shielded
  if (isShieldActive) {
    val head = snake.first()
    val headX = (head.x + 0.5f) * cellW
    val headY = (head.y + 0.5f) * cellH

    drawCircle(
      color = Color(0xFF00E5FF).copy(alpha = 0.22f),
      radius = baseRadius * 1.8f * pulseScale,
      center = Offset(headX, headY)
    )
    drawCircle(
      color = Color(0xFF00E5FF),
      radius = baseRadius * 1.6f,
      center = Offset(headX, headY),
      style = Stroke(width = 3.5f)
    )
  }
}

private fun DrawScope.drawSnakeHead(
  cx: Float,
  cy: Float,
  direction: Direction,
  radius: Float,
  skin: SnakeSkin,
  isShieldActive: Boolean,
  pulseScale: Float
) {
  // Head outer glow
  drawCircle(
    color = (if (isShieldActive) Color(0xFF00E5FF) else skin.glowColor).copy(alpha = 0.45f),
    radius = radius * 1.4f,
    center = Offset(cx, cy)
  )

  // Head main circle
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(skin.secondaryColor, skin.primaryColor),
      center = Offset(cx, cy),
      radius = radius
    ),
    radius = radius,
    center = Offset(cx, cy)
  )

  // Eyes position based on direction
  val eyeOffsetDist = radius * 0.55f
  val eyeSize = radius * 0.32f
  val pupilSize = radius * 0.16f

  val (eye1, eye2) = when (direction) {
    Direction.UP -> Pair(
      Offset(cx - eyeOffsetDist, cy - eyeOffsetDist * 0.4f),
      Offset(cx + eyeOffsetDist, cy - eyeOffsetDist * 0.4f)
    )
    Direction.DOWN -> Pair(
      Offset(cx - eyeOffsetDist, cy + eyeOffsetDist * 0.4f),
      Offset(cx + eyeOffsetDist, cy + eyeOffsetDist * 0.4f)
    )
    Direction.LEFT -> Pair(
      Offset(cx - eyeOffsetDist * 0.4f, cy - eyeOffsetDist),
      Offset(cx - eyeOffsetDist * 0.4f, cy + eyeOffsetDist)
    )
    Direction.RIGHT -> Pair(
      Offset(cx + eyeOffsetDist * 0.4f, cy - eyeOffsetDist),
      Offset(cx + eyeOffsetDist * 0.4f, cy + eyeOffsetDist)
    )
  }

  // Draw eye whites/colors
  drawCircle(color = skin.eyeColor, radius = eyeSize, center = eye1)
  drawCircle(color = skin.eyeColor, radius = eyeSize, center = eye2)

  // Draw pupil black dots
  drawCircle(color = Color.Black, radius = pupilSize, center = eye1)
  drawCircle(color = Color.Black, radius = pupilSize, center = eye2)

  // Draw cute flicking tongue in current direction
  val tongueLength = radius * 0.65f
  val tonguePath = Path()
  when (direction) {
    Direction.UP -> {
      tonguePath.moveTo(cx, cy - radius)
      tonguePath.lineTo(cx, cy - radius - tongueLength)
      tonguePath.lineTo(cx - 5f, cy - radius - tongueLength - 8f)
      tonguePath.moveTo(cx, cy - radius - tongueLength)
      tonguePath.lineTo(cx + 5f, cy - radius - tongueLength - 8f)
    }
    Direction.DOWN -> {
      tonguePath.moveTo(cx, cy + radius)
      tonguePath.lineTo(cx, cy + radius + tongueLength)
      tonguePath.lineTo(cx - 5f, cy + radius + tongueLength + 8f)
      tonguePath.moveTo(cx, cy + radius + tongueLength)
      tonguePath.lineTo(cx + 5f, cy + radius + tongueLength + 8f)
    }
    Direction.LEFT -> {
      tonguePath.moveTo(cx - radius, cy)
      tonguePath.lineTo(cx - radius - tongueLength, cy)
      tonguePath.lineTo(cx - radius - tongueLength - 8f, cy - 5f)
      tonguePath.moveTo(cx - radius - tongueLength, cy)
      tonguePath.lineTo(cx - radius - tongueLength - 8f, cy + 5f)
    }
    Direction.RIGHT -> {
      tonguePath.moveTo(cx + radius, cy)
      tonguePath.lineTo(cx + radius + tongueLength, cy)
      tonguePath.lineTo(cx + radius + tongueLength + 8f, cy - 5f)
      tonguePath.moveTo(cx + radius + tongueLength, cy)
      tonguePath.lineTo(cx + radius + tongueLength + 8f, cy + 5f)
    }
  }
  drawPath(tonguePath, color = Color(0xFFFF1744), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
}
