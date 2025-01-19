package com.hvs.kotlinspringplayground.user.web.rest

import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import com.hvs.kotlinspringplayground.user.dto.UserDataDto
import com.hvs.kotlinspringplayground.user.service.impl.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import org.springframework.http.converter.json.MappingJacksonValue

@ExtendWith(MockitoExtension::class)
class UserResourceTest {

    @Mock
    private lateinit var userService: UserService

    @InjectMocks
    private lateinit var userResource: UserResource

    @Test
    fun `getUsersParametersFiltered returns OK and filters fields`() {
        // GIVEN
        val users = userList()

        whenever(userService.getUsers()).thenReturn(users)

        val fields = listOf("username", "artistIdList")

        // WHEN
        val response = userResource.getUsersParametersFiltered(fields)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        val mapping = response.body
        assertNotNull(mapping)

        val filterProvider = (mapping as MappingJacksonValue).filters
        assertTrue(filterProvider is SimpleFilterProvider)

        verify(userService, times(1)).getUsers()
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
        val dto = UserDataDto(username = "TestUser", listOf("artist1", "artist2"))


        // WHEN
        val response = userResource.saveArtistsForUser(dto)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)
        verify(userService, times(1)).storeArtistListForUser(dto)
    }

    @Test
    fun `saveArtistsForUser returns INTERNAL_SERVER_ERROR when userService throws`() {
        // GIVEN
        val dto = UserDataDto(username = "TestUser", listOf("artist1", "artist2"))
        doThrow(RuntimeException("Failed to store artists")).whenever(userService)
            .storeArtistListForUser(dto)

        // WHEN
        val response = userResource.saveArtistsForUser(dto)

        // THEN
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.statusCode)
        verify(userService, times(1)).storeArtistListForUser(dto)
    }

    private fun userList() = listOf(
        UserDataDto(
            username = "testUser1",
            artistIdList = listOf("testId1", "testId2")
        ),
        UserDataDto(
            username = "testUser2",
            artistIdList = listOf("testId1", "testId2")
        )
    )
}
