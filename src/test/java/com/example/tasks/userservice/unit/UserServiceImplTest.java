package com.example.tasks.userservice.unit;

import com.example.tasks.userservice.dto.UserRequestDto;
import com.example.tasks.userservice.dto.UserResponseDto;
import com.example.tasks.userservice.exception.DuplicateEmailException;
import com.example.tasks.userservice.exception.UserNotFoundException;
import com.example.tasks.userservice.mapper.UserMapper;
import com.example.tasks.userservice.model.User;
import com.example.tasks.userservice.repository.UserRepository;
import com.example.tasks.userservice.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private UserMapper userMapper;

	@InjectMocks
	private UserServiceImpl userService;

	private UserRequestDto createUserRequestDto() {
		UserRequestDto request = new UserRequestDto();
		request.setName("John");
		request.setSurname("Doe");
		request.setEmail("test@example.com");
		request.setBirthDate(LocalDate.of(1990, 1, 1));
		return request;
	}

	private User createUserEntity() {
		User user = new User();
		user.setId(1L);
		user.setName("John");
		user.setSurname("Doe");
		user.setEmail("test@example.com");
		user.setBirthDate(LocalDate.of(1990, 1, 1));
		return user;
	}

	private UserResponseDto createUserResponseDto() {
		UserResponseDto response = new UserResponseDto();
		response.setId(1L);
		response.setName("John");
		response.setSurname("Doe");
		response.setMaskedEmail("t***t@example.com");
		response.setBirthDate(LocalDate.of(1990, 1, 1));
		return response;
	}

	@Test
	void createUser_shouldReturnUserResponseDto_whenEmailIsUnique() {
		UserRequestDto request = createUserRequestDto();
		User userEntity = new User();
		User savedUser = createUserEntity();
		UserResponseDto expectedResponse = createUserResponseDto();

		when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
		when(userMapper.toEntity(request)).thenReturn(userEntity);
		when(userRepository.save(userEntity)).thenReturn(savedUser);
		when(userMapper.toResponseDto(savedUser)).thenReturn(expectedResponse);

		UserResponseDto result = userService.createUser(request);

		assertNotNull(result);
		assertEquals(expectedResponse, result);
		verify(userRepository).existsByEmail("test@example.com");
		verify(userMapper).toEntity(request);
		verify(userRepository).save(userEntity);
		verify(userMapper).toResponseDto(savedUser);
	}

	@Test
	void createUser_ShouldThrowDuplicateEmailException_WhenEmailExists() {
		UserRequestDto request = createUserRequestDto();
		when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

		DuplicateEmailException exception = assertThrows(
				DuplicateEmailException.class,
				() -> userService.createUser(request)
		);

		assertEquals("Email already exists: test@example.com", exception.getMessage());
		verify(userRepository).existsByEmail("test@example.com");
		verifyNoMoreInteractions(userRepository);
		verifyNoInteractions(userMapper);
	}

	@Test
	void updateUser_ShouldUpdate_WhenEmailNotChanged() {
		Long userId = 1L;
		UserRequestDto request = createUserRequestDto();
		User existingUser = createUserEntity();
		UserResponseDto expectedResponse = createUserResponseDto();

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.save(existingUser)).thenReturn(existingUser);
		when(userMapper.toResponseDto(existingUser)).thenReturn(expectedResponse);

		UserResponseDto result = userService.updateUser(userId, request);

		assertNotNull(result);
		assertEquals(expectedResponse, result);
		verify(userRepository).findById(userId);
		verify(userRepository, never()).existsByEmail(any());
		verify(userMapper).updateUserFromDto(request, existingUser);
		verify(userRepository).save(existingUser);
		verify(userMapper).toResponseDto(existingUser);
	}

	@Test
	void updateUser_ShouldReturnUpdatedUser_WhenEmailChangedAndUnique() {
		Long userId = 1L;
		UserRequestDto request = createUserRequestDto();
		request.setEmail("new@example.com");

		User existingUser = createUserEntity();
		existingUser.setEmail("old@example.com");

		UserResponseDto expectedResponse = createUserResponseDto();
		expectedResponse.setMaskedEmail("new@example.com");

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
		when(userRepository.save(existingUser)).thenReturn(existingUser);
		when(userMapper.toResponseDto(existingUser)).thenReturn(expectedResponse);

		UserResponseDto result = userService.updateUser(userId, request);

		assertNotNull(result);
		assertEquals(expectedResponse, result);
		verify(userRepository).findById(userId);
		verify(userRepository).existsByEmail("new@example.com");
		verify(userMapper).updateUserFromDto(request, existingUser);
		verify(userRepository).save(existingUser);
		verify(userMapper).toResponseDto(existingUser);
	}

	@Test
	void updateUser_ShouldThrowDuplicateEmailException_WhenEmailChangedAndExists() {
		Long userId = 1L;
		UserRequestDto request = createUserRequestDto();
		request.setEmail("existing@example.com");

		User existingUser = createUserEntity();
		existingUser.setEmail("old@example.com");

		when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
		when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

		assertThrows(DuplicateEmailException.class, () -> {
			userService.updateUser(userId, request);
		});

		verify(userRepository).findById(userId);
		verify(userRepository).existsByEmail("existing@example.com");
		verify(userRepository, never()).save(any());
	}

	@Test
	void updateUser_ShouldThrowUserNotFoundException_WhenUserNotExists() {
		Long userId = 1L;
		UserRequestDto request = createUserRequestDto();
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		UserNotFoundException exception = assertThrows(
				UserNotFoundException.class,
				() -> userService.updateUser(userId, request)
		);

		assertEquals("User not found with id: 1", exception.getMessage());
		verify(userRepository).findById(userId);
		verifyNoMoreInteractions(userRepository);
		verifyNoInteractions(userMapper);
	}

	@Test
	void deleteUser_ShouldDeleteUser_WhenExists() {
		Long userId = 1L;
		User user = createUserEntity();
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		doNothing().when(userRepository).delete(user);

		userService.deleteUser(userId);

		verify(userRepository).findById(userId);
		verify(userRepository).delete(user);
	}

	@Test
	void getUserById_ShouldReturnUser_WhenExists() {
		Long userId = 1L;
		User user = createUserEntity();
		UserResponseDto expectedResponse = createUserResponseDto();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(userMapper.toResponseDto(user)).thenReturn(expectedResponse);

		UserResponseDto result = userService.getUserById(userId);

		assertNotNull(result);
		assertEquals(expectedResponse, result);
		verify(userRepository).findById(userId);
		verify(userMapper).toResponseDto(user);
	}

	@Test
	void getAllUsers_ShouldReturnListOfUsers() {
		User user = createUserEntity();
		UserResponseDto response = createUserResponseDto();

		when(userRepository.findAll()).thenReturn(List.of(user));
		when(userMapper.toResponseDto(user)).thenReturn(response);

		List<UserResponseDto> result = userService.getAllUsers();

		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(response, result.get(0));
		verify(userRepository).findAll();
		verify(userMapper).toResponseDto(user);
	}

	@Test
	void getAllUsers_ShouldReturnEmptyList_WhenNoUsers() {
		when(userRepository.findAll()).thenReturn(Collections.emptyList());

		List<UserResponseDto> result = userService.getAllUsers();

		assertNotNull(result);
		assertTrue(result.isEmpty());
		verify(userRepository).findAll();
		verifyNoInteractions(userMapper);
	}
}