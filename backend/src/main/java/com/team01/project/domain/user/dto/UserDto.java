package com.team01.project.domain.user.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.team01.project.domain.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserDto {
	private String id;
	private String email;
	private String name;
	private String nickName;
	private LocalDate birthDay;
	private LocalDateTime createdDate;
	private String field;

	//엔티티 -> DTO 변환

	public static UserDto from(User user) {
		return UserDto.builder()
			.id(user.getId())
			.email(user.getEmail())
			.name(user.getName())
			.nickName(user.getNickName())
			.birthDay(user.getBirthDay())
			.createdDate(user.getCreatedDate())
			.field(user.getField())
			.build();
	}
}
