package com.hvs.kotlinspringplayground.artist.dto

import com.hvs.kotlinspringplayground.tidal.client.response.AlbumResponseData
import java.util.UUID


data class ArtistDataDto(
    val id: UUID = UUID.randomUUID(),
    val streamingId: Int,
    val name: String,
    val albumResponseData: List<AlbumResponseData.AlbumData>? = null
)
