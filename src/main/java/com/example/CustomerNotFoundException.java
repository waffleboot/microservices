package com.example;

public class CustomerNotFoundException extends Exception {

	private final Long id;
	
	public CustomerNotFoundException(Long id) {
		super("Customer with id=" + id + " not found");
		this.id = id;
	}

	public Long getCustomerId() {
		return id;
	}
	
}
