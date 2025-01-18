package com.hvs.kotlinspringplayground.release.dto

import java.time.LocalDate

data class ReleaseDto(
    val releaseId: String,
    val artistId: String,
    val totalTracks: Int?,
    val name: String?,
    val releaseDate: LocalDate?,
    val type: String?,
    val spotifyUrl: String?,
)