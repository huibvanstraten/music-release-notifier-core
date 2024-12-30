package com.hvs.kotlinspringplayground.artist.autoconfigure

import com.hvs.kotlinspringplayground.artist.repository.ArtistRepository
import com.hvs.kotlinspringplayground.artist.service.ArtistService
import com.hvs.kotlinspringplayground.tidal.service.TidalService
import com.hvs.kotlinspringplayground.user.UserService
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackageClasses = [ArtistRepository::class])
@EntityScan("com.hvs.kotlinspringplayground.artist.domain.jpa")

class ArtistAutoConfiguration {

    @Bean
    fun artistService(
        tidalService: TidalService,
        userService: UserService,
        artistRepository: ArtistRepository,
    ) = ArtistService(
        tidalService = tidalService,
        userService = userService,
        artistRepository = artistRepository,
    )
}