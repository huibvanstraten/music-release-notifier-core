package com.hvs.kotlinspringplayground.tidal.client.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class AlbumIdListResponseData(
    val data: List<AlbumData>
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class AlbumData(
        val id: String,
    )
}

