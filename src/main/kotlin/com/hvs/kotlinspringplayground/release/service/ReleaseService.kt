package com.hvs.kotlinspringplayground.release.service

import com.hvs.kotlinspringplayground.release.domain.jpa.Release
import org.springframework.data.domain.Page

interface ReleaseService {

    suspend fun getReleasesForArtist(
        artistId: String,
        offset: Int,
        limit: Int,
    ): Page<Release>

    suspend fun storeAllReleasesForArtist(artistId: String)

    fun sendMessageForNewRelease(releaseId: String)
}