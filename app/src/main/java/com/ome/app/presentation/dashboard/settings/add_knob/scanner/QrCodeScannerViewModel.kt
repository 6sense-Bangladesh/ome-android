package com.ome.app.presentation.dashboard.settings.add_knob.scanner

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
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    var stovePosition: Int? = null
    var macAddress: String? = null

    val isKnobAddedFlow = MutableStateFlow<Unit?>(null)

    fun checkKnobOwnership(macAddress: String) = launch(ioContext) {
        val response = stoveRepository.getKnobOwnership(macAddress)
        when (response.status.knobStatus) {
            KnobStatus.InUsedByAnotherUser -> {
                loadingLiveData.postValue(false)
                error(resourceProvider.getString(R.string.knob_in_use))
            }
            KnobStatus.NotInUse -> {
                this@QrCodeScannerViewModel.macAddress = macAddress
                addNewKnob()
            }
            KnobStatus.InUseByYou -> {
                this@QrCodeScannerViewModel.macAddress = macAddress
                isKnobAddedFlow.value = Unit
            }
            KnobStatus.DoesNotExists -> {
                loadingLiveData.postValue(false)
                error(resourceProvider.getString(R.string.knob_doesnt_exist))
            }
        }
    }


    private suspend fun addNewKnob() {
        if (macAddress != null && stovePosition != null) {
            if (stoveRepository.knobsFlow.value.any { it.stovePosition == stovePosition })
                error(resourceProvider.getString(R.string.knob_already_exists))
            stoveRepository.createKnob(
                params = KnobRequest(stovePosition = stovePosition!!),
                macAddress = macAddress!!
            )
            stoveRepository.updateKnobInfo(
                params = KnobRequest(calibrated = false, safetyLock = false),
                macAddress = macAddress!!
            )
            isKnobAddedFlow.value = Unit
        } else error(resourceProvider.getString(R.string.something_went_wrong))
    }
}
