package com.hvs.kotlinspringplayground.artist.domain.jpa

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID


@Entity
@Table(name = "artists")
data class Artist(

    @Id
    val id: UUID,

    val tidalId: String?,

    val spotifyId: String? = null,

    val name: String,
) {

    companion object {
        fun from(artistData: ArtistDataDto): Artist = with(artistData) {
            Artist(
                id = this.id,
                tidalId = this.streamingId.toString(),
                name = this.name,
            )
        }
    }
}
