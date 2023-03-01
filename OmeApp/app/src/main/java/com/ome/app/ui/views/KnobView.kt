package com.ome.app.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.animation.RotateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import com.ome.Ome.databinding.KnobViewLayoutBinding
import com.ome.app.ui.dashboard.settings.add_knob.calibration.CalibrationState
import com.ome.app.utils.inflate
import com.ome.app.utils.makeGone
import com.ome.app.utils.makeVisible

class KnobView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {


    private val ANIMATION_DURATION: Long = 500

    private val binding = inflate<KnobViewLayoutBinding>()

    var prevAngle = 0.0f
    private var mCurrAngle = 0.0f

    init {

    }

    fun setKnobPosition(angle: Float) {
        animateCircle(mCurrAngle, angle, ANIMATION_DURATION)
    }

    fun hideLabel(label: CalibrationState) {
        when (label) {
            CalibrationState.OFF -> binding.offCl.makeGone()
            CalibrationState.LOW -> binding.lowCl.makeGone()
            CalibrationState.MEDIUM -> binding.mediumCl.makeGone()
            CalibrationState.HIGH -> binding.highCl.makeGone()
        }
    }

    fun setStovePosition(position: Int) {
        binding.stovePositionTv.text = position.toString()
    }

    fun setOffPosition(angle: Float) {
        binding.offCl.makeVisible()
        binding.offCl.rotation = angle
        binding.offTv.rotation = -angle
        animateCircle(mCurrAngle, angle, ANIMATION_DURATION)
    }

    fun setLowPosition(angle: Float) {
        binding.lowCl.makeVisible()
        binding.lowCl.rotation = angle
        binding.lowTv.rotation = -angle
        animateCircle(mCurrAngle, angle, ANIMATION_DURATION)
    }

    fun setMediumPosition(angle: Float) {
        binding.mediumCl.makeVisible()
        binding.mediumCl.rotation = angle
        binding.mediumTv.rotation = -angle
        animateCircle(mCurrAngle, angle, ANIMATION_DURATION)
    }

    fun setHighPosition(angle: Float) {
        binding.highCl.makeVisible()
        binding.highCl.rotation = angle
        binding.highTv.rotation = -angle
        animateCircle(mCurrAngle, angle, ANIMATION_DURATION)

    }

    private fun animateCircle(fromDeg: Float, toDeg: Float, durationMilis: Long) {
        val rotateAnimation = RotateAnimation(
            fromDeg, toDeg,
            RotateAnimation.RELATIVE_TO_SELF, 0.5F,
            RotateAnimation.RELATIVE_TO_SELF, 0.5F
        )

        rotateAnimation.duration = durationMilis
        rotateAnimation.isFillEnabled = true
        rotateAnimation.fillAfter = true

        binding.knobCircleCl.startAnimation(rotateAnimation)
        mCurrAngle = toDeg
    }

}
