package com.ome.app.presentation.stove

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.ome.app.R
import com.ome.app.domain.model.network.request.StoveRequest
import com.ome.app.domain.model.network.response.UserResponse
import kotlinx.parcelize.Parcelize

@Parcelize
enum class StoveType(val type: String, val mounting: String, @DrawableRes val imgRes: Int): Parcelable {
    GAS_TOP("gas", "horizontal", R.drawable.ic_gray_gas),
    ELECTRIC_TOP("electric", "horizontal", R.drawable.ic_gray_electric),
    GAS_RANGE("gas", "vertical", R.drawable.ic_gray_gas),
    ELECTRIC_RANGE("electric", "vertical", R.drawable.ic_gray_electric)
}

val UserResponse?.stoveType
    get() = StoveType.entries.find { it.type == this?.stoveGasOrElectric && it.mounting == this.stoveKnobMounting }
val StoveRequest?.stoveType
    get() = StoveType.entries.find { it.type == this?.stoveGasOrElectric && it.mounting == this.stoveKnobMounting }

enum class StoveOrientation(
    val number: Int,
    @DrawableRes val imgRes: Int,
    @IdRes val layoutRes: Int
) {
    FOUR_BURNERS(4, R.drawable.ic_four_burner_blue, R.id.fourBurnersIv),
    FOUR_BAR_BURNERS(51, R.drawable.ic_four_bar_burner_blue, R.id.fourBarBurnersIv),
    FIVE_BURNERS(5, R.drawable.ic_five_burner_blue, R.id.fiveBurnersIv),
    SIX_BURNERS(6, R.drawable.ic_six_burner_blue, R.id.sixBurnersIv),
    TWO_BURNERS_HORIZONTAL(2, R.drawable.ic_two_burner_blue, R.id.twoBurnersHorizontalIv),
    TWO_BURNERS_VERTICAL(21, R.drawable.ic_two_burner_vertical_blue, R.id.twoBurnersVerticalIv)
}

val Int?.stoveOrientation
    get() = StoveOrientation.entries.find { it.number == this }
