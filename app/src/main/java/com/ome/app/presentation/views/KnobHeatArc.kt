package com.ome.app.presentation.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.ome.app.utils.dp
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

class HeatArcDrawer {
    data class Point(val x: Float, val y: Float)

    /**
     * Determines if we need to draw the arc in reverse direction to avoid the off angle
     */
    private fun shouldDrawReversePath(
        startAngle: Float,
        endAngle: Float,
        offAngle: Float
    ): Boolean {
        // Normalize angles to 0-360 range
        val normalizedStart = normalizeAngle(startAngle)
        val normalizedEnd = normalizeAngle(endAngle)
        val normalizedOff = normalizeAngle(offAngle)

        // Check if off angle is between start and end angles
        return if (normalizedStart <= normalizedEnd) {
            normalizedOff in normalizedStart..normalizedEnd
        } else {
            normalizedOff >= normalizedStart || normalizedOff <= normalizedEnd
        }
    }

    /**
     * Normalizes angle to 0-360 range
     */
    private fun normalizeAngle(angle: Float): Float {
        var normalized = angle % 360
        if (normalized < 0) normalized += 360
        return normalized
    }

    /**
     * Draws an arc with gradient, avoiding the off angle if necessary
     */
    fun drawGradientArc(
        canvas: Canvas,
        centerX: Float,
        centerY: Float,
        radius: Float,
        startAngle: Float,
        endAngle: Float,
        offAngle: Float,
        extensionDegrees: Float = 10f
    ) {
        val reversePath = shouldDrawReversePath(startAngle, endAngle, offAngle)

        val extendedStartAngle =
            startAngle - (if (reversePath) -extensionDegrees else extensionDegrees)
        val extendedEndAngle = endAngle + (if (reversePath) -extensionDegrees else extensionDegrees)

        // Calculate actual start and end points
        val startPoint = calculatePointOnCircle(centerX, centerY, radius, startAngle)
        val endPoint = calculatePointOnCircle(centerX, centerY, radius, endAngle)

        // Create gradient shader
        val shader = LinearGradient(
            startPoint.x,
            startPoint.y,
            endPoint.x,
            endPoint.y,
            intArrayOf(
                Color.parseColor("#FF3939"),
                Color.parseColor("#FFA81C"),
                Color.parseColor("#D2CB00"),
            ),
            floatArrayOf(0f, .5f, 1f),
            Shader.TileMode.CLAMP
        )

        val paint = Paint().apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.ROUND
            strokeWidth = 10f.dp
            isAntiAlias = true
            setShader(shader)
        }

        // Create bounding rectangle for the arc
        val rect = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        // Calculate sweep angle based on direction
        val sweepAngle = if (reversePath) {
            if (endAngle > startAngle) {
                -(360 - (extendedEndAngle - extendedStartAngle))
            } else {
                -(extendedStartAngle - extendedEndAngle)
            }
        } else {
            if (endAngle > startAngle) {
                extendedEndAngle - extendedStartAngle
            } else {
                360 - (extendedStartAngle - extendedEndAngle)
            }
        }

        // Draw the extended arc
        canvas.drawArc(rect, extendedStartAngle, sweepAngle, false, paint)
    }

    private fun calculatePointOnCircle(
        centerX: Float,
        centerY: Float,
        radius: Float,
        angleDegrees: Float
    ): Point {
        val angleRadians = Math.toRadians(angleDegrees.toDouble())
        val x = centerX + radius * cos(angleRadians).toFloat()
        val y = centerY + radius * sin(angleRadians).toFloat()
        return Point(x, y)
    }
}

//

class KnobHeatArc @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val gradientArcDrawer = HeatArcDrawer()

    var lowAngle: Float = 0f
        set(value) {
            field = value
            Log.d("KnobProgressView", "Low angle set to: $value")
            invalidate()
        }

    var highAngle: Float = 0f
        set(value) {
            field = value
            Log.d("KnobProgressView", "High angle set to: $value")
            invalidate()
        }

    var offAngle: Float = 0f
        set(value) {
            field = value
            Log.d("KnobProgressView", "Off angle set to: $value")
            invalidate()
        }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Draw the gradient arc
        gradientArcDrawer.drawGradientArc(
            canvas = canvas,
            centerX = width / 2f,
            centerY = height / 2f,
            radius = minOf(width, height) / 2.2f,
            startAngle = highAngle,
            endAngle = lowAngle,
            offAngle = offAngle,
            extensionDegrees = if (max(lowAngle, highAngle) == 0f) 0f else 10f
        )
    }
}