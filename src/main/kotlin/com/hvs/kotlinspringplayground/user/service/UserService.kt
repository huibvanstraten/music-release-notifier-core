package com.hvs.kotlinspringplayground.user.service

interface UserService {

    fun storeArtistsForUser(userName: String, artists: List<String>)
}