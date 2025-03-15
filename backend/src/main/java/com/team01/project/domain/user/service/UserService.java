package com.team01.project.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public List<User> search(String name) {
		return userRepository.searchUser(name);
	}

	public User getUserById(String id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저 찾을 수 없습니다: " + id));
	}
}
