package com.appsdeveloperblog.app.ws.ui.shared;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest {

	@Autowired
	Utils util;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGenerateUserId() {
		String userId1 = util.generateUserId(30);
		String userId2 = util.generateUserId(30);
		assertNotNull(userId1);
		assertTrue(userId1.length() == 30);
		assertTrue(!userId1.equalsIgnoreCase(userId2));
	}

	@Test
	void testHasTokenNotExpired() {
		String token = util.generateEmailVerificationToken("bajkndkjnsad");
		assertNotNull(token);
		boolean hasTokenExpired = Utils.hasTokenExpired(token);
		assertFalse(hasTokenExpired);
	}
	
	
	//Test for the expired token and fail if the token is valid
	@Test
	void testHasTokenExpired() {
		String expiredToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJYUDgyWUM5b3dTY2plRllSRlc0anN0UlNwUUlUY0QiLCJleHAiOjE2MDY3NTgyNTJ9.B_FKWY8PUHkFCF6G8vYAEBHyY4-2NGCKrzgnKBO7Cppbmbg06R-_xlu4B3eStRvRk1GfnvdZGrCaJmWMjYScaA";
		boolean hasTokenExpired = Utils.hasTokenExpired(expiredToken);
		assertTrue(hasTokenExpired);
	}

}
