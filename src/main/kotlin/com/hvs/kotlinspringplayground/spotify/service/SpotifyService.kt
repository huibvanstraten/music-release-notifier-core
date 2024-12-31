package com.hvs.kotlinspringplayground.spotify.service

import com.hvs.kotlinspringplayground.spotify.client.SpotifyClient
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyAlbumsResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class SpotifyService(
    private val spotifyClient: SpotifyClient
) {

    fun getAlbumsArtist(
        artistId: String,
        pageable: Pageable,
    ): Page<SpotifyAlbumsResponse.AlbumItem> {
        return spotifyClient.getArtistAlbumsPageable(artistId, pageable)
    }
}