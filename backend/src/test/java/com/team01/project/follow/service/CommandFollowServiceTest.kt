package com.team01.project.follow.service;

import static com.team01.project.user.entity.UserFixture.유저;
import static com.team01.project.user.entity.UserFixture.유저_이메일;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.team01.project.common.service.ServiceTest;
import com.team01.project.domain.follow.entity.Follow;
import com.team01.project.domain.follow.repository.FollowRepository;
import com.team01.project.domain.follow.service.CommandFollowService;
import com.team01.project.domain.notification.entity.Subscription;
import com.team01.project.domain.notification.repository.SubscriptionRepository;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;


public class CommandFollowServiceTest extends ServiceTest {

	@Autowired
	private CommandFollowService commandFollowService;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SubscriptionRepository subscriptionRepository;

	@Test
	void 팔로우를_생성한다() {
		// given
		User 팔로우_보낼_유저 = 유저_저장(유저("asdfasdf"));
		User 팔로우_받을_유저 = 유저_저장(유저_이메일("qwerqwer", "test1234@gamil.com"));

		// Subscription 객체 생성
		Subscription mockSubscription = Subscription.builder()
				.user(팔로우_받을_유저)
				.endpoint("endpoint")
				.p256dh("p256dh")
				.auth("auth")
				.build();

		subscriptionRepository.save(mockSubscription);

		// when
		commandFollowService.create(팔로우_보낼_유저.getId(), 팔로우_받을_유저.getId());

		// then
		assertThat(팔로우_조회(팔로우_보낼_유저, 팔로우_받을_유저).isPresent()).isEqualTo(true);
	}

	@Test
	void 팔로우를_삭제한다() {
		// given
		User 팔로우_보낼_유저 = 유저_저장(유저("asdfasdf"));
		User 팔로우_받을_유저 = 유저_저장(유저_이메일("qwerqwer", "test1234@gamil.com"));
		Follow 저장된_팔로우 = 팔로우_저장(new Follow(팔로우_받을_유저, 팔로우_보낼_유저));

		// when
		commandFollowService.delete(팔로우_보낼_유저.getId(), 팔로우_받을_유저.getId());

		// then
		assertThat(팔로우_조회(팔로우_보낼_유저, 팔로우_받을_유저)
				.isPresent()).isEqualTo(false);
	}

	private Follow 팔로우_저장(Follow follow) {
		return followRepository.save(follow);
	}

	private Optional<Follow> 팔로우_조회(User formUser, User toUser) {
		return followRepository.findByToUserAndFromUser(toUser, formUser);
	}

	private User 유저_저장(User user) {
		return userRepository.save(user);
	}

}
