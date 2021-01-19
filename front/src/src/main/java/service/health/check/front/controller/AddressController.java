package service.health.check.front.controller;

import service.health.check.models.Address;
import service.health.check.front.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AddressController {

	@Autowired
	private AddressRepository addressRepository;

	@PostMapping("/addresses")
	public Address createAddress(@Valid @RequestBody Address address) {
		return addressRepository.save(address);
	}
}
