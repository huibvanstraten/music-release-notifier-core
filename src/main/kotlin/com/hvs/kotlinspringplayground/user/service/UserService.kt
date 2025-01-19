package com.hvs.kotlinspringplayground.user.service

import com.hvs.kotlinspringplayground.user.dto.UserDataDto

interface UserService {

    fun getUsers(): List<UserDataDto>

    fun getTotalUsers(): Long

    fun createUser(userName: String)

    fun getArtistIdListForUser(username: String): List<String>?

    fun storeArtistListForUser(user: UserDataDto)
}