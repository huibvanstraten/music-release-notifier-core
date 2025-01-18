package com.hvs.kotlinspringplayground.spotify.client.impl

import com.hvs.kotlinspringplayground.spotify.authorisation.SpotifyAuthorisationService
import com.hvs.kotlinspringplayground.spotify.client.response.Album
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyAlbumsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient


class SpotifyAsyncClient(
    private val spotifyAuthorisationService: SpotifyAuthorisationService,
    private val baseUrl: String = "https://api.spotify.com/v1"
) {

    fun getReleasesForArtist(
        artistId: String,
        includeGroups: String? = "album,single,appears_on",
    ): Flow<Page<Album>> = flow {
        var offset = 0
        val limit = 20

        val webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken()}")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .build()

        while (true) {
            val url = "/artists/$artistId/albums" +
                "?include_groups=$includeGroups&limit=$limit&offset=$offset"

            val responseJson = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String::class.java)
                .block()

            if (responseJson != null) {
                val response = Json.decodeFromString<SpotifyAlbumsResponse>(responseJson)
                if (response.items.isNotEmpty()) {
                    emit(PageImpl(response.items, PageRequest.of(offset / limit, limit), response.total.toLong()))
                    offset += response.items.size
                } else {
                    break
                }
            } else {
                break
            }
        }
    }
        .flowOn(Dispatchers.IO)

    val accessToken: () -> String = { spotifyAuthorisationService.getAccessToken() }
}