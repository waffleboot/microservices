package com.example;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.stereotype.Service;

@Service
public class CustomerRepository {

	Collection<Customer> findAll() {
		return Arrays.asList();
	}
	
}
