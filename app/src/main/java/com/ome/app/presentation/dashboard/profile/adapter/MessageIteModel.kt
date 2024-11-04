package com.ome.app.presentation.dashboard.profile.adapter

import com.ome.app.presentation.base.recycler.ItemModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MessageItemModel(val invitationFrom: String, val isRead: Boolean) : ItemModel
