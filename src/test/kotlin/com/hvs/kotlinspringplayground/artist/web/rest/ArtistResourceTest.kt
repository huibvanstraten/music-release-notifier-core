package com.hvs.kotlinspringplayground.artist.web.rest

import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.artist.service.ArtistService
import com.hvs.kotlinspringplayground.tidal.domain.Album
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.time.LocalDate
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
class ArtistResourceTest {

    @Mock
    private lateinit var artistService: ArtistService

    @InjectMocks
    private lateinit var artistResource: ArtistResource

    @Test
    fun `getArtist returns OK and ArtistDataDto when service succeeds`() {
        // GIVEN
        val artistId = "123"
        val artistData = ArtistDataDto(
            id = UUID.randomUUID(),
            artistId = artistId,
            name = "name",
        )
        whenever(artistService.getArtist(artistId)).thenReturn(artistData)

        // WHEN
        val response = artistResource.getArtist(artistId)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(artistData, response.body)
        verify(artistService, times(1)).getArtist(artistId)
    }

    @Test
    fun `getArtist returns INTERNAL_SERVER_ERROR when an exception occurs`() {
        // GIVEN
        val artistId = "errorCase"
        whenever(artistService.getArtist(artistId)).thenThrow(RuntimeException("Service error"))

        // WHEN
        val response = artistResource.getArtist(artistId)

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNull(response.body)
        verify(artistService, times(1)).getArtist(artistId)
    }

    @Test
    fun `getArtistsByName returns OK and ArtistDataDto when service returns valid data`() {
        // GIVEN
        val artistName = "Beatles"
        val artistData = ArtistDataDto(
            id = UUID.randomUUID(),
            artistId = "1234",
            name = artistName,
        )
        whenever(artistService.getArtistFromSpotifyByName(artistName)).thenReturn(artistData)

        // WHEN
        val response = artistResource.getArtistsByName(artistName)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(artistData, response.body)
        verify(artistService, times(1)).getArtistFromSpotifyByName(artistName)
    }

    @Test
    fun `getArtistsByName returns NOT_FOUND when service returns something other than ArtistDataDto`() {
        // GIVEN
        val artistName = "Nonexistent"
        whenever(artistService.getArtistFromSpotifyByName(artistName)).thenReturn(null)

        // WHEN
        val response = artistResource.getArtistsByName(artistName)

        // THEN
        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertNull(response.body)
        verify(artistService, times(1)).getArtistFromSpotifyByName(artistName)
    }

    @Test
    fun `getArtistsByName returns INTERNAL_SERVER_ERROR when an exception occurs`() {
        // GIVEN
        val artistName = "ErrorName"
        whenever(artistService.getArtistFromSpotifyByName(artistName))
            .thenThrow(RuntimeException("Boom!"))

        // WHEN
        val response = artistResource.getArtistsByName(artistName)

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNull(response.body)
        verify(artistService, times(1)).getArtistFromSpotifyByName(artistName)
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
        doThrow(RuntimeException("DB error")).whenever(artistService).storeArtists()

        // WHEN
        val response = artistResource.saveAllArtists()

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        verify(artistService, times(1)).storeArtists()
    }

    @Test
    fun `getNewestAlbum returns OK when service returns an Album`() {
        // GIVEN
        val artistId = 42
        val album = Album(
            id = "12345",
            title = "Batman",
            releaseDate = LocalDate.of(2021, 1, 1),
        )
        whenever(artistService.getNewAlbumForArtist(artistId)).thenReturn(album)

        // WHEN
        val response = artistResource.getNewestAlbum(artistId)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertTrue(response.body is Album)
        verify(artistService, times(1)).getNewAlbumForArtist(artistId)
    }

    @Test
    fun `getNewestAlbum returns NOT_FOUND when service returns something other than an Album`() {
        // GIVEN
        val artistId = 999
        whenever(artistService.getNewAlbumForArtist(artistId)).thenReturn(null)

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
        whenever(artistService.getNewAlbumForArtist(artistId)).thenThrow(RuntimeException("Service error"))

        // WHEN
        val response = artistResource.getNewestAlbum(artistId)

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNotNull(response.body, "Body should contain an error message or map")
        verify(artistService, times(1)).getNewAlbumForArtist(artistId)
    }
}
