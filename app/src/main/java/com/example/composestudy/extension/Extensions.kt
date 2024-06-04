package com.example.composestudy.extension

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.composestudy.ui.theme.grayEA

fun Color.Companion.fromHex(s: String): Color = Color(android.graphics.Color.parseColor(s))

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.bottomBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = height),
                end = Offset(x = width, y = height),
                strokeWidth = strokeWidthPx
            )
        }
    }
)

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.spinnerBorder(strokeWidth: Dp, cornerRadiusDp: Dp, color: Color, expanded: Boolean) = composed(
    factory = {
        if (expanded) {
            val density = LocalDensity.current
            val strokeWidthPx = density.run { strokeWidth.toPx() }
            val cornerRadius = density.run { cornerRadiusDp.toPx() }

            Modifier.drawBehind {
                val width = size.width
                val height = size.height

                //left
                drawLine(
                    color = color,
                    start = Offset(x = 0f, y = height),
                    end = Offset(x = 0f, y = cornerRadius),
                    strokeWidth = strokeWidthPx
                )

                // Top left arc
                drawArc(
                    color = color,
                    startAngle = 180f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset.Zero,
                    size = Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(width = strokeWidthPx)
                )

                //top
                drawLine(
                    color = color,
                    start = Offset(x = cornerRadius, y = 0f),
                    end = Offset(x = width - cornerRadius, y = 0f),
                    strokeWidth = strokeWidthPx
                )

                // Top right arc
                drawArc(
                    color = color,
                    startAngle = 270f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(x = width - cornerRadius * 2, y = 0f),
                    size = Size(cornerRadius * 2, cornerRadius * 2),
                    style = Stroke(width = strokeWidthPx)
                )

                //right
                drawLine(
                    color = color,
                    start = Offset(x = width, y = height),
                    end = Offset(x = width, y = cornerRadius),
                    strokeWidth = strokeWidthPx
                )

            }
        } else {
            Modifier.border(width = 1.dp, color = grayEA, shape = RoundedCornerShape(4.dp))
        }
    }
)

@SuppressLint("ModifierFactoryUnreferencedReceiver")
fun Modifier.dropDownBorder(strokeWidth: Dp, cornerRadiusDp: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }
        val cornerRadius = density.run { cornerRadiusDp.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height

            //left
            drawLine(
                color = color,
                start = Offset(x = 0f, y = height - cornerRadius),
                end = Offset(x = 0f, y = 0f),
                strokeWidth = strokeWidthPx
            )

            // bottom left arc
            drawArc(
                color = color,
                startAngle = 90f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(0f, height - cornerRadius * 2),
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = Stroke(width = strokeWidthPx)
            )

            //bottom
            drawLine(
                color = color,
                start = Offset(x = cornerRadius, y = height),
                end = Offset(x = width - cornerRadius, y = height),
                strokeWidth = strokeWidthPx
            )

            // bottom right arc
            drawArc(
                color = color,
                startAngle = 0f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(x = width - cornerRadius * 2, y = height - cornerRadius * 2),
                size = Size(cornerRadius * 2, cornerRadius * 2),
                style = Stroke(width = strokeWidthPx)
            )

            //right
            drawLine(
                color = color,
                start = Offset(x = width, y = height - cornerRadius),
                end = Offset(x = width, y = 0f),
                strokeWidth = strokeWidthPx
            )

        }
    }
)