package com.team01.project.domain.follow.entity;

import com.team01.project.domain.follow.entity.type.Status;
import com.team01.project.domain.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Follow {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "follow_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "to_user_id")
	private User toUser;

	@ManyToOne
	@JoinColumn(name = "from_user_id")
	private User fromUser;

	@Enumerated(value = EnumType.STRING)
	private Status status;

	public Follow(User toUser, User fromUser) {
		this.toUser = toUser;
		this.fromUser = fromUser;
		this.status = Status.PENDING;
	}

	public void accept() {
		this.status = Status.ACCEPT;
	}
}
