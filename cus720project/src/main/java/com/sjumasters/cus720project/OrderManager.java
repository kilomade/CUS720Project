package com.sjumasters.cus720project;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

public class OrderManager {
	private static Timestamp lastUpdated;
	private static int updateInterval;
	private static HashMap<String, Order> activeOrders;
	private static HashMap<String, Order> pendingOrders;
	private static HashMap<String, Order> fullfilledOrders;

	private static OrderManager instance;

	private OrderManager() {
		activeOrders = new HashMap<String, Order>();
		pendingOrders = new HashMap<String, Order>();
		fullfilledOrders = new HashMap<String, Order>();
	}

	public static OrderManager getInstance() {
		if(instance == null) {
			instance = new OrderManager();
		}

		return instance;
	}

	private void loadOrdersFromExcel(String filepath) {

	}

	//New orders not yet processed -> Cycle 2nd
	public  HashMap<String, Integer> reviewAllPendingOrders(HashMap<String, Integer> currentInventory) {
		Iterator<String> pendingOrderItr = pendingOrders.keySet().iterator();

		while(pendingOrderItr.hasNext()) {
			String targetOrderId = pendingOrderItr.next();
			Order targetOrder = pendingOrders.get(targetOrderId);

			JSONObject awaitingItems = targetOrder.getOpenOrderItemsList();
			JSONObject cloneAwaitingItems = new JSONObject(awaitingItems.toString());

			if(awaitingItems.keySet().size() > 0) {
				Iterator<String> orderSkusItr = awaitingItems.keys();

				while(orderSkusItr.hasNext()) {
					String orderSku = orderSkusItr.next();

					if((int)currentInventory.get(orderSku) == 0) {
						continue;
					}

					int requiredCount = awaitingItems.getInt(orderSku);
					int remainingCount = (int)currentInventory.get(orderSku) - requiredCount;

					if(remainingCount >= 0) {
						targetOrder.processOrderItem(orderSku, awaitingItems.getInt(orderSku));
						currentInventory.put(orderSku, remainingCount);
						cloneAwaitingItems.put(orderSku, 0);
					} else {
						int partialStock = awaitingItems.getInt(orderSku) + remainingCount;
						targetOrder.processOrderItem(orderSku, partialStock);
						currentInventory.put(orderSku, 0);
						cloneAwaitingItems.put(orderSku, requiredCount - partialStock);
					}
				}
			}

			awaitingItems = new JSONObject(cloneAwaitingItems.toString());
			Iterator<String> cloneSkus = cloneAwaitingItems.keys();

			while(cloneSkus.hasNext()){
				String testSku = cloneSkus.next();

				if(cloneAwaitingItems.getInt(testSku) == 0){
					awaitingItems.remove(testSku);
				}
			}

			if(awaitingItems.keySet().size() == 0){
				targetOrder.addComment(0, "Order fulfilled");
				pendingOrders.remove(targetOrderId);
				fullfilledOrders.put(targetOrderId, targetOrder);
			}
		}

		return currentInventory;
	}

	//Tracks orders not completely fullfilled -> Cycle 1st 
	public HashMap<String, Integer> reviewAllActiveOrders(HashMap<String, Integer> currentInventory) {
		Iterator<String> activeOrderItr = activeOrders.keySet().iterator();

		while(activeOrderItr.hasNext()) {
			String targetOrderId = activeOrderItr.next();
			Order targetOrder = activeOrders.get(targetOrderId);

			JSONObject awaitingItems = targetOrder.getOpenOrderItemsList();
			JSONObject cloneAwaitingItems = new JSONObject(awaitingItems.toString());

			if(awaitingItems.keySet().size() > 0) {
				Iterator<String> orderSkusItr = awaitingItems.keys();

				while(orderSkusItr.hasNext()) {
					String orderSku = orderSkusItr.next();

					if((int)currentInventory.get(orderSku) == 0) {
						continue;
					}

					int requiredCount = awaitingItems.getInt(orderSku);
					int remainingCount = (int)currentInventory.get(orderSku) - requiredCount;

					if(remainingCount >= 0) {
						targetOrder.processOrderItem(orderSku, awaitingItems.getInt(orderSku));
						currentInventory.put(orderSku, remainingCount);
						cloneAwaitingItems.put(orderSku, 0);
					} else {
						int partialStock = awaitingItems.getInt(orderSku) + remainingCount;
						targetOrder.processOrderItem(orderSku, partialStock);
						currentInventory.put(orderSku, 0);
						cloneAwaitingItems.put(orderSku, requiredCount - partialStock);
					}
				}
			}

			awaitingItems = new JSONObject(cloneAwaitingItems.toString());
			Iterator<String> cloneSkus = cloneAwaitingItems.keys();

			while(cloneSkus.hasNext()){
				String testSku = cloneSkus.next();

				if(cloneAwaitingItems.getInt(testSku) == 0){
					awaitingItems.remove(testSku);
				}
			}

			if(awaitingItems.keySet().size() == 0){
				targetOrder.addComment(0, "Order fulfilled");
				activeOrders.remove(targetOrderId);
				fullfilledOrders.put(targetOrderId, targetOrder);
			} else {
				pendingOrders.put(targetOrderId, targetOrder);
			}
		}

		return currentInventory;
	}

	public String createNewOrderNumber(JSONObject orderItemset) {
		String proposedId = generateNewOrderId();
		boolean uniqueFlag = false;
		
		while(!uniqueFlag) {
			if(activeOrders.containsKey(proposedId) || pendingOrders.containsKey(proposedId)) {
				proposedId = generateNewOrderId();
				continue;
			} else {
				uniqueFlag = true;
			}
		}
		

		Order newOrder = new Order(proposedId);
		
		Iterator<String> itemSkus = orderItemset.keys();
		
		while(itemSkus.hasNext()) {
			String itemSkuString = itemSkus.next();
			newOrder.addToOrder(itemSkuString, orderItemset.getInt(itemSkuString));
		}
		
		pendingOrders.put(proposedId, newOrder);
		return proposedId;
	}

	private String generateNewOrderId() {
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvxyz";
		StringBuilder sb = new StringBuilder(64);
		
		for (int i = 0; i < 64; i++) {
			int index = (int)(AlphaNumericString.length() * Math.random());
			sb.append(AlphaNumericString.charAt(index));
		}
		return sb.toString();
	}

	public boolean cancelOrder(String orderId) {
		if(pendingOrders.containsKey(orderId)) {
			pendingOrders.remove(orderId);
			return true;
		} else {
			fullfilledOrders.put(orderId, activeOrders.get(orderId));
			activeOrders.remove(orderId);
			return true;
		}
	}

	public JSONObject findOrdersContainingSku(String itemSku) {
		JSONArray activeEntries = new JSONArray();
		JSONArray pendingEntries = new JSONArray();
		JSONArray fullfilledEntries = new JSONArray();		

		JSONObject orderset = new JSONObject();

		Iterator<String> activeOrdersItr = activeOrders.keySet().iterator();

		while(activeOrdersItr.hasNext()) {
			Order testingOrder = activeOrders.get(activeOrdersItr.next());
			if(testingOrder.orderContains(itemSku)) {
				activeEntries.put(testingOrder);
			}
		}

		Iterator<String> pendingOrdersItr = pendingOrders.keySet().iterator();

		while(pendingOrdersItr.hasNext()) {
			Order testingOrder = pendingOrders.get(pendingOrdersItr.next());
			if(testingOrder.orderContains(itemSku)) {
				pendingEntries.put(testingOrder);
			}
		}

		Iterator<String> fullfilledOrdersItr = fullfilledOrders.keySet().iterator();

		while(fullfilledOrdersItr.hasNext()) {
			Order testingOrder = fullfilledOrders.get(fullfilledOrdersItr.next());
			if(testingOrder.orderContains(itemSku)) {
				fullfilledEntries.put(testingOrder);
			}
		}

		orderset.put("active", activeEntries);
		orderset.put("pending", pendingEntries);
		orderset.put("fullfilled", fullfilledEntries);

		return orderset;
	}

	public Order findOrderById(String orderId) {
		if(activeOrders.containsKey(orderId)) {
			return activeOrders.get(orderId);
		} else		
			if(pendingOrders.containsKey(orderId)) {
				return pendingOrders.get(orderId);
			} else
			if(fullfilledOrders.containsKey(orderId)){
				return fullfilledOrders.get(orderId);
			} else{
				return null;
			}
	}

}
