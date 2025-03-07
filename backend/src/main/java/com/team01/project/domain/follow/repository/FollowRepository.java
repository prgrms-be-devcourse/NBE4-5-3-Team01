package com.team01.project.domain.follow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team01.project.domain.follow.domain.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	Optional<Follow> findByToUserIdAndFromUserId(Long toUserId, Long fromUserID);

	List<Follow> findByFromUserId(Long fromUserId);

	List<Follow> findByToUserId(Long toUserId);
}
