package com.team01.project.follow.service;

import static com.team01.project.user.entity.UserFixture.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.team01.project.common.service.ServiceTest;
import com.team01.project.domain.follow.controller.dto.CountFollowResponse;
import com.team01.project.domain.follow.controller.dto.FollowResponse;
import com.team01.project.domain.follow.entity.Follow;
import com.team01.project.domain.follow.repository.FollowRepository;
import com.team01.project.domain.follow.service.QueryFollowService;
import com.team01.project.domain.user.entity.User;
import com.team01.project.domain.user.repository.UserRepository;

public class QueryFollowServiceTest extends ServiceTest {

	@Autowired
	private QueryFollowService queryFollowService;

	@Autowired
	private FollowRepository followRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void 팔로잉_목록을_조회한다() {
		// given
		User 로그인_유저 = userRepository.save(유저("asdf"));
		User 메인_유저 = userRepository.save(유저("asdfasdf"));
		List<User> 유저들 = 유저_전체_생성();
		팔로우_전체_생성(메인_유저, 유저들);

		// when
		List<FollowResponse> followings = queryFollowService.findFollowing(로그인_유저.getId(), 메인_유저.getId());

		// then
		assertThat(followings.size()).isEqualTo(2);
	}

	@Test
	void 팔로워_목록을_조회한다() {
		// given
		User 로그인_유저 = userRepository.save(유저("asdf"));
		User 메인_유저 = userRepository.save(유저("asdfasdf"));
		List<User> 유저들 = 유저_전체_생성();
		팔로우_전체_생성(메인_유저, 유저들);

		// when
		List<FollowResponse> followers = queryFollowService.findFollower(로그인_유저.getId(), 메인_유저.getId());

		// then
		assertThat(followers.size()).isEqualTo(2);
	}

	@Test
	void 팔로잉_팔로워_수를_조회한다() {
		// given
		User 메인_유저 = userRepository.save(유저("asdfasdf"));
		List<User> 유저들 = 유저_전체_생성();
		팔로우_전체_생성(메인_유저, 유저들);

		// when
		CountFollowResponse count = queryFollowService.findCount(메인_유저.getId());

		// then
		assertAll(
			() -> assertThat(count.followerCount()).isEqualTo(2),
			() -> assertThat(count.followingCount()).isEqualTo(2)
		);
	}

	// @Test
	// void 맞팔로우_여부를_확인한다() {
	// 	// given
	// 	User 메인_유저 = userRepository.save(유저("asdfasdf"));
	// 	User 서브_유저 = userRepository.save(유저_이메일("qwerqewr", "test@gamil.com"));
	// 	팔로우_전체_생성(메인_유저, List.of(서브_유저));
	//
	// 	// when
	// 	Boolean response = queryFollowService.checkMutualFollow(메인_유저.getId(), 서브_유저.getId());
	//
	// 	// then
	// 	assertThat(response).isTrue();
	// }

	private void 팔로우_전체_생성(User mainUser, List<User> users) {
		for (User user : users) {
			followRepository.save(new Follow(mainUser, user)).accept();
			followRepository.save(new Follow(user, mainUser)).accept();
		}
	}

	private List<User> 유저_전체_생성() {
		return userRepository.saveAll(
			List.of(
				유저_이메일("qwerqwer", "qwer@gmail.com"),
				유저_이메일("zxcvzxcv", "zxcv@gamil.com")
			)
		);
	}
}
