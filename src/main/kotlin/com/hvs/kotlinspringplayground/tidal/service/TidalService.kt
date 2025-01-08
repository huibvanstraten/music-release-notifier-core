package com.hvs.kotlinspringplayground.tidal.service

import com.hvs.kotlinspringplayground.tidal.client.TidalClient
import com.hvs.kotlinspringplayground.tidal.domain.Album

class TidalService(
    private val tidalClient: TidalClient
) {

    fun getAllArtists() = tidalClient.getArtists()

    fun getArtist(artistId: Int) = tidalClient.getArtist(artistId)

    fun getAlbumIdListForArtist(artistId: Int) = tidalClient.getAlbumIdsForArtist(artistId)

    fun getAlbum(albumId: String): Album? {
        val albumData = tidalClient.getAlbum(albumId)

        return Album.from(albumData.data.first())
    }
}