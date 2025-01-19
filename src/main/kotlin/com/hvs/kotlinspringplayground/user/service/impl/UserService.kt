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
            username = it.username,
            artistIdList = jacksonObjectMapper().convertValue(it.artistIdList, object : TypeReference<List<String>>() {}),
        )
    }

    override fun createUser(userName: String) {
        val userDataDto = UserDataDto(
            username = userName,
            artistIdList = emptyList()
        )
        userRepository.save(User.from(userDataDto))
    }

    override fun getTotalUsers(): Long = userRepository.findAll().size.toLong()

    override fun getArtistIdListForUser(
        username: String,
    ): List<String>? {
        val (_, currentArtists: List<String>?) = getUserWithArtistIdList(username)
        return currentArtists
    }

    override fun storeArtistListForUser(
        userDto: UserDataDto,
    ) {
        val (user, currentArtists: List<String>?) = getUserWithArtistIdList(userDto.username)

        val userDto = UserDataDto(
            username = user.username,
            artistIdList = ((currentArtists ?: emptyList()) + userDto.artistIdList).distinct()
        )

        userRepository.save(User.from(userDto))
    }

    private fun getUserWithArtistIdList(username: String): Pair<User, List<String>?> {
        val user = requireNotNull(userRepository.findByUsername(username))

        val objectMapper = ObjectMapper()
        val currentArtists = user.artistIdList?.let {
            objectMapper.convertValue(it, object : TypeReference<MutableList<String>>() {})
        }
        return Pair(user, currentArtists)
    }
}