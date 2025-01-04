package com.hvs.kotlinspringplayground.artist.service


import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hvs.kotlinspringplayground.artist.domain.jpa.Artist
import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.artist.event.ReleaseEvent
import com.hvs.kotlinspringplayground.artist.repository.ArtistRepository
import com.hvs.kotlinspringplayground.outbox.service.OutboxService
import com.hvs.kotlinspringplayground.tidal.client.response.ArtistResponseData.ArtistData
import com.hvs.kotlinspringplayground.tidal.domain.Album
import com.hvs.kotlinspringplayground.tidal.service.TidalService
import com.hvs.kotlinspringplayground.user.UserService
import java.util.UUID

class ArtistService(
    private val tidalService: TidalService,
    private val artistRepository: ArtistRepository,
    private val userService: UserService,
    private val outboxService: OutboxService,
) {

    // get all artists and save into table: name and id
    // save user and preferred artists into table: user and artist id
    // for each artist, get albums (eps?, loose tracks?) and check releasedate
    // .... etc

    // Pattern to use either tidal or spotify without this service having to know

    /** Resource Rest with parameter based filtering
     *              @GetMapping("/user")
     * public MappingJacksonValue getUser(@RequestParam List<String> fields) {
     *     User user = // get user data
     *     SimpleFilterProvider filterProvider = new SimpleFilterProvider();
     *     filterProvider.addFilter("userFilter", SimpleBeanPropertyFilter.filterOutAllExcept(new HashSet<>(fields)));
     *
     *     MappingJacksonValue mapping = new MappingJacksonValue(user);
     *     mapping.setFilters(filterProvider);
     *     return mapping;
     * }
     */
    // transactional outbox pattern for saving and sending consistently
    // Microservice with Camunda 7 to process

    //messages with whatsapp

    fun storeArtists() {
        val artistDataResponse = tidalService.getAllArtists()
        val artists = artistDataResponse.data.map { artist ->
            ArtistDataDto(
                streamingId = artist.id.toInt(),
                name = artist.attributes.name,
            )
        }

        artists.forEach { artist ->
            artistRepository.save(Artist.from(artist))
        }
    }

    fun storeArtistsForUser(
        userName: String,
        artistNameList: List<String>
    ) {
        val userArtistList = artistNameList.map { artist ->
            val tidalArtist = artistRepository.findByNameIgnoreCase(artist).first()

            ArtistData(
                id = tidalArtist.id.toString(),
                attributes = ArtistData.Attributes(
                    name = tidalArtist.name,
                )
            )
        }

        userService.storeArtistsForUser(userName, userArtistList)
    }

    fun getNewAlbumForArtist(artistId: Int): Album? {

        val artist = tidalService.getArtist(artistId = artistId).data.first()

        val artistAlbumIds = tidalService.getAlbumIdListForArtist(artistId = artist.id.toInt())

        val albums = artistAlbumIds.data.mapNotNull { albumId ->
            tidalService.getAlbum(albumId.id)
        }

        outboxService.send {
            ReleaseEvent(
                id = UUID.randomUUID().toString(),
                release = jacksonObjectMapper().createObjectNode(),
            )
        }

        return albums.maxByOrNull { it.releaseDate }
    }
}