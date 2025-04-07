package com.team01.project.domain.follow.service

import com.team01.project.domain.follow.controller.dto.CountFollowResponse
import com.team01.project.domain.follow.controller.dto.FollowResponse
import com.team01.project.domain.follow.entity.Follow
import com.team01.project.domain.follow.entity.type.Status
import com.team01.project.domain.follow.repository.FollowRepository
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.UserRepository
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class QueryFollowService(
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository
) {

    fun findFollowing(currentUserId: String, userId: String): List<FollowResponse> {
        val currentUser = userRepository.getById(currentUserId)
        val user = userRepository.getById(userId)

        val list = followRepository.findByFromUserAndStatus(user, Status.ACCEPT).stream()
            .filter { follow: Follow -> follow.toUser.id != currentUser.id }
            .map { follow: Follow ->
                FollowResponse.of(
                    follow.toUser,
                    checkFollow(follow.toUser, currentUser),
                    checkFollow(currentUser, follow.toUser)
                )
            }
            .toList()

        println(list)

        return list
    }

    fun findFollower(currentUserId: String, userId: String): List<FollowResponse> {
        val currentUser = userRepository.getById(currentUserId)
        val user = userRepository.getById(userId)

        return followRepository.findByToUserAndStatus(user, Status.ACCEPT).stream()
            .filter { follow: Follow -> follow.fromUser.id != currentUser.id }
            .map { follow: Follow ->
                FollowResponse.of(
                    follow.fromUser,
                    checkFollow(follow.fromUser, currentUser),
                    checkFollow(currentUser, follow.fromUser)
                )
            }
            .toList()
    }

    fun findCount(userId: String): CountFollowResponse {
        val user = userRepository.getById(userId)
        val followingCount = followRepository.countByFromUserAndStatus(user, Status.ACCEPT)
        val followerCount = followRepository.countByToUserAndStatus(user, Status.ACCEPT)

        return CountFollowResponse.of(followingCount, followerCount)
    }

    private fun checkFollow(user: User, currentUser: User): Status {
        return followRepository.findStatusByToUserAndFromUser(user, currentUser)
            .orElse(Status.NONE)
    }

    fun findMyFollowing(currentUserId: String): List<FollowResponse> {
        val currentUser = userRepository.getById(currentUserId)

        return followRepository.findByFromUser(currentUser)
            .stream()
            .map { follow: Follow -> FollowResponse.of(follow.toUser, follow.status, Status.NONE) }
            .toList()
    }

    fun findPendingList(currentUserId: String): List<FollowResponse> {
        val currentUser = userRepository.getById(currentUserId)

        return followRepository.findByToUserAndStatus(currentUser, Status.PENDING).stream()
            .map { follow: Follow -> FollowResponse.of(follow.fromUser, Status.NONE, follow.status) }
            .toList()
    }
}
