package com.example.tasks.userservice.integration.utils;

import com.example.tasks.userservice.dto.CardRequestDto;
import com.example.tasks.userservice.dto.CardResponseDto;
import com.example.tasks.userservice.model.Card;

import java.time.LocalDate;

public class TestCardFactory {
	public static Card createCardEntity() {
		Card card = new Card();
		card.setNumber("1111111111111111");
		card.setHolder("TEST USER");
		card.setExpirationDate(LocalDate.now().plusYears(1));
		return card;
	}

	public static Card createCardEntity(String carNumber) {
		Card card = createCardEntity();
		card.setNumber(carNumber);
		return card;
	}

	public static CardRequestDto createCardRequestDto() {
		CardRequestDto cardRequestDto = new CardRequestDto();
		cardRequestDto.setHolder("TEST USER");
		cardRequestDto.setNumber("1111111111111117");
		cardRequestDto.setExpirationDate(LocalDate.now().plusYears(1));
		return cardRequestDto;
	}

	public static CardRequestDto createCardRequestDto(String holder) {
		CardRequestDto cardRequestDto = createCardRequestDto();
		cardRequestDto.setHolder(holder);
		return cardRequestDto;
	}

	public static CardRequestDto createCardRequestDto(String holder, String number) {
		CardRequestDto cardRequestDto = createCardRequestDto();
		cardRequestDto.setHolder(holder);
		cardRequestDto.setNumber(number);
		return cardRequestDto;
	}

	public static CardRequestDto createCardRequestDto(LocalDate expirationDate) {
		CardRequestDto cardRequestDto = createCardRequestDto();
		cardRequestDto.setExpirationDate(expirationDate);
		return cardRequestDto;
	}

	public static CardResponseDto createCardResponseDto() {
		CardResponseDto cardResponseDto = new CardResponseDto();
		cardResponseDto.setMaskedHolder("TEST U***");
		cardResponseDto.setLastFourDigits("1117");
		cardResponseDto.setExpirationDate(LocalDate.now().plusYears(1));
		return cardResponseDto;
	}

	public static CardResponseDto createCardResponseDto(Long cardId) {
		CardResponseDto cardResponseDto = createCardResponseDto();
		cardResponseDto.setId(cardId);
		return cardResponseDto;
	}

	public static CardResponseDto createCardResponseDto(Long cardId, String maskedHolder) {
		CardResponseDto cardResponseDto = createCardResponseDto();
		cardResponseDto.setId(cardId);
		cardResponseDto.setMaskedHolder(maskedHolder);
		return cardResponseDto;
	}

	public static CardResponseDto createCardResponseDto(String lastFourDigits) {
		CardResponseDto cardResponseDto = createCardResponseDto();
		cardResponseDto.setLastFourDigits(lastFourDigits);
		return cardResponseDto;
	}

	public static CardResponseDto createCardResponseDto(Long cardId, LocalDate expirationDate) {
		CardResponseDto cardResponseDto = createCardResponseDto();
		cardResponseDto.setId(cardId);
		cardResponseDto.setExpirationDate(expirationDate);
		return cardResponseDto;
	}

	public static String maskHolderMethod(String holder) {
		if (holder == null) return null;
		String[] parts = holder.split(" ");
		if (parts.length == 0) return holder;
		StringBuilder masked = new StringBuilder(parts[0]);
		for (int i = 1; i < parts.length; i++) {
			if (!parts[i].isEmpty()) {
				masked.append(" ").append(parts[i].charAt(0)).append("***");
			}
		}
		return masked.toString();
	}
}
