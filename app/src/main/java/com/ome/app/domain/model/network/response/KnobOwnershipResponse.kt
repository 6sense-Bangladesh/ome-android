package com.ome.app.domain.model.network.response

import com.google.gson.annotations.SerializedName

data class KnobOwnershipResponse(
    @SerializedName("status") val status: Int?,
    @SerializedName("statusName") val statusName: String?
)
