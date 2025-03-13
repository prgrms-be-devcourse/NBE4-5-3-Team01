package com.team01.project.domain.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.team01.project.domain.user.dto.SimpleUserResponse;
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
}
