package com.ome.app.domain.model.ui

import com.ome.app.domain.model.network.response.UserResponse
import com.ome.app.ui.base.recycler.ItemModel

data class UserProfileItemModel(
    val firstName: String,
    val lastName: String,
    val email: String
) : ItemModel


fun UserResponse.toItemModel(): UserProfileItemModel = UserProfileItemModel(
    firstName = this.firstName ?: "",
    lastName = this.lastName ?: "",
    email = this.email ?: ""
)