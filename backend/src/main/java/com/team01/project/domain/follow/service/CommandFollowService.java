package com.team01.project.domain.follow.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.follow.domain.Follow;
import com.team01.project.domain.follow.repository.FollowRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommandFollowService {

	private final FollowRepository followRepository;

	public void create(Long toUserId) {
		followRepository.save(new Follow(toUserId, 0L));
	}

	public void delete(Long toUserId) {
		Follow follow = followRepository.findByToUserIdAndFromUserId(toUserId, 0L)
			.orElseThrow(() -> new IllegalArgumentException("팔로우를 찾을 수 없습니다."));

		followRepository.delete(follow);
	}
}
