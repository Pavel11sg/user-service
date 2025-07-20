package com.example.tasks.userservice.config;

import com.example.tasks.userservice.exception.CustomCacheErrorHandler;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig implements CachingConfigurer {
	@Override
	public CacheErrorHandler errorHandler() {
		return new CustomCacheErrorHandler();
	}
}