package com.hvs.kotlinspringplayground.artist.service.impl

import com.hvs.kotlinspringplayground.artist.domain.jpa.Artist
import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.artist.dto.ArtistProfileDataDto
import com.hvs.kotlinspringplayground.artist.repository.ArtistRepository
import com.hvs.kotlinspringplayground.artist.service.ArtistService
import com.hvs.kotlinspringplayground.outbox.service.OutboxService
import com.hvs.kotlinspringplayground.spotify.service.SpotifyService
import com.hvs.kotlinspringplayground.tidal.domain.Album
import com.hvs.kotlinspringplayground.tidal.service.TidalService
import org.springframework.transaction.annotation.Transactional

open class ArtistService(
    private val tidalService: TidalService,
    private val spotifyService: SpotifyService,
    private val artistRepository: ArtistRepository,
    private val outboxService: OutboxService,
): ArtistService {

    // TODO: Pattern to use either tidal or spotify, without it being this services concern
    // TODO: GettingArtistsByName with autocomplete

    override fun getArtist(
        artistId: String,
        ): ArtistDataDto = spotifyService.getArtist(artistId)

    override fun getArtistProfileData(
        artistName: String,
        ): ArtistProfileDataDto? = spotifyService.getArtistProfileData(artistName)


    @Transactional
    override fun getArtistFromSpotifyByName(
        artistName: String
    ): ArtistDataDto? = spotifyService.getArtistByName(artistName).also { artistDataDto ->
//        outboxService.send {
//            ReleaseEvent(
//                id = UUID.randomUUID().toString(),
//                release = jacksonObjectMapper().convertValue(artistDataDto, ObjectNode::class.java)
//            )
//        }
    }

    override fun findSpotifyArtistNamesByName(
        input: String
    ): List<String> = spotifyService.getArtistNamesByName(input)

    override fun storeArtists() {
        val artistDataResponse = tidalService.getAllArtists()
        val artists = artistDataResponse.data.map { artist ->
            ArtistDataDto(
                artistId = artist.id,
                name = artist.attributes.name,
            )
        }

        artists.forEach { artist ->
            artistRepository.save(Artist.Companion.from(artist))
        }
    }

    override fun getNewAlbumForArtist(artistId: Int): Album? {

        val artist = tidalService.getArtist(artistId = artistId).data.first()

        val artistAlbumIds = tidalService.getAlbumIdListForArtist(artistId = artist.id.toInt())

        val albums = artistAlbumIds.data.mapNotNull { albumId ->
            tidalService.getAlbum(albumId.id)
        }

        return albums.maxByOrNull { it.releaseDate }
    }
}