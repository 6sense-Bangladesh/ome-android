package com.ome.app.utils

import com.ome.app.presentation.dashboard.settings.add_knob.calibration.CalibrationState
import kotlin.math.abs

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
                if (highSingleAngle > mediumAngle) {
                    if (angle in mediumAngle..highSingleAngle) {
                        return false
                    }
                } else {
                    if (angle in highSingleAngle..mediumAngle) {
                        return false
                    }
                }
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
        highSingleAngle: Float?,
        mediumAngle: Float?,
        offAngle: Float?,
        lowSingleAngle: Float?,
        angleOffset: Int
    ): Boolean {
        offAngle?.let { if (abs(angle - it) < angleOffset) return false }
        lowSingleAngle?.let { if (abs(angle - it) < angleOffset) return false }
        mediumAngle?.let { if (abs(angle - it) < angleOffset) return false }
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

        logi("highAngle: $highAngle, lowAngle: $lowAngle, result: $mediumAngle")

        return mediumAngle
    }


    /**
     * Normalizes an angle to be within the range of 0 to 359 degrees (inclusive).
     *
     * @param angle The angle to normalize.
     * @return The normalized angle within the range of 0 to 359 degrees.
     */
    fun normalizeAngle(angle: Number): Int {
        return (angle.toInt() + 360) % 360
    }


    /**
     * Processes the result of a dual knob interaction, adjusting the angle value based on various conditions.
     *
     * This function handles the logic for adjusting the angle of a dual knob control, ensuring it stays within
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
    fun processDualKnobResult(
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

        // Handle logic for even set position with a current step angle
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
                )
                        || isAngleBetween(
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
            // Handle logic for odd settings or when there's no current step angle
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

            // Handle specific logic for setting 3
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
}
