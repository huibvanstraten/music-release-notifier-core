package com.hvs.kotlinspringplayground.spotify.service

import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.spotify.client.SpotifyClient
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyAlbumsResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class SpotifyService(
    private val spotifyClient: SpotifyClient
) {

    // TODO: handle several returns
    fun getArtistByName(
        artistName: String
    ): ArtistDataDto? {
        val searchResult = spotifyClient.findArtistByName(artistName)
        if (searchResult.artists == null || searchResult.artists.items == null) return null

        with(searchResult.artists.items.first()) {
            return ArtistDataDto(
                name = this.name,
                streamingId = this.id
            )
        }
    }

    fun getAlbumsOfArtist(
        artistId: String,
        pageable: Pageable,
    ): Page<SpotifyAlbumsResponse.AlbumItem> {
        return spotifyClient.getArtistAlbumsPageable(artistId, pageable)
    }
}