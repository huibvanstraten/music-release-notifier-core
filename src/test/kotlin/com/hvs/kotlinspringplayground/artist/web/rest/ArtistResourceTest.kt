package com.hvs.kotlinspringplayground.artist.web.rest

import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.artist.service.ArtistService
import com.hvs.kotlinspringplayground.tidal.domain.Album
import org.junit.jupiter.api.Assertions.*
import org.mockito.kotlin.whenever
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ArtistResourceTest {

    @Mock
    private lateinit var artistService: ArtistService

    @InjectMocks
    private lateinit var artistResource: ArtistResource

    @Test
    fun `getArtistsByName returns OK and ArtistDataDto when service returns valid data`() {
        // GIVEN
        val artistName = "SomeBand"
        val artistDataDto = ArtistDataDto(
            id = UUID.randomUUID(),
            streamingId = "someId",
            name = artistName,

        )
        whenever(artistService.getArtistsFromSpotifyByName(artistName)).thenReturn(artistDataDto)

        // WHEN
        val response = artistResource.getArtistsByName(artistName)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode, "Expected HTTP 200")
        assertTrue(response.body is ArtistDataDto, "Response body should be an ArtistDataDto")
        verify(artistService, times(1)).getArtistsFromSpotifyByName(artistName)
    }

    @Test
    fun `getArtistsByName returns NOT_FOUND when service returns anything other than ArtistDataDto`() {
        // GIVEN
        val artistName = "UnknownArtist"
        // Suppose your service might return null or a different type indicating not found
        whenever(artistService.getArtistsFromSpotifyByName(artistName)).thenReturn(null)

        // WHEN
        val response = artistResource.getArtistsByName(artistName)

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode, "Expected HTTP 404")
        assertEquals("No artist corresponding artistname $artistName", response.body)
        verify(artistService, times(1)).getArtistsFromSpotifyByName(artistName)
    }

    @Test
    fun `getArtistsByName returns INTERNAL_SERVER_ERROR on exception`() {
        // GIVEN
        val artistName = "ExceptionCase"
        whenever(artistService.getArtistsFromSpotifyByName(artistName)).thenThrow(RuntimeException("Service error"))

        // WHEN
        val response = artistResource.getArtistsByName(artistName)

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode, "Expected HTTP 500")
        assertNull(response.body, "Response body should be null for 500")
        verify(artistService, times(1)).getArtistsFromSpotifyByName(artistName)
    }

    @Test
    fun `saveAllArtists returns OK when service completes successfully`() {
        // GIVEN
        doNothing().whenever(artistService).storeArtists()

        // WHEN
        val response = artistResource.saveAllArtists()

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(artistService, times(1)).storeArtists()
    }

    @Test
    fun `saveAllArtists returns INTERNAL_SERVER_ERROR on exception`() {
        // GIVEN
        doThrow(RuntimeException("DB error")).`when`(artistService).storeArtists()

        // WHEN
        val response = artistResource.saveAllArtists()

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        verify(artistService, times(1)).storeArtists()
    }

    @Test
    fun `getNewestAlbum returns OK when service returns Album`() {
        // GIVEN
        val artistId = 42
        val album = Album(
            id = "someId",
            title = "Some Album",
            releaseDate = LocalDate.of(2021, 1, 1),
        )
        `when`(artistService.getNewAlbumForArtist(artistId)).thenReturn(album)

        // WHEN
        val response = artistResource.getNewestAlbum(artistId)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body is Album)
        verify(artistService, times(1)).getNewAlbumForArtist(artistId)
    }

    @Test
    fun `getNewestAlbum returns NOT_FOUND when service returns not an Album`() {
        // GIVEN
        val artistId = 999
        `when`(artistService.getNewAlbumForArtist(artistId)).thenReturn(null)

        // WHEN
        val response = artistResource.getNewestAlbum(artistId)

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Artist with id $artistId has no albums", response.body)
        verify(artistService, times(1)).getNewAlbumForArtist(artistId)
    }

    @Test
    fun `getNewestAlbum returns INTERNAL_SERVER_ERROR on exception`() {
        // GIVEN
        val artistId = 123
        `when`(artistService.getNewAlbumForArtist(artistId)).thenThrow(RuntimeException("Service error"))

        // WHEN
        val response = artistResource.getNewestAlbum(artistId)

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNotNull(response.body, "Body should contain an error map on 500")
        verify(artistService, times(1)).getNewAlbumForArtist(artistId)
    }
}
