package com.example;

public class CustomerNotFoundException extends Exception {

	public CustomerNotFoundException(Long id) {
		super("Customer with id=" + id + " not found");
	}
	
}
