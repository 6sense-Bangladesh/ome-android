package com.ome.app.presentation.dashboard.settings.auto_shutoff

import com.ome.app.domain.model.network.request.StoveRequest
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
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


    fun loadData() = launch(ioContext) {
        userRepository.userFlow.collect {
            it?.stoveAutoOffMins?.let { value ->
                selectedTime = value
                autoShutOffLiveData.postValue("$value Minutes")
            }
        }
    }


    fun updateAutoShutOffTime() = launch(ioContext) {
        userRepository.userFlow.value?.stoveAutoOffMins?.let {
            if (it != selectedTime) {
                stoveRepository.updateStove(
                    StoveRequest(stoveAutoOffMins = selectedTime),
                    userRepository.userFlow.value?.stoveId ?: ""
                )
                userRepository.getUserData()
                autoShutOffResponseLiveData.postValue(true)
            }
        }

    }

}