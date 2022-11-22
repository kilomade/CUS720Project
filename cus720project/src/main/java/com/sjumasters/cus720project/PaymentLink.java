/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sjumasters.cus720project;


import org.json.JSONObject;
import java.util.Random;
import org.json.JSONArray;
/**
 *
 * @author chris
 */
public class PaymentLink {
    String paymentLinkId;
    JSONArray bankCards = new JSONArray();
    JSONArray bankAccounts = new JSONArray();
    String chargeAccountNumber;
    Double chargeAccountBalance;
    JSONObject addressBook = new JSONObject();
    
    public PaymentLink() {
    	this.paymentLinkId = Integer.toString(this.generateAccountId());
    }
    
    public PaymentLink(String paymentLinkId) {
    	this.paymentLinkId = paymentLinkId;
    	//TODO Use the paymentlinkid to load the data from db here
    }
    
    //Bank Card methods
    public void addBankCard(int cardNumber, String expiration, String name, int cvv, int addressId) {
    	
    }
    
    public void deleteBankCard(int cardNumber) {
    	
    }
    
    public void verfiyBankCard(int cvv, String zipCode) {
    	
    }
    
    public void postBankCard(int cardId, Double amount) {
    	
    }
    
    //Bank Account methods
    public void addBankAccount(int accountRoutingNumber, int accountNumber, String firstName, String lastName, String middleName) {
    	
    }
    
    public void deleteBankAccount(int accountRoutingNumber, int accountNumber) {
    	
    }
    
    public void postBankAccount(int accountRoutingNumber, int accountNumber, Double amount) {
    	
    }
    
    //Charge account
    public void postChargeAccount(Double amount) {
    	
    }
    
    //Internal methods
    private int generateAccountId() {
    	int n = 16;
    	int m = (int) Math.pow(10, n - 1);
        return m + new Random().nextInt(9 * m);
    }
}
