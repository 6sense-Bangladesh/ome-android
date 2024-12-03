package com.ome.app.presentation.dashboard.settings.add_knob.direction

import androidx.lifecycle.SavedStateHandle
import com.ome.app.domain.model.network.request.KnobRequest
import com.ome.app.domain.model.network.request.SetCalibrationRequest
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class DirectionSelectionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val stoveRepository: StoveRepository
    ): BaseViewModel() {
    var continueBtnClicked = false

    var clockwiseDir = -1
    var macAddress = ""
    var calibrated = false
    var calRequest: SetCalibrationRequest? = null

    fun updateDirection(onEnd: () -> Unit) {
        launch {
            if(macAddress.isEmpty()) error("Something went wrong.")
            calRequest?.let {
                if(!calRequest?.zones.isNullOrEmpty()) {
                    stoveRepository.setCalibration(
                        it.copy(rotationDir = clockwiseDir),
                        macAddress = macAddress
                    )
                    if (!calibrated)
                        stoveRepository.updateKnobInfo(KnobRequest(calibrated = false), macAddress)
                    onEnd()
                    successToastFlow.emit("Orientation updated successfully")
                } else error("Knob not configured.")
            } ?: error("Something went wrong.")
        }
    }

}
