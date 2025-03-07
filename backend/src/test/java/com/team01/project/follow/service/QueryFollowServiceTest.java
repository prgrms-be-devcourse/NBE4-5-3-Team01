package com.team01.project.follow.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.team01.project.common.service.ServiceTest;
import com.team01.project.domain.follow.domain.Follow;
import com.team01.project.domain.follow.repository.FollowRepository;
import com.team01.project.domain.follow.service.QueryFollowService;

public class QueryFollowServiceTest extends ServiceTest {

	@Autowired
	private QueryFollowService queryFollowService;

	@Autowired
	private FollowRepository followRepository;

	@Test
	void 팔로잉_목록을_조회한다() {
		// given
		팔로우_전체_생성(1L, 2L);

		// when
		List<Follow> followings = queryFollowService.findFollowing(0L);

		// then
		assertThat(followings.size()).isEqualTo(2);
	}

	@Test
	void 팔로워_목록을_조회한다() {
		// given
		팔로우_전체_생성(1L, 2L);

		// when
		List<Follow> followers = queryFollowService.findFollower(0L);

		// then
		assertThat(followers.size()).isEqualTo(2);
	}

	private void 팔로우_전체_생성(Long... userIds) {
		for (Long userId : userIds) {
			followRepository.save(new Follow(0L, userId));
			followRepository.save(new Follow(userId, 0L));
		}
	}
}
