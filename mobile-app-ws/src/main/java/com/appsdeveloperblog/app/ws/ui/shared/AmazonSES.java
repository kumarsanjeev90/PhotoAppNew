package com.appsdeveloperblog.app.ws.ui.shared;

import org.springframework.stereotype.Component;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;

@Component
public class AmazonSES {
		final String FROM = "kumarsanjj90@gmail.com";
		final String SUBJECT = "One last step to complete your registration with Photoshop";
		final String HTMLBODY = "<h1>Please verify your email address</h1>"
				+ "<p>Thank you for registering with our mobile app. To complete the registration process and be able to login</p>"
				+ ", click on the following link: "
				+ "<a href='http://ec2-65-0-80-10.ap-south-1.compute.amazonaws.com:8080/verification-service/email-verification.html?token=$tokenValue'>"
				+ "Final Step to complete your registration"+"</a><br/><br/>"
				+"Thank you! and we are waiting for you inside!";

		final String TEXTBODY = "Please verify your email address"
				+ "Thank you for registering with our mobile app. To complete the registration process and be able to login"
				+ ", open the below URL in your browsing window: "
				+ "http://ec2-65-0-80-10.ap-south-1.compute.amazonaws.com:8080/verification-service/email-verification.html?token=$tokenValue"
				+ "Final Step to complete your registration"
				+"Thank you! and we are waiting for you inside!";

		final String PASSWORD_RESET_SUBJECT = "Password reset request";
		final String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>"
				+ "<p>Hi, $firstName!</p>"
				+ "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it.</p>"
				+ "otherwise, please click on the link below to set a new password: "
				+ "<a href='http://ec2-65-0-80-10.ap-south-1.compute.amazonaws.com:8080/verification-service/password-reset.html?token=$tokenValue'>"
				+ "Click this link to reset the password"
				+"</a><br/><br/>"
				+"Thank you!";

		final String PASSWORD_RESET_TEXTBODY = "A request to reset your password"
				+ "Hi, $firstName!"
				+ "Someone has requested to reset your password with our project. If it were not you, please ignore it."
				+ "otherwise, please click on the link below to set a new password: "
				+ "http://ec2-65-0-80-10.ap-south-1.compute.amazonaws.com:8080/verification-service/password-reset.html?token=$tokenValue"
				+ "Click this link to reset the password"
				+"Thank you!";
		public void verifyEmail(UserDto userDto) {
			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
					.withRegion(Regions.AP_SOUTH_1)
					.build();
			String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
			String textBodyWithToken = TEXTBODY.replace("$tokenValue", userDto.getEmailVerificationToken());
//			SendEmailRequest request = new SendEmailRequest()
//					.withDestination(new Destination().withToAddresses(userDto.getEmail()))
//					
//					.withMessage(
//					new Message().withBody(
//					new Body()
//					
//					.withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
//					
//					.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken))
//					)
//					.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT))
//					)
//					.withSource(FROM);
			
			SendEmailRequest request = new SendEmailRequest()
					.withDestination(new Destination().withToAddresses(userDto.getEmail()))
					.withMessage(new Message()
							.withBody(new Body()
									.withHtml(new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
									.withText(new Content().withCharset("UTF-8").withData(textBodyWithToken)))
							.withSubject(new Content().withCharset("UTF-8").withData(SUBJECT)))
					.withSource(FROM);
			
			client.sendEmail(request);
			//System.out.println("Email sent!");
		}
		
		public boolean sendPasswordResetRequest(String firstName, String email, String token) {
			boolean returnValue = false;
			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
					.withRegion(Regions.AP_SOUTH_1).build();
			String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue", token);
			htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);
			
			String textBodyWithToken = PASSWORD_RESET_TEXTBODY.replace("$tokenValue", token);
			textBodyWithToken = textBodyWithToken.replace("$firstName", firstName);
			
			SendEmailRequest request = new SendEmailRequest()
					.withDestination(new Destination().withToAddresses(email))
					.withMessage(new Message().withBody(new Body().withHtml(
								new Content().withCharset("UTF-8").withData(htmlBodyWithToken))
							.withText(new Content()
									.withCharset("UTF-8").withData(textBodyWithToken)))
							.withSubject(new Content().withCharset("UTF-8").withData(PASSWORD_RESET_SUBJECT)))
					.withSource(FROM);
			
			SendEmailResult result = client.sendEmail(request);
			if(result != null && (result.getMessageId()!=null && !result.getMessageId().isEmpty())) {
				returnValue = true;
			}
			return returnValue;
		}
		

}
