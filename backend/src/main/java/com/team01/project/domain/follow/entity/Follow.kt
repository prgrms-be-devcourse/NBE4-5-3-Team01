package com.team01.project.domain.follow.entity

import com.team01.project.domain.follow.entity.type.Status
import com.team01.project.domain.follow.entity.type.Status.ACCEPT
import com.team01.project.domain.follow.entity.type.Status.PENDING
import com.team01.project.domain.user.entity.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Follow(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    val id: Long = 0,

    @Enumerated(value = EnumType.STRING)
    var status: Status = PENDING,

    @ManyToOne
    @JoinColumn(name = "to_user_id")
    val toUser: User,

    @ManyToOne
    @JoinColumn(name = "from_user_id")
    val fromUser: User
) {

    fun accept() {
        this.status = ACCEPT
    }
}
