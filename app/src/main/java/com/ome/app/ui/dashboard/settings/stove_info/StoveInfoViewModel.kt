package com.ome.app.ui.dashboard.settings.stove_info

import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.ui.stove.BasePhotoViewModel
import com.ome.app.ui.stove.StoveOrientation
import com.ome.app.ui.stove.StoveType
import com.ome.app.ui.stove.stoveType
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

    fun loadData() = launch(ioContext) {
        userRepository.userFlow.collect { user ->
            user?.let {
                it.stoveGasOrElectric.stoveType?.let { stoveType ->
                    stoveTypeLiveData.postValue(stoveType)
                }
                it.stoveOrientation.enum?.let { orientation ->
                    stoveOrientationLiveData.postValue(orientation)
                }
                stoveNameLiveData.postValue("Family #1 Stove")
            }

        }
    }
}
