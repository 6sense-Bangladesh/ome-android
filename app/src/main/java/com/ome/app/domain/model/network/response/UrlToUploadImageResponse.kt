package com.ome.app.domain.model.network.response

import com.google.gson.annotations.SerializedName

data class UrlToUploadImageResponse(
    @SerializedName("uploadTo") val uploadTo: String
)
