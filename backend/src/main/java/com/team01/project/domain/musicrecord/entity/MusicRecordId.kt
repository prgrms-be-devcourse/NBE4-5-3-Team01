package com.team01.project.domain.musicrecord.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicRecordId implements Serializable {

	private Long calendarDateId;

	private String musicId;

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass()) {
			return false;
		}

		MusicRecordId that = (MusicRecordId)object;

		return Objects.equals(calendarDateId, that.calendarDateId)
			&& Objects.equals(musicId, that.musicId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(calendarDateId, musicId);
	}

}