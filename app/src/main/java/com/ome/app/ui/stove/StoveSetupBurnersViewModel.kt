package com.ome.app.ui.stove

import com.ome.app.R
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.model.base.ResponseWrapper
import com.ome.app.model.network.request.CreateStoveRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveSetupBurnersViewModel @Inject constructor(
    val stoveRepository: StoveRepository,
    val userRepository: UserRepository
) : BaseViewModel() {
    var stoveOrientation: StoveOrientation? = null
    var brand = ""
    var type = ""
    var stoveAutoOffMins = 15

    val createStoveLiveData = SingleLiveEvent<Boolean>()

    fun updateStoveOrientation(stoveId: String) = launch(dispatcher = ioContext) {
        stoveOrientation?.let {
            stoveRepository.updateStove(CreateStoveRequest(stoveOrientation = it.number), stoveId = stoveId)
            userRepository.getUserData()
            loadingLiveData.postValue(false)
        }
    }

    fun createStove() {
        launch(dispatcher = ioContext) {
            stoveOrientation?.number?.let { number ->
                val response = stoveRepository.createStove(
                    CreateStoveRequest(
                        stoveAutoOffMins = stoveAutoOffMins,
                        stoveGasOrElectric = type,
                        stoveMakeModel = brand,
                        stoveOrientation = number
                    )
                )
                if (response is ResponseWrapper.Success) {
                    userRepository.getUserData()
                    createStoveLiveData.postValue(true)
                } else {
                    loadingLiveData.postValue(false)
                }
            }
        }
        //createStoveLiveData.postValue(true)
    }
}

enum class StoveOrientation(val number: Int, val imgRes: Int) {
    FOUR_BURNERS(4, R.drawable.ic_four_burner_blue),
    FOUR_BAR_BURNERS(51, R.drawable.ic_four_bar_burner_blue),
    FIVE_BURNERS(5, R.drawable.ic_five_burner_blue),
    SIX_BURNERS(6, R.drawable.ic_six_burner_blue),
    TWO_BURNERS_HORIZONTAL(2, R.drawable.ic_two_burner_blue),
    TWO_BURNERS_VERTICAL(21, R.drawable.ic_two_burner_vertical_blue)
}
