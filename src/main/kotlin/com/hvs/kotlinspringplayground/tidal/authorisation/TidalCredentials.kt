package com.hvs.kotlinspringplayground.tidal.authorisation

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "tidal")
data class TidalCredentials(
    val clientId: String,
    val clientSecret: String,
)