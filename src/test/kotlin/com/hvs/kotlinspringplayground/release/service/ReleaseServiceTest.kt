package com.hvs.kotlinspringplayground.release.service

import com.hvs.kotlinspringplayground.release.domain.jpa.Release
import com.hvs.kotlinspringplayground.release.repository.ReleaseRepository
import com.hvs.kotlinspringplayground.spotify.client.response.Album
import com.hvs.kotlinspringplayground.spotify.client.response.ExternalUrls
import com.hvs.kotlinspringplayground.spotify.service.SpotifyService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import java.time.LocalDate

class ReleaseServiceTest {

    private val spotifyService: SpotifyService = mock()
    private val releaseRepository: ReleaseRepository = mock()

    private val releaseService = ReleaseService(spotifyService, releaseRepository)

    @Test
    fun `storeAllReleasesForArtist should collect pages from spotify and save them to repository`() = runBlocking {
        // Given
        val artistId = "artist123"

        val album1 = Album(
            id = "album1",
            name = "Album One",
            totalTracks = 10,
            releaseDate = "2023-01-01",
            type = "album",
            externalUrls = ExternalUrls("https://open.spotify.com/album/xxx"),
            albumType = "album",
            availableMarkets = listOf(),
            href = "",
            images = listOf(),
            releaseDatePrecision = "day",
            uri = "",
            artists = listOf(),
            albumGroup = ""
        )

        val album2 = Album(
            id = "album2",
            name = "Album Two",
            totalTracks = 8,
            releaseDate = "2024-05-24",
            type = "album",
            externalUrls = ExternalUrls("https://open.spotify.com/album/yyy"),
            albumType = "album",
            availableMarkets = listOf(),
            href = "",
            images = listOf(),
            releaseDatePrecision = "day",
            uri = "",
            artists = listOf(),
            albumGroup = ""
        )

        val pageOfAlbums = PageImpl(listOf(album1, album2))
        val flowOfPages: Flow<PageImpl<Album>> = flowOf(pageOfAlbums)

        whenever(spotifyService.getReleasesForArtist(artistId)).thenReturn(flowOfPages)

        // When
        releaseService.storeAllReleasesForArtist(artistId)

        // Then
        val captor = argumentCaptor<Release>()
        verify(releaseRepository, Mockito.times(2)).save(captor.capture())

        val savedReleases = captor.allValues
        assertEquals(2, savedReleases.size)

        val first = savedReleases[0]
        assertEquals("album1", first.releaseId)
        assertEquals("Album One", first.name)
        assertEquals(LocalDate.of(2023,1,1), first.releaseDate)
        assertEquals("album", first.type)

        val second = savedReleases[1]
        assertEquals("album2", second.releaseId)
        assertEquals("Album Two", second.name)
        assertEquals(LocalDate.of(2024,5,24), second.releaseDate)
    }
}