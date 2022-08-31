package com.example.inirv.Home.Settings.DeviceSettings

import androidx.lifecycle.ViewModel
import com.example.inirv.managers.KnobManager

class DeviceSettingsViewModel(
    val macID: String,
    val knobManager: KnobManager = KnobManager
) : ViewModel() {

}