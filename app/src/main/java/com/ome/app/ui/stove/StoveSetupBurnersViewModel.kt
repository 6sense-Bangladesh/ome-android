package com.ome.app.ui.stove

import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import com.ome.app.R
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
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
            val result = stoveRepository.updateStove(
                com.ome.app.domain.model.network.request.StoveRequest(
                    stoveOrientation = it.number
                ), stoveId = stoveId)
            if(result is ResponseWrapper.Error)
                error(result.message)
            onEnd()
//            userRepository.getUserData()
            loadingLiveData.postValue(false)
        } ?: error("Please select burner type")
    }

    fun createStove() {
        launch(dispatcher = ioContext) {
            stoveOrientation?.number?.let { number ->
                val response = stoveRepository.createStove(
                    com.ome.app.domain.model.network.request.StoveRequest(
                        stoveAutoOffMins = stoveAutoOffMins,
                        stoveGasOrElectric = type,
                        stoveMakeModel = brand,
                        stoveOrientation = number,
                        stoveKnobMounting = stoveKnobMounting,
                        stoveSetupComplete = true
                    )
                )
                if (response.isSuccess) {
//                    userRepository.getUserData()
                    createStoveLiveData.postValue(true)
                } else {
                    loadingLiveData.postValue(false)
                }
            } ?: error("Please select burner type")
        }
        //createStoveLiveData.postValue(true)
    }

    fun updateUserStove(stoveId: String) {
        launch(dispatcher = ioContext) {
            stoveOrientation?.number?.let { number ->
                val response = stoveRepository.updateStove(
                    com.ome.app.domain.model.network.request.StoveRequest(
                        stoveAutoOffMins = stoveAutoOffMins,
                        stoveGasOrElectric = type,
                        stoveMakeModel = brand,
                        stoveOrientation = number,
                        stoveKnobMounting = stoveKnobMounting,
                        stoveSetupComplete = true
                    ),
                    stoveId = stoveId
                )
                when (response) {
                    is ResponseWrapper.Success ->
                        createStoveLiveData.postValue(true)
                    is ResponseWrapper.Error -> {
                        loadingLiveData.postValue(false)
                        error(response.message)
                    }
                }
            } ?: error("Please select burner type")
        }
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
