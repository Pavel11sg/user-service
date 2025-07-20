package com.example.tasks.userservice.integration.repository;

import com.example.tasks.userservice.model.User;
import com.example.tasks.userservice.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
	@Autowired
	private UserRepository userRepository;
	@AfterEach
	void tearDown() {
		userRepository.deleteAll();
	}
	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	@Test
	void findUsersBornBetween_ShouldFilterByDateRange() {
		User user1 = new User();
		user1.setName("John");
		user1.setSurname("Doe");
		user1.setEmail("test1@example.com");
		user1.setBirthDate(LocalDate.of(1990, 1, 1));
		userRepository.save(user1);

		User user2 = new User();
		user2.setName("Ben");
		user2.setSurname("Parker");
		user2.setEmail("test2@example.com");
		user2.setBirthDate(LocalDate.of(2000, 1, 1));
		userRepository.save(user2);

		List<User> users = userRepository.findUsersBornBetween(
				LocalDate.of(1980, 1, 1),
				LocalDate.of(1995, 1, 1)
		);

		assertThat(users).hasSize(1);
		assertThat(users.get(0).getBirthDate()).isEqualTo("1990-01-01");
	}

	@Test
	void findByEmailEndingWith_ShouldFilterByDomain() {
		User user = new User();
		user.setName("John");
		user.setSurname("Doe");
		user.setBirthDate(LocalDate.of(1990,1,1));
		user.setEmail("user@test.com");
		userRepository.save(user);

		List<User> users = userRepository.findByEmailEndingWith("test.com");

		assertThat(users).hasSize(1);
		assertThat(users.get(0).getEmail()).endsWith("@test.com");
	}
}