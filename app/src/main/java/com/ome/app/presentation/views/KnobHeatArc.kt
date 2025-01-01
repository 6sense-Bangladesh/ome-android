package com.ome.app.presentation.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.ome.app.utils.dp
import com.ome.app.utils.log
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
        strokeSize: Int = 10.dp,
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
            strokeWidth = strokeSize.toFloat()
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

    init {
        rotation = -90f
    }

    private var lowAngle: Float = 0f
    private var highAngle: Float = 0f
    private var offAngle: Float = 0f
    private var strokeSize: Int = 10

    enum class Size(val value: Int){
        Large(12.dp),
        Medium(10.dp),
        Small(7.dp)
    }

    fun setupArc(lowAngle: Int, highAngle: Int, offAngle: Int, size: Size = Size.Large){
        "l-$lowAngle h-$highAngle o-$offAngle".log("KnobProgressView")
        this.lowAngle = lowAngle.toFloat()
        this.highAngle = highAngle.toFloat()
        this.offAngle = offAngle.toFloat()
        strokeSize = size.value
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
            strokeSize = strokeSize,
            extensionDegrees = if (max(lowAngle, highAngle) == 0f) 0f else 10f
        )
    }
}