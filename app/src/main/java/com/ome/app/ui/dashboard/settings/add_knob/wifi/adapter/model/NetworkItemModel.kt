package com.ome.app.ui.dashboard.settings.add_knob.wifi.adapter.model

import com.ome.app.ui.base.recycler.ItemModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class NetworkItemModel(val ssid: String, val securityType: String) : ItemModel
