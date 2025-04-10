package com.team01.project.user.entity

import com.team01.project.domain.user.entity.User

object UserFixture {
    fun 유저(id: String): User {
        return User(id = id, name = "이름", email = "test@gamil.com")
    }

    fun 유저_이메일(id: String, email: String): User {
        return User(id = id, name = "이름", email = email)
    }
}
