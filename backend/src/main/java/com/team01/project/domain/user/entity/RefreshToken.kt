package com.team01.project.domain.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import java.time.LocalDateTime

@Entity
@Table(name = "REFRESH_TOKEN") // 필요시 주석 해제
open class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    open var id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "USER_ID", nullable = false)
    open var user: User? = null,

    @CreatedDate
    @Column(name = "CREATED_AT")
    open var createdAt: LocalDateTime? = null,

    @Column(name = "SPOTIFY_REFRESH_TOKEN")
    open var spotifyRefreshToken: String? = null,

    @Column(name = "REFRESH_TOKEN")
    open var refreshToken: String? = null
)
