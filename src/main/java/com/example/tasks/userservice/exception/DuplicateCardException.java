package com.example.tasks.userservice.exception;

public class DuplicateCardException extends RuntimeException {
	public DuplicateCardException(String msg) {
		super(msg);
	}
}
