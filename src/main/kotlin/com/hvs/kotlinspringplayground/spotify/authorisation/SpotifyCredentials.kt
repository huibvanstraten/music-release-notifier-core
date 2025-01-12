package com.hvs.kotlinspringplayground.spotify.authorisation

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spotify")
data class SpotifyCredentials(
    val clientId: String,
    val clientSecret: String,
)
