package com.hvs.kotlinspringplayground.spotify.authorisation

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient

class SpotifyAuthorisationServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var spotifyAuthorisationService: SpotifyAuthorisationService
    private val spotifyCredentials: SpotifyCredentials = SpotifyCredentials(
        clientId = "fakeClientId",
        clientSecret = "fakeClientSecret"
    )

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val testUrl = mockWebServer.url("/").toString().removeSuffix("/")

        val restClient = RestClient.create()

        spotifyAuthorisationService = SpotifyAuthorisationService(
            spotifyCredentials = spotifyCredentials,
            restClient = restClient,
            authorisationUrl = "$testUrl/api/token"
        )
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `should get access token`() {
        val mockResponseBody = """
            {
              "access_token": "testAccessToken",
              "token_type": "Bearer",
              "expires_in": 3600
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(mockResponseBody)
        )

        // Act
        val token = spotifyAuthorisationService.getAccessToken()

        // Assert
        assertEquals("testAccessToken", token)
    }

    @Test
    fun `should throw error when there is no access token`() {
        val mockResponseBody = """{ "token_type": "Bearer", "expires_in": 3600 }""".trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(mockResponseBody)
        )

        // Act & Assert
        assertThrows(Exception::class.java) {
            spotifyAuthorisationService.getAccessToken()
        }
    }
}
