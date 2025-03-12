package com.team01.project.domain.follow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.follow.entity.Follow;
import com.team01.project.domain.follow.repository.FollowRepository;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommandFollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	public void create(String fromUserId, String toUserId) {
		User fromUser = userRepository.getById(fromUserId);
		User toUser = userRepository.getById(toUserId);
		followRepository.save(new Follow(toUser, fromUser));
	}

	public void delete(String fromUserId, String toUserId) {
		User fromUser = userRepository.getById(fromUserId);
		User toUser = userRepository.getById(toUserId);
		Follow follow = followRepository.findByToUserAndFromUser(toUser, fromUser)
			.orElseThrow(() -> new IllegalArgumentException("팔로우를 찾을 수 없습니다."));

		followRepository.delete(follow);
	}
}
