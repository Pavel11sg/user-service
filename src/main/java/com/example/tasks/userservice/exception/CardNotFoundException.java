package com.example.tasks.userservice.exception;

public class CardNotFoundException extends RuntimeException {
	public CardNotFoundException(String msg) {
		super(msg);
	}
}
