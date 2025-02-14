package com.hvs.kotlinspringplayground.user.service

import com.hvs.kotlinspringplayground.user.dto.UserDataDto

interface UserService {

    fun getUsers(): List<UserDataDto>

    fun getTotalUsers(): Long

    fun createUser(userId: String, username: String)

    fun getArtistIdListForUser(username: String): List<String>?

    fun storeArtistListForUser(userDto: UserDataDto)
}