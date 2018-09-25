package com.example;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
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
@RequestMapping(path = "/v2", produces = "application/hal+json")
public class CustomerHypermediaRestController {

	private final CustomerResourceAssembler customerResourceAssembler;
	private final CustomerRepository customerRepository;
	
	@Autowired
	public CustomerHypermediaRestController(CustomerResourceAssembler customerResourceAssembler,
			                                CustomerRepository customerRepository) {
		this.customerResourceAssembler = customerResourceAssembler;
		this.customerRepository = customerRepository; 
	}
	
	@GetMapping
	ResponseEntity<Resources<?>> root() {
		Resources<?> objects = new Resources<>(Collections.emptyList());
		URI uri = MvcUriComponentsBuilder.fromMethodCall(MvcUriComponentsBuilder.on(getClass()).getCollection()).build().toUri();
		Link link = new Link(uri.toString(), "customers");
		objects.add(link);
		return ResponseEntity.ok(objects);
	}
	
	@GetMapping("/customers")
	ResponseEntity<Resources<Resource<Customer>>> getCollection() {
		List<Resource<Customer>> collect = customerRepository.findAll()
				                                             .stream()
				                                             .map(customerResourceAssembler::toResource)
				                                             .collect(Collectors.<Resource<Customer>>toList());
		Resources<Resource<Customer>> resources = new Resources<>(collect);
		URI self = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
		resources.add(new Link(self.toString(), Link.REL_SELF));
		return ResponseEntity.ok(resources);
	}
	
	@RequestMapping(path = "/customers", method = RequestMethod.OPTIONS)
	ResponseEntity<?> options() {
		return ResponseEntity.ok().allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.PUT, HttpMethod.DELETE).build();
	}
	
	@GetMapping("/customers/{id}")
	ResponseEntity<Resource<Customer>> get(@PathVariable Long id) {
		return customerRepository.findById(id).map(c -> 
			ResponseEntity.ok(customerResourceAssembler.toResource(c))
		).orElseThrow(() -> new CustomerNotFoundException(id));
	}
	
	@PostMapping("/customers")
	ResponseEntity<Resource<Customer>> post(@RequestBody Customer c) {
		Customer customer = customerRepository.save(new Customer(c.getFirstName(),c.getLastName()));
		URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/customers/{id}").buildAndExpand(customer.getId()).toUri();
		return ResponseEntity.created(uri).body(customerResourceAssembler.toResource(customer));
	}

	@DeleteMapping("/customers/{id}")
	ResponseEntity<?> delete(@PathVariable Long id) {
		return customerRepository.findById(id).map(c -> {
			customerRepository.delete(c);
			return ResponseEntity.noContent().build();
		}).orElseThrow(() -> new CustomerNotFoundException(id));
	}
	
	@RequestMapping(path = "/customers/{id}", method = RequestMethod.HEAD)
	ResponseEntity<?> head(@PathVariable Long id) {
		return customerRepository.findById(id).map(c -> ResponseEntity.noContent().build()).orElseThrow(() -> new CustomerNotFoundException(id));
	}
	
	@PutMapping("/customers/{id}")
	ResponseEntity<Resource<Customer>> put(@PathVariable Long id, @RequestBody Customer c) {
		Customer customer = customerRepository.save(new Customer(id,c.getFirstName(),c.getLastName()));
		Resource<Customer> customerResource = customerResourceAssembler.toResource(customer);
		URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
		return ResponseEntity.created(selfLink).body(customerResource);
	}

}
