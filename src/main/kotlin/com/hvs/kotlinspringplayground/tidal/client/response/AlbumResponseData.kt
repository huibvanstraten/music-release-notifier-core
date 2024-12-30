package com.hvs.kotlinspringplayground.tidal.client.response


data class AlbumResponseData(
    val data: List<AlbumData>
) {
    data class AlbumData(
        val attributes: Attributes,
        val relationships: Relationships,
        val links: Links,
        val id: String,
        val type: String
    ) {
        data class Attributes(
            val title: String,
            val barcodeId: String,
            val numberOfVolumes: Int,
            val numberOfItems: Int,
            val duration: String,
            val explicit: Boolean,
            val releaseDate: String,
            val popularity: Double,
            val availability: List<String>,
            val mediaTags: List<String>,
            val imageLinks: List<ImageLink>,
            val videoLinks: List<Any>, // Empty list, kept as Any for flexibility
            val externalLinks: List<ExternalLink>,
            val type: String
        ) {
            data class ImageLink(
                val href: String,
                val meta: Meta
            ) {
                data class Meta(
                    val width: Int,
                    val height: Int
                )
            }

            data class ExternalLink(
                val href: String,
                val meta: Meta
            ) {
                data class Meta(
                    val type: String
                )
            }
        }

        data class Relationships(
            val similarAlbums: Relationship,
            val artists: Relationship,
            val items: Relationship,
            val providers: Relationship
        ) {
            data class Relationship(
                val links: Links
            ) {
                data class Links(
                    val self: String
                )
            }
        }

        data class Links(
            val self: String
        )
    }
}
