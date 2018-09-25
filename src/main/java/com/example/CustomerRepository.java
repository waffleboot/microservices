package com.example;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class CustomerRepository {
	
	private long id;
	private Map<Long,Customer> customers = new ConcurrentHashMap<>();

	Collection<Customer> findAll() {
		return Collections.unmodifiableCollection(customers.values());
	}
	
	Optional<Customer> findById(Long id) {
		return Optional.ofNullable(customers.get(id));
	}

	Customer save(Customer customer) {
		if (customer.getId() == null) {
			customer.setId(++id);			
		}
		customers.put(id, customer);
		return customer;
	}

	public void delete(Customer customer) {
		customers.remove(customer.getId());
	}
	
}
