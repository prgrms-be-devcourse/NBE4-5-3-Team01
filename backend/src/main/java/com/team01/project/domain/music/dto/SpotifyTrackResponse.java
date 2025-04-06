package com.team01.project.domain.music.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpotifyTrackResponse {
	private String id;
	private String name;

	@JsonProperty("uri")
	private String uri;

	@JsonProperty("artists")
	private List<Artist> artists;

	private Album album;

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Album {
		private String name;

		@JsonProperty("release_date")
		private String releaseDate;

		@JsonProperty("images")
		private List<Image> images;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Artist {
		private String id;
		private String name;
	}

	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Image {
		private String url;
	}

	public String getArtistsAsString() {
		return artists.stream().map(Artist::getName).collect(Collectors.joining(", "));
	}

	public String getArtistsIdAsString() {
		return artists.stream().map(Artist::getId).collect(Collectors.joining(", "));
	}
}
