package com.example.tasks.userservice.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
	@NotBlank(message = "Name is mandatory field!")
	@Size(min = 2, max = 50, message = "Name length should be in range 2-50!")
	private String name;
	@NotBlank(message = "Surname is mandatory field!")
	@Size(min = 2, max = 50, message = "Surname length should be in range 2-50!")
	private String surname;
	@NotNull(message = "Birth date should be present!")
	@Past(message = "Birth date should be in the past!")
	private LocalDate birthDate;
	@Email(message = "Incorrect format", regexp = "^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")
	@Size(max = 100, message = "email too long!")
	private String email;
}
