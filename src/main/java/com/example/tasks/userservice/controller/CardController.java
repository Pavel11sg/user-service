package com.example.tasks.userservice.controller;

import com.example.tasks.userservice.dto.CardRequestDto;
import com.example.tasks.userservice.dto.CardResponseDto;
import com.example.tasks.userservice.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userservice/users/{userId}/cards")
@RequiredArgsConstructor
public class CardController {
	private final CardService cardService;

	@PostMapping
	public ResponseEntity<CardResponseDto> createCard(@PathVariable(name = "userId") Long userId, @Valid @RequestBody CardRequestDto cardRequestDto) {
		CardResponseDto card = cardService.createCard(userId, cardRequestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(card);
	}

	@PutMapping("/{cardId}")
	public ResponseEntity<CardResponseDto> updateCard(@PathVariable(name = "userId") Long userId,
													  @PathVariable(name = "cardId") Long cardId,
													  @Valid @RequestBody CardRequestDto cardRequestDto) {
		CardResponseDto card = cardService.updateCard(userId, cardId, cardRequestDto);
		return ResponseEntity.ok(card);
	}

	@GetMapping("/{cardId}")
	public ResponseEntity<CardResponseDto> getCard(@PathVariable(name = "userId") Long userId, @PathVariable(name = "cardId") Long cardId) {
		return ResponseEntity.ok(cardService.getCardByUserIdAndCardId(userId, cardId));
	}

	@GetMapping()
	public ResponseEntity<List<CardResponseDto>> getCards(@PathVariable(name = "userId") Long userId) {
		return ResponseEntity.ok(cardService.getAllCardsByUserId(userId));
	}
	@DeleteMapping("/{cardId}")
	public ResponseEntity<Void> deleteCard(@PathVariable(name = "userId") Long userId, @PathVariable(name = "cardId") Long cardId) {
		cardService.deleteCard(userId, cardId);
		return ResponseEntity.noContent().build();
	}
}
