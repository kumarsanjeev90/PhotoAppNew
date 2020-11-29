package com.appsdeveloperblog.app.ws.security;

import org.springframework.beans.factory.annotation.Autowired;

import com.appsdeveloperblog.app.ws.SpringApplicationContext;

public class SecurityConstants {


//	@Autowired
//	static AppProperties appProperties;
	
	public static final long EXPIRATION_TIME = 864000000; //10 days
	public static final long PASSWORD_RESET_EXPIRATION_TIME = 1000*60*60; //1 hour
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String SIGN_UP_URL = "/mobile-app-ws/users";
	public static final String VERIFICATION_EMAIL_URL = "/mobile-app-ws/users/email-verification";
	public static final String PASSWORD_RESET_REQUEST_URL =	"/mobile-app-ws/users/password-reset-request";
	public static final String PASSWORD_RESET_URL ="/mobile-app-ws/users/password-reset";
	public static final String HEADER_STRING = "Authorization";
	public static String getTokenSecret() {
		AppProperties appProperties = (AppProperties) SpringApplicationContext.getBean("AppProperties");
		return appProperties.getToken();
	}
}