package com.hvs.kotlinspringplayground.tidal.autoconfigure

import com.hvs.kotlinspringplayground.tidal.client.TidalClient
import com.hvs.kotlinspringplayground.tidal.authorisation.AuthorisationService
import com.hvs.kotlinspringplayground.tidal.authorisation.TidalCredentials
import com.hvs.kotlinspringplayground.tidal.service.TidalService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(TidalCredentials::class)
class TidalAutoConfiguration {

    @Bean
    fun authorisationService(
        tidalCredentials: TidalCredentials,
    ) = AuthorisationService(
        tidalCredentials = tidalCredentials,
    )

    @Bean
    fun tidalClient(
        authorisationService: AuthorisationService,
    ) = TidalClient(
        authorisationService = authorisationService,
    )

    @Bean
    fun tidalService(
        tidalClient: TidalClient,
    ) = TidalService(
        tidalClient = tidalClient,
    )
}