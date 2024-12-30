package com.hvs.kotlinspringplayground.tidal.client

import com.hvs.kotlinspringplayground.tidal.authorisation.AuthorisationService
import com.hvs.kotlinspringplayground.tidal.client.response.AlbumIdListResponseData
import com.hvs.kotlinspringplayground.tidal.client.response.AlbumResponseData
import com.hvs.kotlinspringplayground.tidal.client.response.ArtistResponseData
import org.springframework.web.reactive.function.client.WebClient

class TidalClient(
    private val authorisationService: AuthorisationService
) {

    private lateinit var webClient: WebClient

    // API /artists does not work without filter.
    fun getArtists(): ArtistResponseData {
        buildWebClient()
        val uri = "/artists?filter[id]=1566&countryCode={countryCode}"
        return webClient.get()
            .uri(uri, COUNTRY_CODE)
            .retrieve()
            .bodyToMono(ArtistResponseData::class.java)
            .block()!!
    }

    fun getArtist(artistId: Int): ArtistResponseData {
        buildWebClient()
        val uri = "/artists?filter[id]={artistId}&countryCode={countryCode}"
        return webClient.get()
            .uri(uri, artistId, COUNTRY_CODE)
            .retrieve()
            .bodyToMono(ArtistResponseData::class.java)
            .block()!!
    }

    fun getAlbumIdsForArtist(artistId: Int): AlbumIdListResponseData {
        buildWebClient()
        val uri = "/artists/{artistId}/relationships/albums?countryCode={countryCode}"
        return webClient.get()
            .uri(uri, artistId, COUNTRY_CODE)
            .retrieve()
            .bodyToMono(AlbumIdListResponseData::class.java)
            .block()!!
    }

    fun getAlbum(albumId: String): AlbumResponseData {
        buildWebClient()
        val uri = "/albums?filter[id]={albumId}&countryCode={countryCode}"
        return webClient.get()
            .uri(uri, albumId, COUNTRY_CODE)
            .retrieve()
            .bodyToMono(AlbumResponseData::class.java)
            .block()!!
    }

    companion object {
        private const val TIDAL_BASE_URL = "https://openapi.tidal.com/v2"
        private const val COUNTRY_CODE = "NL"
    }

    private fun buildWebClient() {
        val token = authorisationService.getToken()

        webClient = WebClient.builder()
            .baseUrl(TIDAL_BASE_URL)
            .defaultHeader(
                "Authorization",
                "Bearer $token"
            )
            .build()
    }
}