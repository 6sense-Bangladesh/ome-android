package com.ome.app.ui.dashboard.settings.adapter.model

import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.ui.base.recycler.ItemModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsKnobItemModel(val name: String, val macAddr: String, val showDivider: Boolean = true): ItemModel

fun KnobDto.toItemModel(showDivider: Boolean = true) =
    SettingsKnobItemModel(
        name = "Knob #$stovePosition",
        macAddr = macAddr
    )
