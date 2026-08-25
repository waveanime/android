package com.waveanime.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeResponse(
    val heroes: List<HeroItem> = emptyList(),
    val selections: HomeSelections = HomeSelections(),
    @SerialName("current_release_date") val currentReleaseDate: ReleaseDate? = null
)

@Serializable
data class ReleaseDate(
    @SerialName("release_year") val releaseYear: Int? = null,
    @SerialName("release_season") val releaseSeason: Int? = null
)

@Serializable
data class Genre(
    val id: Int,
    val name: String
)

@Serializable
data class HeroItem(
    val id: String,
    val title: String,
    val format: String = "serie",
    val synopsis: String? = null,
    val score: Double? = null,
    val genres: List<Genre> = emptyList()
)

@Serializable
data class HomeSelections(
    val resumes: List<ResumeItem> = emptyList(),
    @SerialName("released_episodes") val releasedEpisodes: List<ReleasedEpisodeItem> = emptyList(),
    @SerialName("current_season") val currentSeason: List<AnimeItem> = emptyList(),
    val news: List<AnimeItem> = emptyList(),
    val suggested: List<AnimeItem> = emptyList(),
    val populars: List<AnimeItem> = emptyList(),
    val kais: List<AnimeItem> = emptyList(),
    val movies: List<AnimeItem> = emptyList()
)

@Serializable
data class ResumeItem(
    val id: String,
    @SerialName("season_id") val seasonId: String,
    @SerialName("serie_id") val serieId: String,
    val number: Int,
    @SerialName("has_thumbnail") val hasThumbnail: Int = 0,
    val duration: Double = 0.0,
    @SerialName("season_number") val seasonNumber: Int = 1,
    @SerialName("serie_title") val serieTitle: String,
    @SerialName("progress_time") val progressTime: Double = 0.0
)

@Serializable
data class ReleasedEpisodeItem(
    val id: String,
    @SerialName("season_id") val seasonId: String,
    @SerialName("serie_id") val serieId: String,
    val number: Int,
    @SerialName("has_thumbnail") val hasThumbnail: Int = 0,
    @SerialName("season_number") val seasonNumber: Int = 1,
    @SerialName("serie_title") val serieTitle: String
)

@Serializable
data class AnimeItem(
    val id: String,
    val title: String,
    val format: String = "serie",
    val score: String? = null
)