package com.multiplatform.data.model.base

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BaseDetailResponse<out T>(
    @SerialName("average_rating")
    val averageRating: Double? = null,
    @SerialName("backdrop_path")
    val backdropPath: String? = null,
    @SerialName("created_by")
    val createdBy: CreatedBy? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("id")
    val id: Int? = null,
    @SerialName("item_count")
    val itemCount: Int? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("page")
    val page: Int? = null,
    @SerialName("poster_path")
    val posterPath: String? = null,
    @SerialName("public")
    val `public`: Boolean? = null,
    @SerialName("results")
    val results: List<T?>? = null,
    @SerialName("revenue")
    val revenue: Long? = null,
    @SerialName("runtime")
    val runtime: Int? = null,
    @SerialName("sort_by")
    val sortBy: String? = null,
    @SerialName("total_pages")
    val totalPages: Int? = null,
    @SerialName("total_results")
    val totalResults: Int? = null
)

@Serializable
data class CreatedBy(
    @SerialName("avatar_path")
    val avatarPath: String? = null,
    @SerialName("gravatar_hash")
    val gravatarHash: String? = null,
    @SerialName("id")
    val id: String? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("username")
    val username: String? = null
)