package com.ome.app.ui.stove

import com.ome.app.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StoveSetupBrandViewModel @Inject constructor() : BaseViewModel() {

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
}
