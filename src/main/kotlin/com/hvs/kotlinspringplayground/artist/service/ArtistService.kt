package com.hvs.kotlinspringplayground.artist.service

import com.hvs.kotlinspringplayground.artist.domain.jpa.Artist
import com.hvs.kotlinspringplayground.artist.dto.ArtistDataDto
import com.hvs.kotlinspringplayground.artist.repository.ArtistRepository
import com.hvs.kotlinspringplayground.spotify.service.SpotifyService
import com.hvs.kotlinspringplayground.tidal.domain.Album
import com.hvs.kotlinspringplayground.tidal.service.TidalService
import com.hvs.kotlinspringplayground.user.service.UserService

class ArtistService(
    private val tidalService: TidalService,
    private val spotifyService: SpotifyService,
    private val artistRepository: ArtistRepository,
    private val userService: UserService,
) {

    // TODO: Pattern to use either tidal or spotify, without it being this services concern
    // TODO: GettingArtistsByName with autocomplete
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

    fun getArtistsFromSpotifyByName(
        artistName: String
    ): ArtistDataDto? = spotifyService.getArtistByName(artistName)

    fun storeArtists() {
        val artistDataResponse = tidalService.getAllArtists()
        val artists = artistDataResponse.data.map { artist ->
            ArtistDataDto(
                streamingId = artist.id,
                name = artist.attributes.name,
            )
        }

        artists.forEach { artist ->
            artistRepository.save(Artist.from(artist))
        }
    }

    fun storeSpotifyArtistsForUser(
        username: String,
        artistIdList: List<String>
    ) = userService.storeArtistsForUser(username, artistIdList)

    fun getNewAlbumForArtist(artistId: Int): Album? {

        val artist = tidalService.getArtist(artistId = artistId).data.first()

        val artistAlbumIds = tidalService.getAlbumIdListForArtist(artistId = artist.id.toInt())

        val albums = artistAlbumIds.data.mapNotNull { albumId ->
            tidalService.getAlbum(albumId.id)
        }

        return albums.maxByOrNull { it.releaseDate }
    }
}