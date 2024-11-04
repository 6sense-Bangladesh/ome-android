package com.ome.app.presentation.views

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.MotionEvent
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setMargins
import com.ome.app.R
import com.ome.app.databinding.KnobViewLayoutBinding
import com.ome.app.domain.model.network.response.*
import com.ome.app.presentation.dashboard.settings.add_knob.calibration.CalibrationState
import com.ome.app.utils.*
import com.ome.app.utils.WifiHandler.Companion.signalStrengthPercentage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.atan2

class KnobView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    companion object{
        private const val ANIMATION_DURATION: Long = 500L
    }

    private val binding = inflate<KnobViewLayoutBinding>()
    private val knobSrc = binding.knobSrc

    var prevAngle = 0.0F
    private var mCurrAngle = 0.0F


    fun setKnobPosition(angle: Float, rotateClockwise: Boolean = true) {
//        val rotationSafeAngle = if (rotateClockwise) angle else angle - 360
        val rotationSafeAngle = angle
        if(rotationSafeAngle != mCurrAngle){
            logi("angle KnobView $rotationSafeAngle")
            animateCircle(mCurrAngle, rotationSafeAngle)
        }
    }

    fun setFontSize(size: Float) {
        binding.offTv.textSize = size
        binding.lowSingleTv.textSize = size
        binding.lowDualTv.textSize = size
        binding.mediumTv.textSize = size
        binding.highSingleTv.textSize = size
        binding.highDualTv.textSize = size

        (binding.offTv.layoutParams as MarginLayoutParams).setMargins(0)
        (binding.lowSingleTv.layoutParams as MarginLayoutParams).setMargins(0)
        (binding.lowDualTv.layoutParams as MarginLayoutParams).setMargins(0)
        (binding.mediumTv.layoutParams as MarginLayoutParams).setMargins(0)
        (binding.highSingleTv.layoutParams as MarginLayoutParams).setMargins(0)
        (binding.highDualTv.layoutParams as MarginLayoutParams).setMargins(0)
    }


    fun hideLabel(label: CalibrationState? = null) {
        when (label) {
            CalibrationState.OFF -> binding.offCl.makeGone()
            CalibrationState.LOW_SINGLE -> binding.lowSingleCl.makeGone()
            CalibrationState.MEDIUM -> binding.mediumCl.makeGone()
            CalibrationState.HIGH_SINGLE -> binding.highSingleCl.makeGone()
            CalibrationState.HIGH_DUAL -> binding.highDualCl.makeGone()
            CalibrationState.LOW_DUAL -> binding.lowDualCl.makeGone()
            else -> {
                binding.offCl.makeGone()
                binding.lowSingleCl.makeGone()
                binding.mediumCl.makeGone()
                binding.highSingleCl.makeGone()
                binding.highDualCl.makeGone()
            }
        }
    }

    var stovePosition : Int
        get() = binding.stovePositionTv.text.toString().toIntOrNull().orMinusOne()
        set(position) {
            binding.stovePositionTv.text = position.toStringLocale()
        }

    fun setOffPosition(angle: Float) {
        if (angle.isMinusOne()) return
        binding.offCl.makeVisible()
        binding.offCl.rotation = angle
        binding.offTv.rotation = -angle
        animateCircle(mCurrAngle, angle)
    }

    fun setLowSinglePosition(angle: Float) {
        if (angle.isZeroOrMinusOne()) return
        binding.lowSingleCl.makeVisible()
        binding.lowSingleCl.rotation = angle
        binding.lowSingleTv.rotation = -angle
        animateCircle(mCurrAngle, angle)
    }

    fun setMediumPosition(angle: Float) {
        if (angle.isZeroOrMinusOne()) return
        binding.mediumCl.makeVisible()
        binding.mediumCl.rotation = angle
        binding.mediumTv.rotation = -angle
        animateCircle(mCurrAngle, angle)
    }

    fun setHighSinglePosition(angle: Float) {
        if (angle.isZeroOrMinusOne()) return
        binding.highSingleCl.makeVisible()
        binding.highSingleCl.rotation = angle
        binding.highSingleTv.rotation = -angle
        animateCircle(mCurrAngle, angle)
    }

    fun setHighDualPosition(angle: Float) {
        if (angle.isZeroOrMinusOne()) return
        binding.highDualCl.makeVisible()
        binding.highDualCl.rotation = angle
        binding.highDualTv.rotation = -angle
        animateCircle(mCurrAngle, angle)
    }

    fun setLowDualPosition(angle: Float) {
        if (angle.isZeroOrMinusOne()) return
        binding.lowDualCl.makeVisible()
        binding.lowDualCl.rotation = angle
        binding.lowDualTv.rotation = -angle
        animateCircle(mCurrAngle, angle)
    }

    fun changeKnobState(knobState: KnobState) {
        knobSrc.animate().alpha(knobState.alpha).start()
        knobSrc.tag = knobState.icon
        knobSrc.setImageResource(knobState.icon)
        binding.stovePositionTv.changeVisibility(knobState != KnobState.ADD)
    }

    fun changeKnobStatus(knob: KnobDto): Boolean {
        changeBatteryState(batteryLevel = knob.battery)
        changeConfigurationState(isCalibrated = knob.calibrated)
        changeConnectionState(
            connectionStatus = knob.connectStatus,
            wifiRSSI = knob.rssi,
            batteryLevel = knob.battery
        )
        return when{ knob.battery <= 15 || knob.calibrated.isFalse() || knob.connectStatus.connectionState == ConnectionState.Offline ||
            ( knob.connectStatus.connectionState == ConnectionState.Online && knob.rssi.signalStrengthPercentage in 0..35) -> {
                hideLabel()
                changeKnobState(KnobState.TRANSPARENT)
                changeKnobProgressVisibility(false)
                false
            }
            else -> {
                changeKnobState(KnobState.NORMAL)
                changeKnobProgressVisibility(true)
                when(knob.calibration.rotationDir.rotation){
                    Calibration.Rotation.CLOCKWISE -> binding.knobProgress.scaleX = 1F
                    Calibration.Rotation.COUNTER_CLOCKWISE -> binding.knobProgress.scaleX = -1F
                    else -> Unit
                }
                true
            }
        }
    }

    fun changeKnobBasicStatus(knob: KnobDto): Boolean {
        changeKnobProgressVisibility(true)
        setKnobPosition(knob.angle.toFloat())
        stovePosition = knob.stovePosition
        return when{ knob.battery <= 15 || knob.calibrated.isFalse() || knob.connectStatus.connectionState == ConnectionState.Offline ||
                ( knob.connectStatus.connectionState == ConnectionState.Online && knob.rssi.signalStrengthPercentage in 0..35) -> {
                    changeKnobState(KnobState.TRANSPARENT)
                    false
                }
            else -> {
                changeKnobState(KnobState.NORMAL)
                when(knob.calibration.rotationDir.rotation){
                    Calibration.Rotation.CLOCKWISE -> binding.knobProgress.scaleX = 1F
                    Calibration.Rotation.COUNTER_CLOCKWISE -> binding.knobProgress.scaleX = -1F
                    else -> Unit
                }
                true
            }
        }
    }

    fun changeWiFiState(wifiRSSI: Rssi) {
        if(wifiRSSI.signalStrengthPercentage in 0..35){
            binding.connectionStatus.visible()
            binding.connectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi_poor, 0, 0, 0)
            binding.connectionStatus.text = context.getString(R.string.poor_signal)
        }else {
            binding.connectionStatus.gone()
        }
    }

    fun changeBatteryState(batteryLevel: Int) {
        if (batteryLevel <= 15) {
            binding.noBattery.visible()
        }else{
            binding.noBattery.gone()
        }
    }

    fun changeConfigurationState(isCalibrated: Boolean?) {
        if (isCalibrated.isFalse()) {
            binding.notConfigured.visible()
        } else{
            binding.notConfigured.gone()
        }
    }

    fun changeConnectionState(connectionStatus: String, wifiRSSI: Rssi, batteryLevel: Int) {
        when (connectionStatus.connectionState) {
            ConnectionState.Online -> {
                binding.connectionStatus.gone()
                changeWiFiState(wifiRSSI)
            }
            ConnectionState.Offline -> {
                binding.connectionStatus.visible()
                binding.connectionStatus.text = context.getString(R.string.no_wifi)
                binding.connectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wifi_off, 0, 0, 0)
            }
            ConnectionState.Charging -> {
                binding.connectionStatus.visible()
                binding.connectionStatus.text = context.getString(R.string.charging)
                when (batteryLevel) {
                    in 0..20 ->
                        binding.connectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_charging_20, 0, 0, 0)
                    in 21..30 ->
                        binding.connectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_charging_30, 0, 0, 0)
                    in 31..50 ->
                        binding.connectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_charging_50, 0, 0, 0)
                    in 51..80 ->
                        binding.connectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_charging_80, 0, 0, 0)
                    in 81..95 ->
                        binding.connectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_charging_90, 0, 0, 0)
                    in 96..100 ->
                        binding.connectionStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_battery_full, 0, 0, 0)
                }
            }
        }
    }

    fun changeKnobProgressVisibility(isVisible: Boolean) {
        binding.knobProgress.changeVisibility(isVisible)
    }

    val knobState : KnobState
        get() = KnobState.entries.find { it.icon == knobSrc.tag } ?: KnobState.NORMAL


    val isKnobInAddState
        get() = knobState == KnobState.ADD

    enum class KnobState(@DrawableRes val icon: Int, val alpha : Float){
        ADD(R.drawable.ic_knob_circle_add, 1F),
        NORMAL(R.drawable.ic_knob_circle, 1F),
        TRANSPARENT(R.drawable.ic_knob_circle, .6F)
    }

    private fun animateCircle(fromDeg: Float, toDeg: Float) {
        mCurrAngle = toDeg
        val rotateAnimation = RotateAnimation(
            fromDeg, toDeg,
            RotateAnimation.RELATIVE_TO_SELF, 0.5F,
            RotateAnimation.RELATIVE_TO_SELF, 0.5F
        )
        rotateAnimation.interpolator = LinearInterpolator()

        rotateAnimation.duration = ANIMATION_DURATION
        rotateAnimation.isFillEnabled = true
        rotateAnimation.fillAfter = true

        binding.knobCircleCl.startAnimation(rotateAnimation)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun doOnRotationChange(doRotate: StateFlow<Boolean>) = callbackFlow{

        binding.knobProgress.setOnTouchListener { v, event ->
            when(event.action){
                MotionEvent.ACTION_UP -> trySend(mCurrAngle)
            }
            // Get location of knobProgress on the screen
            val location = IntArray(2)
            v.getLocationOnScreen(location)

            // Calculate the center of knobProgress
            val centerX = location[0] + v.width / 2
            val centerY = location[1] + v.height / 2

            // Get touch coordinates relative to the screen
            val touchX = event.rawX
            val touchY = event.rawY

            // Calculate the distance from the center to the touch point
            val deltaX = touchX - centerX
            val deltaY = touchY - centerY

            // Calculate the angle in degrees using atan2
            var angle = Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat() + 90

            // Adjust angle to be in the 0–360 range
            if (angle < 0)  angle += 360f
            else if (angle > 360)  angle -= 360f

            Log.d(TAG, "setOnTouchListener: $angle")
            if(doRotate.value)
                setKnobPosition(angle)
            true
        }
        awaitClose()
    }

    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.childrenStates = SparseArray()
        for (i in 0 until childCount) {
            getChildAt(i).saveHierarchyState(ss.childrenStates)
        }
        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        for (i in 0 until childCount) {
            getChildAt(i).restoreHierarchyState(ss.childrenStates)
        }
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    class SavedState(superState: Parcelable?) : BaseSavedState(superState) {
        var childrenStates: SparseArray<Parcelable>? = null

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            childrenStates?.let {
                out.writeSparseArray(it)
            }
        }
    }

}
