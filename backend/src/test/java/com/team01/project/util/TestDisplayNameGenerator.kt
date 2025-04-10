package com.team01.project.util

import org.junit.jupiter.api.DisplayNameGenerator
import java.lang.reflect.Method

/**
 * 테스트 함수 이름을 DisplayName으로 변환
 * ex) fun `캘린더를 아이디로 조회한다`() -> "캘린더를 아이디로 조회한다"
 */
class TestDisplayNameGenerator : DisplayNameGenerator {
    override fun generateDisplayNameForMethod(testClass: Class<*>, testMethod: Method): String {
        return testMethod.name // 함수 이름 그대로 반환
    }

    override fun generateDisplayNameForClass(testClass: Class<*>): String {
        return testClass.simpleName
    }

    override fun generateDisplayNameForNestedClass(nestedClass: Class<*>): String {
        return nestedClass.simpleName
    }
}
