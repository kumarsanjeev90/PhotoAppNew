package com.appsdeveloperblog.app.ws.ui.models.response;

import java.util.List;

public class UserResponseModel {
	
	private String firstName;
	private String lastName;
	private String email;
	private String userId;
	private List<AddressResponseModel> addresses;
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public List<AddressResponseModel> getAddresses() {
		return addresses;
	}
	public void setAddresses(List<AddressResponseModel> addresses) {
		this.addresses = addresses;
	}
	
}
