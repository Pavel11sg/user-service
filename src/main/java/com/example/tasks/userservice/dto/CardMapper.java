package com.example.tasks.userservice.dto;

import com.example.tasks.userservice.model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CardMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	Card toEntity(CardRequestDto cardRequestDto);

	@Mapping(target = "lastFourDigits", source = "number", qualifiedByName = "extractLastFour")
	@Mapping(target = "maskedHolder", source = "holder", qualifiedByName = "maskHolder")
	CardResponseDto toResponseDto(Card card);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "user", ignore = true)
	void updateFromDto(@MappingTarget Card entity, CardRequestDto dto);

	@Named("maskHolder")
	default String maskHolderMethod(String holder) {
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

	@Named("extractLastFour")
	default String extractLastFour(String number) {
		if (number == null || number.length() < 4) return "";
		return number.substring(number.length() - 4);
	}

	@Named("cardsToCardResponseDtoList")
	default List<CardResponseDto> cardsToCardResponseDtoList(List<Card> cards) {
		if (cards == null) {
			return Collections.emptyList();
		}
		return cards.stream()
				.map(this::toResponseDto)
				.toList();
	}
}