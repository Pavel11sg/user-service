package com.example.tasks.userservice.integration.controller;

import com.example.tasks.userservice.controller.UserController;
import com.example.tasks.userservice.dto.UserRequestDto;
import com.example.tasks.userservice.dto.UserResponseDto;
import com.example.tasks.userservice.exception.DuplicateEmailException;
import com.example.tasks.userservice.exception.UserNotFoundException;
import com.example.tasks.userservice.integration.utils.TestUserFactory;
import com.example.tasks.userservice.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@MockitoBean
	private UserService userService;

	@Test
	void createUser_ReturnsCreated() throws Exception {
		UserRequestDto request = TestUserFactory.createUserRequestDto();
		UserResponseDto response = TestUserFactory.createUserResponseDto();

		when(userService.createUser(any(UserRequestDto.class))).thenReturn(response);

		mockMvc.perform(post("/userservice/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.maskedEmail").value("t***************m@example.com"));
	}

	@Test
	void createUser_ThrowsDuplicateEmail() throws Exception {
		UserRequestDto request = TestUserFactory.createUserRequestDto("duplicate@example.com");

		when(userService.createUser(any(UserRequestDto.class))).thenThrow(new DuplicateEmailException("Email already exists"));

		mockMvc.perform(post("/userservice/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isConflict());
	}

	@Test
	void getUserById_ReturnsUser() throws Exception {
		UserResponseDto response = TestUserFactory.createUserResponseDto();

		when(userService.getUserById(1L)).thenReturn(response);

		mockMvc.perform(get("/userservice/users/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.maskedEmail").value("t***************m@example.com"));
	}

	@Test
	void getUserById_ThrowsNotFound() throws Exception {
		when(userService.getUserById(999L)).thenThrow(new UserNotFoundException("User not found"));

		mockMvc.perform(get("/userservice/users/999")).andExpect(status().isNotFound());
	}

	@Test
	void updateUser_ReturnsUpdatedUser() throws Exception {
		UserRequestDto request = TestUserFactory.createUserRequestDto();
		UserResponseDto response = TestUserFactory.createUserResponseDto();

		when(userService.updateUser(eq(1L), any(UserRequestDto.class))).thenReturn(response);

		mockMvc.perform(put("/userservice/users/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.maskedEmail").value("t***************m@example.com"));
	}

	@Test
	void getAllUsers_ReturnsList() throws Exception {
		List<UserResponseDto> users = List.of(
				TestUserFactory.createUserResponseDto(1L, "u***1@example.com"),
				TestUserFactory.createUserResponseDto(2L, "u***2@example.com")
		);

		when(userService.getAllUsers()).thenReturn(users);

		mockMvc.perform(get("/userservice/users"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].maskedEmail").value("u***1@example.com"))
				.andExpect(jsonPath("$[1].maskedEmail").value("u***2@example.com"));
	}

	@Test
	void deleteUser_ShouldReturnNoContent() throws Exception {
		Long userId = 1L;

		doNothing().when(userService).deleteUser(userId);

		mockMvc.perform(delete(("/userservice/users/{id}"), userId))
				.andExpect(status().isNoContent());

		verify(userService).deleteUser(userId);
	}
}