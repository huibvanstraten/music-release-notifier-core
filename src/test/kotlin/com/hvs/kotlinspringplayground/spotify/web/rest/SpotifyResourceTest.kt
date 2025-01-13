package com.hvs.kotlinspringplayground.spotify.web.rest

import com.hvs.kotlinspringplayground.spotify.albumItem
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyAlbumsResponse.AlbumItem
import com.hvs.kotlinspringplayground.spotify.service.SpotifyService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

@ExtendWith(MockitoExtension::class)
class SpotifyResourceTest {

    @Mock
    private lateinit var spotifyService: SpotifyService

    @InjectMocks
    private lateinit var spotifyResource: SpotifyResource

    @Test
    fun `getArtistAlbums returns Page when service returns Page`() {
        // GIVEN
        val artistId = "someArtist"
        val pageable: Pageable = PageRequest.of(0, 5)
        val albumItems = listOf(
            albumItem(),
            albumItem()
        )
        val mockPage: Page<AlbumItem> = PageImpl(albumItems, pageable, 2)

        whenever(spotifyService.getAlbumsOfArtist(artistId, pageable)).thenReturn(mockPage)

        // WHEN
        val result = spotifyResource.getArtistAlbums(artistId, pageable)

        // THEN
        assertEquals(mockPage, result)
        verify(spotifyService, times(1))
            .getAlbumsOfArtist(artistId, pageable)
    }

    @Test
    fun `getArtistAlbums returns empty Page if service returns empty Page`() {
        // GIVEN
        val artistId = "emptyArtist"
        val pageable: Pageable = PageRequest.of(1, 3)
        val mockEmptyPage: Page<AlbumItem> = PageImpl(emptyList(), pageable, 0)

        whenever(spotifyService.getAlbumsOfArtist(artistId, pageable)).thenReturn(mockEmptyPage)

        // WHEN
        val result = spotifyResource.getArtistAlbums(artistId, pageable)

        // THEN
        assertEquals(mockEmptyPage, result)
        assertEquals(0, result.totalElements)
        verify(spotifyService, times(1))
            .getAlbumsOfArtist(artistId, pageable)
    }

    @Test
    fun `getArtistAlbums rethrows exception if service throws`() {
        // GIVEN
        val artistId = "errorArtist"
        val pageable = PageRequest.of(0, 2)
        whenever(spotifyService.getAlbumsOfArtist(artistId, pageable))
            .thenThrow(RuntimeException("Service error"))

        // WHEN & THEN
        assertThrows(RuntimeException::class.java) {
            spotifyResource.getArtistAlbums(artistId, pageable)
        }
        verify(spotifyService, times(1))
            .getAlbumsOfArtist(artistId, pageable)
    }
}
