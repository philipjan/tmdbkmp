package com.multiplatform.data.model


import com.multiplatform.data.model.base.BaseMedia
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TvShow(
    @SerialName("first_air_date")
    val firstAirDate: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("origin_country")
    val originCountry: List<String?>? = null,
    @SerialName("original_name")
    val originalName: String? = null
): BaseMedia()