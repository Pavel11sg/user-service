package com.example.tasks.userservice.service;

import com.example.tasks.userservice.dto.CardRequestDto;
import com.example.tasks.userservice.dto.CardResponseDto;

import java.util.List;

public interface CardService {
	CardResponseDto createCard(Long userId, CardRequestDto cardRequestDto);
	CardResponseDto updateCard(Long userId, Long cardId, CardRequestDto cardRequestDto);
	CardResponseDto getCardByUserIdAndCardId(Long UserId, Long CardId);
	List<CardResponseDto> getAllCardsByUserId(Long userId);
	void deleteCard(Long userId, Long cardId);
}
