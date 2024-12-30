package com.hvs.kotlinspringplayground.tidal.client.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class ArtistListResponseData(
    val data: List<ArtistResponseData>
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class AlbumData(
        val id: String,
    )
}