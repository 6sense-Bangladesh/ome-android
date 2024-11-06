package com.ome.app.presentation.dashboard.settings.add_knob.scanner

import com.ome.app.R
import com.ome.app.data.local.ResourceProvider
import com.ome.app.domain.model.network.request.CreateKnobRequest
import com.ome.app.domain.model.state.KnobStatus
import com.ome.app.domain.model.state.knobStatus
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.presentation.base.BaseViewModel
import com.ome.app.presentation.base.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class QrCodeScannerViewModel @Inject constructor(
    val stoveRepository: StoveRepository,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    var stovePosition: Int? = null
    var macAddress: String? = null

    val isKnobAddedLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val knobCreatedLiveData: SingleLiveEvent<Boolean> = SingleLiveEvent()

    fun checkStoveOwnership(macAddress: String) = launch(ioContext) {
       // val filteredMacID = macAddress.filter { macIDFilter.contains(it) }
        val response = stoveRepository.getKnobOwnership(macAddress)
        when (response.status.knobStatus) {
            KnobStatus.InUsedByAnotherUser -> {
                loadingLiveData.postValue(false)
                defaultErrorLiveData.postValue(resourceProvider.getString(R.string.knob_in_use))
            }
            KnobStatus.NotInUse -> {
                isKnobAddedLiveData.postValue(false)
                this@QrCodeScannerViewModel.macAddress = macAddress
            }
            KnobStatus.InUseByYou -> {
                isKnobAddedLiveData.postValue(true)
                this@QrCodeScannerViewModel.macAddress = macAddress
            }
            KnobStatus.DoesNotExists -> {
                loadingLiveData.postValue(false)
                defaultErrorLiveData.postValue(resourceProvider.getString(R.string.knob_doesnt_exist))
            }
        }

    }


    fun addNewKnob() = launch(ioContext) {
        if (macAddress != null && stovePosition != null) {
            stoveRepository.createKnob(
                params = CreateKnobRequest(
                    highAngle = -1,
                    lowAngle = -1,
                    mediumAngle = -1,
                    offAngle = -1,
                    macID = macAddress!!,
                    stovePosition = stovePosition!!
                ),
                macAddress = macAddress!!
            )
            knobCreatedLiveData.postValue(true)
        }
    }
}
