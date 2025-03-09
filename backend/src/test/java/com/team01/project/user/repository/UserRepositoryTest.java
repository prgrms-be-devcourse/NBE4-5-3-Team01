package com.team01.project.user.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;

@SpringBootTest
public class UserRepositoryTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	public void testCreateUser() {

		LocalDate localDate = LocalDate
			.parse("2000-01-02", DateTimeFormatter
				.ofPattern("yyyy-MM-dd"));

		User user = User.builder()
			.id("asdf1234")
			.email("test@example.com")
			.createdDate(LocalDateTime.now())
			.name("name")
			.nickName("nickName")
			.birthDay(localDate)
			.followId(1L)
			.field("사용자")
			.build();

		userRepository.save(user);
	}
}
