package com.ome.app.ui.dashboard.settings.add_knob.scanner

import com.ome.Ome.R
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.base.SingleLiveEvent
import com.ome.app.data.local.ResourceProvider
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.ui.model.network.request.CreateKnobRequest
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

    fun checkStoveOwnership(macAddress: String) = launch(dispatcher = ioContext) {
       // val filteredMacID = macAddress.filter { macIDFilter.contains(it) }
        val response = stoveRepository.getKnobOwnership(macAddress)
        response.status?.let { status ->
            when (status) {
                0 -> {
                    loadingLiveData.postValue(false)
                    defaultErrorLiveData.postValue(resourceProvider.getString(R.string.knob_in_use))
                }
                1 -> {
                    isKnobAddedLiveData.postValue(false)
                    this@QrCodeScannerViewModel.macAddress = macAddress
                }
                2 -> {
                    isKnobAddedLiveData.postValue(true)
                    this@QrCodeScannerViewModel.macAddress = macAddress
                }
                3 -> {
                    loadingLiveData.postValue(false)
                    defaultErrorLiveData.postValue(resourceProvider.getString(R.string.knob_doesnt_exist))
                }
            }
        }
    }


    fun addNewKnob() = launch(dispatcher = ioContext) {
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
