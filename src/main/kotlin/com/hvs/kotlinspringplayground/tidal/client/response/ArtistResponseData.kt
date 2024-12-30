package com.hvs.kotlinspringplayground.tidal.client.response

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class ArtistResponseData(
    val data: List<ArtistData>
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class ArtistData(
        val attributes: Attributes,
        val id: String,
    ) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Attributes(
            val name: String,
        )
    }
}