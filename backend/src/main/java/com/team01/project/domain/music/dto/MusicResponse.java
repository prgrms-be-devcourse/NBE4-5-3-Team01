package com.team01.project.domain.music.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.team01.project.domain.music.entity.Music;

public record MusicResponse(
	String id,
	String name,
	String singer,
	String singerId,

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonDeserialize(using = LocalDateDeserializer.class)
	LocalDate releaseDate,

	String albumImage,
	String genre,
	String uri
) {
	public static MusicResponse fromEntity(Music music) {
		return new MusicResponse(
			music.getId(),
			music.getName(),
			music.getSinger(),
			music.getSingerId(),
			music.getReleaseDate(),
			music.getAlbumImage(),
			music.getGenre(),
			music.getUri()
		);
	}
}
