package com.hvs.kotlinspringplayground.spotify.client.response

import com.fasterxml.jackson.annotation.JsonProperty

data class SpotifyAlbumsResponse(
    val href: String?,
    val limit: Int?,
    val next: String?,
    val offset: Int?,
    val previous: String?,
    val total: Int?,
    val items: List<AlbumItem>?
) {
    data class AlbumItem(
        @JsonProperty("album_type")
        val albumType: String?,
        @JsonProperty("total_tracks")
        val totalTracks: Int?,
        @JsonProperty("available_markets")
        val availableMarkets: List<String>?,
        @JsonProperty("external_urls")
        val externalUrls: ExternalUrls?,
        val href: String?,
        val id: String?,
        val images: List<Image>?,
        val name: String?,
        @JsonProperty("release_date")
        val releaseDate: String?,
        @JsonProperty("release_date_precision")
        val releaseDatePrecision: String,
        val type: String?,
        val uri: String?,
        val artists: List<AlbumArtist>?,
        @JsonProperty("album_group")
        val albumGroup: String?
    )

    data class ExternalUrls(
        val spotify: String?
    )

    data class Image(
        val url: String?,
        val height: Int?,
        val width: Int?
    )

    data class AlbumArtist(
        @JsonProperty("external_urls") val externalUrls: ExternalUrls?,
        val href: String?,
        val id: String?,
        val name: String?,
        val type: String?,
        val uri: String?
    )
}