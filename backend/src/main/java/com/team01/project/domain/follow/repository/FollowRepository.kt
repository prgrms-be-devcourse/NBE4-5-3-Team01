package com.team01.project.domain.follow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team01.project.domain.follow.entity.Follow;
import com.team01.project.domain.follow.entity.type.Status;
import com.team01.project.domain.user.entity.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {
	Optional<Follow> findByToUserAndFromUser(User toUser, User fromUser);

	List<Follow> findByFromUserAndStatus(User fromUser, Status status);

	List<Follow> findByFromUser(User fromUser);

	List<Follow> findByToUserAndStatus(User toUser, Status status);

	boolean existsByToUserAndFromUser(User toUser, User fromUser);

	boolean existsByToUserAndFromUserAndStatus(User toUser, User fromUser, Status status);

	Long countByFromUserAndStatus(User fromUser, Status status);

	Long countByToUserAndStatus(User toUser, Status status);

	@Query("SELECT f.status FROM Follow f WHERE f.toUser = :toUser AND f.fromUser = :fromUser")
	Optional<Status> findStatusByToUserAndFromUser(@Param("toUser") User toUser, @Param("fromUser") User fromUser);
}
