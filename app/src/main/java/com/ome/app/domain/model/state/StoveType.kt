package com.ome.app.domain.model.state

import androidx.annotation.DrawableRes
import com.ome.app.R

enum class StoveType(val type: String, val mounting: String, @DrawableRes val imgRes: Int){
    GAS_TOP("gas", "horizontal", R.drawable.ic_gas),
    ELECTRIC_TOP("electric", "horizontal", R.drawable.ic_electric),
    GAS_RANGE("gas", "vertical", R.drawable.ic_gas),
    ELECTRIC_RANGE("electric", "vertical", R.drawable.ic_electric)
}


