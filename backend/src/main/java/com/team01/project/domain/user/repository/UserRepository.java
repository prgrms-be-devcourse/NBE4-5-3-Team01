package com.team01.project.domain.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.team01.project.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	List<User> findAll();

	@Query("SELECT u FROM User u WHERE u.name LIKE %:q% OR u.originalName LIKE %:q%")
	List<User> searchUser(@Param("q") String name);

	default User getById(String userId) {
		return findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
	}

	boolean existsById(String id);
}
