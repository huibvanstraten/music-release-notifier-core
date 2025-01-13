package com.hvs.kotlinspringplayground.user.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.user.domain.jpa.User
import com.hvs.kotlinspringplayground.user.repository.UserRepository
import com.hvs.kotlinspringplayground.user.service.impl.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun `getUsers calls findAll with given pageable and returns the page`() {
        // GIVEN
        val pageable = PageRequest.of(0, 5)
        val userList = listOf(
            User(id = UUID.randomUUID(), username = "user1", artistIdList = mapper.createObjectNode()),
            User(id = UUID.randomUUID(), username = "user2", artistIdList = mapper.createObjectNode())
        )
        val pageImpl = PageImpl(userList, pageable, userList.size.toLong())
        whenever(userRepository.findAll(pageable)).thenReturn(pageImpl)

        // WHEN
        val result = userService.getUsers(pageable)

        // THEN
        assertEquals(pageImpl, result)
        verify(userRepository, times(1)).findAll(pageable)
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
        val existingUserId = UUID.randomUUID()
        val storedArtistIds = jacksonObjectMapper().valueToTree<JsonNode>(listOf("testId1", "testId2"))

        val userInDb = User(
            id = existingUserId,
            username = username,
            artistIdList = storedArtistIds
        )

        whenever(userRepository.findByUsername(username)).thenReturn(userInDb)
        doAnswer { invocation ->
            val userArg = invocation.arguments[0] as User
            userArg
        }.whenever(userRepository).save(any<User>())

        // WHEN
        userService.storeArtistListForUser(username, listOf("testId1", "testId2"))

        // THEN

        val captor: KArgumentCaptor<User> = argumentCaptor<User>()

        verify(userRepository, times(1)).save(captor.capture())

        assertEquals(username, captor.firstValue.username)
        assertEquals(existingUserId, captor.firstValue.id)
        assertEquals(storedArtistIds, captor.firstValue.artistIdList)
    }

    @Test
    fun `storeArtistListForUser throws if user not found`() {
        // GIVEN
        val username = "invalidUser"
        whenever(userRepository.findByUsername(username)).thenReturn(null)

        assertThrows<IllegalArgumentException> {
            userService.storeArtistListForUser(username, listOf("artistX"))
        }
    }

    companion object {
        val mapper = jacksonObjectMapper()
    }
}
