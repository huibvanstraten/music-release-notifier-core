package com.hvs.kotlinspringplayground.user.dto

import com.hvs.kotlinspringplayground.tidal.client.response.ArtistResponseData
import java.util.UUID


data class UserDataDto(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val artists: List<ArtistResponseData.ArtistData>? = null
)
