package com.appsdeveloperblog.app.ws.ui.services;

import java.util.List;

import com.appsdeveloperblog.app.ws.ui.shared.AddressDTO;

public interface AddressService {
	List<AddressDTO> getAddresses(String userId);
	AddressDTO getAddress(String addressId);
}
