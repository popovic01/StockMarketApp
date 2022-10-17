package com.example.stockmarketapp.presentation.company_info

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockmarketapp.domain.model.IntradayInfo
import com.example.stockmarketapp.ui.theme.Pink
import kotlin.math.round
import kotlin.math.roundToInt

@Composable
fun StockChart(
    infos: List<IntradayInfo> = emptyList(),
    modifier: Modifier = Modifier,
    graphColor: Color = Pink
) {
    val spacing = 100f
    val transparentGraphColor = remember {
        graphColor.copy(alpha = 0.5f)
    } //for color gradient

    //upper and lower value of the close values
    val upperValue = remember(infos) {
        (infos.maxOfOrNull { it.close }?.plus(1))?.roundToInt() ?: 0
    }
    val lowerValue = remember(infos) {
        infos.minOfOrNull { it.close }?.toInt() ?: 0
    }
    val density = LocalDensity.current //for converting sp to pixels
    //paint objects - to determine how things look on the canvas
    val textPaint = remember(density) {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = density.run { 12.sp.toPx() }
        }
    }
    //on canvas we can draw everything - text, images, shapes etc.
    Canvas(modifier = modifier) {
        val spacePerHour = (size.width - spacing) / infos.size //space in between each hour in pixels
        (0 until infos.size - 1 step 2).forEach { i ->
            val info = infos[i]
            val hour = info.date.hour
            //drawing hours on the x axis
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    hour.toString(),
                    spacing + i * spacePerHour,
                    size.height - 5,
                    textPaint
                )
            }
        }
        //calculating price step
        val priceStep = (upperValue - lowerValue) / 5f //we have 5 numbers we want to show
        (0..4).forEach() { i->
            //drawing close values on the y axis
            drawContext.canvas.nativeCanvas.apply {
                drawText(
                    round(lowerValue + priceStep * i).toString(),
                    30f,
                    size.height - spacing - i * size.height / 5f,
                    textPaint
                )
            }
        }
        var lastX = 0f
        //drawing graph
        val strokePath = Path().apply {
            val height = size.height
            for (i in infos.indices) {
                val info = infos[i]
                val nextInfo = infos.getOrNull(i + 1) ?: infos.last() //this ensures that app won't crash when i is the last index
                val leftRadio = (info.close - lowerValue) / (upperValue - lowerValue)
                val rightRatio = (nextInfo.close - lowerValue) / (upperValue - lowerValue)
                val x1 = spacing + i * spacePerHour
                val y1 = height - spacing - (leftRadio * height).toFloat()
                val x2 = spacing + (i + 1) * spacePerHour
                val y2 = height - spacing - (rightRatio * height).toFloat()
                //first dot of the curve
                if (i == 0) {
                    moveTo(x1, y1)
                }
                lastX = (x1 + x2) / 2f
                //drawing the line with smooth path (without corners)
                //control point determines how the curve is rounded
                quadraticBezierTo(
                    x1, y1, (x1 + x2) / 2f, (y1 + y2) / 2f //this formula is for smooth line
                )
            }
        }
        //drawing transparent gradient color of the graph
        //drawing same path, then down, left and up to close the path and give it a gradient
        val fillPath = android.graphics.Path(strokePath.asAndroidPath())
            .asComposePath() //now we have copied path
            .apply {
                //drawing closed shape
                lineTo(lastX, size.height - spacing)
                lineTo(spacing, size.height - spacing)
                close() //connecting the current coordinate of the path with the starting point
            }
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    transparentGraphColor,
                    Color.Transparent
                ),
                endY = size.height - spacing
            )
        )
        drawPath(
            path = strokePath,
            color = graphColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}