package com.team01.project.follow.controller

import com.team01.project.common.acceptance.AcceptanceTest
import com.team01.project.domain.follow.entity.Follow
import com.team01.project.domain.follow.entity.type.Status
import com.team01.project.domain.follow.repository.FollowRepository
import com.team01.project.domain.user.repository.UserRepository
import com.team01.project.global.security.JwtTokenProvider
import com.team01.project.user.entity.UserFixture
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class FollowControllerTest : AcceptanceTest() {

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val followRepository: FollowRepository? = null

    @Autowired
    private val jwtTokenProvider: JwtTokenProvider? = null

    @Test
    fun 팔로우_생성하면_201을_반환한다() {
        // given
        val 팔로우_보낼_유저 = userRepository!!.save(UserFixture.유저("asdfasdf"))
        val 팔로우_받을_유저 = userRepository.save(UserFixture.유저_이메일("qwerqwer", "test1234@gamil.com"))
        val accessToken = jwtTokenProvider!!.generateJwtToken(팔로우_보낼_유저.id, "qwerqwer")

        val 요청_준비 = given(spec).header("Authorization", "Bearer $accessToken")

        // when
        val 응답 = 요청_준비.`when`().post("/follows/{userId}", 팔로우_받을_유저.id)

        // then
        응답.then().statusCode(HttpStatus.CREATED.value())
    }

    @Test
    fun 팔로우를_취소하면_200을_반환한다() {
        // given
        val 팔로우_보낼_유저 = userRepository!!.save(UserFixture.유저("asdfasdf"))
        val 팔로우_받을_유저 = userRepository.save(UserFixture.유저_이메일("qwerqwer", "test1234@gamil.com"))
        val accessToken = jwtTokenProvider!!.generateJwtToken(팔로우_보낼_유저.id, "qwerqwer")
        followRepository!!.save(Follow(0, Status.PENDING, 팔로우_받을_유저, 팔로우_보낼_유저))

        val 요청_준비 = given(spec).header("Authorization", "Bearer $accessToken")

        // when
        val 응답 = 요청_준비.`when`().delete("/follows/delete/{userId}", 팔로우_받을_유저.id)

        // then
        응답.then().statusCode(HttpStatus.OK.value())
    }

    @Nested
    internal inner class 팔로우_조회 {
        @Test
        fun 팔로잉_목록을_조회시_200을_반환한다() {
            // given
            val 팔로우_보낼_유저 = userRepository!!.save(UserFixture.유저("asdfasdf"))
            val 팔로우_받을_유저 = userRepository.save(UserFixture.유저_이메일("qwerqwer", "test1234@gamil.com"))
            val accessToken = jwtTokenProvider!!.generateJwtToken(팔로우_보낼_유저.id, "qwerqwer")
            followRepository!!.save(Follow(0, Status.ACCEPT, 팔로우_받을_유저, 팔로우_보낼_유저))

            val 요청_준비 = given(spec).header("Authorization", "Bearer $accessToken")

            // when
            val 응답 = 요청_준비.`when`().get("/follows/following/{userId}", 팔로우_받을_유저.id)

            // then
            응답.then().statusCode(HttpStatus.OK.value())
        }

        @Test
        fun 팔로워_목록을_조회시_200을_반환한다() {
            // given
            val 팔로우_보낼_유저 = userRepository!!.save(UserFixture.유저("asdfasdf"))
            val 팔로우_받을_유저 = userRepository.save(UserFixture.유저_이메일("qwerqwer", "test1234@gamil.com"))
            val accessToken = jwtTokenProvider!!.generateJwtToken(팔로우_보낼_유저.id, "qwerqwer")
            followRepository!!.save(Follow(0, Status.ACCEPT, 팔로우_받을_유저, 팔로우_보낼_유저))

            val 요청_준비 = given(spec).header("Authorization", "Bearer $accessToken")

            // when
            val 응답 = 요청_준비.`when`().get("/follows/follower/{userId}", 팔로우_받을_유저.id)

            // then
            응답.then().statusCode(HttpStatus.OK.value())
        }
    }
}
