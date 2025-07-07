package com.example.tasks.userservice.controller;

import com.example.tasks.userservice.dto.UserRequestDto;
import com.example.tasks.userservice.dto.UserResponseDto;
import com.example.tasks.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userservice/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
		UserResponseDto createdUser = userService.createUser(userRequestDto);
		return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponseDto> updateUser(@Valid @PathVariable(name = "id") Long id, @RequestBody UserRequestDto userRequestDto) {
		UserResponseDto updatedUser = userService.updateUser(id, userRequestDto);
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable(name = "id") Long id) {
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponseDto> getUserById(@PathVariable(name = "id") Long id) {
		UserResponseDto user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}

	@GetMapping
	public ResponseEntity<List<UserResponseDto>> getAllUsers() {
		List<UserResponseDto> users = userService.getAllUsers();
		return ResponseEntity.ok(users);
	}
}
