package com.team01.project.common.acceptance;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:truncate.sql"}, executionPhase = AFTER_TEST_METHOD)
public abstract class AcceptanceTest {

	protected RequestSpecification spec;

	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp() {
		spec = new RequestSpecBuilder()
			.setPort(port)
			.addHeader("Content-Type", "application/json")
			.build();
	}
}
