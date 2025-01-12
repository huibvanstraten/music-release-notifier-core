package com.hvs.kotlinspringplayground.artist.web.rest

import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.artist.service.ArtistService
import com.hvs.kotlinspringplayground.tidal.domain.Album
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/artist")
class ArtistResource(
    private val artistService: ArtistService
) {

    @GetMapping
    fun getArtist(
        @RequestParam artistId: String
    ): ResponseEntity<ArtistDataDto> {
        return try {
            val response = artistService.getArtist(artistId)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch artist" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/name")
    fun getArtistsByName(
        @RequestParam artistName: String,
    ): ResponseEntity<ArtistDataDto> {
        return try {
            when (val response = artistService.getArtistFromSpotifyByName(artistName)) {
                is ArtistDataDto -> ResponseEntity.ok(response)
                else -> ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to fetch artist" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping
    fun saveAllArtists(): ResponseEntity<Void> {
        return try {
            artistService.storeArtists()
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e) { "Failed to store artists" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("album")
    fun getNewestAlbum(
        @RequestParam artistId: Int
    ): ResponseEntity<Any> {
        return try {
            when (val response = artistService.getNewAlbumForArtist(artistId)) {
                is Album -> ResponseEntity.ok(response)
                else -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Artist with id $artistId has no albums")
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to get album for $artistId" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("error" to "An unexpected error occurred"))
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}