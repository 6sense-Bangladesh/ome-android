package com.ome.app.ui.views

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.util.TypedValue
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.constraintlayout.widget.ConstraintLayout
import com.ome.Ome.databinding.KnobViewLayoutBinding
import com.ome.app.ui.dashboard.settings.add_knob.calibration.CalibrationState
import com.ome.app.utils.inflate
import com.ome.app.utils.logi
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
        if(angle!=mCurrAngle){
            logi("angle KnobView $angle")
            animateCircle(mCurrAngle, angle, ANIMATION_DURATION)
        }
    }

    fun setFontSize(size: Float) {
        binding.offTv.textSize = size
        binding.lowSingleTv.textSize = size
        binding.lowDualTv.textSize = size
        binding.mediumTv.textSize = size
        binding.highSingleTv.textSize = size
        binding.highDualTv.textSize = size
    }


    fun hideLabel(label: CalibrationState) {
        when (label) {
            CalibrationState.OFF -> binding.offCl.makeGone()
            CalibrationState.LOW_SINGLE -> binding.lowSingleCl.makeGone()
            CalibrationState.MEDIUM -> binding.mediumCl.makeGone()
            CalibrationState.HIGH_SINGLE -> binding.highSingleCl.makeGone()
            CalibrationState.HIGH_DUAL -> binding.highDualCl.makeGone()
            CalibrationState.LOW_DUAL -> binding.lowDualCl.makeGone()
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

    fun setLowSinglePosition(angle: Float) {
        binding.lowSingleCl.makeVisible()
        binding.lowSingleCl.rotation = angle
        binding.lowSingleTv.rotation = -angle
        animateCircle(mCurrAngle, angle, ANIMATION_DURATION)
    }

    fun setMediumPosition(angle: Float) {
        binding.mediumCl.makeVisible()
        binding.mediumCl.rotation = angle
        binding.mediumTv.rotation = -angle
        animateCircle(mCurrAngle, angle, ANIMATION_DURATION)
    }

    fun setHighSinglePosition(angle: Float) {
        binding.highSingleCl.makeVisible()
        binding.highSingleCl.rotation = angle
        binding.highSingleTv.rotation = -angle
        animateCircle(mCurrAngle, angle, ANIMATION_DURATION)
    }

    fun setHighDualPosition(angle: Float) {
        binding.highDualCl.makeVisible()
        binding.highDualCl.rotation = angle
        binding.highDualTv.rotation = -angle
        animateCircle(mCurrAngle, angle, ANIMATION_DURATION)
    }

    fun setLowDualPosition(angle: Float) {
        binding.lowDualCl.makeVisible()
        binding.lowDualCl.rotation = angle
        binding.lowDualTv.rotation = -angle
        animateCircle(mCurrAngle, angle, ANIMATION_DURATION)
    }

    private fun animateCircle(fromDeg: Float, toDeg: Float, durationMilis: Long) {
        val rotateAnimation = RotateAnimation(
            fromDeg, toDeg,
            RotateAnimation.RELATIVE_TO_SELF, 0.5F,
            RotateAnimation.RELATIVE_TO_SELF, 0.5F
        )
        rotateAnimation.interpolator = LinearInterpolator()

        rotateAnimation.duration = durationMilis
        rotateAnimation.isFillEnabled = true
        rotateAnimation.fillAfter = true

        binding.knobCircleCl.startAnimation(rotateAnimation)
        mCurrAngle = toDeg
    }

    @Suppress("UNCHECKED_CAST")
    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.childrenStates = SparseArray()
        for (i in 0 until childCount) {
            getChildAt(i).saveHierarchyState(ss.childrenStates as SparseArray<Parcelable>)
        }
        return ss
    }

    @Suppress("UNCHECKED_CAST")
    public override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        for (i in 0 until childCount) {
            getChildAt(i).restoreHierarchyState(ss.childrenStates as SparseArray<Parcelable>)
        }
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    class SavedState(superState: Parcelable?) : View.BaseSavedState(superState) {
        var childrenStates: SparseArray<Any>? = null

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            childrenStates?.let {
                out.writeSparseArray(it)
            }
        }
    }

}
