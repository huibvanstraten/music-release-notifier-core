package com.hvs.kotlinspringplayground.release.service

import com.hvs.kotlinspringplayground.release.domain.jpa.Release
import com.hvs.kotlinspringplayground.release.dto.ReleaseDto
import com.hvs.kotlinspringplayground.release.repository.ReleaseRepository
import com.hvs.kotlinspringplayground.spotify.service.SpotifyService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import java.time.LocalDate

class ReleaseService(
    private val spotifyService: SpotifyService,
    private val releaseRepository: ReleaseRepository,
) {

    suspend fun getReleasesForArtist(
        artistId: String,
        offset: Int,
        limit: Int,
    ): Page<Release> {
        storeAllReleasesForArtist(artistId)
        val pageNumber = offset / limit
        val pageable = PageRequest.of(pageNumber, limit)
        return releaseRepository.findAllByArtistId(artistId, pageable)
    }

    suspend fun storeAllReleasesForArtist(artistId: String) {
        spotifyService.getReleasesForArtist(artistId).collect { page ->
            println("Collecting page with ${page.content.size} albums...")

            page.content.forEach { album ->
                val releaseDto = ReleaseDto(
                    releaseId = album.id,
                    artistId = artistId,
                    totalTracks = album.totalTracks,
                    name = album.name,
                    releaseDate = LocalDate.parse(album.releaseDate),
                    type = album.type,
                    spotifyUrl = album.externalUrls.spotify
                )

                val release = Release.from(releaseDto)
                releaseRepository.save(release)
            }
        }
    }
}