package com.example.tasks.userservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardResponseDto {
	private Long id;
	private String lastFourDigits;
	private String maskedHolder;
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate expirationDate;
}
