package com.hvs.kotlinspringplayground.user

import com.hvs.kotlinspringplayground.tidal.client.response.ArtistResponseData
import com.hvs.kotlinspringplayground.user.dto.UserDataDto
import com.hvs.kotlinspringplayground.user.domain.jpa.User
import com.hvs.kotlinspringplayground.user.repository.UserRepository

class UserService(
    private val userRepository: UserRepository,
) {

    fun createUser(userName: String) {
        val userDataDto = UserDataDto(
            name = userName,
        )
        userRepository.save(User.from(userDataDto))
    }

    fun storeArtistsForUser(
        userName: String,
        artists: List<ArtistResponseData.ArtistData>
    ) {

        val user = requireNotNull(userRepository.findByUsername(userName))

        val userDto = UserDataDto(
            id = user.id,
            name = user.username,
            artists = artists
        )

        userRepository.save(User.from(userDto))
    }
}