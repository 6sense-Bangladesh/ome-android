package com.ome.app.ui.dashboard.settings.add_knob.burner

import com.ome.app.base.BaseViewModel
import com.ome.app.base.SingleLiveEvent
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.ui.stove.StoveOrientation
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectBurnerViewModel @Inject constructor(
    val stoveRepository: StoveRepository,
    val userRepository: UserRepository
) : BaseViewModel() {

    var selectedBurnerIndex: Int? = null

    val selectedIndexesLiveData: SingleLiveEvent<Pair<StoveOrientation, List<Int>>> =
        SingleLiveEvent()

    fun loadData() = launch(dispatcher = ioContext) {
        stoveRepository.knobsFlow.collect { knobs ->
            if (knobs != null && userRepository.userFlow.value?.stoveOrientation != null) {
                val foundValue = StoveOrientation.values()
                    .firstOrNull { it.number == userRepository.userFlow.value?.stoveOrientation }
                foundValue?.let { stoveOrientation ->
                    selectedIndexesLiveData.postValue(stoveOrientation to knobs.map { it.stovePosition })
                }
            }

        }
    }
}
