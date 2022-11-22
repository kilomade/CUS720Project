package com.sjumasters.cus720project;

import org.json.JSONArray;

public class Item {
	String itemId;
	String sku;
	String commonName;
	JSONArray imageLinks;
	int onHand;
	int onBackOrder;
	boolean canOrder;
	boolean canShip;
	double price;
	
	public Item(String itemSKU, String commonName, String[] imageLinks, int onHand, int onBackOrder, boolean canOrder, double price) {
//		this.itemId = itemId;
		this.sku = itemSKU;
		this.commonName = commonName;
		this.imageLinks = new JSONArray(imageLinks);
		this.onHand = onHand;
		this.onBackOrder = onBackOrder;
		this.canOrder = canOrder;
		this.price = price;
	}
	
	public Item() {
		
	}
	
	public String toString() {
		String outputString = "\nSKU: " + this.sku + "\n\tCommon Name: " + this.commonName + "\n\tImageLinks:";
		
		for(int i =0; i < this.imageLinks.length(); ++i) {
			outputString = outputString + "\n\t\t" + this.imageLinks.getString(i);
		}
		
		outputString = outputString + "\n\nAVAILABILITY REPORT\n\tOn Hand: " + this.onHand + "\n\tOn Backorder: " + this.onBackOrder + "\n\tCan Order: " + this.canOrder + "\n\tPrice: " + this.price;
		return outputString;
	}
	
	public void setPrice(double newPrice) {
		this.price = newPrice;
	}
	
	public void setCanShip(boolean flag) {
		this.canOrder = flag;
	}
	
	public void setCanOrder(boolean flag) {
		this.canOrder = flag;
	}
	
	public void setImageLinks(JSONArray imageLinks) {
		this.imageLinks = imageLinks;
	}
	
	public void setOnHand(int count) {
		this.onHand = count;
	}
	
	public void addItems(int incrementValue) {
		this.onHand += incrementValue;
	}
	
	public int pull(int incrementValue) {
		if(this.onHand - incrementValue > 0) {
			this.onHand -= incrementValue;
			return 0;
		} else {
			if(this.canOrder == true) {
				
				return setBackOrder(incrementValue);
			} else {
				return -1;
			}
		}
	}
	
	public int setBackOrder(int incrementValue) {
		if(incrementValue > 0) {
			this.onBackOrder += incrementValue;
			return incrementValue;
		} else {
			this.onBackOrder += (-1 * incrementValue);
			return incrementValue;
		}
		
	}
	
	
}
