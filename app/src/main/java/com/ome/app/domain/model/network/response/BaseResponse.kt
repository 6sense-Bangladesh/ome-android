package com.ome.app.domain.model.network.response

import com.google.gson.annotations.SerializedName

data class BaseResponse(
    @SerializedName("message")
    val message: Any? = null
)
