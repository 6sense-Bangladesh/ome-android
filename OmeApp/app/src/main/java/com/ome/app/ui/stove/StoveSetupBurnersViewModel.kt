package com.ome.app.ui.stove

import com.ome.app.base.BaseViewModel
import com.ome.app.base.SingleLiveEvent
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
) :
    BaseViewModel() {
    var stoveOrientation = 0
    var brand = ""
    var type = ""
    var stoveAutoOffMins = 15

    val createStoveLiveData = SingleLiveEvent<Boolean>()


    fun createStove() {
        launch(dispatcher = ioContext) {
            val response = stoveRepository.createStove(
                CreateStoveRequest(
                    stoveAutoOffMins = stoveAutoOffMins,
                    stoveGasOrElectric = type,
                    stoveMakeModel = brand,
                    stoveOrientation = stoveOrientation
                )
            )
            if (response is ResponseWrapper.Success) {
                userRepository.getUserData()
                createStoveLiveData.postValue(true)
            }
        }
        //createStoveLiveData.postValue(true)
    }
}
