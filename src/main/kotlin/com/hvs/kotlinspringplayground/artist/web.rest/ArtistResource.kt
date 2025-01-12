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
    fun getArtistsByName(
        @RequestParam artistName: String,
    ): ResponseEntity<Any> {
        return try {
            when (val response = artistService.getArtistsFromSpotifyByName(artistName)) {
                is ArtistDataDto -> ResponseEntity.ok(response)
                else -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("No artist corresponding artistname $artistName")
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to store artists" }
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
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("error" to "An unexpected error occurred"))        }
    }

    @PostMapping("user")
    fun saveArtistsForUser(
        @RequestParam username: String,
        @RequestParam artistIdList: List<String>,
    ): ResponseEntity<Void> {
        return try {
            artistService.storeSpotifyArtistsForUser(username,artistIdList)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e) { "Failed to store artists for user $username" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}