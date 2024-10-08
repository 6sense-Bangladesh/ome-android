package com.ome.app.ui.views


interface BottomNavbar {
    var currentActiveTab: BottomItem
    var destinationListener: ((activeBottomItem: BottomItem, commitNow: Boolean) -> Unit)?
    fun setActiveTab(activeBottomItem: BottomItem)
//    fun setMarker(newNotification: Boolean, bottomItem: BottomItem)
}
