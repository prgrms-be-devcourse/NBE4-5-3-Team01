package com.team01.project.domain.music.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.team01.project.domain.music.entity.Music;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MusicRequest {

	@NotBlank
	private String id;

	@NotBlank
	private String name;

	@NotBlank
	private String singer;

	@NotBlank
	private String singerId;

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate releaseDate;

	@NotBlank
	private String albumImage;

	private String genre;

	private String uri;

	// 생성자
	public MusicRequest(String id, String name, String singer, String singerId, LocalDate releaseDate,
						String albumImage, String genre, String uri) {
		this.id = id;
		this.name = name;
		this.singer = singer;
		this.singerId = singerId;
		this.releaseDate = releaseDate;
		this.albumImage = albumImage;
		this.genre = genre;
		this.uri = uri;
	}

	// 엔티티 변환 메서드
	public Music toEntity() {
		return new Music(id, name, singer, singerId, releaseDate, albumImage, genre, uri);
	}

	// Setter 추가 (record에서는 불가능했던 부분 해결)
	public void setGenres(String genres) {
		this.genre = genres;
	}
}
