package com.ome.app.presentation.dashboard.settings.add_knob.scanner

import com.ome.app.R
import com.ome.app.data.local.ResourceProvider
import com.ome.app.domain.model.network.request.CreateKnobRequest
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

    val isKnobAddedFlow = MutableStateFlow<Unit?>(Unit)

    fun checkKnobOwnership(macAddress: String) = launch(ioContext) {
        val response = stoveRepository.getKnobOwnership(macAddress)
        when (response.status.knobStatus) {
            KnobStatus.InUsedByAnotherUser -> {
                loadingLiveData.value = false
                defaultErrorLiveData.value = resourceProvider.getString(R.string.knob_in_use)
            }
            KnobStatus.NotInUse -> {
                addNewKnob()
                this@QrCodeScannerViewModel.macAddress = macAddress
            }
            KnobStatus.InUseByYou -> {
                isKnobAddedFlow.value = Unit
                this@QrCodeScannerViewModel.macAddress = macAddress
            }
            KnobStatus.DoesNotExists -> {
                loadingLiveData.value = false
                defaultErrorLiveData.value = resourceProvider.getString(R.string.knob_doesnt_exist)
            }
        }

    }


    private suspend fun addNewKnob(){
        if (macAddress != null && stovePosition != null) {
            stoveRepository.createKnob(
                params = CreateKnobRequest(stovePosition = stovePosition!!, calibrated = false),
                macAddress = macAddress!!
            )
            isKnobAddedFlow.value = Unit
        }
    }
}
