package com.example.tasks.userservice.unit;

import com.example.tasks.userservice.dto.CardRequestDto;
import com.example.tasks.userservice.dto.CardResponseDto;
import com.example.tasks.userservice.exception.CardNotFoundException;
import com.example.tasks.userservice.exception.CardNotOwnedException;
import com.example.tasks.userservice.exception.DuplicateCardException;
import com.example.tasks.userservice.exception.UserNotFoundException;
import com.example.tasks.userservice.mapper.CardMapper;
import com.example.tasks.userservice.model.Card;
import com.example.tasks.userservice.model.User;
import com.example.tasks.userservice.repository.CardRepository;
import com.example.tasks.userservice.repository.UserRepository;
import com.example.tasks.userservice.service.CardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private CardRepository cardRepository;

	@Mock
	private CardMapper cardMapper;

	@InjectMocks
	private CardServiceImpl cardService;

	private User createUser() {
		User user = new User();
		user.setId(1L);
		return user;
	}

	private Card createCard() {
		Card card = new Card();
		card.setId(1L);
		card.setNumber("1234567890123456");
		card.setHolder("IVAN PETROV");
		card.setExpirationDate(LocalDate.now().plusYears(1));
		card.setUser(createUser());
		return card;
	}

	private CardRequestDto createCardRequestDto() {
		CardRequestDto request = new CardRequestDto();
		request.setNumber("1234567890123456");
		request.setHolder("IVAN PETROV");
		request.setExpirationDate(LocalDate.now().plusYears(1));
		return request;
	}

	private CardResponseDto createCardResponseDto() {
		CardResponseDto response = new CardResponseDto();
		response.setId(1L);
		response.setLastFourDigits("3456");
		response.setMaskedHolder("IVAN P***");
		response.setExpirationDate(LocalDate.now().plusYears(1));
		return response;
	}

	@Test
	void createCard_shouldReturnCardResponse_whenDataValid() {
		User user = createUser();
		CardRequestDto request = createCardRequestDto();
		Card card = createCard();
		Card savedCard = createCard();
		CardResponseDto expectedResponse = createCardResponseDto();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(cardRepository.existsByUserAndNumber(user, "1234567890123456")).thenReturn(false);
		when(cardMapper.toEntity(request)).thenReturn(card);
		when(cardRepository.save(card)).thenReturn(savedCard);
		when(cardMapper.toResponseDto(savedCard)).thenReturn(expectedResponse);

		CardResponseDto result = cardService.createCard(1L, request);

		assertNotNull(result);
		assertEquals(expectedResponse, result);
		verify(userRepository).findById(1L);
		verify(cardRepository).existsByUserAndNumber(user, "1234567890123456");
		verify(cardMapper).toEntity(request);
		verify(cardRepository).save(card);
		verify(cardMapper).toResponseDto(savedCard);
	}

	@Test
	void createCard_shouldThrowUserNotFoundException_whenUserNotExists() {
		CardRequestDto request = createCardRequestDto();
		when(userRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(UserNotFoundException.class, () -> cardService.createCard(1L, request));
		verify(userRepository).findById(1L);
		verifyNoMoreInteractions(userRepository);
		verifyNoInteractions(cardRepository, cardMapper);
	}

	@Test
	void createCard_shouldThrowDuplicateCardException_whenCardExists() {
		User user = createUser();
		CardRequestDto request = createCardRequestDto();

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		when(cardRepository.existsByUserAndNumber(user, "1234567890123456")).thenReturn(true);

		assertThrows(DuplicateCardException.class, () -> cardService.createCard(1L, request));
		verify(userRepository).findById(1L);
		verify(cardRepository).existsByUserAndNumber(user, "1234567890123456");
		verifyNoMoreInteractions(cardRepository);
		verifyNoInteractions(cardMapper);
	}

	@Test
	void updateCard_shouldReturnUpdatedCard_whenDataValid() {
		Card card = createCard();
		CardRequestDto request = createCardRequestDto();
		CardResponseDto expectedResponse = createCardResponseDto();

		when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
		when(cardRepository.save(card)).thenReturn(card);
		when(cardMapper.toResponseDto(card)).thenReturn(expectedResponse);

		CardResponseDto result = cardService.updateCard(1L, 1L, request);

		assertNotNull(result);
		assertEquals(expectedResponse, result);
		verify(cardRepository).findById(1L);
		verify(cardMapper).updateFromDto(card, request);
		verify(cardRepository).save(card);
		verify(cardMapper).toResponseDto(card);
	}

	@Test
	void updateCard_shouldThrowCardNotFoundException_whenCardNotExists() {
		CardRequestDto request = createCardRequestDto();
		when(cardRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(CardNotFoundException.class, () -> cardService.updateCard(1L, 1L, request));
		verify(cardRepository).findById(1L);
		verifyNoMoreInteractions(cardRepository);
		verifyNoInteractions(cardMapper);
	}

	@Test
	void updateCard_shouldThrowCardNotOwnedException_whenUserNotOwner() {
		Card card = createCard();
		card.getUser().setId(2L);
		CardRequestDto request = createCardRequestDto();

		when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

		assertThrows(CardNotOwnedException.class, () -> cardService.updateCard(1L, 1L, request));
		verify(cardRepository).findById(1L);
		verifyNoMoreInteractions(cardRepository);
		verifyNoInteractions(cardMapper);
	}

	@Test
	void getCardByUserIdAndCardId_shouldReturnCard_whenExists() {
		Card card = createCard();
		CardResponseDto expectedResponse = createCardResponseDto();

		when(cardRepository.findByUserIdAndId(1L, 1L)).thenReturn(Optional.of(card));
		when(cardMapper.toResponseDto(card)).thenReturn(expectedResponse);

		CardResponseDto result = cardService.getCardByUserIdAndCardId(1L, 1L);

		assertNotNull(result);
		assertEquals(expectedResponse, result);
		verify(cardRepository).findByUserIdAndId(1L, 1L);
		verify(cardMapper).toResponseDto(card);
	}

	@Test
	void getCardByUserIdAndCardId_shouldThrowCardNotFoundException_whenNotExists() {
		when(cardRepository.findByUserIdAndId(1L, 1L)).thenReturn(Optional.empty());

		assertThrows(CardNotFoundException.class, () -> cardService.getCardByUserIdAndCardId(1L, 1L));
		verify(cardRepository).findByUserIdAndId(1L, 1L);
		verifyNoInteractions(cardMapper);
	}

	@Test
	void getAllCardsByUserId_shouldReturnCardsList_whenUserExists() {
		Card card = createCard();
		CardResponseDto response = createCardResponseDto();
		List<Card> cards = List.of(card);

		when(userRepository.existsById(1L)).thenReturn(true);
		when(cardRepository.findAllByUserId(1L)).thenReturn(cards);
		when(cardMapper.toResponseDto(card)).thenReturn(response);

		List<CardResponseDto> result = cardService.getAllCardsByUserId(1L);

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(response, result.get(0));
		verify(userRepository).existsById(1L);
		verify(cardRepository).findAllByUserId(1L);
		verify(cardMapper).toResponseDto(card);
	}

	@Test
	void getAllCardsByUserId_shouldThrowUserNotFoundException_whenUserNotExists() {
		when(userRepository.existsById(1L)).thenReturn(false);

		assertThrows(UserNotFoundException.class, () -> cardService.getAllCardsByUserId(1L));
		verify(userRepository).existsById(1L);
		verifyNoInteractions(cardRepository, cardMapper);
	}

	@Test
	void deleteCard_shouldDeleteCard_whenUserIsOwner() {
		Card card = createCard();

		when(userRepository.existsById(1L)).thenReturn(true);
		when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
		doNothing().when(cardRepository).delete(card);

		cardService.deleteCard(1L, 1L);

		verify(userRepository).existsById(1L);
		verify(cardRepository).findById(1L);
		verify(cardRepository).delete(card);
	}

	@Test
	void deleteCard_shouldThrowUserNotFoundException_whenUserNotExists() {
		when(userRepository.existsById(1L)).thenReturn(false);

		assertThrows(UserNotFoundException.class, () -> cardService.deleteCard(1L, 1L));
		verify(userRepository).existsById(1L);
		verifyNoInteractions(cardRepository);
	}

	@Test
	void deleteCard_shouldThrowCardNotFoundException_whenCardNotExists() {
		when(userRepository.existsById(1L)).thenReturn(true);
		when(cardRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(CardNotFoundException.class, () -> cardService.deleteCard(1L, 1L));
		verify(userRepository).existsById(1L);
		verify(cardRepository).findById(1L);
		verifyNoMoreInteractions(cardRepository);
	}

	@Test
	void deleteCard_shouldThrowCardNotOwnedException_whenUserNotOwner() {
		Card card = createCard();
		card.getUser().setId(2L);

		when(userRepository.existsById(1L)).thenReturn(true);
		when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

		assertThrows(CardNotOwnedException.class, () -> cardService.deleteCard(1L, 1L));
		verify(userRepository).existsById(1L);
		verify(cardRepository).findById(1L);
		verifyNoMoreInteractions(cardRepository);
	}
}
