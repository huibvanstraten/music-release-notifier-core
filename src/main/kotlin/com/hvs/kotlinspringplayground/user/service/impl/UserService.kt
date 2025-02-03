package com.hvs.kotlinspringplayground.user.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.user.domain.jpa.User
import com.hvs.kotlinspringplayground.user.dto.UserDataDto
import com.hvs.kotlinspringplayground.user.repository.UserRepository
import com.hvs.kotlinspringplayground.user.service.UserService

class UserService(
    private val userRepository: UserRepository,
): UserService {

    override fun getUsers(): List<UserDataDto> = userRepository.findAll().map {
        UserDataDto(
            userId = it.id,
            username = it.username,
            artistIdList = jacksonObjectMapper().convertValue(it.artistIdList, object : TypeReference<List<String>>() {}),
        )
    }

    override fun createUser(userId: String, userName: String) {
        val userDataDto = UserDataDto(
            userId = userId,
            username = userName,
            artistIdList = emptyList()
        )
        userRepository.save(User.from(userDataDto))
    }

    override fun getTotalUsers(): Long = userRepository.findAll().size.toLong()

    override fun getArtistIdListForUser(
        username: String,
    ): List<String>? {
        val currentArtists: List<String>? = getUserWithArtistIdList(username)
        return currentArtists
    }

    override fun storeArtistListForUser(
        userDto: UserDataDto,
    ) {
        val currentArtists: List<String>? = getUserWithArtistIdList(userDto.username)

        val updatedUserDto = userDto.copy(
            artistIdList = ((currentArtists ?: emptyList()) + userDto.artistIdList).distinct()
        )

        userRepository.save(User.from(updatedUserDto))
    }

    private fun getUserWithArtistIdList(username: String): List<String>? {
        val user = userRepository.findByUsername(username)

        val objectMapper = ObjectMapper()
        val currentArtists = user?.artistIdList?.let {
            objectMapper.convertValue(it, object : TypeReference<MutableList<String>>() {})
        }
        return currentArtists
    }
}