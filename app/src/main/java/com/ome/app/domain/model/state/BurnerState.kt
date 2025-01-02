package com.ome.app.domain.model.state

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.ome.app.R

sealed class BurnerState(val level: Int, val type: State, val zone: Int) {
    enum class State(@ColorRes val background: Int, @ColorRes val text: Int){
        Off(R.color.off_white , R.color.gray),
        High(R.color.colorHigh , R.color.white),
        HighMid(R.color.colorHigh , R.color.white),
        Medium(R.color.colorMedium , R.color.black),
        Low(R.color.colorLow , R.color.black),
        LowMid(R.color.colorLow , R.color.black)
    }

    private val State.nm get() = name.replace(Regex("([a-z])([A-Z])"), "$1-$2")

    fun Chip.applyState(){
//        val zoneName = when (zone) {
//            1 -> " First"
//            2 -> " Second"
//            else -> ""
//        }
//
//        text = if(isDualZone) type.nm + zoneName else type.nm
        text = type.nm
        context?.let {
            chipBackgroundColor = ContextCompat.getColorStateList(it, type.background)
            setTextColor(ContextCompat.getColor(it, type.text))
        }
    }

    class Off(level: Int) : BurnerState(level, State.Off, 0)
    class High(level: Int, zone: Int) : BurnerState(level, State.High, zone)
    class HighMid(level: Int) : BurnerState(level, State.HighMid, 1)
    class Medium(level: Int, zone: Int) : BurnerState(level, State.Medium, zone)
    class Low(level: Int, zone: Int) : BurnerState(level, State.Low, zone)
    class LowMid(level: Int) : BurnerState(level, State.LowMid, 1)
}