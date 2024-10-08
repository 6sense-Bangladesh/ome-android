package com.ome.app.ui.dashboard.settings.stove_info

import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.ui.stove.BasePhotoViewModel
import com.ome.app.ui.stove.StoveOrientation
import com.ome.app.ui.stove.StoveType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveInfoViewModel @Inject constructor(
    private val stoveRepository: StoveRepository,
    userRepository: UserRepository
) : BasePhotoViewModel(userRepository) {


    val stoveOrientationLiveData: SingleLiveEvent<StoveOrientation> = SingleLiveEvent()
    val stoveTypeLiveData: SingleLiveEvent<StoveType> = SingleLiveEvent()
    val stoveNameLiveData: SingleLiveEvent<String> = SingleLiveEvent()

    var stoveId = ""

    fun loadData() = launch(dispatcher = ioContext) {
        userRepository.userFlow.collect { user ->
            user?.let {
                it.stoveGasOrElectric?.let { stoveType ->
                    val foundValue = StoveType.values().firstOrNull { it.type == stoveType }
                    foundValue?.let {
                        stoveTypeLiveData.postValue(it)
                    }
                }
                it.stoveOrientation?.let { stoveOrientation ->
                    val foundValue =
                        StoveOrientation.values().firstOrNull { it.number == stoveOrientation }
                    foundValue?.let { orientation ->
                        stoveOrientationLiveData.postValue(orientation)
                    }
                }
                stoveNameLiveData.postValue("Family #1 Stove")
            }

        }
    }
}
