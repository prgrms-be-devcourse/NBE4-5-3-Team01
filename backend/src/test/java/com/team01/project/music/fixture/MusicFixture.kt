package com.team01.project.music.fixture

import com.team01.project.domain.music.entity.Music

object MusicFixture {
    fun music(id: String): Music {
        return Music(
            id = id,
            name = "title",
            singer = "singer",
            singerId = "default singer id",
            albumImage = "https://example.com/default.jpg"
        )
    }
}
