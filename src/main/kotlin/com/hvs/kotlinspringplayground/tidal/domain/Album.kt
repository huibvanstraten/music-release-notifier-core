package com.hvs.kotlinspringplayground.tidal.domain

import com.hvs.kotlinspringplayground.tidal.client.response.AlbumResponseData.AlbumData
import java.time.LocalDate

data class Album(
    val id: String,
    val title: String,
    val releaseDate: LocalDate,
) {

    companion object {
        fun from(albumData: AlbumData): Album = Album(
            id = albumData.id,
            title = albumData.attributes.title,
            releaseDate = LocalDate.parse(albumData.attributes.releaseDate),
        )
    }
}
