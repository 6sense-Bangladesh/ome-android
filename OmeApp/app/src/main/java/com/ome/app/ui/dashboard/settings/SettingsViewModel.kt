package com.ome.app.ui.dashboard.settings

import com.ome.app.base.BaseViewModel
import com.ome.app.base.SingleLiveEvent
import com.ome.app.data.remote.stove.StoveRepository
import com.ome.app.data.remote.user.UserRepository
import com.ome.app.ui.base.recycler.ItemModel
import com.ome.app.ui.dashboard.settings.adapter.model.SettingsItemModel
import com.ome.app.ui.dashboard.settings.adapter.SettingsTitleItemModel
import com.ome.app.ui.dashboard.settings.adapter.model.SettingsKnobItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    val userRepository: UserRepository,
    val stoveRepository: StoveRepository
) : BaseViewModel() {

    val settingsList = SingleLiveEvent<List<ItemModel>>()

    fun loadSettings() = launch(dispatcher = ioContext) {
        settingsList.postValue(
            mutableListOf(
                SettingsItemModel(option = Settings.STOVE_INFO_SETTINGS.option, isActive = true),
                SettingsItemModel(option = Settings.STOVE_AUTO_SHUT_OFF.option, isActive = true),
                SettingsItemModel(option = Settings.STOVE_HISTORY.option, isActive = true),
                SettingsItemModel(option = Settings.LEAVE_STOVE.option, isActive = true),
                SettingsTitleItemModel(title = "ABOUT DEVICES"),
                SettingsItemModel(option = Settings.ADD_NEW_KNOB.option, isActive = true)
            )
        )
        stoveRepository.knobsFlow.collect { knobs ->
            if (knobs != null) {
                val settings = mutableListOf(
                    SettingsItemModel(option = Settings.STOVE_INFO_SETTINGS.option, isActive = true),
                    SettingsItemModel(option = Settings.STOVE_AUTO_SHUT_OFF.option, isActive = true),
                    SettingsItemModel(option = Settings.STOVE_HISTORY.option, isActive = true),
                    SettingsItemModel(option = Settings.LEAVE_STOVE.option, isActive = true),
                    SettingsTitleItemModel(title = "ABOUT DEVICES")
                )
                knobs.forEach {
                    settings.add(SettingsKnobItemModel(name = "Knob #${it.stovePosition}", macAddr = it.macAddr))
                }
                settings.add(
                    SettingsItemModel(
                        option = Settings.ADD_NEW_KNOB.option,
                        isActive = true
                    )
                )
                settingsList.postValue(settings)
            }
        }
    }

}

enum class Settings(val option: String) {
    STOVE_INFO_SETTINGS("Stove Information Settings"),
    STOVE_AUTO_SHUT_OFF("Stove Auto-Off Settings"),
    STOVE_HISTORY("Stove History"),
    LEAVE_STOVE("Leave Stove"),
    ADD_NEW_KNOB("Add New Knob")
}
