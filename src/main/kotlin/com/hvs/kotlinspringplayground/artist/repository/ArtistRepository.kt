package com.hvs.kotlinspringplayground.artist.repository

import com.hvs.kotlinspringplayground.artist.domain.jpa.Artist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ArtistRepository : JpaRepository<Artist, Long> {

    fun findByNameIgnoreCase(names: String): List<Artist>

}
