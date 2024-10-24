package com.ome.app.ui.dashboard.settings.adapter.model

import android.os.Parcelable
import com.ome.app.ui.base.recycler.ItemModel
import com.ome.app.ui.dashboard.settings.Settings
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsItemModel(val option: String, val showDivider: Boolean = true, val isActive: Boolean = true): ItemModel, Parcelable

fun Settings.toItemModel(showDivider: Boolean = true) =
    SettingsItemModel(option = option, showDivider = showDivider)
