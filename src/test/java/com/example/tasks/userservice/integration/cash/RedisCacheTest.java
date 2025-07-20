package com.example.tasks.userservice.integration.cash;

import com.example.tasks.userservice.dto.CardRequestDto;
import com.example.tasks.userservice.dto.CardResponseDto;
import com.example.tasks.userservice.dto.UserRequestDto;
import com.example.tasks.userservice.dto.UserResponseDto;
import com.example.tasks.userservice.integration.utils.TestCardFactory;
import com.example.tasks.userservice.integration.utils.TestUserFactory;
import com.example.tasks.userservice.service.CardService;
import com.example.tasks.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = "classpath:/sql/cleanup.sql",
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class RedisCacheTest {
	@Container
	@ServiceConnection
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");
	@Container
	@ServiceConnection
	private static final GenericContainer<?> redisContainer = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
			.withExposedPorts(6379);
	@Autowired
	private UserService userService;
	@Autowired
	private CardService cardService;
	@Autowired
	private CacheManager cacheManager;
	@BeforeEach
	@Transactional
	void setUp() {
		cacheManager.getCache("users").clear();
		cacheManager.getCache("cards").clear();
	}

	@Test
	void getUserById_ShouldCacheResult() {
		UserRequestDto request = TestUserFactory.createUserRequestDto();
		UserResponseDto user = userService.createUser(request);
		Long userId = user.getId();

		UserResponseDto firstCall = userService.getUserById(userId);
		assertNotNull(firstCall);

		UserResponseDto secondCall = userService.getUserById(userId);
		assertNotNull(secondCall);

		assertNotNull(cacheManager.getCache("users").get(userId));
		assertEquals(firstCall, secondCall);
	}

	@Test
	void updateUser_ShouldEvictResult() {
		UserRequestDto userRequestDto = TestUserFactory.createUserRequestDto("user@example.com");
		UserRequestDto updatedUserRequestDto = TestUserFactory.createUserRequestDto("updatedUser@example.com");

		UserResponseDto userResponseDto = userService.createUser(userRequestDto);
		Long userId = userResponseDto.getId();
		userService.getUserById(userId);

		UserResponseDto cashedUser = (UserResponseDto) cacheManager.getCache("users").get(userId).get();
		assertNotNull(cashedUser);
		assertEquals(TestUserFactory.maskEmail(userRequestDto.getEmail()), cashedUser.getMaskedEmail());

		UserResponseDto updatedUserResponseDto = userService.updateUser(userId, updatedUserRequestDto);
		assertNotNull(updatedUserResponseDto);
		assertNull(cacheManager.getCache("users").get(userId));
	}

	@Test
	void deleteUser_ShouldEvictResult() {
		UserRequestDto userRequestDto = TestUserFactory.createUserRequestDto("user@example.com");
		UserResponseDto userResponseDto = userService.createUser(userRequestDto);
		Long userId = userResponseDto.getId();
		userService.getUserById(userId);

		UserResponseDto cashedUser = (UserResponseDto) cacheManager.getCache("users").get(userId).get();
		assertNotNull(cashedUser);
		assertEquals(TestUserFactory.maskEmail(userRequestDto.getEmail()), cashedUser.getMaskedEmail());

		userService.deleteUser(userId);
		assertNull(cacheManager.getCache("users").get(userId));
	}

	@Test
	void getCardByUserIdAndCardId_ShouldCacheResult() {
		UserRequestDto userRequestDto = TestUserFactory.createUserRequestDto();
		UserResponseDto userResponseDto = userService.createUser(userRequestDto);
		Long userId = userResponseDto.getId();
		CardRequestDto cardRequestDto = TestCardFactory.createCardRequestDto();
		CardResponseDto cardResponseDto = cardService.createCard(userId, cardRequestDto);

		CardResponseDto firstCall = cardService.getCardByUserIdAndCardId(userId, cardResponseDto.getId());
		assertNotNull(firstCall);

		CardResponseDto secondCall = cardService.getCardByUserIdAndCardId(userId, cardResponseDto.getId());
		assertNotNull(secondCall);

		assertNotNull(cacheManager.getCache("cards").get(userId + "_" + cardResponseDto.getId()));
		assertEquals(firstCall, secondCall);
	}

	@Test
	void createCard_ShouldEvictUserFromCache() {
		UserRequestDto userRequestDto = TestUserFactory.createUserRequestDto();
		UserResponseDto userResponseDto = userService.createUser(userRequestDto);
		Long userId = userResponseDto.getId();
		CardRequestDto cardRequestDto = TestCardFactory.createCardRequestDto();
		CardResponseDto savedCard1 = cardService.createCard(userId, cardRequestDto);

		CardResponseDto cachedCard = cardService.getCardByUserIdAndCardId(userId, savedCard1.getId());
		assertNotNull(cachedCard);
		assertNotNull(cacheManager.getCache("cards").get(userId + "_" + cachedCard.getId()));
		assertEquals(cachedCard.getMaskedHolder(), savedCard1.getMaskedHolder());

		CardRequestDto secondCardRequestDto = TestCardFactory.createCardRequestDto();
		secondCardRequestDto.setNumber("1111111111111111");
		CardResponseDto savedCard2 = cardService.createCard(userId, secondCardRequestDto);
		assertNotNull(savedCard2);
		assertNull(cacheManager.getCache("users").get(userId));
	}

	@Test
	void updateCard_ShouldEvictCardFromCache() {
		UserRequestDto userRequestDto = TestUserFactory.createUserRequestDto();
		UserResponseDto user = userService.createUser(userRequestDto);
		Long userId = user.getId();

		CardRequestDto cardRequestDto = TestCardFactory.createCardRequestDto();
		CardResponseDto createdCard = cardService.createCard(userId, cardRequestDto);
		Long cardId = createdCard.getId();

		CardResponseDto firstCall = cardService.getCardByUserIdAndCardId(userId, cardId);
		assertNotNull(firstCall);
		assertNotNull(cacheManager.getCache("cards").get(userId + "_" + cardId));

		CardRequestDto updateRequest = TestCardFactory.createCardRequestDto("UPDATED HOLDER","5555555555555555");

		CardResponseDto updatedCard = cardService.updateCard(userId, cardId, updateRequest);
		assertNotNull(updatedCard);

		assertNull(cacheManager.getCache("cards").get(userId + "_" + cardId));
	}

	@Test
	void deleteCard_ShouldEvictCardFromCache() {
		UserRequestDto userRequestDto = TestUserFactory.createUserRequestDto();
		UserResponseDto user = userService.createUser(userRequestDto);
		Long userId = user.getId();

		CardRequestDto cardRequestDto = TestCardFactory.createCardRequestDto();
		CardResponseDto createdCard = cardService.createCard(userId, cardRequestDto);
		Long cardId = createdCard.getId();

		CardResponseDto firstCall = cardService.getCardByUserIdAndCardId(userId, cardId);
		assertNotNull(firstCall);
		assertNotNull(cacheManager.getCache("cards").get(userId + "_" + cardId));

		cardService.deleteCard(userId, cardId);

		assertNull(cacheManager.getCache("cards").get(userId + "_" + cardId));
	}
}
