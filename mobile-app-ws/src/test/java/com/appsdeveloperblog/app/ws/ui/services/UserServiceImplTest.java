package com.appsdeveloperblog.app.ws.ui.services;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.appsdeveloperblog.app.ws.data.AddressEntity;
import com.appsdeveloperblog.app.ws.data.UserEntity;
import com.appsdeveloperblog.app.ws.data.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.ui.shared.AddressDTO;
import com.appsdeveloperblog.app.ws.ui.shared.AmazonSES;
import com.appsdeveloperblog.app.ws.ui.shared.UserDto;
import com.appsdeveloperblog.app.ws.ui.shared.Utils;
import com.appsdeveloperblog.app.ws.ws.ui.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	
	@Mock
	UserRepository userRepos;
	
	@Mock
	Utils util;
	
	@Mock
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Mock
	AmazonSES amazonSES;
	
	@InjectMocks
	UserServiceImpl userServiceImpl;
	
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	
	UserEntity userEntity;
	
	String addressId = "sdadksad";
	String encryptedPassword = "jdkjansdjsnadlad";
	String userId = "ndkjandandl";
	String emailVerificationToken = "jdbkjadnlandakm";
	String email = "test@test.com";
	String firstName = "Sanjeev";
	String lastName = "Kumar";
	String password = "bdkjandljand";
	@BeforeEach
	void setUp() throws Exception {
		
		userEntity = new UserEntity();
		userEntity.setFirstName(firstName);
		userEntity.setLastName(lastName);
		userEntity.setUserId(userId);
		userEntity.setId(1L);
		userEntity.setEncryptedPassword(encryptedPassword);
		userEntity.setEmailVerificationToken(emailVerificationToken);
		userEntity.setEmail(email);
		userEntity.setAddresses(getAddressEntity());
		
	}

	@Test
	final void testGetUser() {
		when(userRepos.findUserByEmail(anyString())).thenReturn(userEntity);
		UserDto storedUser= userServiceImpl.getUser("Dummy");
		assertEquals("Sanjeev", storedUser.getFirstName());
	}
	
	@Test
	final void testGetUser_UserServicexception() {
		when(userRepos.findUserByEmail(anyString())).thenReturn(null);
		
		assertThatExceptionOfType(UserServiceException.class).isThrownBy(() -> {
			userServiceImpl.getUser("test@test.com");	
		});
	}
	
	@Test
	final void testCreateUser() {
		when(userRepos.findUserByEmail(anyString())).thenReturn(null);
		when(util.generateAddressId(anyInt())).thenReturn(addressId);
		when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
		when(util.generateUserId(anyInt())).thenReturn(userId);
		when(util.generateEmailVerificationToken(anyString())).thenReturn(emailVerificationToken);
		when(userRepos.save(any(UserEntity.class))).thenReturn(userEntity);
		Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));

		UserDto userDto = new UserDto();
		userDto.setEmail(email);
		userDto.setPassword(password);
		userDto.setFirstName(firstName);
		userDto.setLastName(lastName);
		
		userDto.setAddresses(getAddressDto());
		
		UserDto storedUser = userServiceImpl.createUser(userDto);
		assertNotNull(storedUser);
		
		assertNotNull(storedUser.getUserId());
		 //		assertEquals("sanjeev", storedUser.getFirstName());
		assertEquals(storedUser.getAddresses().size(), userEntity.getAddresses().size());
		
		//To verify if the generateAddressId() method has been called twice for each addressDto:
		verify(util, times(storedUser.getAddresses().size())).generateAddressId(30);
		
		//to Verify if the encode() method has been called once or not:
		verify(bCryptPasswordEncoder, times(1)).encode(password);
		
		//to verify if the userRepository calls the save() method once:
		verify(userRepos, times(1)).save(any(UserEntity.class));
		
		
		//In this way, i am testing if all my mocks have worked properly and passed the test or not.
		
		
	}
	
	
	@Test
	void testCreateUser_CreatUserException() {
		when(userRepos.findUserByEmail(anyString())).thenReturn(userEntity);
		UserDto userDto = new UserDto();
		userDto.setEmail(email);
		userDto.setPassword(password);
		userDto.setFirstName(firstName);
		userDto.setLastName(lastName);
		
		userDto.setAddresses(getAddressDto());
		
		assertThatExceptionOfType(UserServiceException.class).isThrownBy(() -> {
			userServiceImpl.createUser(userDto);
			
		});
	}
	
	private List<AddressDTO> getAddressDto(){
		
		AddressDTO addressDto = new AddressDTO();
		addressDto.setType("shipping");
		addressDto.setCity("Vancouver");
		addressDto.setCountry("Canada");
		addressDto.setPostalCode("sjdads");
		addressDto.setStreetName("123 Street");
		
		AddressDTO billingAddressDto = new AddressDTO();
		addressDto.setType("billing");
		addressDto.setCity("Vancouver");
		addressDto.setCountry("Canada");
		addressDto.setPostalCode("sjdads");
		addressDto.setStreetName("123 Street");
		List<AddressDTO> addresses = new ArrayList<>();
		
		addresses.add(addressDto);
		addresses.add(billingAddressDto);
		return addresses;
	}
	
	private List<AddressEntity> getAddressEntity(){
		List<AddressDTO> addresses = getAddressDto();
		Type listType = new TypeToken<List<AddressEntity>>() {}.getType();
		List<AddressEntity> characters = new ModelMapper().map(addresses, listType);
		return characters;
	}

}
