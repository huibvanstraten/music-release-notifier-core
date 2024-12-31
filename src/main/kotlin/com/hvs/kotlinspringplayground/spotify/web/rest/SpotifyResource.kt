package com.hvs.kotlinspringplayground.spotify.web.rest

import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyAlbumsResponse
import com.hvs.kotlinspringplayground.spotify.service.SpotifyService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/spotify")
class SpotifyResource(
    private val spotifyService: SpotifyService
) {

    @GetMapping("/artists/{artistId}/albums")
    fun getArtistAlbums(
        @PathVariable artistId: String,
        pageable: Pageable
    ): Page<SpotifyAlbumsResponse.AlbumItem> {
        // Extract token from the header if needed ("Bearer XXX"), or pass directly.
        // Assuming the header is "Bearer <token>", you can parse it accordingly.

        return spotifyService.getAlbumsArtist(
            artistId = artistId,
            pageable = pageable
        )
    }
}
