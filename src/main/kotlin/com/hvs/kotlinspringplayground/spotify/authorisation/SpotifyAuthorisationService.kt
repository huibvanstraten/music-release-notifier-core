package com.hvs.kotlinspringplayground.spotify.authorisation

import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClient

class SpotifyAuthorisationService(
    private val spotifyCredentials: SpotifyCredentials,
    ) {

        private val restClient: RestClient = RestClient.create()

        fun getAccessToken(): String {
            val formData: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "client_credentials")
                add("client_id", spotifyCredentials.clientId)
                add("client_secret", spotifyCredentials.clientSecret)
            }

            val response = restClient
                .post()
                .uri(SPOTIFY_AUTH_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData) // Form data body
                .retrieve()
                .body(SpotifyAuthorisationToken::class.java)

            return response?.accessToken ?: throw RuntimeException("Failed to retrieve access token")
        }

    companion object {
        private const val SPOTIFY_AUTH_URL = "https://accounts.spotify.com/api/token"
    }
}
