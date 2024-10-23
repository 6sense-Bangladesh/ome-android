package com.ome.app.ui.stove

import com.ome.app.domain.model.network.request.StoveRequest
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveSetupTypeViewModel @Inject constructor(
    val stoveRepository: StoveRepository,
    val userRepository: UserRepository
) : BaseViewModel() {

    var stoveType : StoveType? = null

    fun saveStoveType(stoveId: String, onEnd :() ->Unit) = launch(ioContext) {
        stoveRepository.updateStove(
            StoveRequest(
                stoveGasOrElectric = stoveType?.type,
                stoveKnobMounting = stoveType?.mounting
            ), stoveId)
        onEnd()
//        userRepository.getUserData()
        loadingLiveData.postValue(true)
    }

}
