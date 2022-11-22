package com.sjumasters.cus720project;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;

public class Cart {
	String userId;
	String cartId;
	HashMap<String, Integer> itemset;
	
	public Cart() {
	}
	
	public void addToCart(String itemId, int quantity) {
		if(itemset.containsKey(itemId)){
			int currentCount = (int)itemset.get(itemId);
			itemset.put(itemId, quantity + currentCount);
		} else {
			itemset.put(itemId, quantity);
		}
	}
	
	public void removeFromCart(String itemId, int quantity) {
		if(itemset.containsKey(itemId)){
			int currentCount = (int)itemset.get(itemId) - quantity;
			itemset.put(itemId, currentCount);
		}
	}
	
	public void emptyCart() {
		this.itemset.clear();
	}
	
	public HashMap<String,Integer> checkout() {
		return itemset;
	}

	public String toString(){
		String output = "Cart Contents:";

		Iterator<String> items = itemset.keySet().iterator();

		while(items.hasNext()){
			String itemSku = items.next();
			output = output + "\n\t" + itemSku + "\t" + (int)itemset.get(itemSku);
		}

		return output;
	}
	
}
