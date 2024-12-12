package com.ome.app.domain.model.state

import androidx.annotation.IdRes
import com.ome.app.R

enum class StoveOrientation(
    val number: Int,
    @IdRes val layoutRes: Int
) {
    FOUR_BURNERS(4, R.id.fourBurnersIv),
    FOUR_BAR_BURNERS(51, R.id.fourBarBurnersIv),
    FIVE_BURNERS(5, R.id.fiveBurnersIv),
    SIX_BURNERS(6, R.id.sixBurnersIv),
    TWO_BURNERS_HORIZONTAL(2, R.id.twoBurnersHorizontalIv),
    TWO_BURNERS_VERTICAL(21, R.id.twoBurnersVerticalIv)
}

