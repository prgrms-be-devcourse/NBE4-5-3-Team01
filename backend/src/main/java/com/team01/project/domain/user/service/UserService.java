package com.team01.project.domain.user.service;

import org.springframework.stereotype.Service;

import com.team01.project.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
}
