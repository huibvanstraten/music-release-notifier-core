package com.hvs.kotlinspringplayground.spotify.service

import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.spotify.client.impl.SpotifyAsyncClient
import com.hvs.kotlinspringplayground.spotify.client.impl.SpotifyClient
import com.hvs.kotlinspringplayground.spotify.client.response.Album
import kotlinx.coroutines.flow.Flow
import org.springframework.data.domain.Page

class SpotifyService(
    private val spotifyClient: SpotifyClient,
    private val spotifyAsyncClient: SpotifyAsyncClient,
) {

    fun getArtist(
        artistId: String,
    ): ArtistDataDto = spotifyClient.getArtist(artistId).run {
        ArtistDataDto(
            name = name,
            artistId = artistId,
        )
    }

    fun getArtistByName(
        artistName: String
    ): ArtistDataDto? {
        val searchResult = spotifyClient.findArtistByName(artistName)
        if (searchResult.artists == null || searchResult.artists.items == null) return null

        return searchResult.artists.items.firstOrNull { it.name.equals(artistName, ignoreCase = true) }?.let {
            ArtistDataDto(
                name = it.name,
                artistId = it.id
            )
        }
    }

    fun getReleasesForArtist(
        artistId: String,
    ): Flow<Page<Album>> {
        return spotifyAsyncClient.getReleasesForArtist(artistId)
    }
}