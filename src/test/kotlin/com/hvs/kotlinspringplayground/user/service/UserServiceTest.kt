package com.hvs.kotlinspringplayground.user.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.user.domain.jpa.User
import com.hvs.kotlinspringplayground.user.dto.UserDataDto
import com.hvs.kotlinspringplayground.user.repository.UserRepository
import com.hvs.kotlinspringplayground.user.service.impl.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.KArgumentCaptor
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun `getUsers calls findAll and returns list of users`() {
        // GIVEN
        val userList = listOf(
            User(id = UUID.randomUUID(), username = "user1", artistIdList = mapper.createArrayNode()),
            User(id = UUID.randomUUID(), username = "user2", artistIdList = mapper.createArrayNode().add("id")),
        )
        whenever(userRepository.findAll()).thenReturn(userList)

        // WHEN
        val result = userService.getUsers()

        // THEN
        verify(userRepository, times(1)).findAll()
        assertEquals(result[1].username, "user2")
        assertEquals(result[0].artistIdList, emptyList<String>())
        assertEquals(result[1].artistIdList.size, 1)
        assertEquals(result[1].artistIdList.first(), "id")

    }

    @Test
    fun `createUser saves a new user`() {
        // GIVEN
        val userName = "john_doe"

        // WHEN
        userService.createUser(userName)

        // THEN
        val captor: KArgumentCaptor<User> = argumentCaptor<User>()

        verify(userRepository, times(1)).save(captor.capture())

        assertEquals(userName, captor.firstValue.username)
    }

    @Test
    fun `getTotalUsers returns size of findAll list`() {
        // GIVEN
        val userList = listOf(
            User(id = UUID.randomUUID(), username = "user1", artistIdList = mapper.createObjectNode()),
            User(id = UUID.randomUUID(), username = "user2", artistIdList = mapper.createObjectNode()),
            User(id = UUID.randomUUID(), username = "user3", artistIdList = mapper.createObjectNode())
        )
        whenever(userRepository.findAll()).thenReturn(userList)

        // WHEN
        val result = userService.getTotalUsers()

        // THEN
        assertEquals(3, result)
        verify(userRepository, times(1)).findAll()
    }

    @Test
    fun `getArtistIdListForUser returns artist list when found`() {
        // GIVEN
        val username = "Test"
        val userId = UUID.randomUUID()
        val storedArtistIds = jacksonObjectMapper().valueToTree<JsonNode>(listOf("testId1", "testId2"))

        val user = User(
            id = userId,
            username = username,
            artistIdList = storedArtistIds
        )
        whenever(userRepository.findByUsername(username)).thenReturn(user)

        // WHEN
        val result = userService.getArtistIdListForUser(username)

        // THEN
        assertEquals(listOf("testId1", "testId2"), result)
        verify(userRepository, times(1)).findByUsername(username)
    }

    @Test
    fun `getArtistIdListForUser returns null for artist list when no artists found`() {
        // GIVEN
        val username = "NotExistingTestUser"
        val userId = UUID.randomUUID()
        val storedArtistIds = null

        val user = User(
            id = userId,
            username = username,
            artistIdList = storedArtistIds
        )
        whenever(userRepository.findByUsername(username)).thenReturn(user)

        // WHEN
        val result = userService.getArtistIdListForUser(username)

        // THEN
        assertEquals(null, result)
        verify(userRepository, times(1)).findByUsername(username)
    }


    @Test
    fun `storeArtistListForUser appends new artists distinctively and saves`() {
        // GIVEN
        val username = "testUser"
        val storedArtistIds = jacksonObjectMapper().valueToTree<JsonNode>(listOf("testId1", "testId2"))

        val userInDb = User(
            username = username,
            artistIdList = storedArtistIds
        )

        whenever(userRepository.findByUsername(username)).thenReturn(userInDb)
        doAnswer { invocation ->
            val userArg = invocation.arguments[0] as User
            userArg
        }.whenever(userRepository).save(any<User>())

        // WHEN
        userService.storeArtistListForUser(UserDataDto(username, listOf("testId1", "testId2")))

        // THEN

        val captor: KArgumentCaptor<User> = argumentCaptor<User>()

        verify(userRepository, times(1)).save(captor.capture())

        assertEquals(username, captor.firstValue.username)
        assertEquals(storedArtistIds, captor.firstValue.artistIdList)
    }

    companion object {
        val mapper = jacksonObjectMapper()
    }
}
