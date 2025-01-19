package com.hvs.kotlinspringplayground.release.web.rest

import com.hvs.kotlinspringplayground.release.web.rest.response.ReleasePagedResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/release")
class ReleaseResource(
    private val releaseService: com.hvs.kotlinspringplayground.release.service.ReleaseService
) {

    @GetMapping
    suspend fun getReleasesForArtist(
        @RequestParam artistId: String,
        @RequestParam(defaultValue = "0") offset: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): ResponseEntity<ReleasePagedResponse> {
        return try {
            val pageOfReleases = releaseService.getReleasesForArtist(artistId = artistId, offset, limit)

            val total = pageOfReleases.totalElements
            val content = pageOfReleases.content

            val nextOffset = offset + limit
            val nextLink = if (nextOffset < total) {
                "/release?artistId=$artistId&offset=$nextOffset&limit=$limit"
            } else null

            val prevOffset = (offset - limit).coerceAtLeast(0)
            val prevLink = if (offset > 0) {
                "/release?artistId=$artistId&offset=$prevOffset&limit=$limit"
            } else null

            val response = ReleasePagedResponse(
                content = content,
                total = total,
                offset = offset,
                limit = limit,
                next = nextLink,
                previous = prevLink
            )

            ResponseEntity.ok(response)
        } catch (e: Exception) {
            logger.error(e) { "Failed to get releases" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/new")
    fun newReleaseMessage(
        @RequestParam releaseId: String,
    ): ResponseEntity<Unit> {
        releaseService.sendMessageForNewRelease(releaseId)
        return ResponseEntity.ok().build()
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}
