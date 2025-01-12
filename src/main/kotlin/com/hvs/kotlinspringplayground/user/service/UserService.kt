package com.hvs.kotlinspringplayground.user.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.hvs.kotlinspringplayground.user.domain.jpa.User
import com.hvs.kotlinspringplayground.user.dto.UserDataDto
import com.hvs.kotlinspringplayground.user.repository.UserRepository
import org.springframework.transaction.annotation.Transactional

open class UserService(
    private val userRepository: UserRepository,
) {

    fun createUser(userName: String) {
        val userDataDto = UserDataDto(
            name = userName,
        )
        userRepository.save(User.Companion.from(userDataDto))
    }

    @Transactional
    open fun storeArtistsForUser(
        userName: String,
        artists: List<String>
    ) {

        val user = requireNotNull(userRepository.findByUsername(userName))

        val objectMapper = ObjectMapper()
        val currentArtists: List<String> = user.artists?.let {
            objectMapper.convertValue(it, object : TypeReference<MutableList<String>>() {})
        } ?: emptyList()


        val userDto = UserDataDto(
            id = user.id,
            name = user.username,
            artistIdList = (currentArtists + artists).distinct()
        )

        userRepository.save(User.Companion.from(userDto))
    }
}