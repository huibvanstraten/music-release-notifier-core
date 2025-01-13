package com.hvs.kotlinspringplayground.user.web.rest

import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.hvs.kotlinspringplayground.artist.web.rest.ArtistResource.Companion.logger
import com.hvs.kotlinspringplayground.user.domain.jpa.User
import com.hvs.kotlinspringplayground.user.service.impl.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.MappingJacksonValue
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

    @GetMapping("/paginated")
    fun getUsersPaginated(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
    ): ResponseEntity<Page<User>> {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().build()
        }

        val pageable = PageRequest.of(page, size)
        val userPage = userService.getUsers(pageable)
        return ResponseEntity.ok(userPage)
    }

    @GetMapping("/parameters")
    fun getUsersParametersFiltered(
        @RequestParam fields: List<String>
    ): ResponseEntity<MappingJacksonValue> {
        val usersPage = userService.getUsers(PageRequest.of(0, 100))
        val userList = usersPage.content

        val filterProvider = SimpleFilterProvider()
            .addFilter("userFilter", SimpleBeanPropertyFilter.filterOutAllExcept(fields.toSet()))

        val mapping = MappingJacksonValue(userList)
        mapping.filters = filterProvider

        return ResponseEntity.status(HttpStatus.OK).body(mapping)
    }

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

    @PostMapping("list")
    fun saveArtistsForUser(
        @RequestParam username: String,
        @RequestParam artistIdList: List<String>,
    ): ResponseEntity<Void> {
        return try {
            userService.storeArtistListForUser(username,artistIdList)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            logger.error(e) { "Failed to store artists for user $username" }
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}