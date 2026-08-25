package com.waveanime.data.util

object MediaUrl {
    private const val BASE_URL = "https://waveanime.fr/media"

    fun poster(serieId: String): String = "$BASE_URL/posters/$serieId-large.avif"
    fun cover(serieId: String): String = "$BASE_URL/covers/$serieId-large.avif"

    fun thumbnail(episodeId: String, serieId: String, hasThumbnail: Boolean): String {
        return if (hasThumbnail) {
            "$BASE_URL/thumbnails/$episodeId-large.avif"
        } else {
            cover(serieId)
        }
    }

    fun getSeasonName(season: Int?): String = when (season) {
        0 -> "d'hiver"
        1 -> "de printemps"
        2 -> "d'été"
        3 -> "d'automne"
        else -> ""
    }
}