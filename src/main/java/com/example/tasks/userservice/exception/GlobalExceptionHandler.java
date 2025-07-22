package com.example.tasks.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<String> handleDuplicateEmail(DuplicateEmailException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
	}

	@ExceptionHandler(DuplicateCardException.class)
	public ResponseEntity<Map<String, String>> handleDuplicateCard(
			DuplicateCardException ex) {
		Map<String, String> error = new HashMap<>();
		error.put("error", "Duplicate card");
		error.put("message", ex.getMessage());
		error.put("timestamp", LocalDateTime.now().toString());
		return ResponseEntity
				.status(HttpStatus.CONFLICT)
				.body(error);
	}

	@ExceptionHandler(CardNotFoundException.class)
	public ResponseEntity<String> handleCardNotFound(CardNotFoundException ex) {
		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(ex.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return ResponseEntity.badRequest().body(errors);
	}
}
