package com.example.tasks.userservice.exception;

public class CardNotOwnedException extends RuntimeException {
	public CardNotOwnedException(String msg) {
		super(msg);
	}
}
