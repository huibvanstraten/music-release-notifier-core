package com.hvs.kotlinspringplayground.release.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.artist.event.ReleaseEvent
import com.hvs.kotlinspringplayground.outbox.service.OutboxService
import com.hvs.kotlinspringplayground.release.domain.jpa.Release
import com.hvs.kotlinspringplayground.release.dto.ReleaseDto
import com.hvs.kotlinspringplayground.release.repository.ReleaseRepository
import com.hvs.kotlinspringplayground.release.service.ReleaseService
import com.hvs.kotlinspringplayground.spotify.service.SpotifyService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

open class ReleaseService(
    private val spotifyService: SpotifyService,
    private val outboxService: OutboxService,
    private val releaseRepository: ReleaseRepository,
    private val objectMapper: ObjectMapper = jacksonObjectMapper()
        .registerModule(JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
): ReleaseService {

    override suspend fun getReleasesForArtist(
        artistId: String,
        offset: Int,
        limit: Int,
    ): Page<Release> {
        storeAllReleasesForArtist(artistId)
        val pageNumber = offset / limit
        val pageable = PageRequest.of(pageNumber, limit)
        return releaseRepository.findAllByArtistId(artistId, pageable)
    }

    override suspend fun storeAllReleasesForArtist(artistId: String) {
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

                val release = Release.Companion.from(releaseDto)
                releaseRepository.save(release)
            }
        }
    }

    @Transactional
    override fun sendMessageForNewRelease(releaseId: String) {
        val release = releaseRepository.findById(releaseId).getOrNull()?.let { release ->
            ReleaseDto(
                releaseId = release.releaseId,
                totalTracks = release.totalTracks,
                name = release.name,
                releaseDate = release.releaseDate,
                type = release.type,
                artistId = release.artistId,
                spotifyUrl = release.spotifyUrl

            )
        }

        if(release == null) return

        outboxService.send {
            ReleaseEvent(
                id = UUID.randomUUID().toString(),
                release = objectMapper.convertValue(release, ObjectNode::class.java)
            )
        }
    }
}