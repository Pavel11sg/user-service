package com.example.tasks.userservice.repository;

import com.example.tasks.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findById(Long id);

	List<User> findAllById(Iterable<Long> ids);

	Optional<User> findByEmail(String email);

	void deleteById(Long id);

	boolean existsByEmail(String email);

	@Query("select u from User u where u.birthDate between :start and :end")
	List<User> findUsersBornBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

	@Query(value = "select * from users where email like '%@' || :domain", nativeQuery = true)
	List<User> findByEmailEndingWith(@Param("domain") String domain);
}
