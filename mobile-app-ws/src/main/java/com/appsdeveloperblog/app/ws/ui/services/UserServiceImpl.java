package com.appsdeveloperblog.app.ws.ui.services;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.data.PasswordResetTokenEntity;
import com.appsdeveloperblog.app.ws.data.UserEntity;
import com.appsdeveloperblog.app.ws.data.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.ui.models.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.shared.AddressDTO;
import com.appsdeveloperblog.app.ws.ui.shared.AmazonSES;
import com.appsdeveloperblog.app.ws.ui.shared.UserDto;
import com.appsdeveloperblog.app.ws.ui.shared.Utils;
import com.appsdeveloperblog.app.ws.ws.ui.repositories.PasswordResetTokenRepository;
import com.appsdeveloperblog.app.ws.ws.ui.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	UserRepository userRepos;
	
	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepos;
	@Override
	public UserDto createUser(UserDto userDto) {
		UserEntity userEntity = new UserEntity();
		if(userRepos.findUserByEmail(userDto.getEmail()) != null)
			throw new RuntimeException("Record already exists");
		
		for(int i=0; i<userDto.getAddresses().size(); i++) {
			AddressDTO addressDto = userDto.getAddresses().get(i);
			String addressId = utils.generateAddressId(30);
			addressDto.setUserDetails(userDto);
			addressDto.setAddressId(addressId);
			userDto.getAddresses().set(i, addressDto);
			
		}
		ModelMapper mp = new ModelMapper();
		userEntity = mp.map(userDto, UserEntity.class);

		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		String publicUserId = utils.generateUserId(30); 
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);
		userEntity.setUserId(publicUserId);
		UserEntity storedUser = userRepos.save(userEntity);
		
		UserDto newUserDto = new UserDto();
		newUserDto = mp.map(storedUser, UserDto.class);
		//send an email to the user to verify their email address
		new AmazonSES().verifyEmail(newUserDto);
		return newUserDto;
	}

	@Override
	public UserDto getUser(String email) {
		UserEntity userEntity = userRepos.findUserByEmail(email);
		if(userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);
		//returnValue.setFirstName(null);
		return returnValue;
		
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		
		UserEntity userEntity = userRepos.findUserByEmail(email);
		if(userEntity == null)
			throw new UsernameNotFoundException(email);
		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), 
				userEntity.getEmailVerificationStatus(),
				true, true, 
				true, new ArrayList<>());
		//return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserEntity userEntity = userRepos.findByUserId(userId);
		if(userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userEntity, userDto);
		return userDto;
	}

	@Override
	public UserDto updateUser(String userId, UserDto userDto) {
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepos.findByUserId(userId);
		if(userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}
		if(userDto.getFirstName() == null || userDto.getLastName() == null)
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		//update only those fields that are required. Don't update the email id or password from here
		userEntity.setFirstName(userDto.getFirstName());
		userEntity.setLastName(userDto.getLastName());
		UserEntity updatedUserEntity = userRepos.save(userEntity);
		BeanUtils.copyProperties(updatedUserEntity, returnValue);
		return returnValue;
	}

	@Override
	public void deleteUser(String userId) {
		UserEntity userEntity = userRepos.findByUserId(userId);
		if(userEntity == null) {
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		}
		userRepos.delete(userEntity);
		
	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		List<UserDto> returnValue = new ArrayList<>();

	Pageable pageableRequest = PageRequest.of(page, limit);
	Page<UserEntity> usersPage = userRepos.findAll(pageableRequest);
	List<UserEntity> userEntities = usersPage.getContent();
		for(UserEntity userEntity: userEntities)
		{
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
		}
		return returnValue;
	}

	@Override
	public boolean verifyEmailToken(String token) {
		boolean returnValue = false;
		UserEntity userEntity = userRepos.findUserByEmailVerificationToken(token);
		if(userEntity != null) {
			boolean hasTokenExpired = Utils.hasTokenExpired(token);
			if(!hasTokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepos.save(userEntity);
				returnValue = true;
				
			}
		}
		
		return returnValue;
	}

	@Override
	public boolean requestPasswordReset(String email) {
		
		boolean returnValue = false;
		UserEntity userEntity = userRepos.findUserByEmail(email);
		if(userEntity == null) {
			return returnValue;
		}
		//Generate password reset token
		String token = new Utils().generatePasswordResetToken(userEntity.getUserId());
		//we need to store this token in the database
		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		passwordResetTokenRepos.save(passwordResetTokenEntity);
		returnValue = new AmazonSES().sendPasswordResetRequest(
				userEntity.getFirstName(),
				userEntity.getEmail(),
				token);
		
		return returnValue;
	}

	@Override
	public boolean resetPassword(String token, String password) {
		boolean returnValue = false;
		if(Utils.hasTokenExpired(token))
			return returnValue;
		PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepos.findByToken(token);
		if(passwordResetTokenEntity == null)
			return returnValue;
		
		//prepare a new password
		String encodedPassword = bCryptPasswordEncoder.encode(password);
		
		//update user password in the database
		UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
		userEntity.setEncryptedPassword(encodedPassword);
		UserEntity savedUserEntity = userRepos.save(userEntity);
		
		
		//Verify if the password was saved successfully
		if(savedUserEntity != null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)){
			returnValue = true;
		}

		//remove password reset token from the database
		passwordResetTokenRepos.delete(passwordResetTokenEntity);
		
		
		return returnValue;
	} 
	
	
	
	
	
}
