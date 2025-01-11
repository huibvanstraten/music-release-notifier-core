package com.hvs.kotlinspringplayground.artist.service

import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.tidal.domain.Album

interface ArtistService {

    fun getArtist(artistId: String): ArtistDataDto

    fun getArtistFromSpotifyByName(artistName: String): ArtistDataDto?

    fun storeArtists()

    fun getNewAlbumForArtist(artistId: Int): Album?
}