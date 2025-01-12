package com.hvs.kotlinspringplayground.artist.service

import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.tidal.domain.Album

interface ArtistService {

    fun getArtistsFromSpotifyByName(artistName: String): ArtistDataDto?

    fun storeArtists()

    fun storeSpotifyArtistsForUser(
        username: String,
        artistIdList: List<String>
    )

    fun getNewAlbumForArtist(artistId: Int): Album?
}