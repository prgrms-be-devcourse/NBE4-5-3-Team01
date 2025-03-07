package com.team01.project.domain.music.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class Music {

	@Id
	@Column(name = "music_id", unique = true, nullable = false)
	private String id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "singer", nullable = false)
	private String singer;

	@Column(name = "release_date", nullable = false)
	private LocalDate releaseDate;

	@Column(name = "album_image", nullable = false)
	private String albumImage;

	@Column(name = "genre")
	private String genre;
}
