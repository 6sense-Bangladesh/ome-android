package com.ome.app.ui.dashboard.settings.auto_shutoff

import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.ui.model.network.request.CreateStoveRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AutoShutOffViewModel @Inject constructor(
    private val stoveRepository: StoveRepository,
    private val userRepository: UserRepository
) : BaseViewModel() {

    val autoShutOffLiveData: SingleLiveEvent<String> = SingleLiveEvent()
    val autoShutOffResponseLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var selectedTime = 0
    var timeList = listOf(
        "15 Minutes",
        "20 Minutes",
        "25 Minutes",
        "30 Minutes",
        "35 Minutes",
        "40 Minutes",
        "45 Minutes",
        "50 Minutes",
        "55 Minutes",
        "60 Minutes"
    )


    fun loadData() = launch(dispatcher = ioContext) {
        userRepository.userFlow.collect {
            it?.stoveAutoOffMins?.let { value ->
                selectedTime = value
                autoShutOffLiveData.postValue("$value Minutes")
            }
        }
    }


    fun updateAutoShutOffTime() = launch(dispatcher = ioContext) {
        userRepository.userFlow.value?.stoveAutoOffMins?.let {
            if (it != selectedTime) {
                stoveRepository.updateStove(
                    CreateStoveRequest(stoveAutoOffMins = selectedTime),
                    userRepository.userFlow.value?.stoveId ?: ""
                )
                userRepository.getUserData()
                autoShutOffResponseLiveData.postValue(true)
            }
        }

    }

}
