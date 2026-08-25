package com.waveanime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchAnimeItem(
    val id: String = "",
    val title: String = "",
    val format: String = "serie",
    @SerialName("created_timestamp") val createdTimestamp: Long? = null,
    @SerialName("release_season") val releaseSeason: Int? = null,
    @SerialName("release_year") val releaseYear: Int? = null,
    @SerialName("last_released_episode_timestamp") val lastReleasedEpisodeTimestamp: Long? = null,
    @SerialName("has_episodes") val hasEpisodes: Int? = null,
    val score: Double? = null,
    val synopsis: String? = null
)