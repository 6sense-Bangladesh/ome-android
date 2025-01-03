package com.ome.app.presentation.dashboard.settings.adapter.model

import com.ome.app.domain.model.network.response.KnobDto
import com.ome.app.presentation.base.recycler.ItemModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsKnobItemModel(val stovePosition: Int, val macAddr: String, val showDivider: Boolean = true): ItemModel

fun KnobDto.toItemModel(showDivider: Boolean = true) =
    SettingsKnobItemModel(
        stovePosition = stovePosition,
        macAddr = macAddr,
        showDivider = showDivider
    )
