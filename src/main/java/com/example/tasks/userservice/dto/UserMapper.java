package com.example.tasks.userservice.dto;

import com.example.tasks.userservice.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
		uses = CardMapper.class,
		nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "cards", ignore = true)
	User toEntity(UserRequestDto dto);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "cards", ignore = true)
	void updateUserFromDto(UserRequestDto userRequestDto, @MappingTarget User user);

	@Mapping(target = "maskedEmail", source = "email", qualifiedByName = "maskEmail")
	@Mapping(target = "cards", source = "cards")
	UserResponseDto toResponseDto(User user);

	@Named("maskEmail")
	default String maskEmail(String email) {
		if (email == null || email.length() < 5) return email;
		int atIndex = email.indexOf('@');
		if (atIndex < 2) return email;
		return email.charAt(0) + "***" + email.substring(atIndex - 1);
	}
}