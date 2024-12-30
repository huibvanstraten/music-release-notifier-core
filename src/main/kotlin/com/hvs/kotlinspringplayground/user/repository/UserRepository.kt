package com.hvs.kotlinspringplayground.user.repository

import com.hvs.kotlinspringplayground.user.domain.jpa.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {

    fun findByUsername(username: String): User?
}