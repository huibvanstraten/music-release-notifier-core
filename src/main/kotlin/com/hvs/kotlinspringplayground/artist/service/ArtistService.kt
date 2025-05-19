package com.hvs.kotlinspringplayground.artist.service

import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.artist.dto.ArtistProfileDataDto
import com.hvs.kotlinspringplayground.tidal.domain.Album

interface ArtistService {

    fun getArtist(artistId: String): ArtistDataDto

    fun getArtistProfileData(artistId: String): ArtistProfileDataDto?

    fun getArtistFromSpotifyByName(artistName: String): ArtistDataDto?

    fun findSpotifyArtistNamesByName(input: String): List<String>

    fun storeArtists()

    fun getNewAlbumForArtist(artistId: Int): Album?
}