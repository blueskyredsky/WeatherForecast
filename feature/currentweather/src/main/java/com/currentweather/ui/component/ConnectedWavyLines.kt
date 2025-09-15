package com.currentweather.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
internal fun ConnectedWavyLines(
    modifier: Modifier = Modifier,
    color: Color = Color.Black
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerX = width * 0.5f
        val centerY = height * 0.5f
        val circleRadius = 8.dp.toPx()
        val lineGap = 2.dp.toPx()

        val lineEndLeft = centerX - circleRadius - lineGap
        val lineStartRight = centerX + circleRadius + lineGap

        // Left Wavy Line with Gradient
        val pathLeft = Path().apply {
            moveTo(0f, centerY)
            quadraticTo(width * 0.2f, centerY * 0.7f, lineEndLeft, centerY)
        }
        drawPath(
            path = pathLeft,
            brush = Brush.horizontalGradient(
                colors = listOf(Color.LightGray, Color.DarkGray),
                startX = 0f,
                endX = lineEndLeft
            ),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Right Wavy Line
        val pathRight = Path().apply {
            moveTo(lineStartRight, centerY)
            cubicTo(
                width * 0.6f, centerY * 1.25f,
                width * 0.8f, centerY * 0.75f,
                width, centerY
            )
        }
        drawPath(
            path = pathRight,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // Connecting Circle
        drawCircle(
            color = color,
            radius = circleRadius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 2.dp.toPx())
        )

        // Vertical Line
        drawLine(
            color = color,
            start = Offset(centerX, centerY + circleRadius),
            end = Offset(centerX, centerY + circleRadius + 32.dp.toPx()),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}