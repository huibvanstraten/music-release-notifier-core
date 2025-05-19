package com.hvs.kotlinspringplayground.spotify.service

import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.artist.dto.ArtistProfileDataDto
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

    fun getArtistProfileData(
        artistName: String,
    ): ArtistProfileDataDto? {
        val artistId = spotifyClient.findArtistNamesByName(artistName).artists?.items?.first()?.id ?: return null
        return spotifyClient.getArtist(artistId).run {
            ArtistProfileDataDto(
                name = name,
                artistId = artistId,
                genres = genres,
                image = with(images.first()) {
                    ArtistProfileDataDto.SpotifyImage(
                        url = this.url,
                        height = this.height,
                        width = this.width
                    )
                },
                spotifyLink = externalUrls.spotify,
            )

        }
    }

    fun getArtistByName(
        artistName: String
    ): ArtistDataDto? {
        val searchResult = spotifyClient.findArtistNamesByName(artistName)
        if (searchResult.artists == null || searchResult.artists.items == null) return null

        return searchResult.artists.items.firstOrNull { it.name.equals(artistName, ignoreCase = true) }?.let {
            ArtistDataDto(
                name = it.name,
                artistId = it.id
            )
        }
    }

    fun getArtistNamesByName(
        artistName: String
    ): List<String> {
        val searchResult = spotifyClient.findArtistNamesByName(artistName)
        return searchResult.artists?.items?.map {
            it.name
        } ?: emptyList()
    }

    fun getReleasesForArtist(
        artistId: String,
    ): Flow<Page<Album>> {
        return spotifyAsyncClient.getReleasesForArtist(artistId)
    }
}