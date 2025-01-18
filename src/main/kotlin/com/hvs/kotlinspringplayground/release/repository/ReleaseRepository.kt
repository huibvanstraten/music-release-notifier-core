package com.hvs.kotlinspringplayground.release.repository

import com.hvs.kotlinspringplayground.release.domain.jpa.Release
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReleaseRepository : JpaRepository<Release, String> {

    fun findAllByArtistId(artistId: String, pageable: Pageable): Page<Release>
}