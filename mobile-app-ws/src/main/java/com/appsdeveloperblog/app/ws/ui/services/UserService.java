package com.appsdeveloperblog.app.ws.ui.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.appsdeveloperblog.app.ws.ui.shared.UserDto;

public interface UserService extends UserDetailsService {
	 UserDto createUser(UserDto userDto);
	 UserDto getUserByUserId(String userId);
	 UserDto getUser(String email);
	 UserDto updateUser(String userId, UserDto userDto);
	 void deleteUser(String userId);
	 List<UserDto> getUsers(int page, int limit);
	 boolean verifyEmailToken(String token);
	 boolean requestPasswordReset(String email);
	 boolean resetPassword(String token, String password);
}
