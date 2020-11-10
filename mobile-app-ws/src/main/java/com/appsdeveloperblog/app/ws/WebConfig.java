package com.appsdeveloperblog.app.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
	registry.addMapping("/**")
	.allowedMethods("*")
	.allowedOrigins("*");
		
	
	}

	
	
	//public void addCorsMappings(CorsRegistry registry) {
		// TODO Auto-generated method stub
		//registry.addMapping("/users/email-verification");
		//it is applicable only to /email-verification uri
		
		
		//registry.addMapping("/**").allowedMethods("GET", "POST", "PUT");
		//It is applicable to all the controllers(the first asterisk) and to all the methods(the second asterisk)
		//allowed methods allow to specify the methods that are accessible: - Get, post and put in the above example
		
//		registry
//		.addMapping("/**")
//		.allowedMethods("*")
//		.allowedOrigins("*");
		
		
		//allowedMethods(*) allows all the methods
		
		//we need to go to WebSecurity class add cors() in the http configure
		
	//}

}
