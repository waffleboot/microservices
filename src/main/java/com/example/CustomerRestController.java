package com.example;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/customers")
public class CustomerRestController {

	@Autowired
	private CustomerRepository customerRepository;
	
	@RequestMapping(method = RequestMethod.OPTIONS)
	ResponseEntity<?> options() {
		return ResponseEntity.ok()
				.allow(HttpMethod.GET, 
					   HttpMethod.POST, 
					   HttpMethod.HEAD, 
					   HttpMethod.OPTIONS, 
					   HttpMethod.PUT, 
					   HttpMethod.DELETE)
				.build();
	}
	
	@GetMapping
	ResponseEntity<Collection<Customer>> getCollection() {
		return ResponseEntity.ok(customerRepository.findAll());
	}
	
}
