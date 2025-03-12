package com.team01.project.user.entity;

import com.team01.project.domain.user.entity.User;

public class UserFixture {

	public static User 유저(String id) {
		return User.builder().id(id).name("이름").email("test@gmail.com").build();
	}

	public static User 유저_이메일(String id, String email) {
		return User.builder().id(id).name("이름").email(email).build();
	}
}
