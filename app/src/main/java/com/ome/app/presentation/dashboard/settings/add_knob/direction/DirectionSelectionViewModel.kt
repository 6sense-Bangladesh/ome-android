package com.ome.app.presentation.dashboard.settings.add_knob.direction

import androidx.lifecycle.SavedStateHandle
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
    fun updateDirection() {
        launch {
            if(macAddress.isEmpty()) error("Something went wrong.")
            stoveRepository.setCalibration(
                SetCalibrationRequest(
                    rotationDir = clockwiseDir,
                ),
                macAddress = macAddress
            )
        }
    }

    var clockwiseDir = -1
    var macAddress = ""
}
