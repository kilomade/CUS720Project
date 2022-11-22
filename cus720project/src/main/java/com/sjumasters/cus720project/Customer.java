package com.sjumasters.cus720project;

public class Customer {
	String userId;
	String firstName;
	String lastName;
	String emailAddress;
	String phoneNumber;
	PaymentLink paymentAccountLink;
	int role;
	
	public Customer() {
		
	}
	
	public Customer(String userId, String firstName, String lastName, String emailAddress, String phoneNumber, String paymentLinkId, int role) {
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.emailAddress = emailAddress;
		this.phoneNumber = phoneNumber;
		this.paymentAccountLink = new PaymentLink(paymentLinkId);
		this.role = role;
	}
	
}
