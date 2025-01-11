package com.hvs.kotlinspringplayground.artist.dto

import java.util.UUID

data class ArtistDataDto(
    val id: UUID = UUID.randomUUID(),
    val artistId: String,
    val name: String,
)
