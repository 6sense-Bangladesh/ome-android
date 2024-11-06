package com.ome.app.domain.model.state

import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.ome.app.R

sealed class BurnerState(val level: Int, val type: State) {
    enum class State(@ColorRes val background: Int, @ColorRes val text: Int){
        Off(R.color.off_white , R.color.gray),
        High(R.color.colorHigh , R.color.black),
        Medium(R.color.colorMedium , R.color.black),
        Low(R.color.colorLow , R.color.black)
    }

    fun Chip.applyState(){
        text = type.name
        context?.let {
            chipBackgroundColor = ContextCompat.getColorStateList(it, type.background)
            setTextColor(ContextCompat.getColor(it, type.text))
        }
    }

    class Off(level: Int) : BurnerState(level, State.Off)
    class High(level: Int) : BurnerState(level, State.High)
    class Medium(level: Int) : BurnerState(level, State.Medium)
    class Low(level: Int) : BurnerState(level, State.Low)
}