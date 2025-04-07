package com.team01.project.domain.music.dto;

public record SpotifyPlaylistResponse(
	String id,
	String name,
	String image,
	int trackCount
) {
}