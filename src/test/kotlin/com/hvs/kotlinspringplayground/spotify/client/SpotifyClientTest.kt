package com.hvs.kotlinspringplayground.spotify.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.spotify.authorisation.SpotifyAuthorisationService
import com.hvs.kotlinspringplayground.spotify.client.impl.SpotifyClient
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyArtistResponse
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyArtistSearchResponse
import com.hvs.kotlinspringplayground.spotify.spotifyArtistResponse
import com.hvs.kotlinspringplayground.spotify.spotifyArtistSearchResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.web.client.RestClient

class SpotifyClientTest {

    private val spotifyAuthorisationService: SpotifyAuthorisationService = mock()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var spotifyClient: SpotifyClient

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val baseUrl = mockWebServer.url("/").toString().removeSuffix("/")

        val restClient = RestClient.create()

        spotifyClient = SpotifyClient(
            spotifyAuthorisationService = spotifyAuthorisationService,
            restClient = restClient,
            baseUrl = baseUrl
        )

        whenever(spotifyAuthorisationService.getAccessToken()).thenReturn("test-token")
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test getArtist successful response`() {
        // Arrange
        val mockResponseBody = """
            ${jacksonObjectMapper().valueToTree<JsonNode>(spotifyArtistResponse())}
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(mockResponseBody)
                .setResponseCode(200)
        )

        // Act
        val response: SpotifyArtistResponse = spotifyClient.getArtist("artist123")

        // Assert
        assertEquals("artist123", response.id)
        assertEquals("Test Artist", response.name)
        assertEquals(listOf("Rock", "Pop"), response.genres)
        assertEquals(72, response.popularity)
    }

    @Test
    fun `test findArtistByName successful response`() {
        // Arrange
        val mockResponseBody = """
            ${jacksonObjectMapper().valueToTree<JsonNode>(spotifyArtistSearchResponse())}
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .addHeader("Content-Type", "application/json")
                .setBody(mockResponseBody)
                .setResponseCode(200)
        )

        // Act
        val response: SpotifyArtistSearchResponse = spotifyClient.findArtistByName("Artist Name")

        // Assert
        val items = response.artists?.items
        assertEquals(2, items!!.size)
        assertEquals("artist1", items[0].id)
        assertEquals("Test Artist 1", items[0].name)
        assertEquals("artist2", items[1].id)
        assertEquals("Test Artist 2", items[1].name)
    }
}
