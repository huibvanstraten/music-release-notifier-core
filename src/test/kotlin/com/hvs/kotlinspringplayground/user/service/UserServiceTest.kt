package com.hvs.kotlinspringplayground.user.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.artist.service.ArtistService
import com.hvs.kotlinspringplayground.user.domain.jpa.User
import com.hvs.kotlinspringplayground.user.dto.UserArtistListDto
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

const val ID =  "7fba1f85-7d7d-45bf-9303-a7e191ee9328"
const val USERNAME_1 = "user1"
const val USERNAME_2 = "user2"

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var artistService: ArtistService

    @InjectMocks
    private lateinit var userService: UserService

    @Test
    fun `getUsers calls findAll and returns list of users`() {
        // GIVEN
        val userList = listOf(
            User(id = ID, username = USERNAME_1, artistIdList = mapper.createArrayNode()),
            User(id = ID, username = USERNAME_2, artistIdList = mapper.createArrayNode().add("id")),
        )
        whenever(userRepository.findAll()).thenReturn(userList)

        // WHEN
        val result = userService.getUsers()

        // THEN
        verify(userRepository, times(1)).findAll()
        assertEquals(result[1].username, USERNAME_2)
        assertEquals(result[0].artistIdList, emptyList<String>())
        assertEquals(result[1].artistIdList.size, 1)
        assertEquals(result[1].artistIdList.first(), "id")
    }

    @Test
    fun `createUser saves a new user`() {

        // WHEN
        userService.createUser(ID, USERNAME_1)

        // THEN
        val captor: KArgumentCaptor<User> = argumentCaptor<User>()

        verify(userRepository, times(1)).save(captor.capture())

        assertEquals(USERNAME_1, captor.firstValue.username)
    }

    @Test
    fun `getTotalUsers returns size of findAll list`() {
        // GIVEN
        val userList = listOf(
            User(id = ID, username = USERNAME_1, artistIdList = mapper.createObjectNode()),
            User(id = ID, username = USERNAME_2, artistIdList = mapper.createObjectNode()),
            User(id = ID, username = USERNAME_1, artistIdList = mapper.createObjectNode())
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
        val storedArtistIds = jacksonObjectMapper().valueToTree<JsonNode>(listOf("testId1", "testId2"))

        val user = User(
            id = ID,
            username = USERNAME_1,
            artistIdList = storedArtistIds
        )
        whenever(userRepository.findByUsername(USERNAME_1)).thenReturn(user)

        // WHEN
        val result = userService.getArtistIdListForUser(USERNAME_1)

        // THEN
        assertEquals(listOf("testId1", "testId2"), result)
        verify(userRepository, times(1)).findByUsername(USERNAME_1)
    }

    @Test
    fun `getArtistIdListForUser returns null for artist list when no artists found`() {
        // GIVEN
        val storedArtistIds = null

        val user = User(
            id = ID,
            username = USERNAME_1,
            artistIdList = storedArtistIds
        )
        whenever(userRepository.findByUsername(USERNAME_1)).thenReturn(user)

        // WHEN
        val result = userService.getArtistIdListForUser(USERNAME_1)

        // THEN
        assertEquals(null, result)
        verify(userRepository, times(1)).findByUsername(USERNAME_1)
    }


    @Test
    fun `storeArtistListForUser appends new artists distinctively and saves`() {
        // GIVEN
        val storedArtistIds = jacksonObjectMapper().valueToTree<JsonNode>(listOf("testId1", "testId2"))

        val userInDb = User(
            id = ID,
            username = USERNAME_1,
            artistIdList = storedArtistIds
        )

        whenever(userRepository.findByUsername(USERNAME_1)).thenReturn(userInDb)
        doAnswer { invocation ->
            val userArg = invocation.arguments[0] as User
            userArg
        }.whenever(userRepository).save(any<User>())

        // WHEN
        userService.storeArtistListForUser(UserArtistListDto(userId = ID, username = USERNAME_1, artistIdList = listOf("testId1", "testId2") ))

        // THEN

        val captor: KArgumentCaptor<User> = argumentCaptor<User>()

        verify(userRepository, times(1)).save(captor.capture())

        assertEquals(USERNAME_1, captor.firstValue.username)
        assertEquals(storedArtistIds, captor.firstValue.artistIdList)
    }

    companion object {
        val mapper = jacksonObjectMapper()
    }
}
