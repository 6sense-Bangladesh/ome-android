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

    val autoShutOffLiveData: SingleLiveEvent<Int> = SingleLiveEvent()
    val autoShutOffResponseLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var selectedTime = -1
    var timeList = listOf(
        "15 Minutes" to 15,
        "20 Minutes" to 20,
        "25 Minutes" to 25,
        "30 Minutes" to 30,
        "35 Minutes" to 35,
        "40 Minutes" to 40,
        "45 Minutes" to 45,
        "50 Minutes" to 50,
        "55 Minutes" to 55,
        "60 Minutes" to 60
    )


    fun loadData() = launch(ioContext, showLoading = false) {
        userRepository.userFlow.collect {
            it?.stoveAutoOffMins?.let { value ->
                selectedTime = value
                autoShutOffLiveData.postValue(timeList.indexOfFirst { it.second == value })
            }
        }
    }


    fun updateAutoShutOffTime() = launch(ioContext) {
        if (selectedTime != -1) {
            stoveRepository.updateStove(
                StoveRequest(stoveAutoOffMins = selectedTime),
                userRepository.userFlow.value?.stoveId ?: ""
            )
            userRepository.getUserData()
            autoShutOffResponseLiveData.postValue(true)
        }else{
            error("Please select a time")
        }

    }

}
