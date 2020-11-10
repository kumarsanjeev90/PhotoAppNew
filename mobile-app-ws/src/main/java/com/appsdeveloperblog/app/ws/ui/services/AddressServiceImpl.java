package com.appsdeveloperblog.app.ws.ui.services;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appsdeveloperblog.app.ws.data.AddressEntity;
import com.appsdeveloperblog.app.ws.data.UserEntity;
import com.appsdeveloperblog.app.ws.ui.shared.AddressDTO;
import com.appsdeveloperblog.app.ws.ws.ui.repositories.AddressRepository;
import com.appsdeveloperblog.app.ws.ws.ui.repositories.UserRepository;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private UserRepository userRepos;
	
	@Autowired
	private AddressRepository addressRepository;
	@Override
	public List<AddressDTO> getAddresses(String userId) {
		List<AddressDTO> returnValue = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = userRepos.findByUserId(userId);
		if(userEntity == null) {
			return returnValue;
		}
		Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
//		for(AddressEntity addressEntity:addresses) {
//			returnValue.add(modelMapper.map(addressEntity, AddressDTO.class));
//		}
		
		Type listType = new TypeToken<List<AddressDTO>>() {}.getType();
		returnValue = modelMapper.map(addresses, listType);
		
		
		return returnValue;
	}
	@Override
	public AddressDTO getAddress(String addressId) {
		AddressDTO returnValue = null;
		AddressEntity addressEntity = addressRepository.findByAddressId(addressId);
		if(addressEntity != null)
		{
			returnValue = new ModelMapper().map(addressEntity, AddressDTO.class);
		}
		return returnValue;
	}

	
}
