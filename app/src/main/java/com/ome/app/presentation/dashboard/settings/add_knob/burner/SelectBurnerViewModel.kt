package com.ome.app.presentation.dashboard.settings.add_knob.burner

import com.ome.app.domain.model.network.request.KnobRequest
import com.ome.app.domain.model.state.StoveOrientation
import com.ome.app.domain.model.state.stoveOrientation
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.utils.orMinusOne
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class SelectBurnerViewModel @Inject constructor(
    val stoveRepository: StoveRepository,
    val userRepository: UserRepository
) : BaseViewModel() {

    var selectedBurnerIndex: Int? = null
    var stoveOrientation: StoveOrientation? = null

    var macAddress = ""

    val selectedIndexes=  MutableStateFlow<Triple<StoveOrientation, List<Int>, Int>?>(null)

    fun loadData() = launch(ioContext, false) {
        stoveRepository.knobsFlow.collect { knobs ->
            userRepository.userFlow.value?.stoveOrientation.stoveOrientation?.let { stoveOrientation ->
                this@SelectBurnerViewModel.stoveOrientation = stoveOrientation
                val editModeIndex = knobs.find {
                    it.macAddr == macAddress
                }?.stovePosition?.minus(1).orMinusOne()
                selectedIndexes.value = Triple(stoveOrientation , knobs.map { it.stovePosition } , editModeIndex)
            }
        }
    }

    fun changeKnobPosition(stovePosition: Int) = launch(ioContext) {
        stoveRepository.updateKnobInfo(
            params = KnobRequest(
                stovePosition = stovePosition,
            ),
            macAddress = macAddress
        )
        stoveRepository.getAllKnobs()
    }
}
