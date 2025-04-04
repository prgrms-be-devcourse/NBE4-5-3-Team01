package com.team01.project.user.service; // UserServiceTest.java

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.team01.project.domain.user.dto.UserDto;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;
import com.team01.project.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	void testAddUser() {
		// given: 테스트에 사용할 UserDto 객체 생성
		UserDto userDto = UserDto.builder()
			.id("kmnj2100")
			.email("test@example.com")
			.name("Test User")
			.originalName("Original Test User")
			.field("Test Field")
			.password("password")
			.build();

		when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

		User expectedUser = User.builder()
			.id(userDto.getId())
			.email(userDto.getEmail())
			.name(userDto.getName())
			.originalName(userDto.getOriginalName())
			.field(userDto.getField())
			.userPassword("encodedPassword")
			.build();

		// repository.save() 호출 시 expectedUser를 반환하도록 설정
		when(userRepository.save(any(User.class))).thenReturn(expectedUser);

		// when: 서비스 메서드 호출
		User actualUser = userService.addUser(userDto);

		// then: 반환된 User 객체의 필드 값 검증
		assertNotNull(actualUser);
		assertEquals(expectedUser.getId(), actualUser.getId());
		assertEquals(expectedUser.getEmail(), actualUser.getEmail());
		assertEquals(expectedUser.getName(), actualUser.getName());
		assertEquals(expectedUser.getOriginalName(), actualUser.getOriginalName());
		assertEquals(expectedUser.getField(), actualUser.getField());
		assertEquals(expectedUser.getUserPassword(), actualUser.getUserPassword());
	}
}