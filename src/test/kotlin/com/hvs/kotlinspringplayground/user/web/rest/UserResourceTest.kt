package com.hvs.kotlinspringplayground.user.web.rest

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.user.domain.jpa.User
import com.hvs.kotlinspringplayground.user.service.impl.UserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.converter.json.MappingJacksonValue
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserResourceTest {

    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var userResource: UserResource

    @Test
    fun `getUsersPaginated returns BAD_REQUEST when page is negative`() {
        // WHEN
        val response = userResource.getUsersPaginated(page = -1, size = 10)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNull(response.body)
        verify(userService, times(0)).getUsers(any())
    }

    @Test
    fun `getUsersPaginated returns BAD_REQUEST when size is not positive`() {
        // WHEN
        val response = userResource.getUsersPaginated(page = 0, size = 0)

        // THEN
        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertNull(response.body)
        verify(userService, times(0)).getUsers(any())
    }

    @Test
    fun `getUsersPaginated returns OK with page of users`() {
        // GIVEN
        val pageReq = PageRequest.of(0, 10)
        val users = userList()
        val mockPage: Page<User> = PageImpl(users, pageReq, 2)

        whenever(userService.getUsers(pageReq)).thenReturn(mockPage)

        // WHEN
        val response = userResource.getUsersPaginated(page = 0, size = 10)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(mockPage, response.body)
        verify(userService, times(1)).getUsers(pageReq)
    }

    @Test
    fun `getUsersParametersFiltered returns OK and filters fields`() {
        // GIVEN
        val pageReq = PageRequest.of(0, 100)
        val users = userList()
        val mockPage: Page<User> = PageImpl(users, pageReq, 2)

        whenever(userService.getUsers(pageReq)).thenReturn(mockPage)

        val fields = listOf("username", "artistIdList")

        // WHEN
        val response = userResource.getUsersParametersFiltered(fields)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        val mapping = response.body
        assertNotNull(mapping)

        val filterProvider = (mapping as MappingJacksonValue).filters
        assertTrue(filterProvider is SimpleFilterProvider)

        verify(userService, times(1)).getUsers(pageReq)
    }

    @Test
    fun `createUser returns OK when userService completes successfully`() {
        // GIVEN
        val username = "TestUser"

        // WHEN
        val response = userResource.createUser(username)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(userService, times(1)).createUser(username)
    }

    @Test
    fun `createUser returns INTERNAL_SERVER_ERROR when userService throws`() {
        // GIVEN
        val username = "ErrorUser"
        doThrow(RuntimeException("DB error")).whenever(userService).createUser(username)

        // WHEN
        val response = userResource.createUser(username)

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        verify(userService, times(1)).createUser(username)
    }

    @Test
    fun `getArtistIdListForUser returns OK and list of artist IDs when service succeeds`() {
        // GIVEN
        val username = "TestUser"
        val artistIds = listOf("artist1", "artist2")

        whenever(userService.getArtistIdListForUser(username)).thenReturn(artistIds)

        // WHEN
        val response = userResource.getArtistIdListForUser(username)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(artistIds, response.body)
        verify(userService, times(1)).getArtistIdListForUser(username)
    }

    @Test
    fun `getArtistIdListForUser returns INTERNAL_SERVER_ERROR when service throws exception`() {
        // GIVEN
        val username = "error_user"

        whenever(userService.getArtistIdListForUser(username)).thenThrow(RuntimeException("Service error"))

        // WHEN
        val response = userResource.getArtistIdListForUser(username)

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        assertNull(response.body)
        verify(userService, times(1)).getArtistIdListForUser(username)
    }

    @Test
    fun `saveArtistsForUser returns OK when userService completes successfully`() {
        // GIVEN
        val username = "TestUser"
        val artists = listOf("artist1", "artist2")

        // WHEN
        val response = userResource.saveArtistsForUser(username, artists)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(userService, times(1)).storeArtistListForUser(username, artists)
    }

    @Test
    fun `saveArtistsForUser returns INTERNAL_SERVER_ERROR when userService throws`() {
        // GIVEN
        val username = "UnknownUser"
        val artists = listOf("artist1", "artist2")
        doThrow(RuntimeException("Failed to store artists")).whenever(userService)
            .storeArtistListForUser(username, artists)

        // WHEN
        val response = userResource.saveArtistsForUser(username, artists)

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        verify(userService, times(1)).storeArtistListForUser(username, artists)
    }

    private fun userList() = listOf(
        User(
            username = "testUser1",
            id = UUID.randomUUID(),
            artistIdList = mapper.createObjectNode()
        ), User(
            username = "testUser2",
            id = UUID.randomUUID(),
            artistIdList = mapper.createObjectNode()
        )
    )

    companion object {
        private val mapper = jacksonObjectMapper()
    }
}
