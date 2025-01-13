package com.hvs.kotlinspringplayground.spotify

import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyAlbumsResponse.AlbumArtist
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyAlbumsResponse.AlbumItem
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyAlbumsResponse.ExternalUrls
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyAlbumsResponse.Image
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyArtistResponse
import com.hvs.kotlinspringplayground.spotify.client.response.SpotifyArtistSearchResponse

fun albumItem() = AlbumItem(
    albumType = "album",
    totalTracks = 10,
    availableMarkets = listOf("US", "GB", "DE"),
    externalUrls = ExternalUrls(
        spotify = "https://open.spotify.com/album/abc123"
    ),
    href = "https://api.spotify.com/v1/albums/abc123",
    id = "abc123",
    images = listOf(
        Image(
            url = "https://test.jpg",
            height = 640,
            width = 640
        ),
        Image(
            url = "https://test.jpg",
            height = 300,
            width = 300
        )
    ),
    name = "Test Album",
    releaseDate = "2021-05-14",
    releaseDatePrecision = "day",
    type = "album",
    uri = "spotify:album:abc123",
    artists = listOf(
        AlbumArtist(
            id = "artist1",
            name = "Test Artist 1",
            externalUrls = null,
            href = null,
            uri = null,
            type = "album",

            ),
        AlbumArtist(
            id = "artist2",
            name = "Test Artist 2",
            externalUrls = null,
            href = null,
            uri = null,
            type = "album",
        )
    ),
    albumGroup = "album"
)

fun spotifyArtistSearchResponse() = SpotifyArtistSearchResponse(
    artists = SpotifyArtistSearchResponse.Artists(
        href = "https://api.spotify.com/v1/search?query=test&type=artist&offset=0&limit=2",
        limit = 2,
        next = "https://api.spotify.com/v1/search?query=test&type=artist&offset=2&limit=2",
        offset = 0,
        previous = null,
        total = 10,
        items = listOf(
            SpotifyArtistSearchResponse.Artist(
                externalUrls = SpotifyArtistSearchResponse.ExternalUrls(
                    spotify = "https://open.spotify.com/artist/artist1"
                ),
                followers = SpotifyArtistSearchResponse.Followers(
                    href = null,
                    total = 5000
                ),
                genres = listOf("Pop", "Dance"),
                href = "https://api.spotify.com/v1/artists/artist1",
                id = "artist1",
                images = listOf(
                    SpotifyArtistSearchResponse.Image(
                        url = "https://test.com",
                        height = 640,
                        width = 640
                    )
                ),
                name = "Test Artist 1",
                popularity = 75,
                type = "artist",
                uri = "spotify:artist:artist1"
            ),
            SpotifyArtistSearchResponse.Artist(
                externalUrls = SpotifyArtistSearchResponse.ExternalUrls(
                    spotify = "https://open.spotify.com/artist/artist2"
                ),
                followers = SpotifyArtistSearchResponse.Followers(
                    href = null,
                    total = 12000
                ),
                genres = listOf("Rock"),
                href = "https://api.spotify.com/v1/artists/artist2",
                id = "artist2",
                images = listOf(
                    SpotifyArtistSearchResponse.Image(
                        url = "https://test.com",
                        height = 300,
                        width = 300
                    )
                ),
                name = "Test Artist 2",
                popularity = 88,
                type = "artist",
                uri = "spotify:artist:artist2"
            )
        )
    )
)

fun spotifyArtistResponse(): SpotifyArtistResponse = SpotifyArtistResponse(
    externalUrls = SpotifyArtistResponse.ExternalUrls(
        spotify = "https://open.spotify.com/artist/123"
    ),
    followers = SpotifyArtistResponse.Followers(
        href = null,
        total = 99999
    ),
    genres = listOf("Rock", "Pop"),
    href = "https://api.spotify.com/v1/artists/123",
    id = "artist123",
    images = listOf(
        SpotifyArtistResponse.Image(
            height = 640,
            url = "https://test.com",
            width = 640
        ),
        SpotifyArtistResponse.Image(
            height = 300,
            url = "https://test.com",
            width = 300
        )
    ),
    name = "Test Artist",
    popularity = 72,
    type = "artist",
    uri = "spotify:artist:123"
)