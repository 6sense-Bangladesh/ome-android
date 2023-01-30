package com.ome.app.ui.dashboard.settings

import com.ome.app.base.BaseViewModel
import com.ome.app.base.SingleLiveEvent
import com.ome.app.ui.base.recycler.ItemModel
import com.ome.app.ui.dashboard.settings.adapter.SettingsItemModel
import com.ome.app.ui.dashboard.settings.adapter.SettingsTitleItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor() : BaseViewModel() {

    val settingsList = SingleLiveEvent<List<ItemModel>>()

    fun loadSettings() {
        val settings = mutableListOf<ItemModel>(
            SettingsItemModel(option = "Stove Information Settings", isActive = true),
            SettingsItemModel(option = "Stove Auto-Off Settings", isActive = true),
            SettingsItemModel(option = "Stove History", isActive = true),
            SettingsItemModel(option = "Leave Stove", isActive = true),
            SettingsTitleItemModel(title = "ABOUT DEVICES"),
            SettingsItemModel(option = "Knob #1", isActive = true),
            SettingsItemModel(option = "Knob #2", isActive = true),
            SettingsItemModel(option = "Knob #3", isActive = true),
            SettingsItemModel(option = "Add New Knob", isActive = true),
        )

        settingsList.postValue(settings)
    }

}
