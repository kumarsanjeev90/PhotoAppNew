package com.appsdeveloperblog.app.ws.ui.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.junit.jupiter.MockitoExtension;

import com.appsdeveloperblog.app.ws.data.UserEntity;
import com.appsdeveloperblog.app.ws.ui.models.response.UserResponseModel;
import com.appsdeveloperblog.app.ws.ui.services.UserServiceImpl;
import com.appsdeveloperblog.app.ws.ui.shared.AddressDTO;
import com.appsdeveloperblog.app.ws.ui.shared.UserDto;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	UserEntity userEntity;
	
	@Rule
	MockitoRule mockitoRule = MockitoJUnit.rule();
	
	
	@Mock
	UserServiceImpl userService;
	
	@InjectMocks
	UserController userController;
	
	String userId = "adnsaldnl";
	String encryptedPassword = "dnadkmlakdmaffkfmas";
	String firstName = "Sanjeev";
	String lastName = "Kumar";
	String email = "test@test.com";
	
 	@BeforeEach
	void setUp() throws Exception {
		userEntity = new UserEntity();
		userEntity.setUserId(userId);
	}
	

	@Test
	void testGetUser() {
		UserDto userDto = new UserDto();
		userDto.setUserId(userId);
		userDto.setFirstName(firstName);
		userDto.setLastName(lastName);
		userDto.setEmail(email);
		userDto.setEmailVerificationStatus(Boolean.FALSE);
		userDto.setEmailVerificationToken("aldadlasdnadnadm");
		userDto.setEncryptedPassword("fnjsdfnlsdnfsdkff");
		userDto.setAddresses(getAddressDto());
		when(userService.getUserByUserId(anyString())).thenReturn(userDto);
		
		UserResponseModel userResponseModel = userController.getUser(userId);
		assertNotNull(userResponseModel);
		assertEquals(userId, userResponseModel.getUserId());
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
	

}
