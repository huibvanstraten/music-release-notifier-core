package com.hvs.kotlinspringplayground.spotify.authorisation

import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestClient

class SpotifyAuthorisationService(
    private val spotifyCredentials: SpotifyCredentials,
    private val restClient: RestClient = RestClient.create(),
    private val authorisationUrl: String = "https://accounts.spotify.com/api/token"
) {

        fun getAccessToken(): String {
            val formData: MultiValueMap<String, String> = LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "client_credentials")
                add("client_id", spotifyCredentials.clientId)
                add("client_secret", spotifyCredentials.clientSecret)
            }

            val response = restClient
                .post()
                .uri(authorisationUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(SpotifyAuthorisationToken::class.java)

            return response!!.accessToken
            }
}
