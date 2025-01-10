package com.hvs.kotlinspringplayground.user.web.rest

import com.hvs.kotlinspringplayground.artist.web.rest.ArtistResource.Companion.logger
import com.hvs.kotlinspringplayground.user.service.impl.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserResource(
    private val userService: UserService
) {

    @PostMapping
    fun createUser(
        @RequestParam username: String,
    ): ResponseEntity<Void> {
        return try {
            userService.createUser(username)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e) { "Failed to store artists" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("user")
    fun getArtistsForUser(
        @RequestParam username: String,
    ): ResponseEntity<Void> {
        return try {
            userService.getArtistsForUser(username)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e) { "Failed to get artists for user $username" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}