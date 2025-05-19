package com.hvs.kotlinspringplayground.user.autoconfigure

import com.hvs.kotlinspringplayground.artist.service.ArtistService
import com.hvs.kotlinspringplayground.user.service.impl.UserService
import com.hvs.kotlinspringplayground.user.repository.UserRepository
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EnableJpaRepositories(basePackageClasses = [UserRepository::class])
@EntityScan("com.hvs.kotlinspringplayground.user.domain.jpa")
class UserAutoConfiguration {

    @Bean
    fun userService(
        userRepository: UserRepository,
        artistService: ArtistService,
    ): UserService = UserService(
        userRepository = userRepository,
        artistService = artistService,
    )
}