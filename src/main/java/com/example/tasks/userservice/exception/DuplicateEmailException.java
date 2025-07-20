package com.example.tasks.userservice.exception;

public class DuplicateEmailException extends RuntimeException {
	public DuplicateEmailException(String msg) {
		super(msg);
	}
}
