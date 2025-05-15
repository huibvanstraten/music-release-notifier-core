package com.hvs.kotlinspringplayground.spotify.service

import com.hvs.kotlinspringplayground.spotify.client.impl.SpotifyAsyncClient
import com.hvs.kotlinspringplayground.spotify.client.impl.SpotifyClient
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyArtistSearchResponse
import com.hvs.kotlinspringplayground.spotify.spotifyArtistResponse
import com.hvs.kotlinspringplayground.spotify.spotifyArtistSearchResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class SpotifyServiceTest {

    @Mock
    private lateinit var spotifyClient: SpotifyClient

    @Mock
    private lateinit var spotifyCAsyncClient: SpotifyAsyncClient

    @InjectMocks
    private lateinit var spotifyService: SpotifyService

    @Nested
    inner class GetArtistTests {

        @Test
        fun `getArtist maps SpotifyArtistResponse to ArtistDataDto`() {
            // GIVEN
            val artistId = "123"
            val spotifyResponse = spotifyArtistResponse()

            whenever(spotifyClient.getArtist(eq(artistId))).thenReturn(spotifyResponse)

            // WHEN
            val result = spotifyService.getArtist(artistId)

            // THEN
            assertEquals("Test Artist", result.name)
            assertEquals("123", result.artistId)
        }
    }

    @Nested
    inner class GetArtistByNameTests {

        @Test
        fun `getArtistByName returns ArtistDataDto if artist is found`() {
            // GIVEN
            val artistName = "Test Artist 1"

            whenever(spotifyClient.findArtistNamesByName(eq(artistName))).thenReturn(spotifyArtistSearchResponse())

            // WHEN
            val result = spotifyService.getArtistByName(artistName)

            // THEN
            assertNotNull(result)
            assertEquals(artistName, result!!.name)
            assertEquals("artist1", result.artistId)
        }

        @Test
        fun `getArtistByName returns null if no matching artist is found`() {
            // GIVEN
            val artistName = "NonExistent"
            val spotifyResponse = spotifyArtistSearchResponse()
            whenever(spotifyClient.findArtistNamesByName(eq(artistName))).thenReturn(spotifyResponse)

            // WHEN
            val result = spotifyService.getArtistByName(artistName)

            // THEN
            assertNull(result, "We expect null if no artist matches the search name ignoring case")
        }

        @Test
        fun `getArtistByName returns null if artists or items is null`() {
            // GIVEN
            val artistName = "WeirdCase"
            val incompleteResponse = SpotifyArtistSearchResponse(artists = null)
            whenever(spotifyClient.findArtistNamesByName(eq(artistName))).thenReturn(incompleteResponse)

            // WHEN
            val result = spotifyService.getArtistByName(artistName)

            // THEN
            assertNull(result)
        }
    }
}
