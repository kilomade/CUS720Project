package com.sjumasters.cus720project;

import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class Order {
	String orderId;
	String status;
	JSONObject orderItemset;
	JSONObject processedItemset;
	Timestamp dateCreated;
	Timestamp dateLastUpdated;
	JSONArray comments;
	
	public Order(String orderId) {
		this.orderId = orderId;
	}
	
	public String getOrderId() {
		return this.orderId;
	}
	
	public boolean orderContains(String itemSku) {
		if(this.orderItemset.has(itemSku)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void loadOrderDetails(long createdTime, String orderItemset, String processedItemset, String dateCreated, String comments) {
		this.orderItemset = new JSONObject(orderItemset);
		this.processedItemset = new JSONObject(processedItemset);
		this.comments = new JSONArray(comments);
		
		this.dateCreated = new Timestamp(createdTime);
		
		String lastComment = (String)this.comments.get(-1);
		String[] lastCommentTimestamp = lastComment.split(" -- ");
		String[] delimitedTimestamp = lastCommentTimestamp[0].split("\\.");
		Date dateTempHolder = new Date();
		dateTempHolder.setYear(Integer.parseInt(delimitedTimestamp[0]));
		dateTempHolder.setMonth(Integer.parseInt(delimitedTimestamp[1]));
		dateTempHolder.setDate(Integer.parseInt(delimitedTimestamp[2]));
		dateTempHolder.setHours(Integer.parseInt(delimitedTimestamp[3]));
		dateTempHolder.setMinutes(Integer.parseInt(delimitedTimestamp[4]));
		this.dateLastUpdated = new Timestamp(dateTempHolder.getTime());
	}
	
	public void addComment(int userId, String comment) {
		this.dateLastUpdated = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat logTimestampFormatted = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
		String commentBody = logTimestampFormatted.format(this.dateLastUpdated) + " -- " + Integer.toString(userId) + " -- " + comment;
		this.comments.put(commentBody);
	}
	
	public void processOrderItem(String itemSku, int itemQuantity) {
		int currentProcessed = this.processedItemset.getInt(itemSku) + itemQuantity;
		this.processedItemset.put(itemSku, currentProcessed);
		addComment(0, "Processed " + itemQuantity + " of " + itemSku);
	}
	
	public JSONObject getOpenOrderItemsList() {
		String[] fullorderlist = (String[])this.orderItemset.keySet().toArray();
		String[] processedorderlist = (String[])this.processedItemset.keySet().toArray();
		
		JSONObject openItems = new JSONObject();
		
		for(int i = 0; i < fullorderlist.length; ++i) {
			String testSku = fullorderlist[i];
			int totalNeeded = this.orderItemset.getInt(testSku);
			int provided = this.processedItemset.getInt(testSku);
			
			if(totalNeeded - provided == 0) {
				continue;
			} else {
				openItems.append(testSku, totalNeeded - provided);
			}
		}
		
		return openItems;
	}
	
	public boolean addToOrder(String itemSku, int itemQuanity) {
		
		if(orderItemset.has(itemSku)) {
			int currentQuantity = (this.orderItemset.getInt(itemSku)) + itemQuanity;
			this.orderItemset.put(itemSku, currentQuantity);
		} else {
			this.orderItemset.put(itemSku, itemQuanity);
		}
		return true;
	}
	
	public boolean removeFromOrder(String itemSku, int itemQuantity) {
		if(orderItemset.has(itemSku)) {
			int currentQuantity = (this.orderItemset.getInt(itemSku)) - itemQuantity;
			
			if(currentQuantity == 0) {
				this.orderItemset.remove(itemSku);
			} else {
				this.orderItemset.put(itemSku, currentQuantity);
			}
			
		} 
		return true;
	}
	
	public String toString(){
		String output = "Order ID: " + this.orderId + "\nItems";
		Iterator<String> orderItemsetItr = this.orderItemset.keys();

		while(orderItemsetItr.hasNext()){
			String itemId = orderItemsetItr.next();
			output = output + "\n\t" + itemId + " --> Required" + this.orderItemset.getInt(itemId) + "\n\t\tFullfilled: " + this.processedItemset.getInt(itemId);
		}

		return output;
	}
	
	public void submitOrder() {
		this.dateCreated = new Timestamp(System.currentTimeMillis());
	}
	
}
