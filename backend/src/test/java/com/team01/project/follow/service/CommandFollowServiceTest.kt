package com.team01.project.follow.service

import com.team01.project.common.service.ServiceTest
import com.team01.project.domain.follow.entity.Follow
import com.team01.project.domain.follow.entity.type.Status
import com.team01.project.domain.follow.repository.FollowRepository
import com.team01.project.domain.follow.service.CommandFollowService
import com.team01.project.domain.notification.entity.Subscription
import com.team01.project.domain.notification.repository.SubscriptionRepository
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.user.entity.UserFixture
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.*

class CommandFollowServiceTest : ServiceTest() {
    @Autowired
    private val commandFollowService: CommandFollowService? = null

    @Autowired
    private val followRepository: FollowRepository? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val subscriptionRepository: SubscriptionRepository? = null

    @Test
    fun 팔로우를_생성한다() {
        // given
        val 팔로우_보낼_유저 = 유저_저장(UserFixture.유저("asdfasdf"))
        val 팔로우_받을_유저 = 유저_저장(UserFixture.유저_이메일("qwerqwer", "test1234@gamil.com"))

        // Subscription 객체 생성
        val mockSubscription = Subscription.builder()
            .user(팔로우_받을_유저)
            .endpoint("endpoint")
            .p256dh("p256dh")
            .auth("auth")
            .build()

        subscriptionRepository!!.save(mockSubscription)

        // when
        commandFollowService!!.create(팔로우_보낼_유저.id, 팔로우_받을_유저.id)

        // then
        Assertions.assertThat(팔로우_조회(팔로우_보낼_유저, 팔로우_받을_유저).isPresent).isEqualTo(true)
    }

    @Test
    fun 팔로우를_삭제한다() {
        // given
        val 팔로우_보낼_유저 = 유저_저장(UserFixture.유저("asdfasdf"))
        val 팔로우_받을_유저 = 유저_저장(UserFixture.유저_이메일("qwerqwer", "test1234@gamil.com"))
        val 저장된_팔로우 = 팔로우_저장(Follow(0, Status.ACCEPT, 팔로우_받을_유저, 팔로우_보낼_유저))

        // when
        commandFollowService!!.delete(팔로우_보낼_유저.id, 팔로우_받을_유저.id)

        // then
        Assertions.assertThat(
            팔로우_조회(팔로우_보낼_유저, 팔로우_받을_유저)
                .isPresent
        ).isEqualTo(false)
    }

    private fun 팔로우_저장(follow: Follow): Follow {
        return followRepository!!.save(follow)
    }

    private fun 팔로우_조회(formUser: User, toUser: User): Optional<Follow> {
        return followRepository!!.findByToUserAndFromUser(toUser, formUser)
    }

    private fun 유저_저장(user: User): User {
        return userRepository!!.save(user)
    }
}
