package com.hvs.kotlinspringplayground.artist.service.impl

import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.artist.repository.ArtistRepository
import com.hvs.kotlinspringplayground.outbox.service.OutboxService
import com.hvs.kotlinspringplayground.spotify.service.SpotifyService
import com.hvs.kotlinspringplayground.tidal.service.TidalService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ArtistServiceTest {

    @Mock
    private lateinit var tidalService: TidalService

    @Mock
    private lateinit var spotifyService: SpotifyService

    @Mock
    private lateinit var artistRepository: ArtistRepository

    @Mock
    private lateinit var outboxService: OutboxService

    @InjectMocks
    private lateinit var artistServiceImpl: ArtistService


    @Test
    fun `getArtist returns ArtistDataDto from spotifyService`() {
        // GIVEN
        val artistId = "1234"
        val artistData = ArtistDataDto(
            id = UUID.randomUUID(),
            artistId = artistId,
            name = "name",
        )
        whenever(spotifyService.getArtist(artistId)).thenReturn(artistData)

        // WHEN
        val result = artistServiceImpl.getArtist(artistId)

        // THEN
        assertEquals(artistData, result)
        verify(spotifyService, times(1)).getArtist(artistId)
    }

    @Test
    fun `getArtistFromSpotifyByName returns ArtistDataDto and sends ReleaseEvent`() {
        // GIVEN
        val artistName = "Beatles"
        val artistData = ArtistDataDto(
            id = UUID.randomUUID(),
            artistId = "1234",
            name = artistName,
        )
        whenever(spotifyService.getArtistByName(artistName)).thenReturn(artistData)

        // WHEN
        artistServiceImpl.getArtistFromSpotifyByName(artistName)

        verify(spotifyService, times(1)).getArtistByName(artistName)
    }

    @Test
    fun `getArtistFromSpotifyByName returns null and sends ReleaseEvent when Spotify returns null`() {
        // GIVEN
        val artistName = "NonExistentArtist"
        whenever(spotifyService.getArtistByName(artistName)).thenReturn(null)

        // WHEN
        val result = artistServiceImpl.getArtistFromSpotifyByName(artistName)

        // THEN
        assertNull(result)
        verify(spotifyService, times(1)).getArtistByName(artistName)
    }
}
