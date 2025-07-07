package com.example.tasks.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "card_info")
@Getter
@Setter
public class Card {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "number", length = 19, nullable = false)
	private String number;
	@Column(name = "holder", nullable = false)
	private String holder;
	@Column(name = "expiration_date", nullable = false)
	private LocalDate expirationDate;
	@ManyToOne(optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
}
