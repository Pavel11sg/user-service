package com.example.tasks.userservice.service;

import com.example.tasks.userservice.mapper.UserMapper;
import com.example.tasks.userservice.dto.UserRequestDto;
import com.example.tasks.userservice.dto.UserResponseDto;
import com.example.tasks.userservice.exception.DuplicateEmailException;
import com.example.tasks.userservice.exception.UserNotFoundException;
import com.example.tasks.userservice.model.User;
import com.example.tasks.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final UserMapper userMapper;

	@Override
	@Transactional
	public UserResponseDto createUser(UserRequestDto userRequestDto) {
		if (userRepository.existsByEmail(userRequestDto.getEmail())) {
			throw new DuplicateEmailException("Email already exists: " + userRequestDto.getEmail());
		}
		User user = userMapper.toEntity(userRequestDto);
		User savedUser = userRepository.save(user);
		return userMapper.toResponseDto(savedUser);
	}

	@Override
	@Transactional
	@CacheEvict(value = "users", key = "#p0")
	public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
		User existingUser = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
		if (!existingUser.getEmail().equals(userRequestDto.getEmail()) && userRepository.existsByEmail(userRequestDto.getEmail())) {
			throw new DuplicateEmailException("Email already exists: " + id);
		}
		userMapper.updateUserFromDto(userRequestDto, existingUser);
		User updatedUser = userRepository.save(existingUser);
		return userMapper.toResponseDto(updatedUser);
	}

	@Override
	@Transactional
	@CacheEvict(value = "users", key = "#p0")
	public void deleteUser(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(("User not found with id: " + id)));
		userRepository.delete(user);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "users", key = "#p0")
	public UserResponseDto getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(("User not found with id: " + id)));
		return userMapper.toResponseDto(user);
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserResponseDto> getAllUsers() {
		List<User> users = userRepository.findAll();
		return users.stream().map(userMapper::toResponseDto).toList();
	}
}
