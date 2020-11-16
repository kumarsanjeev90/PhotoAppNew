package com.appsdeveloperblog.app.ws.ui.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.appsdeveloperblog.app.ws.data.UserEntity;
import com.appsdeveloperblog.app.ws.data.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.ui.shared.UserDto;
import com.appsdeveloperblog.app.ws.ws.ui.repositories.UserRepository;
class UserServiceImplTest {

	
	@Mock
	UserRepository userRepository;
	@InjectMocks
	UserServiceImpl userServiceImpl;
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	final void testGetUser() {
		UserEntity userEntity = new UserEntity();
		userEntity.setFirstName("Sergey");
		userEntity.setLastName("Kargopolov");
		userEntity.setId(1L);
		userEntity.setUserId("jnkads");
		userEntity.setEncryptedPassword("kajnaldnld3324");
		when(userRepository.findUserByEmail(anyString())).thenReturn(userEntity);
		UserDto userDto = userServiceImpl.getUser("test@gmail.com");
		assertThat(userDto).isNotNull();
		assertThat(userDto.getFirstName()).isEqualTo("Sergey");
	}
	
	@Test
	final void testGetUser_UserServicexception() {
		when(userRepository.findUserByEmail(anyString())).thenReturn(null);
		
		assertThatExceptionOfType(UserServiceException.class).isThrownBy(() -> {
			userServiceImpl.getUser("test@test.com");	
		});
		
	}

}
