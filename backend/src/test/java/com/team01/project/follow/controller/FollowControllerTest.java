package com.team01.project.follow.controller;

import static io.restassured.RestAssured.*;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.team01.project.common.acceptance.AcceptanceTest;
import com.team01.project.domain.follow.domain.Follow;
import com.team01.project.domain.follow.repository.FollowRepository;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class FollowControllerTest extends AcceptanceTest {

	@Autowired
	private FollowRepository followRepository;

	@Test
	void 팔로우_생성하면_201을_반환한다() {
		// given
		RequestSpecification 요청_준비 = given(spec);

		// when
		Response 응답 = 요청_준비.when().post("/follows/1");

		// then
		응답.then().statusCode(CREATED.value());
	}

	@Test
	void 팔로우를_취소하면_200을_반환한다() {
		// given
		followRepository.save(new Follow(1L, 0L));
		RequestSpecification 요청_준비 = given(spec);

		// when
		Response 응답 = 요청_준비.when().delete("/follows/1");

		// then
		응답.then().statusCode(OK.value());
	}

	@Nested
	class 팔로우_조회 {

		@Test
		void 팔로잉_목록을_조회시_200을_반환한다() {
			// given
			followRepository.save(new Follow(0L, 1L));
			RequestSpecification 요청_준비 = given(spec);

			// when
			Response 응답 = 요청_준비.when().get("/follows/following/1");

			// then
			응답.then().statusCode(OK.value());
		}

		@Test
		void 팔로워_목록을_조회시_200을_반환한다() {
			// given
			followRepository.save(new Follow(1L, 0L));
			RequestSpecification 요청_준비 = given(spec);

			// when
			Response 응답 = 요청_준비.when().get("/follows/follower/1");

			// then
			응답.then().statusCode(OK.value());
		}
	}
}
