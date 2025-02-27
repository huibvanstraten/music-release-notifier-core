package com.hvs.kotlinspringplayground.release.autoconfigure

import com.hvs.kotlinspringplayground.outbox.service.OutboxService
import com.hvs.kotlinspringplayground.release.repository.ReleaseRepository
import com.hvs.kotlinspringplayground.release.service.impl.ReleaseService
import com.hvs.kotlinspringplayground.release.web.rest.ReleaseResource
import com.hvs.kotlinspringplayground.spotify.service.SpotifyService
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackageClasses = [ReleaseRepository::class])
@EntityScan("com.hvs.kotlinspringplayground.release.domain.jpa")
class ReleaseAutoConfiguration {

    @Bean
    fun releaseService(
        spotifyService: SpotifyService,
        outboxService: OutboxService,
        releaseRepository: ReleaseRepository,
    ): ReleaseService = ReleaseService(
        spotifyService,
        outboxService,
        releaseRepository
    )

    @Bean
    fun releaseResource(
        releaseService: com.hvs.kotlinspringplayground.release.service.ReleaseService
    ): ReleaseResource = ReleaseResource(
        releaseService
    )
}