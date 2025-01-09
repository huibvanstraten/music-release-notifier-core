package com.hvs.kotlinspringplayground.user.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.hvs.kotlinspringplayground.user.domain.jpa.User
import com.hvs.kotlinspringplayground.user.dto.UserDataDto
import com.hvs.kotlinspringplayground.user.repository.UserRepository
import com.hvs.kotlinspringplayground.user.service.UserService
import org.springframework.transaction.annotation.Transactional

class UserService(
    private val userRepository: UserRepository,
): UserService {

    fun createUser(userName: String) {
        val userDataDto = UserDataDto(
            name = userName,
        )
        userRepository.save(User.Companion.from(userDataDto))
    }

    override fun storeArtistsForUser(
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