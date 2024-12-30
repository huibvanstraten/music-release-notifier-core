package com.hvs.kotlinspringplayground.tidal.authorisation

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import java.util.Base64

class AuthorisationService(
    private val tidalCredentials: TidalCredentials,
) {

    private val authWebClient: WebClient by lazy {
        WebClient.builder()
            .baseUrl(TIDAL_AUTH_URL)
            .build()
    }

    fun getToken(): String {
        val credentials = "${tidalCredentials.clientId}:${tidalCredentials.clientSecret}"
        val encodedCredentials = Base64.getEncoder().encodeToString(credentials.toByteArray())

        val response = authWebClient.post()
            .header(HttpHeaders.AUTHORIZATION, "Basic $encodedCredentials")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(
                BodyInserters.fromFormData("grant_type", "client_credentials")
            )
            .retrieve()
            .bodyToMono(TidalAuthorisationToken::class.java)
            .block()

        return response?.accessToken ?: throw RuntimeException("Failed to retrieve access token")
    }

    companion object {
        private const val TIDAL_AUTH_URL = "https://auth.tidal.com/v1/oauth2/token"
    }
}