package com.example.mypocketguide.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetExhibitResponseDto(
    @SerialName("exhibit_id")          val exhibitId: String,
    @SerialName("exhibit_title")       val exhibitTitle: String,
    @SerialName("exhibit_description") val exhibitDescription: String,
    @SerialName("exhibit_image")       val exhibitImage: String
)