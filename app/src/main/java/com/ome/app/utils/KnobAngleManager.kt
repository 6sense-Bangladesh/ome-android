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
        when (calibrationState) {
            CalibrationState.OFF -> {}
            CalibrationState.HIGH_SINGLE -> {}
            CalibrationState.MEDIUM -> {}
            CalibrationState.LOW_SINGLE -> {
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
            else -> {}
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


    private fun normalizeAngle(angle: Int): Int {
        return (angle + 360) % 360
    }


    fun processDualKnobResult(
        angleValue: Float,
        firstDiv: Int,
        secondDiv: Int,
        currentStepAngle: Int?,
        currSetting: Int,
        highSingleAngle: Float?,
        angleDualOffset: Int
    ): Float {
        var angle = angleValue
        var firstBorder: Int
        var secBorder: Int


        if (currSetting % 2 == 0 && currentStepAngle != null) {


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


            if (currSetting == 3) {
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
