package com.ome.app.ui.stove

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.ome.app.R
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.ui.model.base.ResponseWrapper
import com.ome.app.ui.model.network.request.CreateStoveRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveSetupBurnersViewModel @Inject constructor(
    private val stoveRepository: StoveRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {
    var stoveOrientation: StoveOrientation? = null
    var brand = ""
    var type = ""
    var stoveAutoOffMins = 15
    var stoveKnobMounting = "vertical"

    val createStoveLiveData = SingleLiveEvent<Boolean>()

    fun updateStoveOrientation(stoveId: String, onEnd :() ->Unit) = launch(dispatcher = ioContext) {
        stoveOrientation?.let {
            stoveRepository.updateStove(CreateStoveRequest(stoveOrientation = it.number), stoveId = stoveId)
            onEnd()
//            userRepository.getUserData()
            loadingLiveData.postValue(false)
        } ?: error("Please select burner type")
    }

    fun createStove(onEnd :() ->Unit) {
        launch(dispatcher = ioContext) {
            stoveOrientation?.number?.let { number ->
                val response = stoveRepository.createStove(
                    CreateStoveRequest(
                        stoveAutoOffMins = stoveAutoOffMins,
                        stoveGasOrElectric = type,
                        stoveMakeModel = brand,
                        stoveOrientation = number,
                        stoveKnobMounting = stoveKnobMounting,
                        stoveSetupComplete = true
                    )
                )
                if (response is ResponseWrapper.Success) {
                    onEnd()
//                    userRepository.getUserData()
                    createStoveLiveData.postValue(true)
                } else {
                    loadingLiveData.postValue(false)
                }
            } ?: error("Please select burner type")
        }
        //createStoveLiveData.postValue(true)
    }
}

enum class StoveOrientation(val number: Int,@DrawableRes val imgRes: Int, @IdRes val layoutRes: Int) {
    FOUR_BURNERS(4, R.drawable.ic_four_burner_blue, R.id.fourBurnersIv),
    FOUR_BAR_BURNERS(51, R.drawable.ic_four_bar_burner_blue, R.id.fourBarBurnersIv),
    FIVE_BURNERS(5, R.drawable.ic_five_burner_blue, R.id.fiveBurnersIv),
    SIX_BURNERS(6, R.drawable.ic_six_burner_blue, R.id.sixBurnersIv),
    TWO_BURNERS_HORIZONTAL(2, R.drawable.ic_two_burner_blue, R.id.twoBurnersHorizontalIv),
    TWO_BURNERS_VERTICAL(21, R.drawable.ic_two_burner_vertical_blue, R.id.twoBurnersVerticalIv)
}

val Int?.enum
    get() = StoveOrientation.entries.find { it.number == this }
