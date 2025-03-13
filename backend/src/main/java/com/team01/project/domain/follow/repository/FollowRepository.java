package com.team01.project.domain.follow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team01.project.domain.follow.entity.Follow;
import com.team01.project.domain.user.entity.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	Optional<Follow> findByToUserAndFromUser(User toUser, User fromUser);

	List<Follow> findByFromUser(User fromUser);

	List<Follow> findByToUser(User toUser);

	boolean existsByToUserAndFromUser(User toUser, User fromUser);

	Long countByFromUser(User fromUser);

	Long countByToUser(User toUser);
}
