package com.ome.app.presentation.stove

import com.ome.app.domain.model.base.ResponseWrapper
import com.ome.app.domain.model.state.StoveOrientation
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveSetupBurnersViewModel @Inject constructor(
    private val stoveRepository: StoveRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {
    var stoveOrientation: StoveOrientation? = null
    var brand = ""
    var stoveAutoOffMins = 15
    var stoveType = ""
    var stoveKnobMounting = ""

    val createStoveLiveData = SingleLiveEvent<Boolean>()

    fun updateStoveOrientation(stoveId: String, onEnd :() ->Unit) = launch(ioContext) {
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
        launch(ioContext) {
            stoveOrientation?.number?.let { number ->
                val response = stoveRepository.createStove(
                    com.ome.app.domain.model.network.request.StoveRequest(
                        stoveAutoOffMins = stoveAutoOffMins,
                        stoveGasOrElectric = stoveType,
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
        launch(ioContext) {
            stoveOrientation?.number?.let { number ->
                val response = stoveRepository.updateStove(
                    com.ome.app.domain.model.network.request.StoveRequest(
                        stoveAutoOffMins = stoveAutoOffMins,
                        stoveGasOrElectric = stoveType,
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
