package com.ome.app.ui.dashboard.settings.add_knob.burner

import com.ome.app.domain.model.network.request.CreateKnobRequest
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.ui.stove.StoveOrientation
import com.ome.app.ui.stove.stoveOrientation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectBurnerViewModel @Inject constructor(
    val stoveRepository: StoveRepository,
    val userRepository: UserRepository
) : BaseViewModel() {

    var selectedBurnerIndex: Int? = null

    var macAddress = ""

    val selectedIndexesLiveData: SingleLiveEvent<Pair<StoveOrientation, List<Int>>> =
        SingleLiveEvent()

    val knobPositionResponseLiveData: SingleLiveEvent<Boolean> =
        SingleLiveEvent()

    fun loadData() = launch(ioContext) {
        stoveRepository.knobsFlow.collect { knobs ->
            if (knobs.isNotEmpty() && userRepository.userFlow.value?.stoveOrientation != null) {
                userRepository.userFlow.value?.stoveOrientation.stoveOrientation
                ?.let { stoveOrientation ->
                    selectedIndexesLiveData.postValue(stoveOrientation to knobs.map { it.stovePosition })
                }
            }

        }
    }

    fun changeKnobPosition(stovePosition: Int) = launch(ioContext) {
        stoveRepository.updateKnobInfo(
            params = CreateKnobRequest(
                stovePosition = stovePosition,
                macID = macAddress
            ),
            macAddress = macAddress
        )
        stoveRepository.getAllKnobs()
        knobPositionResponseLiveData.postValue(true)
    }
}
