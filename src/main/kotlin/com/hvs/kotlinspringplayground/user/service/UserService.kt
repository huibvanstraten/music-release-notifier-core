package com.hvs.kotlinspringplayground.user.service

import com.hvs.kotlinspringplayground.user.domain.jpa.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface UserService {

    fun getUsers(pageable: Pageable): Page<User>

    fun getTotalUsers(): Long

    fun createUser(userName: String)

    fun getArtistIdListForUser(username: String): List<String>?

    fun storeArtistListForUser(userName: String, artists: List<String>)
}