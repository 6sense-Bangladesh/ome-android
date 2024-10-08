package com.ome.app.ui.views

import android.graphics.Color
import com.ome.Ome.R

enum class BottomItem(
    val rootId: Int,
    val viewId: Int,
    val iconResDefaultWhite: Int,
    val iconResDefaultBlue: Int,
    var iconResActive: Int,
    val title: Int,
    val colorTitleUnpressed: Int,
    val colorTitleActiveBlueBackground: Int,
    val colorTitleActiveWhiteBackground: Int,
    var openInner: OpenFrom? = null,
    var params: Any? = null
) {
    SETTINGS(
        1,
        viewId = R.id.ivSettings,
        iconResDefaultWhite = R.drawable.ic_settings_white,
        iconResDefaultBlue = R.drawable.ic_settings_blue,
        iconResActive = R.drawable.ic_settings_blue_pressed,
        title = R.string.menu_settings,
        colorTitleUnpressed = R.color.menu_item_gray_color,
        colorTitleActiveBlueBackground = Color.WHITE,
        colorTitleActiveWhiteBackground = R.color.menu_item_blue_color,
    ),

    MY_STOVE(
        2,
        viewId = R.id.ivMyStove,
        iconResDefaultWhite = R.drawable.ic_my_stove,
        iconResDefaultBlue = R.drawable.ic_my_stove,
        iconResActive = R.drawable.ic_my_stove_pressed,
        title = R.string.menu_my_stove,
        colorTitleUnpressed = R.color.menu_item_gray_color,
        colorTitleActiveBlueBackground = Color.WHITE,
        colorTitleActiveWhiteBackground = R.color.menu_item_blue_color,
    ),
    MEMBERS(
        3,
        viewId = R.id.ivMember,
        iconResDefaultWhite = R.drawable.ic_members_white,
        iconResDefaultBlue = R.drawable.ic_members_blue,
        iconResActive = R.drawable.ic_members_pressed,
        title = R.string.menu_members,
        colorTitleUnpressed = R.color.menu_item_gray_color,
        colorTitleActiveBlueBackground = Color.WHITE,
        colorTitleActiveWhiteBackground = R.color.menu_item_blue_color,
    ),

    PROFILE(
        4,
        viewId = R.id.ivProfile,
        iconResDefaultWhite = R.drawable.ic_profile_white,
        iconResDefaultBlue = R.drawable.ic_profile_blue,
        iconResActive = R.drawable.ic_profile_pressed,
        title = R.string.menu_profile,
        colorTitleUnpressed = R.color.menu_item_gray_color,
        colorTitleActiveBlueBackground = Color.WHITE,
        colorTitleActiveWhiteBackground = R.color.menu_item_blue_color,
    );


    companion object {
        fun findByViewID(viewId: Int): BottomItem =
            values().firstOrNull { it.viewId == viewId } ?: MY_STOVE
    }
}

enum class OpenFrom {
    GO_TO_BOARD
}
