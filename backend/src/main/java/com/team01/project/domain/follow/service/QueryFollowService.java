package com.team01.project.domain.follow.service;

import static com.team01.project.domain.follow.entity.type.Status.*;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.follow.controller.dto.CountFollowResponse;
import com.team01.project.domain.follow.controller.dto.FollowResponse;
import com.team01.project.domain.follow.entity.type.Status;
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

		List<FollowResponse> list = followRepository.findByFromUserAndStatus(user, ACCEPT).stream()
			.filter(follow -> !follow.getToUser().getId().equals(currentUser.getId()))
			.map(follow -> FollowResponse.of(
				follow.getToUser(),
				checkFollow(follow.getToUser(), currentUser),
				checkFollow(currentUser, follow.getToUser())
			))
			.toList();

		System.out.println(list);

		return list;
	}

	public List<FollowResponse> findFollower(String currentUserId, String userId) {
		User currentUser = userRepository.getById(currentUserId);
		User user = userRepository.getById(userId);

		return followRepository.findByToUserAndStatus(user, ACCEPT).stream()
			.filter(follow -> !follow.getFromUser().getId().equals(currentUser.getId()))
			.map(follow -> FollowResponse.of(
				follow.getFromUser(),
				checkFollow(follow.getFromUser(), currentUser),
				checkFollow(currentUser, follow.getFromUser())
			))
			.toList();
	}

	public CountFollowResponse findCount(String userId) {
		User user = userRepository.getById(userId);
		Long followingCount = followRepository.countByFromUserAndStatus(user, ACCEPT);
		Long followerCount = followRepository.countByToUserAndStatus(user, ACCEPT);

		return CountFollowResponse.of(followingCount, followerCount);
	}

	private Status checkFollow(User user, User currentUser) {
		return followRepository.findStatusByToUserAndFromUser(user, currentUser)
			.orElse(NONE);
	}

	// public Boolean checkMutualFollow(String currentUserId, String userId) {
	// 	User user = userRepository.getById(userId);
	// 	User currentUser = userRepository.getById(currentUserId);
	// 	boolean checkFollower = followRepository.existsByToUserAndFromUserAndStatus(currentUser, user, ACCEPT);
	// 	boolean checkFollowing = followRepository.existsByToUserAndFromUserAndStatus(user, currentUser, ACCEPT);
	//
	// 	return checkFollower && checkFollowing;
	// }

	public List<FollowResponse> findMyFollowing(String currentUserId) {
		User currentUser = userRepository.getById(currentUserId);

		return followRepository.findByFromUser(currentUser)
			.stream()
			.map(follow -> FollowResponse.of(follow.getToUser(), follow.getStatus(), NONE))
			.toList();
	}

	public List<FollowResponse> findPendingList(String currentUserId) {
		User currentUser = userRepository.getById(currentUserId);

		return followRepository.findByToUserAndStatus(currentUser, PENDING).stream()
			.map(follow -> FollowResponse.of(follow.getFromUser(), NONE, follow.getStatus()))
			.toList();
	}
}
