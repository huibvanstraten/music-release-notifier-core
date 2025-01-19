package com.hvs.kotlinspringplayground.release.web.rest

import com.hvs.kotlinspringplayground.release.domain.jpa.Release
import com.hvs.kotlinspringplayground.release.service.impl.ReleaseService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import java.time.LocalDate

class ReleaseResourceTest {

    // Create a mock ReleaseService with Mockito
    private val releaseService = mock(ReleaseService::class.java)

    // Instantiate the controller with the mocked service
    private val releaseResource = ReleaseResource(releaseService)

    @Test
    fun `should return 200 and valid response with next link when offset+limit less than total`(): Unit = runBlocking {
        // Given
        val artistId = "someArtist"
        val offset = 0
        val limit = 2
        val totalElements = 5L

        val content = listOf(release())
        val pageRequest = PageRequest.of(0, limit)
        val page: Page<Release> = PageImpl(content, pageRequest, totalElements)

        whenever(releaseService.getReleasesForArtist(artistId, offset, limit))
            .thenReturn(page)

        // When
        val response = releaseResource.getReleasesForArtist(artistId, offset, limit)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        response.body?.let {
            assertEquals(content, it.content)
            assertEquals(totalElements, it.total)
            assertEquals(offset, it.offset)
            assertEquals(limit, it.limit)
            assertNotNull(it.next)
            assertNull(it.previous)
        }
    }

    @Test
    fun `should have no next link when offset+limit equals or exceeds total`() = runBlocking {
        // Given
        val artistId = "someArtist"
        val offset = 2
        val limit = 3
        val totalElements = 5L

        val content = listOf(release())
        val pageRequest = PageRequest.of(offset / limit, limit)
        val page: Page<Release> = PageImpl(content, pageRequest, totalElements)

        `when`(releaseService.getReleasesForArtist(artistId, offset, limit))
            .thenReturn(page)

        // When
        val response = releaseResource.getReleasesForArtist(artistId, offset, limit)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertNull(response.body?.next, "No next link if offset+limit >= total")
    }

    @Test
    fun `should have previous link when offset is greater than 0`() = runBlocking {
        // Given
        val artistId = "someArtist"
        val offset = 10
        val limit = 5
        val totalElements = 20L

        val content = listOf(release())
        val pageRequest = PageRequest.of(offset / limit, limit)
        val page: Page<Release> = PageImpl(content, pageRequest, totalElements)

        `when`(releaseService.getReleasesForArtist(artistId, offset, limit))
            .thenReturn(page)

        // When
        val response = releaseResource.getReleasesForArtist(artistId, offset, limit)

        // Then
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertNotNull(response.body?.previous, "Should have previous link if offset>0")
    }

    @Test
    fun `should return 500 if service throws exception`() = runBlocking {
        // Given
        val artistId = "someArtist"
        val offset = 0
        val limit = 10

        `when`(releaseService.getReleasesForArtist(artistId, offset, limit))
            .thenThrow(RuntimeException("Something went wrong"))

        // When
        val response = releaseResource.getReleasesForArtist(artistId, offset, limit)

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNull(response.body)
    }

    private fun release() = Release(
        releaseId = "testReleaseId",
        artistId = "testArtistId",
        totalTracks = 10,
        name = "testName",
        releaseDate = LocalDate.now(),
        type = "testType",
        spotifyUrl = "https://api.spotify.com/v1/release",
    )
}
