package com.example.tasks.userservice.integration.utils;

import com.example.tasks.userservice.dto.CardResponseDto;
import com.example.tasks.userservice.dto.UserRequestDto;
import com.example.tasks.userservice.dto.UserResponseDto;
import com.example.tasks.userservice.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestUserFactory {

	public static User createUserEntity() {
		User user = new User();
		user.setName("testName");
		user.setSurname("testSurname");
		user.setEmail("testNametestSurname@example.com");
		user.setBirthDate(LocalDate.of(1990, 1, 1));
		return user;
	}

	public static UserRequestDto createUserRequestDto() {
		UserRequestDto userRequestDto = new UserRequestDto();
		userRequestDto.setName("testName");
		userRequestDto.setSurname("testSurname");
		userRequestDto.setEmail("testNametestSurname@example.com");
		userRequestDto.setBirthDate(LocalDate.of(1990, 1, 1));
		return userRequestDto;
	}

	public static UserRequestDto createUserRequestDto(String email) {
		UserRequestDto dto = new UserRequestDto();
		dto.setName("testName");
		dto.setSurname("testSurname");
		dto.setEmail(email);
		dto.setBirthDate(LocalDate.of(1990, 1, 1));
		return dto;
	}

	public static UserResponseDto createUserResponseDto() {
		CardResponseDto cardResponseDto = new CardResponseDto();
		cardResponseDto.setId(1L);
		cardResponseDto.setLastFourDigits("1111");
		cardResponseDto.setMaskedHolder("testName t**********");
		cardResponseDto.setExpirationDate(LocalDate.now().plusYears(1L));
		List<CardResponseDto> cards = new ArrayList<>();
		cards.add(cardResponseDto);
		UserResponseDto userResponseDto = new UserResponseDto();
		userResponseDto.setId(1L);
		userResponseDto.setName("testName");
		userResponseDto.setSurname("testSurname");
		userResponseDto.setMaskedEmail("t***************m@example.com");
		userResponseDto.setBirthDate(LocalDate.of(1990, 1, 1));
		userResponseDto.setCards(cards);
		return userResponseDto;
	}

	public static UserResponseDto createUserResponseDto(Long id, String maskedEmail) {
		UserResponseDto userResponseDto = createUserResponseDto();
		userResponseDto.setId(id);
		userResponseDto.setMaskedEmail(maskedEmail);
		return userResponseDto;
	}

	public static String maskEmail(String email) {
		if (email == null || email.length() < 5) return email;
		int atIndex = email.indexOf('@');
		if (atIndex < 2) return email;
		return email.charAt(0) + "***" + email.substring(atIndex - 1);
	}
}