package com.example.tasks.userservice.integration.controller;

import com.example.tasks.userservice.controller.CardController;
import com.example.tasks.userservice.dto.CardRequestDto;
import com.example.tasks.userservice.dto.CardResponseDto;
import com.example.tasks.userservice.integration.utils.TestCardFactory;
import com.example.tasks.userservice.service.CardService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(MockitoExtension.class)
@WebMvcTest(CardController.class)
class CardControllerTest {
	@MockitoBean
	private CardService cardService;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@Test
	void createCard_ShouldReturnCreatedCard() throws Exception {
		Long userId = 1L;
		CardRequestDto requestDto = TestCardFactory.createCardRequestDto();
		CardResponseDto expectedResponse = TestCardFactory.createCardResponseDto();

		when(cardService.createCard(eq(userId), any(CardRequestDto.class))).thenReturn(expectedResponse);

		String responseJson = mockMvc.perform(post("/userservice/users/{userId}/cards", userId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(requestDto)))
				.andExpect(status().isCreated())
				.andReturn()
				.getResponse()
				.getContentAsString();

		CardResponseDto actualResponse = objectMapper.readValue(responseJson, CardResponseDto.class);
		assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
		verify(cardService).createCard(eq(userId), any(CardRequestDto.class));
	}

	@Test
	void updateCard_ShouldUpdateExistingCard() throws Exception {
		Long userId = 1L;
		Long cardId = 1L;
		CardRequestDto updatedRequestDto = TestCardFactory.createCardRequestDto();
		updatedRequestDto.setHolder("JOHN CONOR");
		CardResponseDto expectedResponse = TestCardFactory.createCardResponseDto();
		expectedResponse.setMaskedHolder(TestCardFactory.maskHolderMethod("JOHN CONOR"));

		when(cardService.updateCard(eq(userId), eq(cardId), any(CardRequestDto.class))).thenReturn(expectedResponse);

		String responseJson = mockMvc.perform(put(("/userservice/users/{userId}/cards/{cardId}"), userId, cardId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updatedRequestDto)))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		CardResponseDto actualResponse = objectMapper.readValue(responseJson, CardResponseDto.class);
		assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
		verify(cardService).updateCard(eq(userId), eq(cardId), any(CardRequestDto.class));
	}

	@Test
	void getCard_ShouldReturnCardIfExists() throws Exception {
		Long userId = 1L;
		Long cardId = 1L;
		CardResponseDto expectedResponse = TestCardFactory.createCardResponseDto();

		when(cardService.getCardByUserIdAndCardId(userId, cardId)).thenReturn(expectedResponse);

		String responseJson = mockMvc.perform(get("/userservice/users/{userId}/cards/{cardId}", userId, cardId))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		CardResponseDto actualResponse = objectMapper.readValue(responseJson, CardResponseDto.class);
		assertThat(actualResponse).usingRecursiveComparison().isEqualTo(expectedResponse);
		verify(cardService).getCardByUserIdAndCardId(eq(userId), eq(cardId));
	}

	@Test
	void getCards_ShouldReturnAllAvailableCardsForUser() throws Exception {
		Long userId = 1L;
		CardResponseDto card1 = TestCardFactory.createCardResponseDto(1L, TestCardFactory.maskHolderMethod("JOHN DOE"));
		CardResponseDto card2 = TestCardFactory.createCardResponseDto(2L, TestCardFactory.maskHolderMethod("ALEX ALEXANDROV"));
		List<CardResponseDto> cards = List.of(card1, card2);

		when(cardService.getAllCardsByUserId(userId)).thenReturn(cards);

		String responseJson = mockMvc.perform(get(("/userservice/users/{userId}/cards"), userId))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();

		List<CardResponseDto> actualResponse = objectMapper.readValue(responseJson, new TypeReference<List<CardResponseDto>>() {
		});
		assertThat(actualResponse)
				.usingRecursiveComparison()
				.isEqualTo(cards);
	}

	@Test
	void deleteCard_ShouldRemoveCardFromSpecifiedUser() throws Exception {
		Long userId = 1L;
		Long cardId = 1L;

		doNothing().when(cardService).deleteCard(userId, cardId);

		mockMvc.perform(delete("/userservice/users/{userId}/cards/{cardId}", userId, cardId))
				.andExpect(status().isNoContent());

		verify(cardService).deleteCard(userId, cardId);
	}
}
