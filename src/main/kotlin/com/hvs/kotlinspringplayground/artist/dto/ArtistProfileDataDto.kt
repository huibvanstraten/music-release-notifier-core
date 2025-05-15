package com.hvs.kotlinspringplayground.artist.dto

data class ArtistProfileDataDto(
    val artistId: String,
    val name: String,
    val genres: List<String>,
    val image: SpotifyImage?,
    val spotifyLink: String,
) {

    data class SpotifyImage(
        val url: String?,
        val width: Int?,
        val height: Int?,
    )
}
