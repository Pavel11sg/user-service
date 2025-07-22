package com.example.tasks.userservice.service;

import com.example.tasks.userservice.dto.UserRequestDto;
import com.example.tasks.userservice.dto.UserResponseDto;

import java.util.List;

public interface UserService {
	UserResponseDto createUser(UserRequestDto userRequestDto);

	UserResponseDto updateUser(Long id, UserRequestDto userRequestDto);

	void deleteUser(Long id);

	UserResponseDto getUserById(Long id);

	List<UserResponseDto> getAllUsers();
}
