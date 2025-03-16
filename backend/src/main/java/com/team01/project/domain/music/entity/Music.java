package com.team01.project.domain.music.entity;

import java.time.LocalDate;
import java.util.Objects;

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

	@Column(name = "singer_id", nullable = false)
	private String singerId;

	@Column(name = "release_date", nullable = false)
	private LocalDate releaseDate;

	@Column(name = "album_image", nullable = false)
	private String albumImage;

	@Column(name = "genre")
	private String genre;

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public boolean isSameAs(Music other) {
		return this.name.equals(other.getName())
			&& this.singer.equals(other.getSinger())
			&& this.singerId.equals(other.getSingerId())
			&& this.releaseDate.equals(other.getReleaseDate())
			&& this.albumImage.equals(other.getAlbumImage())
			&& Objects.equals(this.genre, other.getGenre());
	}

	public void updateMusic(Music updatedMusic) {
		this.name = updatedMusic.getName();
		this.singer = updatedMusic.getSinger();
		this.singerId = updatedMusic.getSingerId();
		this.releaseDate = updatedMusic.getReleaseDate();
		this.albumImage = updatedMusic.getAlbumImage();
		this.genre = updatedMusic.getGenre();
	}
}
