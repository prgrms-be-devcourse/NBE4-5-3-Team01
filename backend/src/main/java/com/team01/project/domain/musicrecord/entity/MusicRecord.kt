package com.team01.project.domain.musicrecord.entity;

import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.music.entity.Music;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MusicRecord {

	@EmbeddedId
	private MusicRecordId id;

	@MapsId("calendarDateId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "calendar_date_id", nullable = false)
	private CalendarDate calendarDate;

	@MapsId("musicId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "music_id", nullable = false)
	private Music music;

}