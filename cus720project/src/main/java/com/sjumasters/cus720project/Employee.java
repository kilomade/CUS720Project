package com.sjumasters.cus720project;

public class Employee {
	String userId;
	String firstName;
	String lastName;
	String emailAddress;
	String phoneNumber;
	int role;
	
	public Employee() {
		
	}
	
	public Employee(String userId, String firstName, String lastName, String emailAddress, String phoneNumber, int role) {
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.phoneNumber = phoneNumber;
		this.role = role;
	}
}
