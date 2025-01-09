package com.hvs.kotlinspringplayground.spotify.client.response

import com.fasterxml.jackson.annotation.JsonProperty

data class SpotifyArtistSearchResponse(
    val artists: Artists?
) {
    data class Artists(
        val href: String?,
        val limit: Int?,
        val next: String?,
        val offset: Int?,
        val previous: String?,
        val total: Int?,
        val items: List<Artist>?
    )

    data class Artist(
        @JsonProperty("external_urls")
        val externalUrls: ExternalUrls?,
        val followers: Followers?,
        val genres: List<String>?,
        val href: String?,
        val id: String,
        val images: List<Image>?,
        val name: String,
        val popularity: Int?,
        val type: String?,
        val uri: String?
    )

    data class ExternalUrls(
        val spotify: String?
    )

    data class Followers(
        val href: String?,
        val total: Int?
    )

    data class Image(
        val url: String?,
        val height: Int?,
        val width: Int?
    )
}

