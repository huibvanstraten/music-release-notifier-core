package com.hvs.kotlinspringplayground.spotify.client

import com.hvs.kotlinspringplayground.spotify.authorisation.SpotifyAuthorisationService
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyAlbumsResponse
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyArtistResponse
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyArtistSearchResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.web.client.RestClient
import org.springframework.http.HttpHeaders

class SpotifyClient(
    private val spotifyAuthorisationService: SpotifyAuthorisationService,
    private val restClient: RestClient = RestClient.create(),
    private val baseUrl: String = "https://api.spotify.com/v1"
) {

    fun getArtist(artistId: String): SpotifyArtistResponse {
        val response = restClient
            .get()
            .uri("${baseUrl}/artists/$artistId")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken()}")
            .retrieve()
            .body(SpotifyArtistResponse::class.java)

        return response?: throw RuntimeException("Failed to retrieve artist data for artist: $artistId")
    }

    fun findArtistByName(
        artistName: String
    ): SpotifyArtistSearchResponse {
        val response = restClient
            .get()
            .uri("${baseUrl}/search?q=$artistName&type=artist")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken()}")
            .retrieve()
            .body(SpotifyArtistSearchResponse::class.java)

        return response?: throw RuntimeException("Failed to retrieve artist data for artist: $artistName")
    }

    fun getArtistAlbumsPageable(
        artistId: String,
        pageable: Pageable,
        includeGroups: String? = "album,single,appears_on"
    ): Page<SpotifyAlbumsResponse.AlbumItem> {

        val limit = pageable.pageSize
        val offset = pageable.offset

        val url = "${baseUrl}/artists/$artistId/albums" +
            "?include_groups=$includeGroups&limit=$limit&offset=$offset"

        val response = restClient
            .get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken()}")
            .retrieve()
            .body(SpotifyAlbumsResponse::class.java)

        val items = response?.items ?: emptyList()
        val total = response?.total?.toLong() ?: 0L

        return PageImpl(items, pageable, total)
    }


    private fun accessToken() = spotifyAuthorisationService.getAccessToken()
}