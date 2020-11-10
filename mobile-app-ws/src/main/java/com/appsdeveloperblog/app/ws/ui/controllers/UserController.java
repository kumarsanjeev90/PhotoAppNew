package com.appsdeveloperblog.app.ws.ui.controllers;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appsdeveloperblog.app.ws.data.exceptions.UserServiceException;
import com.appsdeveloperblog.app.ws.ui.models.request.PasswordResetModel;
import com.appsdeveloperblog.app.ws.ui.models.request.PasswordResetRequestModel;
import com.appsdeveloperblog.app.ws.ui.models.request.UserRequestModel;
import com.appsdeveloperblog.app.ws.ui.models.response.AddressResponseModel;
import com.appsdeveloperblog.app.ws.ui.models.response.ErrorMessages;
import com.appsdeveloperblog.app.ws.ui.models.response.OperationStatusModel;
import com.appsdeveloperblog.app.ws.ui.models.response.RequestOperationName;
import com.appsdeveloperblog.app.ws.ui.models.response.RequestOperationStatus;
import com.appsdeveloperblog.app.ws.ui.models.response.UserResponseModel;
import com.appsdeveloperblog.app.ws.ui.services.AddressService;
import com.appsdeveloperblog.app.ws.ui.services.UserService;
import com.appsdeveloperblog.app.ws.ui.shared.AddressDTO;
import com.appsdeveloperblog.app.ws.ui.shared.UserDto;

@RestController
@RequestMapping("users")
@CrossOrigin(origins = {"http://localhost:8083", "http://localhost:8084"})
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private AddressService addressService;
	
	@GetMapping(produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<UserResponseModel> getUsers(@RequestParam(value="page", defaultValue="0") int page, 
			@RequestParam(value="limit", defaultValue="2") int limit) {
		if(page > 0) {
			page -= 1;
		}
		ModelMapper mp = new ModelMapper();
		List<UserResponseModel> returnValue = new ArrayList<>();
		List<UserDto> userDto = userService.getUsers(page, limit);
		
//			UserResponseModel userResponseModel = new UserResponseModel();
//			BeanUtils.copyProperties(user, userResponseModel);
//			returnValue.add(userResponseModel);
			
		Type listType = new TypeToken<List<UserResponseModel>>() {}.getType();
		returnValue = mp.map(userDto, listType);
			
		
		return returnValue;
	}
	@GetMapping(path="/{userId}", produces= { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }
	 )
	public UserResponseModel showUser(@PathVariable("userId") String userId) {
		UserResponseModel userResponse = new UserResponseModel();
		UserDto userDto = userService.getUserByUserId(userId);
		BeanUtils.copyProperties(userDto, userResponse);
		return userResponse;
	}
	
	@PostMapping(consumes={ MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
			produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserResponseModel createUser(@RequestBody UserRequestModel userRequestModel) throws Exception {
 		if(userRequestModel.getFirstName().isEmpty()) {
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		}
		UserDto userDto = new UserDto();
		UserResponseModel returnValue = new UserResponseModel();
//		BeanUtils.copyProperties(userRequestModel, userDto);
		ModelMapper mp = new ModelMapper();
		userDto = mp.map(userRequestModel, UserDto.class);
		UserDto createdUserDto = userService.createUser(userDto);
		
		
		returnValue = mp.map(createdUserDto, UserResponseModel.class);
		return returnValue;
	}
	
	@PutMapping(path="/{userId}",
			consumes={ MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, 
			produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserResponseModel updateUser(@PathVariable String userId, @RequestBody UserRequestModel userRequestModel) {
		UserResponseModel userResponse = new UserResponseModel();
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userRequestModel, userDto);
		UserDto returnValue = userService.updateUser(userId, userDto);
		BeanUtils.copyProperties(returnValue, userResponse);
		
		return userResponse;
	}
	
	@DeleteMapping(path="/{userId}",
			 	produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String userId) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		userService.deleteUser(userId);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	@GetMapping(path="/{userId}/addresses",
			produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public List<AddressResponseModel> getUserAddresses(@PathVariable String userId) {
		
		List<AddressResponseModel> returnValue = new ArrayList<>();
		List<AddressDTO> addressDtos = addressService.getAddresses(userId);
		if(addressDtos != null && !addressDtos.isEmpty()) {
			Type listType = new TypeToken<List<AddressResponseModel>>() {}.getType();
			ModelMapper mp = new ModelMapper();
			returnValue = mp.map(addressDtos, listType);
		}
		
	return returnValue;
	}

	
	@GetMapping(path="/{userId}/addresses/{addressId}",
			produces= { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })

	public AddressResponseModel getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

		AddressDTO addressDto = addressService.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();
		AddressResponseModel returnValue = modelMapper.map(addressDto, AddressResponseModel.class);
		//start creating links for the Hateoas
		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class)
				.slash(userId)
				.withRel("user");
		//WebMvcLinkBuilder class is used to build link. It has a method called linkTo
		//that takes in a controller argument. This will create a link that starts with
		//http://<our machine name>:<portnumber>/<request mapping of the controller>/<userId>
		//http://localhost:8080/users/userId
		//withRel("user") -> "user" is a static text which is basically a Json key.
		//Now i will add this link to the returnValue.
		
		
		//http://localhost:8080/users/{userId}/addresses/{addressId}
		Link userAddressesLink = WebMvcLinkBuilder.linkTo(UserController.class)
				.slash(userId)
				.slash("addresses")
				.withRel("addresses");
		
		
		
		//for creating a link to this resource itself. it is going to be a self link
		//http://localhost:8080/users/userId/addresses/addressId
		Link selfLink = WebMvcLinkBuilder.linkTo(UserController.class)
				.slash(userId)
				.slash("addresses")
				.slash(addressId)
				.withSelfRel();
		
		
		returnValue.add(userLink);
		returnValue.add(userAddressesLink);
		returnValue.add(selfLink);
		return returnValue;
	}
	
	@GetMapping(path="/email-verification", produces= {MediaType.APPLICATION_JSON_VALUE,
			MediaType.APPLICATION_XML_VALUE})
	//@CrossOrigin(origins="*") 
	//this will allow the request from any origin.
	//Suppose I have an app running on port 8084 and the request has to be allowed only from that app:
	//@CrossOrigin(origins="http://localhost:8084")
	//In order to allow requests from multiple regions:
//	@CrossOrigin(origins = {"http://localhost:8083", "http://localhost:8084"})
	
	//Now if you put cross origin annotation above this method, then the request to the url /email-verification will be allowed.
	//if you want to allow the requests to all the URIs in this controller, move the annotation above to the controller.
	public OperationStatusModel verifyEmailToken(@RequestParam(value="token") String token) {
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		boolean isVerified = userService.verifyEmailToken(token);
		if(isVerified) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
			
		}
		else
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		
		
		return returnValue;
	}
	
	@PostMapping(path="/password-reset-request", consumes= {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
			produces= {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public OperationStatusModel passwordReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
		returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		if(operationResult) {
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		}
		return null;
	}
	
	@PostMapping(path="/password-reset", consumes= {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
		OperationStatusModel returnValue = new OperationStatusModel();
		boolean operationResult = userService.resetPassword(
				passwordResetModel.getToken(),
				passwordResetModel.getPassword());
		
		returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
		returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		if(operationResult)
		{
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
			
		}
		return returnValue;
	}
	
}
