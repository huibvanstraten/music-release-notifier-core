package com.hvs.kotlinspringplayground.spotify.client.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyAlbumsResponse(
    @SerialName("href") val href: String?,
    @SerialName("limit") val limit: Int?,
    @SerialName("next") val next: String?,
    @SerialName("offset") val offset: Int,
    @SerialName("previous") val previous: String?,
    @SerialName("total") val total: Int,
    @SerialName("items") val items: List<Album>
)

@Serializable
data class Album(
    @SerialName("album_type") val albumType: String,
    @SerialName("total_tracks") val totalTracks: Int,
    @SerialName("available_markets") val availableMarkets: List<String>,
    @SerialName("external_urls") val externalUrls: ExternalUrls,
    @SerialName("href") val href: String,
    @SerialName("id") val id: String,
    @SerialName("images") val images: List<Image>,
    @SerialName("name") val name: String,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("release_date_precision") val releaseDatePrecision: String,
    @SerialName("type") val type: String,
    @SerialName("uri") val uri: String,
    @SerialName("artists") val artists: List<Artist>,
    @SerialName("album_group") val albumGroup: String
)

@Serializable
data class ExternalUrls(
    @SerialName("spotify") val spotify: String
)

@Serializable
data class Image(
    @SerialName("url") val url: String,
    @SerialName("height") val height: Int,
    @SerialName("width") val width: Int
)

@Serializable
data class Artist(
    @SerialName("external_urls") val externalUrls: ExternalUrls?,
    @SerialName("href") val href: String?,
    @SerialName("id") val id: String?,
    @SerialName("name") val name: String,
    @SerialName("type") val type: String,
    @SerialName("uri") val uri: String?
)