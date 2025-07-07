package com.example.tasks.userservice.service;

import com.example.tasks.userservice.mapper.CardMapper;
import com.example.tasks.userservice.dto.CardRequestDto;
import com.example.tasks.userservice.dto.CardResponseDto;
import com.example.tasks.userservice.exception.CardNotFoundException;
import com.example.tasks.userservice.exception.CardNotOwnedException;
import com.example.tasks.userservice.exception.DuplicateCardException;
import com.example.tasks.userservice.exception.UserNotFoundException;
import com.example.tasks.userservice.model.Card;
import com.example.tasks.userservice.model.User;
import com.example.tasks.userservice.repository.CardRepository;
import com.example.tasks.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {
	private final UserRepository userRepository;
	private final CardRepository cardRepository;
	private final CardMapper cardMapper;

	@Override
	@Transactional
	@CacheEvict(value = "users", key = "#p0")
	public CardResponseDto createCard(Long userId, CardRequestDto cardRequestDto) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
		if (cardRepository.existsByUserAndNumber(user, cardRequestDto.getNumber())) {
			throw new DuplicateCardException("Card with number " + cardRequestDto.getNumber() + " already exists for this user");
		}
		Card card = cardMapper.toEntity(cardRequestDto);
		card.setUser(user);
		Card savedCard = cardRepository.save(card);
		return cardMapper.toResponseDto(savedCard);
	}

	@Override
	@Transactional
	@CacheEvict(value = "cards", key = "#p0 + '_' + #p1")
	public CardResponseDto updateCard(Long userId, Long cardId, CardRequestDto cardRequestDto) {
		Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("Card not found with cardId: " + cardId));
		if (!card.getUser().getId().equals(userId)) {
			throw new CardNotOwnedException("Card with id " + cardId + " does not belong to user " + userId);
		}
		cardMapper.updateFromDto(card, cardRequestDto);
		Card updatedCard = cardRepository.save(card);
		return cardMapper.toResponseDto(updatedCard);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "cards", key = "#p0 + '_' + #p1")
	public CardResponseDto getCardByUserIdAndCardId(Long userId, Long cardId) {
		Card card = cardRepository.findByUserIdAndId(userId, cardId)
				.orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId + " for user: " + userId));
		return cardMapper.toResponseDto(card);
	}

	@Override
	@Transactional(readOnly = true)
	public List<CardResponseDto> getAllCardsByUserId(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}
		return cardRepository.findAllByUserId(userId).stream().map(cardMapper::toResponseDto).toList();
	}

	@Override
	@Transactional
	@CacheEvict(value = "cards", key = "#p0 + '_' + #p1")
	public void deleteCard(Long userId, Long cardId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException("User not found with id: " + userId);
		}
		Card card = cardRepository.findById(cardId)
				.orElseThrow(() -> new CardNotFoundException("Card not found with id: " + cardId));
		if (!card.getUser().getId().equals(userId)) {
			throw new CardNotOwnedException("Card with id " + cardId + " does not belong to user " + userId);
		}
		cardRepository.delete(card);
	}
}
