package com.team01.project.domain.follow.service;

import java.time.LocalTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team01.project.domain.follow.entity.Follow;
import com.team01.project.domain.follow.repository.FollowRepository;
import com.team01.project.domain.notification.event.NotificationFollowEvent;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CommandFollowService {

	private final FollowRepository followRepository;
	private final UserRepository userRepository;
	private final ApplicationEventPublisher eventPublisher;

	public void create(String fromUserId, String toUserId) {
		User fromUser = userRepository.getById(fromUserId);
		User toUser = userRepository.getById(toUserId);

		if (followRepository.existsByToUserAndFromUser(toUser, fromUser)) {
			throw new IllegalStateException("이미 팔로우 요청을 보냈습니다.");
		}
		followRepository.save(new Follow(toUser, fromUser));

		eventPublisher.publishEvent(new NotificationFollowEvent(this, LocalTime.now(), toUser, fromUser));
	}

	public void delete(String fromUserId, String toUserId) {
		User fromUser = userRepository.getById(fromUserId);
		User toUser = userRepository.getById(toUserId);
		Follow follow = followRepository.findByToUserAndFromUser(toUser, fromUser)
			.orElseThrow(() -> new IllegalArgumentException("팔로우를 찾을 수 없습니다."));

		followRepository.delete(follow);
	}

	public void accept(String fromUserId, String toUserId) {
		User fromUser = userRepository.getById(fromUserId);
		User toUser = userRepository.getById(toUserId);

		Follow follow = followRepository.findByToUserAndFromUser(toUser, fromUser)
			.orElseThrow(() -> new IllegalArgumentException("팔로우를 찾을 수 없습니다."));
		follow.accept();
	}
}
