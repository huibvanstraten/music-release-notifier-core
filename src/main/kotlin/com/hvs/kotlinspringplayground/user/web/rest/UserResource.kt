package com.hvs.kotlinspringplayground.user.web.rest

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.hvs.kotlinspringplayground.user.dto.UserArtistDto
import com.hvs.kotlinspringplayground.user.dto.UserArtistListDto
import com.hvs.kotlinspringplayground.user.service.impl.UserService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
class UserResource(
    private val userService: UserService
) {

    @GetMapping("/parameters")
    fun getUsersParametersFiltered(
        @RequestParam fields: List<String>
    ): ResponseEntity<MappingJacksonValue> {
        val users = userService.getUsers()

        val filterProvider = SimpleFilterProvider()
            .addFilter("userFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fields.toSet()))

        val mapping = MappingJacksonValue(users)
        mapping.filters = filterProvider

        return ResponseEntity.status(HttpStatus.OK).body(mapping)
    }

    @PostMapping
    fun createUser(
        @RequestParam userId: String,
        @RequestParam username: String,
    ): ResponseEntity<Void> {
        return try {
            userService.createUser(userId, username)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e) { "Failed to store artists" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("list")
    fun getArtistIdListForUser(
        @RequestParam username: String,
    ): ResponseEntity<List<String>?> {
        return try {
            val artistIdList = userService.getArtistIdListForUser(username)
            ResponseEntity.ok().body(artistIdList)
        } catch (e: Exception) {
            logger.error(e) { "Failed to get artists for user $username" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("artistnamelist")
    fun getArtistNameListForUser(
        @RequestParam username: String,
    ): ResponseEntity<List<String>?> {
        return try {
            val artistNameList = userService.getArtistNameListForUser(username)
            ResponseEntity.ok().body(artistNameList)
        } catch (e: Exception) {
            logger.error(e) { "Failed to get artists for user $username" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping("artist")
    fun saveArtistForUser(
        @RequestBody user: UserArtistDto,
    ): ResponseEntity<Void> {
        return try {
            userService.storeArtistForUser(user)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            if (e is IllegalArgumentException) {
                ResponseEntity.status(HttpStatus.ALREADY_REPORTED).build()
            } else {
                logger.error(e) { "Failed to store artists for user ${user.username}" }
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
            }
        }
    }

    @PostMapping("list")
    fun saveArtistsForUser(
        @RequestBody user: UserArtistListDto,
    ): ResponseEntity<Void> {
        return try {
            userService.storeArtistListForUser(user)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e) { "Failed to store artists for user ${user.username}" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @DeleteMapping("removeartist")
    fun removeArtistForUser(
        @RequestParam username: String,
        @RequestParam artistname: String
    ): ResponseEntity<Void> {
        return try {
            userService.removeArtistForUser(username, artistname)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e) { "Failed to remove artist for user $username" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    companion object {
        val logger = KotlinLogging.logger {}
    }
}