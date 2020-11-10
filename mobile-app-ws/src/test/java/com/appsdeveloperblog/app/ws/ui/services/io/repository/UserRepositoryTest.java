package com.appsdeveloperblog.app.ws.ui.services.io.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.appsdeveloperblog.app.ws.data.AddressEntity;
import com.appsdeveloperblog.app.ws.data.UserEntity;
import com.appsdeveloperblog.app.ws.ws.ui.repositories.UserRepository;

//ExtendWith annotation is used to make a JUnit5 test an Integration Test to check Restful services
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRepositoryTest {

	@Autowired
	UserRepository userRepos;
	
	@BeforeEach
	void setUp() throws Exception {
		UserEntity userEntity = new UserEntity();
		userEntity.setFirstName("Sergey");
		userEntity.setLastName("Kargopolov");
		userEntity.setEmail("trest@test.com");
		userEntity.setUserId("hdjan");
		userEntity.setEncryptedPassword("adcaccsdvavnlkm2131");
		userEntity.setEmailVerificationStatus(true);
		
		AddressEntity addressEntity = new AddressEntity();
		addressEntity.setType("billing");
		addressEntity.setAddressId("jndland");
		addressEntity.setCity("Rourkela");
		addressEntity.setCountry("India");
		addressEntity.setStreetName("koelnagar");
		addressEntity.setPostalCode("nlnsld");
		List<AddressEntity> addresses = new ArrayList<>();
		addresses.add(addressEntity);
		userEntity.setAddresses(addresses);
		userRepos.save(userEntity);
	}

	@Test
	final void testGetVerifiedUsers() {
		Pageable request = PageRequest.of(0, 2);
		Page<UserEntity> pages = userRepos.findAllUsersWithConfirmedEmailAddress(request);
		assertNotNull(pages);
		List<UserEntity> userEntities = pages.getContent();
		assertNotNull(userEntities);
		assertTrue(userEntities.size() == 1);
	}

}
