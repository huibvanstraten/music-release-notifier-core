package com.hvs.kotlinspringplayground.spotify.authorisation

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class SpotifyAuthorisationToken(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Int
)