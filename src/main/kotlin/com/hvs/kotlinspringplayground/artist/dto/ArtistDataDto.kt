package com.hvs.kotlinspringplayground.artist.dto

import java.util.UUID

data class ArtistDataDto(
    val id: UUID = UUID.randomUUID(), //TODO: move random id to place to domain or repo
    val artistId: String,
    val name: String,
)
