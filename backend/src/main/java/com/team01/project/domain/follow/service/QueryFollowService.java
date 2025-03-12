package com.team01.project.domain.follow.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.follow.controller.dto.FollowResponse;
import com.team01.project.domain.follow.repository.FollowRepository;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QueryFollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;

	public List<FollowResponse> findFollowing(String currentUserId, String userId) {
		Optional<User> currentUser = userRepository.findById(currentUserId);
		User user = userRepository.getById(userId);

		return followRepository.findByFromUser(user).stream()
			.map(follow -> FollowResponse.of(
				follow.getFromUser(), currentUser.isPresent() && checkFollow(follow.getFromUser(), currentUser.get())
			))
			.toList();
	}

	public List<FollowResponse> findFollower(String currentUserId, String userId) {
		Optional<User> currentUser = userRepository.findById(currentUserId);
		User user = userRepository.getById(userId);

		return followRepository.findByToUser(user).stream()
			.map(follow -> FollowResponse.of(
				follow.getToUser(), currentUser.isPresent() && checkFollow(follow.getToUser(), currentUser.get())
			))
			.toList();
	}

	private boolean checkFollow(User user, User currentUser) {
		return followRepository.existsByToUserAndFromUser(user, currentUser);
	}
}
