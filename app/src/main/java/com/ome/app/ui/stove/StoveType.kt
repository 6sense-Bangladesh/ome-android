package com.ome.app.ui.stove

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.ome.app.R

enum class StoveType(val type: String, val imgRes: Int) {
    GAS("gas", R.drawable.ic_gray_gas),
    ELECTRIC("electric", R.drawable.ic_gray_electric)
}

val String?.stoveType
    get() = StoveType.entries.find { it.type == this }

enum class StoveOrientation(val number: Int, @DrawableRes val imgRes: Int, @IdRes val layoutRes: Int) {
    FOUR_BURNERS(4, R.drawable.ic_four_burner_blue, R.id.fourBurnersIv),
    FOUR_BAR_BURNERS(51, R.drawable.ic_four_bar_burner_blue, R.id.fourBarBurnersIv),
    FIVE_BURNERS(5, R.drawable.ic_five_burner_blue, R.id.fiveBurnersIv),
    SIX_BURNERS(6, R.drawable.ic_six_burner_blue, R.id.sixBurnersIv),
    TWO_BURNERS_HORIZONTAL(2, R.drawable.ic_two_burner_blue, R.id.twoBurnersHorizontalIv),
    TWO_BURNERS_VERTICAL(21, R.drawable.ic_two_burner_vertical_blue, R.id.twoBurnersVerticalIv)
}

val Int?.stoveOrientation
    get() = StoveOrientation.entries.find { it.number == this }
