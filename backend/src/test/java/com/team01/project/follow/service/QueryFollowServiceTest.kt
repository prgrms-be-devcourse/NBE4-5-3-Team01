package com.team01.project.follow.service

import com.team01.project.common.service.ServiceTest
import com.team01.project.domain.follow.entity.Follow
import com.team01.project.domain.follow.entity.type.Status
import com.team01.project.domain.follow.repository.FollowRepository
import com.team01.project.domain.follow.service.QueryFollowService
import com.team01.project.domain.user.entity.User
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.user.entity.UserFixture.유저
import com.team01.project.user.entity.UserFixture.유저_이메일
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import org.springframework.beans.factory.annotation.Autowired

class QueryFollowServiceTest : ServiceTest() {
    @Autowired
    private val queryFollowService: QueryFollowService? = null

    @Autowired
    private val followRepository: FollowRepository? = null

    @Autowired
    private val userRepository: UserRepository? = null

    @Test
    fun 팔로잉_목록을_조회한다() {
        // given
        val 로그인_유저: User = userRepository!!.save(유저("asdf"))
        val 메인_유저: User = userRepository.save(유저("asdfasdf"))
        val 유저들 = 유저_전체_생성()
        팔로우_전체_생성(메인_유저, 유저들)

        // when
        val followings = queryFollowService!!.findFollowing(로그인_유저.id, 메인_유저.id)

        // then
        Assertions.assertThat(followings.size).isEqualTo(2)
    }

    @Test
    fun 팔로워_목록을_조회한다() {
        // given
        val 로그인_유저: User = userRepository!!.save(유저("asdf"))
        val 메인_유저: User = userRepository.save(유저("asdfasdf"))
        val 유저들 = 유저_전체_생성()
        팔로우_전체_생성(메인_유저, 유저들)

        // when
        val followers = queryFollowService!!.findFollower(로그인_유저.id, 메인_유저.id)

        // then
        Assertions.assertThat(followers.size).isEqualTo(2)
    }

    @Test
    fun 팔로잉_팔로워_수를_조회한다() {
        // given
        val 메인_유저: User = userRepository!!.save(유저("asdfasdf"))
        val 유저들 = 유저_전체_생성()
        팔로우_전체_생성(메인_유저, 유저들)

        // when
        val count = queryFollowService!!.findCount(메인_유저.id)

        // then
        org.junit.jupiter.api.Assertions.assertAll(
            Executable { Assertions.assertThat(count.followerCount).isEqualTo(2) },
            Executable { Assertions.assertThat(count.followingCount).isEqualTo(2) }
        )
    }

    private fun 팔로우_전체_생성(mainUser: User, users: List<User>) {
        for (user in users) {
            followRepository!!.save(Follow(0, Status.ACCEPT, mainUser, user))
            followRepository.save(Follow(0, Status.ACCEPT, user, mainUser))
        }
    }

    private fun 유저_전체_생성(): List<User> {
        return userRepository!!.saveAll(
            java.util.List.of(
                유저_이메일("qwerqwer", "qwer@gmail.com"),
                유저_이메일("zxcvzxcv", "zxcv@gamil.com")
            )
        )
    }
}
