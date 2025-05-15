package com.hvs.kotlinspringplayground.user.service

import com.hvs.kotlinspringplayground.user.dto.UserArtistDto
import com.hvs.kotlinspringplayground.user.dto.UserArtistListDto

interface UserService {

    fun getUsers(): List<UserArtistListDto>

    fun getTotalUsers(): Long

    fun createUser(userId: String, username: String)

    fun getArtistIdListForUser(username: String): List<String>?

    fun getArtistNameListForUser(username: String): List<String>?

    fun storeArtistListForUser(userDto: UserArtistListDto)

    fun storeArtistForUser(user: UserArtistDto)

    fun removeArtistForUser(username: String, artistname: String)
}