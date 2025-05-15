package com.hvs.kotlinspringplayground.user.dto

import com.fasterxml.jackson.annotation.JsonFilter

data class UserArtistDto(
    val username: String,
    val userId: String,
    val artistId: String
)