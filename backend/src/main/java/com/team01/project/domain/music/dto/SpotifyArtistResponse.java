package com.team01.project.domain.music.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpotifyArtistResponse {
	private String id;
	private String name;
	private List<String> genres;
}
