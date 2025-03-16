package com.team01.project.domain.follow.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.follow.controller.dto.CountFollowResponse;
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
		User currentUser = userRepository.getById(currentUserId);
		User user = userRepository.getById(userId);

		return followRepository.findByFromUser(user).stream()
			.map(follow -> FollowResponse.of(
				follow.getToUser(),
				checkFollow(follow.getToUser(), currentUser),
				checkFollow(currentUser, follow.getToUser())
			))
			.toList();
	}

	public List<FollowResponse> findFollower(String currentUserId, String userId) {
		User currentUser = userRepository.getById(currentUserId);
		User user = userRepository.getById(userId);

		return followRepository.findByToUser(user).stream()
			.map(follow -> FollowResponse.of(
				follow.getFromUser(),
				checkFollow(follow.getFromUser(), currentUser),
				checkFollow(currentUser, follow.getFromUser())
			))
			.toList();
	}

	public CountFollowResponse findCount(String userId) {
		User user = userRepository.getById(userId);
		Long followingCount = followRepository.countByFromUser(user);
		Long followerCount = followRepository.countByToUser(user);

		return CountFollowResponse.of(followingCount, followerCount);
	}



	private boolean checkFollow(User user, User currentUser) {
		return followRepository.existsByToUserAndFromUser(user, currentUser);
	}

	public Boolean checkMutualFollow(String currentUserId, String userId) {
		User user = userRepository.getById(userId);
		User currentUser = userRepository.getById(currentUserId);
		boolean checkFollower = followRepository.existsByToUserAndFromUser(currentUser, user);
		boolean checkFollowing = followRepository.existsByToUserAndFromUser(user, currentUser);

		return checkFollower && checkFollowing;
	}
}
