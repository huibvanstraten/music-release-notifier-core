package com.hvs.kotlinspringplayground.release.domain.jpa

import com.hvs.kotlinspringplayground.release.dto.ReleaseDto
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "releases")
data class Release(

    @Id
    @Column(name = "release_id", updatable = false, nullable = false)
    val releaseId: String,

    @Column(name = "artist_id")
    val artistId: String,

    @Column(name = "total_tracks")
    val totalTracks: Int?,

    @Column(name = "name")
    val name: String?,

    @Column(name = "release_date")
    val releaseDate: LocalDate?,

    @Column(name = "type")
    val type: String?,

    @Column(name = "spotify_url")
    val spotifyUrl: String?,
) {

    companion object {
        fun from(releaseDto: ReleaseDto): Release = with(releaseDto) {
            Release(
                releaseId = this@with.releaseId,
                artistId = this@with.artistId,
                type = this@with.type,
                spotifyUrl = this@with.spotifyUrl,
                totalTracks = this@with.totalTracks,
                name = this@with.name,
                releaseDate = this@with.releaseDate,
            )
        }
    }
}