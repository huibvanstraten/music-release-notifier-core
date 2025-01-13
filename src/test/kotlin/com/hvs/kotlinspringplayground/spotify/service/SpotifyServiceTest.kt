package com.hvs.kotlinspringplayground.spotify.service

import com.hvs.kotlinspringplayground.spotify.albumItem
import com.hvs.kotlinspringplayground.spotify.client.SpotifyClient
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
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest

@ExtendWith(MockitoExtension::class)
class SpotifyServiceTest {

    @Mock
    private lateinit var spotifyClient: SpotifyClient

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

            whenever(spotifyClient.findArtistByName(eq(artistName))).thenReturn(spotifyArtistSearchResponse())

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
            whenever(spotifyClient.findArtistByName(eq(artistName))).thenReturn(spotifyResponse)

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
            whenever(spotifyClient.findArtistByName(eq(artistName))).thenReturn(incompleteResponse)

            // WHEN
            val result = spotifyService.getArtistByName(artistName)

            // THEN
            assertNull(result)
        }
    }

    @Nested
    inner class GetAlbumsOfArtistTests {

        @Test
        fun `getAlbumsOfArtist returns the page from SpotifyClient`() {
            // GIVEN
            val artistId = "123"
            val pageable = PageRequest.of(1, 5)
            val albumList = listOf(
                albumItem(),
                albumItem()
            )
            val mockPage = PageImpl(albumList, pageable, 10)
            whenever(spotifyClient.getArtistAlbumsPageable(artistId = eq(artistId), pageable = eq(pageable), any())).thenReturn(mockPage)

            // WHEN
            val resultPage = spotifyService.getAlbumsOfArtist(artistId, pageable)

            // THEN
            assertEquals(2, resultPage.content.size)
            assertEquals("Test Album", resultPage.content[0].name)
        }
    }
}