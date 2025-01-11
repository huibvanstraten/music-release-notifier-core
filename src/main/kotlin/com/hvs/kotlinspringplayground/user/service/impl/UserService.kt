package com.hvs.kotlinspringplayground.user.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.hvs.kotlinspringplayground.user.domain.jpa.User
import com.hvs.kotlinspringplayground.user.dto.UserDataDto
import com.hvs.kotlinspringplayground.user.repository.UserRepository
import com.hvs.kotlinspringplayground.user.service.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

class UserService(
    private val userRepository: UserRepository,
): UserService {

    override fun getUsers(pageable: Pageable): Page<User> = userRepository.findAll(pageable)

    override fun createUser(userName: String) {
        val userDataDto = UserDataDto(
            username = userName,
        )
        userRepository.save(User.Companion.from(userDataDto))
    }

    override fun getTotalUsers(): Long = userRepository.findAll().size.toLong()

    override fun getArtistIdListForUser(
        username: String,
    ): List<String>? {
        val (_, currentArtists: List<String>) = getUserWithArtistIdList(username)
        return currentArtists
    }

    override fun storeArtistListForUser(
        username: String,
        artists: List<String>
    ) {

        val (user, currentArtists: List<String>) = getUserWithArtistIdList(username)

        val userDto = UserDataDto(
            id = user.id,
            username = user.username,
            artistIdList = (currentArtists + artists).distinct()
        )

        userRepository.save(User.Companion.from(userDto))
    }

    private fun getUserWithArtistIdList(username: String): Pair<User, List<String>> {
        val user = requireNotNull(userRepository.findByUsername(username))

        val objectMapper = ObjectMapper()
        val currentArtists: List<String> = user.artistIdList?.let {
            objectMapper.convertValue(it, object : TypeReference<MutableList<String>>() {})
        } ?: emptyList()
        return Pair(user, currentArtists)
    }
}