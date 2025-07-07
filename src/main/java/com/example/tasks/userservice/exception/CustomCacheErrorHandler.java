package com.example.tasks.userservice.exception;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

import java.util.logging.Logger;

public class CustomCacheErrorHandler implements CacheErrorHandler {
	private final Logger logger = Logger.getLogger(CustomCacheErrorHandler.class.getName());

	@Override
	public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
		logger.info(() -> String.format("Error of reading from cash! Key: %s, Error: %s", key, exception.getMessage()));
	}

	@Override
	public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
		logger.info(() -> String.format("Error of writing to cash! Key: %s, Error: %s", key, exception.getMessage()));
	}

	@Override
	public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
		throw exception;
	}

	@Override
	public void handleCacheClearError(RuntimeException exception, Cache cache) {
		throw exception;
	}
}