package com.ome.app.ui.stove

import com.ome.app.base.BaseViewModel
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.model.network.request.CreateStoveRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveSetupTypeViewModel @Inject constructor(
    val stoveRepository: StoveRepository,
    val userRepository: UserRepository
) : BaseViewModel() {

    var stoveType = ""

    fun saveStoveType(stoveId: String) = launch(dispatcher = ioContext) {
        stoveRepository.updateStove(CreateStoveRequest(stoveGasOrElectric = stoveType), stoveId)
        userRepository.getUserData()
        loadingLiveData.postValue(true)
    }

}
