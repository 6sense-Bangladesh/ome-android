package com.ome.app.ui.stove

import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.domain.model.network.request.StoveRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveSetupTypeViewModel @Inject constructor(
    val stoveRepository: StoveRepository,
    val userRepository: UserRepository
) : BaseViewModel() {

    var stoveType = ""

    fun saveStoveType(stoveId: String, onEnd :() ->Unit) = launch(dispatcher = ioContext) {
        stoveRepository.updateStove(
            com.ome.app.domain.model.network.request.StoveRequest(
                stoveGasOrElectric = stoveType
            ), stoveId)
        onEnd()
//        userRepository.getUserData()
        loadingLiveData.postValue(true)
    }

}
