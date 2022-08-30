package com.example.inirv.Home.Settings.Settings

import androidx.lifecycle.ViewModel
import com.example.inirv.Knob.Knob
import com.example.inirv.managers.KnobManager

class SettingsViewModel(
    val knobManager: KnobManager = KnobManager
) : ViewModel(), SettingsFragmentDelegate {


    override fun getKnobs(): List<Knob> {
        return knobManager.knobs
    }
}