package com.hvs.kotlinspringplayground.user.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.artist.service.ArtistService
import com.hvs.kotlinspringplayground.user.domain.jpa.User
import com.hvs.kotlinspringplayground.user.dto.UserArtistDto
import com.hvs.kotlinspringplayground.user.dto.UserArtistListDto
import com.hvs.kotlinspringplayground.user.repository.UserRepository
import com.hvs.kotlinspringplayground.user.service.UserService

class UserService(
    private val userRepository: UserRepository,
    private val artistService: ArtistService,
) : UserService {

    override fun getUsers(): List<UserArtistListDto> = userRepository.findAll().map {
        UserArtistListDto(
            userId = it.id,
            username = it.username,
            artistIdList = jacksonObjectMapper().convertValue(
                it.artistIdList,
                object : TypeReference<List<String>>() {}),
        )
    }

    override fun createUser(userId: String, username: String) {
        val userArtistListDto = UserArtistListDto(
            userId = userId,
            username = username,
            artistIdList = emptyList()
        )
        userRepository.save(User.from(userArtistListDto))
    }

    override fun getTotalUsers(): Long = userRepository.findAll().size.toLong()

    override fun getArtistIdListForUser(
        username: String,
    ): List<String>? {
        val currentArtists: List<String>? = getUserArtistIdList(username)
        return currentArtists
    }

    override fun getArtistNameListForUser(
        username: String,
    ): List<String>? {
        val currentArtistIdList: List<String>? = getUserArtistIdList(username)
        val artistNameList = currentArtistIdList?.map { artistId ->
            artistService.getArtist(artistId).name
        }

        return artistNameList
    }

    override fun storeArtistListForUser(
        userDto: UserArtistListDto,
    ) {
        val currentArtists: List<String>? = getUserArtistIdList(userDto.username)

        val updatedUserDto = userDto.copy(
            artistIdList = ((currentArtists ?: emptyList()) + userDto.artistIdList).distinct()
        )

        userRepository.save(User.from(updatedUserDto))
    }

    override fun storeArtistForUser(
        user: UserArtistDto,
    ) {
        val currentArtists: List<String>? = getUserArtistIdList(user.username)

        if (currentArtists?.any { artistId -> artistId == user.artistId } ?: false) {
            throw IllegalStateException("Artist is already present in list")
        }
        val userArtistListDto = UserArtistListDto(
            userId = user.userId,
            username = user.username,
            artistIdList = currentArtists?.plus(user.artistId) ?: listOf(user.artistId),
        )


        userRepository.save(User.from(userArtistListDto))
    }

    override fun removeArtistForUser(
        username: String,
        artistname: String
    ) {
        val user = userRepository.findByUsername(username)
        val currentArtists: List<String>? = getUserArtistIdList(username)
        val artistId = artistService.getArtistFromSpotifyByName(artistname)?.artistId
        val updatedList = currentArtists?.filterNot { id -> id == artistId }

        if (updatedList == null || user == null) return

        userRepository.save(User.from(
            UserArtistListDto(
                userId = user.id,
                username = username,
                artistIdList = updatedList
        )))
    }

    private fun getUserArtistIdList(username: String): List<String>? {
        val user = userRepository.findByUsername(username)

        val objectMapper = ObjectMapper()
        val currentArtists = user?.artistIdList?.let {
            objectMapper.convertValue(it, object : TypeReference<MutableList<String>>() {})
        }
        return currentArtists
    }
}

