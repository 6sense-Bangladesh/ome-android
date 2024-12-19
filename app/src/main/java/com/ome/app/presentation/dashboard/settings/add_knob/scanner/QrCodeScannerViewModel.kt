package com.ome.app.presentation.dashboard.settings.add_knob.scanner

import androidx.lifecycle.SavedStateHandle
import com.ome.app.R
import com.ome.app.data.local.ResourceProvider
import com.ome.app.domain.model.network.request.KnobRequest
import com.ome.app.domain.model.state.KnobStatus
import com.ome.app.domain.model.state.knobStatus
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class QrCodeScannerViewModel @Inject constructor(
    val stoveRepository: StoveRepository,
    private val resourceProvider: ResourceProvider,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    val params by lazy { QrCodeScannerFragmentArgs.fromSavedStateHandle(savedStateHandle).params }


    var macAddress: String? = null

    val isKnobAddedFlow = MutableStateFlow<Unit?>(null)

    fun checkKnobOwnership(mac: String) = launch(ioContext) {
        val response = stoveRepository.getKnobOwnership(mac)
        when (response.status.knobStatus) {
            KnobStatus.InUsedByAnotherUser -> {
                loadingLiveData.postValue(false)
                error(resourceProvider.getString(R.string.knob_in_use))
            }
            KnobStatus.NotInUse -> {
                macAddress = mac
                addNewKnob()
            }
            KnobStatus.InUseByYou -> {
                macAddress = mac
                changeKnobPosition()
            }
            KnobStatus.DoesNotExists -> {
                loadingLiveData.postValue(false)
                error(resourceProvider.getString(R.string.knob_doesnt_exist))
            }
        }
    }


    private suspend fun addNewKnob() {
        if (macAddress != null) {
            if (stoveRepository.knobsFlow.value.any { it.stovePosition == params.selectedKnobPosition })
                error(resourceProvider.getString(R.string.knob_already_exists))
            stoveRepository.createKnob(
                params = KnobRequest(stovePosition = params.selectedKnobPosition),
                macAddress = macAddress!!
            )
            stoveRepository.updateKnobInfo(
                params = KnobRequest(calibrated = false),
                macAddress = macAddress!!
            )
            isKnobAddedFlow.value = Unit
        } else error(resourceProvider.getString(R.string.something_went_wrong))
    }

    private suspend fun changeKnobPosition() {
        val mac = macAddress ?: error(resourceProvider.getString(R.string.something_went_wrong))
        // Check if the knob is already in the desired position
        if (stoveRepository.knobsFlow.value.find { it.macAddr == mac }?.stovePosition == params.selectedKnobPosition) {
            isKnobAddedFlow.value = Unit; return
        }
        // Check if another knob is already at the selected position
        if (stoveRepository.knobsFlow.value.any { it.stovePosition == params.selectedKnobPosition })
            error(resourceProvider.getString(R.string.knob_already_exists))
        stoveRepository.updateKnobInfo(
            params = KnobRequest(stovePosition = params.selectedKnobPosition, calibrated = false),
            macAddress = mac
        )
        isKnobAddedFlow.value = Unit
    }
}
