package com.ome.app.ui.dashboard.profile.adapter

import com.ome.app.ui.base.recycler.ItemModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageItemModel(val invitationFrom: String, val isRead: Boolean) : ItemModel
