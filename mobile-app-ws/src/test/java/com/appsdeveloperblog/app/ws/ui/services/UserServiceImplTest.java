package com.appsdeveloperblog.app.ws.ui.services;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.app.ws.data.UserEntity;
import com.appsdeveloperblog.app.ws.data.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.ui.shared.UserDto;
import com.appsdeveloperblog.app.ws.ui.shared.Utils;
import com.appsdeveloperblog.app.ws.ws.ui.repositories.UserRepository;

class UserServiceImplTest {
	
	@InjectMocks
	UserServiceImpl userService;
	
	@Mock
	UserRepository userRepos;
		
	@Mock
	Utils utils;

	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	String userId = "slkclmkacas";
	String encryptedPassword = "anjndaneq34342";
	UserEntity userEntity;
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		UserEntity userEntity = new UserEntity();
		userEntity.setId(2L);
		userEntity.setFirstName("sergey");
		userEntity.setUserId(userId);
		userEntity.setEncryptedPassword(encryptedPassword);

	}

	@Test
	final void testGetUser() {
			when(userRepos.findUserByEmail(anyString())).thenReturn(userEntity);
		UserDto userDto = userService.getUser("test123@mail.com");
		assertNotNull(userDto);
		assertEquals("sergey", userDto.getFirstName());
		
	}
	
	@Test
	final void testGetUser_UsernameNotFoundException(){
		when(userRepos.findUserByEmail(anyString())).thenReturn(null);
		assertThrows(UserServiceException.class, () -> {
			UserDto userDto = userService.getUser("test@mail.com");
		});
		
		
	}

//	@Test
//	final void testCreateUser() {
//		when(userRepos.findUserByEmail(anyString())).thenReturn(null);
//		when(utils.generateAddressId(anyInt())).thenReturn("dsdfefwefwef");
//		when(utils.generateUserId(anyInt())).thenReturn(userId);
//		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
//		when(userRepos.save(any(UserEntity.class))).thenReturn(userEntity);
//		
//	}
}
