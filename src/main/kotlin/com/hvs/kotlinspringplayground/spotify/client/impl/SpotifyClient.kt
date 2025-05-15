package com.hvs.kotlinspringplayground.spotify.client.impl

import com.hvs.kotlinspringplayground.spotify.authorisation.SpotifyAuthorisationService
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyArtistResponse
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyArtistSearchResponse
import org.springframework.http.HttpHeaders
import org.springframework.web.client.RestClient

class SpotifyClient(
    private val spotifyAuthorisationService: SpotifyAuthorisationService,
    private val restClient: RestClient = RestClient.create(),
    private val baseUrl: String = "https://api.spotify.com/v1"
) {

    fun getArtist(artistId: String): SpotifyArtistResponse {
        val response = restClient
            .get()
            .uri("${baseUrl}/artists/$artistId")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken()}")
            .retrieve()
            .body(SpotifyArtistResponse::class.java)

        return response?: throw RuntimeException("Failed to retrieve artist data for artist: $artistId")
    }

    fun findArtistNamesByName(
        artistName: String
    ): SpotifyArtistSearchResponse {
        val response = restClient
            .get()
            .uri("${baseUrl}/search?q=$artistName&type=artist")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken()}")
            .retrieve()
            .body(SpotifyArtistSearchResponse::class.java)

        return response?: throw RuntimeException("Failed to retrieve artist data for artist: $artistName")
    }

    private val accessToken: () -> String = { spotifyAuthorisationService.getAccessToken() }
}