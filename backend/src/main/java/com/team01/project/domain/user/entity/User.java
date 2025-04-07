package com.team01.project.domain.user.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.CreatedDate;

import com.team01.project.domain.notification.entity.Notification;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "user_tbl")
public class User {
	@Id
	@Column(name = "user_id")
	private String id;

	private String userPassword;

	@Email
	private String email;

	private String name;

	private String originalName;

	private String userIntro;

	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String image;

	@Column(name = "birthday")
	private LocalDate birthDay;

	@CreatedDate
	@Column(name = "create_at")
	private LocalDateTime createdDate;

	private String field;

	@Column(name = "calendar_visibility", nullable = false)
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private CalendarVisibility calendarVisibility = CalendarVisibility.PUBLIC;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private RefreshToken refreshTokens;

	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notification> notifications = new ArrayList<>();

	public void updateCalendarVisibility(CalendarVisibility newCalendarVisibility) {
		if (this.calendarVisibility != newCalendarVisibility) {
			this.calendarVisibility = newCalendarVisibility;
		}
	}

	@Override
	public boolean equals(Object object) {
		if (object == null || getClass() != object.getClass()) {
			return false;
		}

		User user = (User)object;

		return Objects.equals(id, user.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}