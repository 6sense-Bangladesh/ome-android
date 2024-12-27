package com.ome.app.presentation.dashboard.settings.adapter.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import com.ome.app.R
import com.ome.app.presentation.base.recycler.ItemModel
import com.ome.app.presentation.dashboard.settings.Settings
import kotlinx.parcelize.Parcelize

@Parcelize
data class SettingsItemModel(val option: String, val showDivider: Boolean = true, val isActive: Boolean = true): ItemModel

fun Settings.toItemModel(showDivider: Boolean = true) =
    SettingsItemModel(option = option, showDivider = showDivider)


//@Parcelize
//data class DeviceSettingsItemModel(val settingItemType: SettingItemType): ItemModel


@Parcelize
enum class DeviceSettingsItemModel(val option: String, @DrawableRes val icon: Int): Parcelable, ItemModel{
    KnobPosition("Change Knob Position", R.drawable.ic_stove_mini),
    KnobWiFI("Change Knob Wi-Fi", R.drawable.ic_wifi),
    KnobCalibration("Change Knob Calibration", R.drawable.ic_rotate),
    DeleteKnob("Delete Knob", R.drawable.ic_delete)
}

//fun SettingItemType.toItemModel() = DeviceSettingsItemModel(settingItemType = this)
