package com.team01.project.domain.music.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class Music(
    @Id
    @Column(name = "music_id", unique = true, nullable = false)
    var id: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "singer", nullable = false)
    var singer: String,

    @Column(name = "singer_id", nullable = false)
    var singerId: String,

    @Column(name = "release_date", nullable = true)
    var releaseDate: LocalDate? = null,

    @Column(name = "album_image", nullable = false)
    var albumImage: String,

    @Column(name = "genre")
    var genre: String? = null,

    @Column(name = "uri")
    var uri: String? = null
) {

    protected constructor() : this(
        id = "",
        name = "",
        singer = "",
        singerId = "",
        releaseDate = LocalDate.now(),
        albumImage = "",
        genre = null,
        uri = null
    )

    fun isSameAs(other: Music): Boolean {
        return name == other.name &&
                singer == other.singer &&
                singerId == other.singerId &&
                releaseDate == other.releaseDate &&
                albumImage == other.albumImage &&
                genre == other.genre &&
                uri == other.uri
    }

    fun updateMusic(updatedMusic: Music) {
        name = updatedMusic.name
        singer = updatedMusic.singer
        singerId = updatedMusic.singerId
        releaseDate = updatedMusic.releaseDate
        albumImage = updatedMusic.albumImage
        genre = updatedMusic.genre
        uri = updatedMusic.uri
    }
}
