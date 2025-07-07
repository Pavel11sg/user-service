package com.example.tasks.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "name", nullable = false)
	private String name;
	@Column(name = "surname", nullable = false)
	private String surname;
	@Column(name = "birth_date")
	private LocalDate birthDate;
	@Column(name = "email", unique = true, nullable = false)
	private String email;
	@OneToMany(mappedBy = "user",
			cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
			orphanRemoval = true)
	private List<Card> cards;
}
