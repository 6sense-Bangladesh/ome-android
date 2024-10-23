package com.ome.app.ui.stove

import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.domain.model.network.request.StoveRequest
import com.ome.app.utils.isNotEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveSetupBrandViewModel @Inject constructor(
    private val stoveRepository: StoveRepository
) : BaseViewModel() {

    val brandArray = listOf(
        "Samsung",
        "Thermador",
        "Dacor",
        "GE",
        "Jenn-air",
        "Wolf",
        "Electrolux",
        "Maytag",
        "Kenmore",
        "KitchenAid",
        "Whirlpool",
        "Viking",
        "LG",
        "Frigidaire",
        "Bosch",
        "Miele",
        "OTHER"
    )

    var selectedBrand = ""

    fun updateSelectedBrand(stoveId: String, onEnd :() ->Unit) = launch(dispatcher = ioContext) {
        selectedBrand.isNotEmpty {
            stoveRepository.updateStove(
                com.ome.app.domain.model.network.request.StoveRequest(
                    stoveMakeModel = selectedBrand
                ), stoveId = stoveId)
            onEnd()
//            userRepository.getUserData()
            loadingLiveData.postValue(false)
        } ?: error("Please select a brand")
    }
}
