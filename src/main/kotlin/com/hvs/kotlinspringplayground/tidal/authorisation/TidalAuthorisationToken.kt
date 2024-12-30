package com.hvs.kotlinspringplayground.tidal.authorisation

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class TidalAuthorisationToken(
    val scope: String,
    val tokenType: String,
    val accessToken: String,
    val expiresIn: Int,
)