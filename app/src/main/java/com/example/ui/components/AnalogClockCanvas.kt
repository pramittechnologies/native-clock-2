package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnalogClockCanvas(
    timeMillis: Long,
    modifier: Modifier = Modifier,
    timeZoneId: String = TimeZone.getDefault().id,
    size: Dp = 160.dp,
    showSeconds: Boolean = true,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    accentColor: Color = MaterialTheme.colorScheme.tertiary,
    surfaceColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onSurfaceColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId)).apply {
        timeInMillis = timeMillis
    }

    val hours = calendar.get(Calendar.HOUR)
    val minutes = calendar.get(Calendar.MINUTE)
    val seconds = calendar.get(Calendar.SECOND)

    val hourAngle = (hours + minutes / 60f + seconds / 3600f) * 30f - 90f
    val minuteAngle = (minutes + seconds / 60f) * 6f - 90f
    val secondAngle = seconds * 6f - 90f

    val animatedSecondAngle by animateFloatAsState(
        targetValue = secondAngle,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "second_angle"
    )

    Canvas(modifier = modifier.size(size)) {
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val radius = this.size.minDimension / 2f - 4.dp.toPx()

        // Background Disc
        drawCircle(
            color = surfaceColor.copy(alpha = 0.6f),
            radius = radius,
            center = center
        )

        // Outer Ring
        drawCircle(
            color = onSurfaceColor.copy(alpha = 0.25f),
            radius = radius,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )

        // Hour Ticks (12 hours)
        for (i in 0 until 12) {
            val angleDeg = i * 30.0 - 90.0
            val angleRad = Math.toRadians(angleDeg)
            val isMainTick = (i % 3 == 0)

            val tickLength = if (isMainTick) radius * 0.18f else radius * 0.10f
            val strokeWidth = if (isMainTick) 2.5.dp.toPx() else 1.2.dp.toPx()
            val tickColor = if (isMainTick) primaryColor else onSurfaceColor.copy(alpha = 0.5f)

            val outerX = center.x + (radius - 4.dp.toPx()) * cos(angleRad).toFloat()
            val outerY = center.y + (radius - 4.dp.toPx()) * sin(angleRad).toFloat()
            val innerX = center.x + (radius - 4.dp.toPx() - tickLength) * cos(angleRad).toFloat()
            val innerY = center.y + (radius - 4.dp.toPx() - tickLength) * sin(angleRad).toFloat()

            drawLine(
                color = tickColor,
                start = Offset(innerX, innerY),
                end = Offset(outerX, outerY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }

        // Hour Hand
        val hourRad = Math.toRadians(hourAngle.toDouble())
        val hourLength = radius * 0.52f
        val hourEndX = center.x + hourLength * cos(hourRad).toFloat()
        val hourEndY = center.y + hourLength * sin(hourRad).toFloat()
        drawLine(
            color = onSurfaceColor,
            start = center,
            end = Offset(hourEndX, hourEndY),
            strokeWidth = 3.8.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Minute Hand
        val minuteRad = Math.toRadians(minuteAngle.toDouble())
        val minuteLength = radius * 0.72f
        val minuteEndX = center.x + minuteLength * cos(minuteRad).toFloat()
        val minuteEndY = center.y + minuteLength * sin(minuteRad).toFloat()
        drawLine(
            color = primaryColor,
            start = center,
            end = Offset(minuteEndX, minuteEndY),
            strokeWidth = 2.6.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Second Hand
        if (showSeconds) {
            val secondRad = Math.toRadians(animatedSecondAngle.toDouble())
            val secondLength = radius * 0.82f
            val secondTailLength = radius * 0.18f

            val secondEndX = center.x + secondLength * cos(secondRad).toFloat()
            val secondEndY = center.y + secondLength * sin(secondRad).toFloat()
            val secondTailX = center.x - secondTailLength * cos(secondRad).toFloat()
            val secondTailY = center.y - secondTailLength * sin(secondRad).toFloat()

            drawLine(
                color = accentColor,
                start = Offset(secondTailX, secondTailY),
                end = Offset(secondEndX, secondEndY),
                strokeWidth = 1.6.dp.toPx(),
                cap = StrokeCap.Round
            )

            // Second hand small dot
            drawCircle(
                color = accentColor,
                radius = 3.dp.toPx(),
                center = center
            )
        }

        // Center Pin
        drawCircle(
            color = primaryColor,
            radius = 4.5.dp.toPx(),
            center = center
        )
        drawCircle(
            color = surfaceColor,
            radius = 2.dp.toPx(),
            center = center
        )
    }
}
