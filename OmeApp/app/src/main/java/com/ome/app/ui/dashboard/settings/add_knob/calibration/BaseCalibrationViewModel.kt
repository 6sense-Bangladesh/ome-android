package com.ome.app.ui.dashboard.settings.add_knob.calibration

import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.websocket.WebSocketManager
import kotlin.math.abs

abstract class BaseCalibrationViewModel constructor(
    private val webSocketManager: WebSocketManager,
    private val stoveRepository: StoveRepository,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    val knobAngleLiveData = SingleLiveEvent<Float>()
    val zoneLiveData = SingleLiveEvent<Int>()

    var labelLiveData = SingleLiveEvent<Pair<CalibrationState, Float>>()

    var macAddress = ""

    var isDualKnob = false

    var offAngle: Float? = null
    var lowSingleAngle: Float? = null
    var mediumAngle: Float? = null
    var highSingleAngle: Float? = null
    var lowDualAngle: Float? = null
    var highDualAngle: Float? = null

    var leftAllowedZoneStartAngle: Float = 0f
    var leftAllowedZoneEndAngle: Float = 0f

    var rightAllowedZoneStartAngle: Float = 0f
    var rightAllowedZoneEndAngle: Float = 0f


    var currSetting: Int = 0

    var firstDiv: Int = 0
    var secondDiv: Int = 0

    var rotationDir: Int? = null

    val angleOffset = 15
    val angleDualOffset = 31

    val calibrationStatesSequenceSingleZone = arrayListOf(
        CalibrationState.OFF,
        CalibrationState.HIGH_SINGLE,
        CalibrationState.MEDIUM,
        CalibrationState.LOW_SINGLE
    )

    val calibrationStatesSequenceDualZone = arrayListOf(
        CalibrationState.OFF,
        CalibrationState.HIGH_SINGLE,
        CalibrationState.LOW_SINGLE,
        CalibrationState.HIGH_DUAL,
        CalibrationState.LOW_DUAL
    )


    private fun normalizeAngle(angle: Int): Int {
        return (angle + 360) % 360
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


    fun generateMediumAngle(highAngle: Int, lowAngle: Int): Int {
        var mediumAngle: Int = -1

        var hAngle = highAngle
        var lAngle = lowAngle

        if (abs(highAngle - lowAngle) > 180) {
            if (highAngle < lowAngle) {
                hAngle += 360
            } else {
                lAngle += 360
            }
        }
        mediumAngle = ((hAngle + lAngle) / 2) % 360

        return mediumAngle
    }


    open fun handleDualKnobUpdated(value: Float) {
        var angle = value
        var firstBorder = firstDiv
        var secBorder = secondDiv

        val currentStepAngle = labelLiveData.value?.second?.toInt()

        if (currSetting % 2 == 0 && currentStepAngle != null) {


            if (isAngleBetween(
                    angleAlpha = normalizeAngle(angle = firstDiv - 1),
                    angleBeta = secondDiv,
                    angleTheta = currentStepAngle
                )
            ) {
                firstBorder = normalizeAngle(angle = firstDiv - angleOffset)
                secBorder = normalizeAngle(angle = secondDiv + angleOffset)
            } else {
                firstBorder = normalizeAngle(angle = firstDiv + angleOffset)
                secBorder = normalizeAngle(angle = secondDiv - angleOffset)
            }

            if ((isAngleBetween(
                    angleAlpha = currentStepAngle,
                    angleBeta = firstBorder,
                    angleTheta = angle.toInt()
                )
                        || isAngleBetween(
                    angleAlpha = currentStepAngle,
                    angleBeta = secBorder,
                    angleTheta = angle.toInt()
                ))
                && !isAngleBetween(
                    angleAlpha = currentStepAngle + 10,
                    angleBeta = currentStepAngle - 10,
                    angleTheta = angle.toInt()
                )
            ) {
            } else {
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
                    if (firstBorder == normalizeAngle(angle = firstDiv + angleDualOffset)) {
                        firstBorder = normalizeAngle(angle = firstDiv - angleDualOffset)
                    } else {
                        firstBorder = normalizeAngle(angle = firstDiv + angleDualOffset)
                    }
                    angle = firstBorder.toFloat()
                } else if (isAngleBetween(
                        angleAlpha = highSingleAngle!!.toInt(),
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

        knobAngleLiveData.postValue(angle)
    }

    fun initSubscriptions() {
        launch(dispatcher = ioContext) {
            webSocketManager.knobAngleFlow.collect {
                it?.let {
                    var angle = it.value.toFloat()
                    if (!isDualKnob) {
                        knobAngleLiveData.postValue(angle)
                    } else {
                        if (offAngle != null) {
                            handleDualKnobUpdated(angle)
                        } else {
                            knobAngleLiveData.postValue(angle)
                        }
                    }

                }
            }
        }
        launch(dispatcher = ioContext) {
            stoveRepository.knobsFlow.collect { knobs ->
                knobs?.let {
                    val foundKnob = knobs.firstOrNull { it.macAddr == macAddress }
                    foundKnob?.let {
                        if (webSocketManager.knobAngleFlow.value == null) {
                            knobAngleLiveData.postValue(it.angle.toFloat())
                        }
                        zoneLiveData.postValue(foundKnob.stovePosition)
                    }
                }
            }
        }
    }
}
