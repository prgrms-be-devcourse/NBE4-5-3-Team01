package com.team01.project.domain.follow.service

import com.team01.project.domain.follow.entity.Follow
import com.team01.project.domain.follow.repository.FollowRepository
import com.team01.project.domain.notification.event.NotificationFollowAcceptEvent
import com.team01.project.domain.notification.event.NotificationFollowEvent
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.domain.user.repository.findByIdOrThrow
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalTime

@Service
@Transactional
class CommandFollowService(
    private val followRepository: FollowRepository,
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher
) {

    fun create(fromUserId: String, toUserId: String) {
        val fromUser = userRepository.findByIdOrThrow(fromUserId)
        val toUser = userRepository.findByIdOrThrow(toUserId)

        check(!followRepository.existsByToUserAndFromUser(toUser, fromUser)) { "이미 팔로우 요청을 보냈습니다." }
        followRepository.save(Follow(toUser = toUser, fromUser = fromUser))

        eventPublisher.publishEvent(NotificationFollowEvent(this, LocalTime.now(), toUser, fromUser))
    }

    fun delete(fromUserId: String, toUserId: String) {
        val fromUser = userRepository.findByIdOrThrow(fromUserId)
        val toUser = userRepository.findByIdOrThrow(toUserId)
        val follow = followRepository.findByToUserAndFromUser(toUser, fromUser)
            .orElseThrow { IllegalArgumentException("팔로우를 찾을 수 없습니다.") }

        followRepository.delete(follow)
    }

    fun accept(fromUserId: String, toUserId: String) {
        val fromUser = userRepository.findByIdOrThrow(fromUserId)
        val toUser = userRepository.findByIdOrThrow(toUserId)

        val follow = followRepository.findByToUserAndFromUser(toUser, fromUser)
            .orElseThrow { IllegalArgumentException("팔로우를 찾을 수 없습니다.") }
        follow.accept()

        eventPublisher.publishEvent(NotificationFollowAcceptEvent(this, LocalTime.now(), toUser, fromUser))
    }
}
