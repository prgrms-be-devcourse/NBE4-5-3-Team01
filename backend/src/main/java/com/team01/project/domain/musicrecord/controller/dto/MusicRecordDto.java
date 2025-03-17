package com.team01.project.domain.musicrecord.controller.dto;

import java.time.LocalDate;

import com.team01.project.domain.calendardate.entity.CalendarDate;
import com.team01.project.domain.music.entity.Music;
import com.team01.project.domain.musicrecord.entity.MusicRecord;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MusicRecordDto {

	// CalendarDate 관련 정보
	private Long calendarDateId;
	private LocalDate date;
	private String memo;

	// Music 관련 정보
	private String musicId;
	private String musicName;
	private String singer;
	private String singerId;
	private LocalDate releaseDate;
	private String albumImage;
	private String genre;

	// 엔티티 -> DTO 변환 메서드
	public static MusicRecordDto from(MusicRecord musicRecord) {
		CalendarDate calendarDate = musicRecord.getCalendarDate();
		Music music = musicRecord.getMusic();
		return MusicRecordDto.builder()
				.calendarDateId(calendarDate.getId())
				.date(calendarDate.getDate())
				.memo(calendarDate.getMemo())
				.musicId(music.getId())
				.musicName(music.getName())
				.singer(music.getSinger())
				.singerId(music.getSingerId())
				.releaseDate(music.getReleaseDate())
				.albumImage(music.getAlbumImage())
				.genre(music.getGenre())
				.build();
	}
}
