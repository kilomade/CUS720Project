package com.sjumasters.cus720project;

import java.util.HashMap;

public class UserManagement {
	private static RolesManager permissionsMap;
	private static HashMap<String, Employee> employeeUsers;
	private static HashMap<String, Customer> customerUsers;
	private static HashMap<String, PaymentLink> paymentLinkHandler;
	
	private static UserManagement instance = null;
	
	private UserManagement() {
		//Load employee user information
		//Load customer user information
		//Load payment link information
	}
	
	public static UserManagement getInstance() {
		if(instance == null) {
			instance = new UserManagement();
		}
		
		return instance;
	}
	
	public void createNewEmployeeAccount(String fname, String lname, String emailAddress, String phoneNumber, int role, String password) {
		
	}
	
	public boolean loginEmployee(String emailAddress, String password) {
		
		return false;
	}
	
	public void createNewCustomerAccount(String fname, String lname, String emailAddress, String phoneNumber, String paymentAccountLinkId, int role, String password) {
		
	}
	
	public boolean loginCustomer(String emailAddress, String password) {
		
		return false;
	}
	
	public String getCustomerPaymentLinkAccount(String userId) {
		
		return "";
	}
	
	public boolean createPaymentLink(String userId) {
		
		return false;
	}
	
	public boolean updateEmail(String userId, String emailAddress) {
		
		return false;
	}
	
	public boolean updatePhoneNumber(String userId, String phoneNumber) {
		
		return false;
	}
	
	public boolean deleteEmployeeAccount(String userId) {
		
		return false;
	}
	
	public boolean deleteCustomerAccount(String userId) {
		
		return false;
	}
}
