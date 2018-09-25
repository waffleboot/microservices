package com.example;

import java.net.URI;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
	
	@GetMapping("/{id}")
	ResponseEntity<Customer> get(@PathVariable Long id) throws CustomerNotFoundException {
		return customerRepository.findById(id)
				                 .map(ResponseEntity::ok)
				                 .orElseThrow(() -> new CustomerNotFoundException(id));
	}
	
	@PostMapping
	ResponseEntity<Customer> post(@RequestBody Customer c) {
		Customer customer = customerRepository.save(new Customer(c.getFirstName(),c.getLastName()));
		URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}").buildAndExpand(customer.getId()).toUri();
		return ResponseEntity.created(uri).body(customer);
	}
	
	@DeleteMapping("/{id}")
	ResponseEntity<?> delete(@PathVariable Long id) throws CustomerNotFoundException {
		return customerRepository.findById(id).map(c -> {
			customerRepository.delete(c);
			return ResponseEntity.noContent().build();
		}).orElseThrow(() -> new CustomerNotFoundException(id));
	}
	
	@RequestMapping(method = RequestMethod.HEAD, value = "/{id}")
	ResponseEntity<?> head(@PathVariable Long id) throws CustomerNotFoundException {
		return customerRepository.findById(id)
				                 .map(c -> ResponseEntity.noContent().build())
				                 .orElseThrow(() -> new CustomerNotFoundException(id));
	}

	@PutMapping("/{id}")
	ResponseEntity<Customer> put(@PathVariable Long id, @RequestBody Customer c) throws CustomerNotFoundException {
		return customerRepository.findById(id)
					             .map(existing -> {
					            	 Customer customer = new Customer(existing.getId(), c.getFirstName(), c.getLastName());
					            	 customer = customerRepository.save(customer);
					            	 URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
					            	 return ResponseEntity.created(selfLink).body(customer);
					             }).orElseThrow(() -> new CustomerNotFoundException(id));
	}

}
