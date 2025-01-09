package com.hvs.kotlinspringplayground.artist.domain.jpa

import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID


@Entity
@Table(name = "artists")
data class Artist(

    @Id
    val id: UUID,

    val artistId: String?,

    val name: String,
) {

    companion object {
        fun from(artistData: ArtistDataDto): Artist = with(artistData) {
            Artist(
                id = this.id,
                artistId = this.streamingId.toString(),
                name = this.name,
            )
        }
    }
}
