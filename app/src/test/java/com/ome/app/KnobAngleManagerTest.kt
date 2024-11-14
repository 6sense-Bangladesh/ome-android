package com.ome.app

import com.ome.app.presentation.dashboard.settings.add_knob.calibration.CalibrationState
import com.ome.app.utils.KnobAngleManager
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class KnobAngleManagerTest {

    @Test
    fun validateSingleKnobAngle_ReturnsTrue() {
        assertTrue(
            KnobAngleManager.validateSingleKnobAngle(
                angle = 45f,
                calibrationState = CalibrationState.LOW_SINGLE,
                offAngle = 359f,
                highSingleAngle = 180f,
                mediumAngle = 90f,
                lowSingleAngle = null,
                angleOffset = 15
            )
        )
    }

    @Test
    fun validateSingleKnobAngle_LowBetweenMediumAndHigh_ReturnsFalse() {
        assertFalse(
            KnobAngleManager.validateSingleKnobAngle(
                angle = 90f,
                calibrationState = CalibrationState.LOW_SINGLE,
                offAngle = 359f,
                highSingleAngle = 180f,
                mediumAngle = 45f,
                lowSingleAngle = null,
                angleOffset = 15
            )
        )
    }

    @Test
    fun validateSingleKnobAngle_labelOnTopAnother_ReturnsFalse() {
        assertFalse(
            KnobAngleManager.validateSingleKnobAngle(
                angle = 170f,
                calibrationState = CalibrationState.MEDIUM,
                offAngle = 359f,
                highSingleAngle = 180f,
                mediumAngle = null,
                lowSingleAngle = null,
                angleOffset = 15
            )
        )
    }

    @Test
    fun validateDualKnobAngle_OffPositionSet_ReturnsTrue() {
        assertTrue(
            KnobAngleManager.processDualKnobResult(
                angleValue = 187f,
                firstDiv = 0,
                secondDiv = 180,
                currentStepAngle = 0,
                currSetPosition = 1,
                highSingleAngle = null,
                angleDualOffset = 31
            ) == 211f
        )
    }

    @Test
    fun validateDualKnobAngle_HighSinglePositionSetOppositeBottomAngle_ReturnsTrue() {
        assertTrue(
            KnobAngleManager.processDualKnobResult(
                angleValue = 211f,
                firstDiv = 0,
                secondDiv = 180,
                currentStepAngle = 140,
                currSetPosition = 2,
                highSingleAngle = 140f,
                angleDualOffset = 31
            ) == 149f
        )
    }

    @Test
    fun validateDualKnobAngle_HighSinglePositionSetOppositeTopAngle_ReturnsTrue() {
        assertTrue(
            KnobAngleManager.processDualKnobResult(
                angleValue = 336f,
                firstDiv = 0,
                secondDiv = 180,
                currentStepAngle = 140,
                currSetPosition = 2,
                highSingleAngle = 140f,
                angleDualOffset = 31
            ) == 31f
        )
    }


    @Test
    fun validateDualKnobAngle_LowSingleSetPointerInSingleSection_ReturnsTrue() {
        assertTrue(
            KnobAngleManager.processDualKnobResult(
                angleValue = 84f,
                firstDiv = 0,
                secondDiv = 180,
                currentStepAngle = 42,
                currSetPosition = 3,
                highSingleAngle = 140f,
                angleDualOffset = 31
            ) == 329f
        )
    }


}
