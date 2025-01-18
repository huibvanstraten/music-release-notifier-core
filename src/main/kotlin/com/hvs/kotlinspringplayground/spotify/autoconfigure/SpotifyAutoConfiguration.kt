package com.hvs.kotlinspringplayground.spotify.autoconfigure

import com.hvs.kotlinspringplayground.spotify.authorisation.SpotifyAuthorisationService
import com.hvs.kotlinspringplayground.spotify.authorisation.SpotifyCredentials
import com.hvs.kotlinspringplayground.spotify.client.impl.SpotifyAsyncClient
import com.hvs.kotlinspringplayground.spotify.client.impl.SpotifyClient
import com.hvs.kotlinspringplayground.spotify.service.SpotifyService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(SpotifyCredentials::class)
class SpotifyAutoConfiguration {

    @Bean
    fun spotifyAuthorisationService(
        spotifyCredentials: SpotifyCredentials
    ): SpotifyAuthorisationService = SpotifyAuthorisationService(
        spotifyCredentials = spotifyCredentials
    )

    @Bean
    fun spotifyClient(
        spotifyAuthorisationService: SpotifyAuthorisationService
    ): SpotifyClient = SpotifyClient(
        spotifyAuthorisationService = spotifyAuthorisationService
    )

    @Bean
    fun spotifyAsyncClient(
        spotifyAuthorisationService: SpotifyAuthorisationService
    ): SpotifyAsyncClient = SpotifyAsyncClient(
        spotifyAuthorisationService = spotifyAuthorisationService
    )

    @Bean
    fun spotifyService(
        spotifyClient: SpotifyClient,
        spotifyAsyncClient: SpotifyAsyncClient,
    ): SpotifyService = SpotifyService(
        spotifyClient = spotifyClient,
        spotifyAsyncClient = spotifyAsyncClient
    )
}