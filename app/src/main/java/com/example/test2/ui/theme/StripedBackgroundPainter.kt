package com.example.test2.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class StripedBackgroundPainter(
    private val white: Color = Color.White,
    private val redLight: Color,
    private val primary: Color,
    private val redLighter: Color,
    private val redDarker: Color
) : Painter() {

    override val intrinsicSize = Size.Unspecified

    override fun DrawScope.onDraw() {
        // Fondo blanco
        drawRect(white)

        fun drawStripe(
            color: Color,
            left: Dp,
            width: Dp
        ) {
            drawRect(
                color = color,
                topLeft = Offset(left.toPx(), 0f),
                size = Size(
                    width = width.toPx(),
                    height = size.height
                )
            )
        }

        // Franjas del extremo izquierdo
        drawStripe(redLight, 0.dp, 30.dp)
        drawStripe(primary, 30.dp, 30.dp)
        drawStripe(redLighter, 60.dp, 35.dp)
        drawStripe(redDarker, 95.dp, 15.dp)

        // Franjas del extremo derecho
        drawStripe(redDarker, size.width.toDp() - 110.dp, 15.dp)
        drawStripe(redLighter, size.width.toDp() - 95.dp, 35.dp)
        drawStripe(primary, size.width.toDp() - 60.dp, 30.dp)
        drawStripe(redLight, size.width.toDp() - 30.dp, 30.dp)
    }
}