package com.ome.app.ui.dashboard.settings

import androidx.lifecycle.SavedStateHandle
import com.ome.app.domain.repo.StoveRepository
import com.ome.app.domain.repo.UserRepository
import com.ome.app.ui.base.BaseViewModel
import com.ome.app.ui.dashboard.settings.adapter.SettingsTitleItemModel
import com.ome.app.ui.dashboard.settings.adapter.model.toItemModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    val userRepository: UserRepository,
    val stoveRepository: StoveRepository,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val defaultSettingOptions = buildList {
        add(SettingsTitleItemModel(title = "General"))
        addAll(Settings.entries.toMutableList().apply { removeLastOrNull() }.map { it.toItemModel() })
        add(Settings.entries.last().toItemModel(false))
        add(SettingsTitleItemModel(title = "My Devices"))
    }

    val settingsList = savedStateHandle.getStateFlow("settingsList", defaultSettingOptions)

    fun loadSettings() = launch(ioContext) {
        stoveRepository.knobsFlow.collect { knobs ->
            if (knobs.isNotEmpty()) {
                val settings = defaultSettingOptions.toMutableList()
                settings.addAll(knobs.toMutableList().apply { removeLastOrNull() }.map { it.toItemModel() })
                settings.add(knobs.last().toItemModel(false))
                savedStateHandle["settingsList"] = settings
            }
        }
    }

}

enum class Settings(val option: String) {
    ADD_NEW_KNOB("Add New Knob"),
    STOVE_BRAND("Stove Brand"),
    STOVE_TYPE("Stove Type"),
    STOVE_LAYOUT("Stove layout"),
    STOVE_AUTO_SHUT_OFF("Stove Auto-Off Settings"),
//    STOVE_INFO_SETTINGS("Stove Information Settings"),
//    STOVE_HISTORY("Stove History"),
//    LEAVE_STOVE("Leave Stove")
}
