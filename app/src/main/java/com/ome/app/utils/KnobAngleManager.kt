package com.ome.app.utils

import com.ome.app.domain.model.base.Pointer
import com.ome.app.presentation.dashboard.settings.add_knob.calibration.CalibrationState
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

object KnobAngleManager {

    fun validateSingleKnobAngle(
        angle: Float,
        calibrationState: CalibrationState,
        highSingleAngle: Float?,
        mediumAngle: Float?,
        offAngle: Float?,
        lowSingleAngle: Float?,
        angleOffset: Int
    ): Boolean {
        if (calibrationState == CalibrationState.LOW_SINGLE) {
            if (highSingleAngle != null && mediumAngle != null) {
                if(angle.isInRangeShortagePath(highSingleAngle, mediumAngle) && offAngle?.isInRangeShortagePath(highSingleAngle, mediumAngle).isFalse() )
                    return false
                else if(!angle.isInRangeShortagePath(highSingleAngle, mediumAngle) && angle.isInRangeClockwise(highSingleAngle, mediumAngle) && offAngle?.isInRangeShortagePath(highSingleAngle, mediumAngle).isTrue())
                    return false
            }
        }
        offAngle?.let { if (abs(angle - it) < angleOffset) return false }
        lowSingleAngle?.let { if (abs(angle - it) < angleOffset) return false }
        mediumAngle?.let { if (abs(angle - it) < angleOffset) return false }
        highSingleAngle?.let { if (abs(angle - it) < angleOffset) return false }
        return true
    }

    fun validateDualKnobAngle(
        angle: Float,
        offAngle: Float?,
        highSingleAngle: Float?,
        lowSingleAngle: Float?,
        highDualAngle: Float?,
        lowDualAngle: Float?,
        angleOffset: Int
    ): Boolean {
        offAngle?.let { if (abs(angle - it) < angleOffset) return false }
        lowSingleAngle?.let { if (abs(angle - it) < angleOffset) return false }
        highDualAngle?.let { if (abs(angle - it) < angleOffset) return false }
        lowDualAngle?.let { if (abs(angle - it) < angleOffset) return false }
        highSingleAngle?.let { if (abs(angle - it) < angleOffset) return false }
        return true
    }

    fun generateMediumAngle(highAngle: Int, lowAngle: Int): Int {
        var hAngle = highAngle
        var lAngle = lowAngle

        if (abs(highAngle - lowAngle) > 180) {
            if (highAngle < lowAngle) {
                hAngle += 360
            } else {
                lAngle += 360
            }
        }
        val mediumAngle = ((hAngle + lAngle) / 2) % 360

        log("highAngle: $highAngle, lowAngle: $lowAngle, result: $mediumAngle")

        return mediumAngle
    }


    /**
     * Normalizes an angle to be within the range of 0 to 359 degrees (inclusive).
     *
     * @param angle The angle to normalize.
     * @return The normalized angle within the range of 0 to 359 degrees.
     */
    fun normalizeAngle(angle: Number): Int {
        return (((angle.toDouble() % 360) + 360) % 360).toInt()
    }


    fun averageAngle(angle1: Int, angle2: Int): Int {
        val normalizedAngle1 = normalizeAngle(angle1)
        val normalizedAngle2 = normalizeAngle(angle2)

        val delta = normalizeAngle(normalizedAngle2 - normalizedAngle1)

        // Find the shortest path between the two angles
        val average = if (delta <= 180) {
            normalizedAngle1 + delta / 2
        } else {
            normalizedAngle1 - (360 - delta) / 2
        }

        return normalizeAngle(average).apply {
            "angle1: $angle1, angle2: $angle2, averageAngle: $this".log("averageAngle")
        }
    }


    /**
     * Processes the result of a dual knob interaction, adjusting the angle value based on various conditions.
     *
     * This function handles the logc for adjusting the angle of a dual knob control, ensuring it stays within
     * defined boundaries and behaves correctly based on the current setting and other parameters.
     *
     * @param angleValue The initial angle value from the knob interaction.
     * @param firstDiv The angle representing the first division start point.
     * @param secondDiv The angle representing the second division start point.
     * @param currentStepAngle The current step angle of the knob.
     * @param currSetPosition The current step position to set.
     * @param highSingleAngle The angle representing the high point in single knob mode.
     * @param angleDualOffset The offset angle applied in dual knob mode.
     * @return The adjusted angle value.
     */
    fun processDualKnobRotation(
        angleValue: Float,
        firstDiv: Int,
        secondDiv: Int,
        currentStepAngle: Int?,
        currSetPosition: Int,
        highSingleAngle: Float?,
        angleDualOffset: Int
    ): Float {
        var angle = angleValue
        var firstBorder: Int
        var secBorder: Int

        // Handle logc for even set position with a current step angle
        if (currSetPosition % 2 == 0 && currentStepAngle != null) {
            // Determine the borders based on the current step angle's position
            if (isAngleBetween(
                    angleAlpha = normalizeAngle(angle = firstDiv - 1),
                    angleBeta = secondDiv,
                    angleTheta = currentStepAngle
                )
            ) {
                firstBorder = normalizeAngle(angle = firstDiv - angleDualOffset)
                secBorder = normalizeAngle(angle = secondDiv + angleDualOffset)
            } else {
                firstBorder = normalizeAngle(angle = firstDiv + angleDualOffset)
                secBorder = normalizeAngle(angle = secondDiv - angleDualOffset)
            }

            // Adjust angle if it's not within the allowed range or near the current step angle
            if (!(isAngleBetween(
                    angleAlpha = currentStepAngle,
                    angleBeta = firstBorder,
                    angleTheta = angle.toInt()
                ) || isAngleBetween(
                    angleAlpha = currentStepAngle,
                    angleBeta = secBorder,
                    angleTheta = angle.toInt()
                )) || isAngleBetween(
                    angleAlpha = currentStepAngle + 10,
                    angleBeta = currentStepAngle - 10,
                    angleTheta = angle.toInt()
                )
            ) {
                // Fine-tune angle if it's close to the current step angle
                if (isAngleBetween(
                        angleAlpha = currentStepAngle + 10,
                        angleBeta = currentStepAngle,
                        angleTheta = angle.toInt()
                    )
                ) {
                    angle = (currentStepAngle + 10).toFloat()
                } else if (isAngleBetween(
                        angleAlpha = currentStepAngle - 10,
                        angleBeta = currentStepAngle,
                        angleTheta = angle.toInt()
                    )
                ) {
                    angle = (currentStepAngle - 10).toFloat()
                } else if (currentStepAngle == angle.toInt()) {
                    angle = (currentStepAngle - 10).toFloat()
                } else {
                    // Snap angle to the nearest border if it's within a certain range
                    if (isAngleBetween(
                            angleAlpha = firstBorder,
                            angleBeta = angle.toInt(),
                            angleTheta = firstDiv
                        ) || isAngleBetween(
                            angleAlpha = firstBorder,
                            angleBeta = firstDiv,
                            angleTheta = angle.toInt()
                        ) || angle.toInt() == firstDiv
                    ) {
                        angle = firstBorder.toFloat()
                    } else if (isAngleBetween(
                            angleAlpha = secBorder,
                            angleBeta = angle.toInt(),
                            angleTheta = secondDiv
                        ) || isAngleBetween(
                            angleAlpha = secBorder,
                            angleBeta = secondDiv,
                            angleTheta = angle.toInt()
                        ) || angle.toInt() == secondDiv
                    ) {
                        angle = secBorder.toFloat()
                    }
                }
            }
        } else {
            // Handle logc for odd settings or when there's no current step angle
            // Determine the borders based on the angle's position
            if (isAngleBetween(
                    angleAlpha = normalizeAngle(angle = firstDiv - 1),
                    angleBeta = secondDiv,
                    angleTheta = angle.toInt()
                )
            ) {
                firstBorder = normalizeAngle(angle = firstDiv - angleDualOffset)
                secBorder = normalizeAngle(angle = secondDiv + angleDualOffset)
            } else {
                firstBorder = normalizeAngle(angle = firstDiv + angleDualOffset)
                secBorder = normalizeAngle(angle = secondDiv - angleDualOffset)
            }

            // Snap angle to the nearest border if it's within a certain range
            if (isAngleBetween(
                    angleAlpha = firstBorder, angleBeta = firstDiv, angleTheta = angle.toInt()
                ) || angle.toInt() == firstDiv
            ) {
                angle = firstBorder.toFloat()
            } else if (isAngleBetween(
                    angleAlpha = secBorder, angleBeta = secondDiv, angleTheta = angle.toInt()
                ) || angle.toInt() == secondDiv
            ) {
                angle = secBorder.toFloat()
            }

            // Handle specific logc for setting 3
            if (currSetPosition == 3) {
                // Adjust borders and angle based on highSingleAngle
                if (isAngleBetween(
                        angleAlpha = highSingleAngle!!.toInt(),
                        angleBeta = firstDiv,
                        angleTheta = firstBorder
                    )
                ) {
                    firstBorder = if (firstBorder == normalizeAngle(angle = firstDiv + angleDualOffset))
                        normalizeAngle(angle = firstDiv - angleDualOffset)
                    else
                        normalizeAngle(angle = firstDiv + angleDualOffset)
                    angle = firstBorder.toFloat()
                } else if (isAngleBetween(
                        angleAlpha = highSingleAngle.toInt(),
                        angleBeta = secondDiv,
                        angleTheta = secBorder
                    )
                ) {
                    secBorder =
                        if (secBorder == normalizeAngle(angle = secondDiv + angleDualOffset)) {
                            normalizeAngle(angle = secondDiv - angleDualOffset)
                        } else {
                            normalizeAngle(angle = secondDiv + angleDualOffset)
                        }

                    angle = secBorder.toFloat()
                }

            }
        }
        return angle
    }

//    fun processDualKnobRotation(
//        initAngle: StateFlow<Int?>,
//        newAngle: Float,
//        offAngle: Int,
//        angleDualOffset: Int = 25
//    ): Float {
//        return processDualKnobResult(
//            initAngle = initAngle,
//            newAngle = newAngle,
//            firstDiv = offAngle,
//            angleDualOffset = angleDualOffset,
//            offAngle = offAngle
//        )
//    }

    private var oldAngle = 0F
    fun processDualKnob2ProtectOffOpposite(
        newAngle: Float,
        offAngle: Int,
        angleDualOffset: Int,
    ): Float {
        val secondDiv = normalizeAngle(offAngle + 180)
        val p1 = normalizeAngle(secondDiv - angleDualOffset)
        val p2 = normalizeAngle(secondDiv + angleDualOffset)
        return if(!newAngle.isInRange(p1, p2))
            newAngle.also { oldAngle = it }
        else
            oldAngle
    }

    fun processDualKnobRotation(
        initAngle: Pointer<Int>,
        newAngle: Float,
        offAngle: Int, //firstDiv
        angleDualOffset: Int = 25,
        isRightZone: Boolean? = null
    ): Float {
        val firstDiv = normalizeAngle(offAngle)
        val secondDiv = normalizeAngle(offAngle + 180)
        // Normalize the input angle to the 0–360 range
        val isFirstZone = isRightZone ?: initAngle.isRightZone(offAngle) ?: return newAngle
        val normalizedAngle = normalizeAngle(newAngle)
        println("normalizedAngle: $normalizedAngle, angleValue: $newAngle, firstDiv: $firstDiv, secondDiv: $secondDiv".log("processDualKnobResult"))
        return if (isFirstZone) {
            // If keeping between first and second division, apply the offset within the range
            if (normalizedAngle.isInRange(angleAlpha = firstDiv + angleDualOffset, angleBeta = secondDiv - angleDualOffset).ifTrue { log("processDualKnobResult if") })
                normalizedAngle.toFloat()
            else if (normalizedAngle.isInRange(angleAlpha = firstDiv - 90, angleBeta = firstDiv + angleDualOffset).ifTrue{ log("processDualKnobResult else if 1st") })
                offAngle.toFloat()
            else //if (normalizedAngle.isInRange(angleAlpha = normalizeAngle(secondDiv + angleDualOffset), angleBeta = normalizeAngle(secondDiv + 90)).ifTrue{ log("processDualKnobResult !else if 2nd") }) {
                normalizeAngle(secondDiv - angleDualOffset).toFloat()
        } else {
            if (normalizedAngle.isInRange(angleAlpha = secondDiv + angleDualOffset, angleBeta = firstDiv - angleDualOffset).ifTrue { log("processDualKnobResult !if") })
                normalizedAngle.toFloat()
            else if (normalizedAngle.isInRange(angleAlpha = firstDiv - angleDualOffset, angleBeta = firstDiv + 90).ifTrue { log("processDualKnobResult !else if 1st") })
                offAngle.toFloat()
            else //if (normalizedAngle.isInRange(angleAlpha = normalizeAngle(firstDiv + 90), angleBeta = normalizeAngle( secondDiv + angleDualOffset)).ifTrue { log("processDualKnobResult !else if 2nd") }) {
                normalizeAngle(secondDiv + angleDualOffset).toFloat()
        }
    }

    fun Pointer<Int>.isRightZone(
        offAngle: Int,
    ): Boolean? = value?.isInRange(offAngle, normalizeAngle(offAngle + 180))


    /**
     * Checks if an angle (angleTheta) is between two other angles (angleAlpha and angleBeta).
     *
     * @param angleAlpha The starting angle of the range.
     * @param angleBeta The ending angle of the range.
     * @param angleTheta The angle to check if it's within the range.
     * @return True if angleTheta is between angleAlpha and angleBeta, false otherwise.
     */
    private fun isAngleBetween(angleAlpha: Int, angleBeta: Int, angleTheta: Int): Boolean {
        var alpha = angleAlpha
        var beta = angleBeta
        var theta = angleTheta

        while (abs(alpha - beta) > 180) {
            if (alpha > beta) {
                beta += 360
            } else {
                alpha += 360
            }
        }

        if (alpha > beta) {
            val delta = alpha
            alpha = beta
            beta = delta
        }

        theta += (((beta - theta) / 360) * 360)

        return (alpha < theta) && (theta < beta)
    }

    /**
     * Checks if an angle (angleTheta) is between two other angles (angleAlpha and angleBeta).
     *
     * @param angleAlpha The starting angle of the range.
     * @param angleBeta The ending angle of the range.
     * @return True if angleTheta is between angleAlpha and angleBeta, false otherwise.
     */
    private fun Number.isInRange(angleAlpha: Number, angleBeta: Number): Boolean {
        // Normalize all angles
        val alpha = normalizeAngle(angleAlpha)
        val beta = normalizeAngle(angleBeta)
        val theta = normalizeAngle(this)

        return if (alpha <= beta) {
            theta in alpha..beta
        } else {
            // Handles the case where the range wraps around 0 (e.g., 350° to 10°)
            theta >= alpha || theta <= beta
        }
    }

    private fun Number.isInRangeClockwise(angleAlpha: Number, angleBeta: Number): Boolean {
        val alpha = normalizeAngle(min(angleAlpha.toInt(), angleBeta.toInt()))
        val beta = normalizeAngle(max(angleAlpha.toInt(), angleBeta.toInt()))

        return isInRange(alpha, beta)
    }
    private fun Number.isInRangeShortagePath(angleAlpha: Number, angleBeta: Number, useShortPath: Boolean = true): Boolean {
        // Normalize all angles to the range [0, 360)
        val alpha = normalizeAngle(angleAlpha)
        val beta = normalizeAngle(angleBeta)
        val theta = normalizeAngle(this)

        // Calculate the clockwise and counterclockwise distance
        val clockwiseDistance = (beta - alpha + 360) % 360
        val counterClockwiseDistance = (alpha - beta + 360) % 360

        return if (useShortPath) {
            // Shortest path based on the shortest distance
            if (clockwiseDistance <= counterClockwiseDistance) {
                // Shortest path is clockwise
                if (alpha <= beta) theta in alpha..beta else theta >= alpha || theta <= beta
            } else {
                // Shortest path is counterclockwise
                if (beta <= alpha) theta in beta..alpha else theta >= beta || theta <= alpha
            }
        } else {
            // If we are not using the shortest path, just check clockwise range
            if (alpha <= beta) {
                theta in alpha..beta
            } else {
                theta >= alpha || theta <= beta
            }
        }
    }


    private fun calculateSweepAngle(startAngle: Int, endAngle: Int): Int {
        return if (startAngle <= endAngle) {
            endAngle - startAngle
        } else {
            359 - (startAngle - endAngle)
        }
    }

    fun isAngleWithinSweep(angle: Int, startAngle: Int, endAngle: Int): Boolean {
        val sweepAngle = calculateSweepAngle(startAngle, endAngle)
        val normalizedAngle = normalizeAngle(angle - startAngle)
        return normalizedAngle in 0..sweepAngle
    }

     fun calculateAngularDistance(angle1: Int, angle2: Int): Int {
        var distance = abs(angle2 - angle1)
        if (distance > 180) {
            distance = 360 - distance
        }
        return distance
     }
}

fun main() {
    println(
        KnobAngleManager.processDualKnobRotation(
            initAngle = Pointer(320),
            newAngle = 160F,
            offAngle = 0
        )
    )
}
