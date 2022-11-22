package com.sjumasters.cus720project;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.Scanner;

public class TransactionManagement {
	HashMap<String, Cart> shoppingCardMap;
	static InventoryManager inventory;
	static OrderManager orders = OrderManager.getInstance();
	
	
	public static JSONObject retrieveCart(String userId) {
		
		return new JSONObject();
	}
	
	public static boolean purchaseCard(String userID) {
		return false;
	}
	
	public static JSONObject addToCard(String userId, String itemID, int itemQuantity) {
		
		return new JSONObject();
	}
	
	public static JSONObject removeFromCard(String userId, String itemId, int itemQuantity) {
		
		return new JSONObject();
	}
	
	public static JSONObject calculateCartSubtotal(String userId) {
		
		return new JSONObject();
	}
	
	public static void clearCart(String userId) {
		
	}
	
	public static void menu() {
		System.out.println(ConsoleColors.WHITE_BOLD_BRIGHT);
		System.out.println("Welcome to the SJU Inventory Mangaement 2.0");
		System.out.println(ConsoleColors.RESET);
		
		/*
		 * Menu
		 * Order Managagement
		 * 1 - Search orders by orderId
		 * 2 - Search orders by items
		 * 3 - Create new orders
		 * 4 - Update all orders
		 * 
		 * Inventory Managment
		 * 1 - Update inventory by item sku
		 * 2 - Enable/Disable ordering
		 * 3 - Check inventory by itemId
		 * 4 - Print inventory
		 * */

		System.out.println(ConsoleColors.GREEN);
		String topMenu = "Order Management"
				+ "\n\t1 Search orders by orderId"
				+ "\n\t2 Search orders by content"
				+ "\n\t3 Create new order"
				+ "\n\t4 Update all orders"
				+ "\nInventory Management"
				+ "\n\t5 Add inventory by item sku"
				+ "\n\t6 Enable/Disable item ordering"
				+ "\n\t7 Check inventory by itemId"
				+ "\n\t8 Print inventory";
		System.out.println(topMenu + "\n\nSelection: ");
		System.out.println(ConsoleColors.RESET);
		Scanner userInput = new Scanner(System.in);

		int topMenuSelection = userInput.nextInt();

		switch(topMenuSelection){
			case 1:
				while(true){
					System.out.println(ConsoleColors.GREEN);
					System.out.println("Enter order id(e to exit): ");
					String orderId = userInput.nextLine();

					if(orderId.equals("e")){
						break;
					}
					Order retrivedOrder = orders.findOrderById(orderId);

					if(retrivedOrder == null){
						System.out.println("No orders present");
					} else {
						System.out.println(retrivedOrder.toString());
					}

					System.out.println(ConsoleColors.RESET);
				}

				break;
			case 2:
				while(true){
					System.out.println(ConsoleColors.GREEN);
					System.out.println("Enter item sku(e to exit): ");
					String content = userInput.nextLine();

					if(content.equals("e")){
						break;
					}
					JSONObject retrievedOrders = orders.findOrdersContainingSku(content);
					Iterator<String> retrievedOrderItr = retrievedOrders.keys();

					String orderList = "Orders Founds containting " + content;

					while(retrievedOrderItr.hasNext()){
						String orderId = retrievedOrderItr.next();
						orderList = orderList + "\n\t" + orderId;
					}

					System.out.println(orderList);
					System.out.println(ConsoleColors.RESET);
				}
				break;
			case 3:
				boolean newOrderFlag = true;
				while(newOrderFlag){
					Cart temporaryCart = new Cart();

					System.out.println(ConsoleColors.CYAN);
					System.out.println("Orderabled inventory: ");
					JSONObject fullInventory = inventory.printInventoryCommonNames();

					Iterator<String> fullInventoryItr = fullInventory.keys();
					ArrayList<String> itemIndex = new ArrayList<String>();
					while(fullInventoryItr.hasNext()){
						String itemSku = fullInventoryItr.next();
						itemIndex.add(itemSku);
					}

					for(int i = 0; i < itemIndex.size(); ++i){
						System.out.println("\n\t" + itemIndex.get(i) + "\t" + fullInventory.getString(itemIndex.get(i)));
					}

					System.out.println(ConsoleColors.GREEN);

					System.out.println("To print cart, enter V");
					System.out.println("To add item, enter A");
					System.out.println("To remove item, enter R");
					System.out.println("To close cart, enter C");
					System.out.println("To exit, enter e");

					String instantOrderSelection = userInput.next();

					switch(instantOrderSelection){
						case "V":
							System.out.println(ConsoleColors.YELLOW);
							System.out.println(temporaryCart.toString());
							System.out.println(ConsoleColors.GREEN);
							break;
						case "A":
							System.out.println("Enter the itemId: ");
							String newItemToBeAdded = userInput.next();

							if(fullInventory.has(newItemToBeAdded)){
								System.out.println("Enter the required count: ");
								int quantity = userInput.nextInt();
								temporaryCart.addToCart(newItemToBeAdded, quantity);
							} else {
								break;
							}
							break;
						case "R":
							System.out.println("Enter the itemId: ");
							String newItemToBeRemoved = userInput.next();

							if(fullInventory.has(newItemToBeRemoved)){
								System.out.println("Enter the quantity to count: ");
								int quantity = userInput.nextInt();
								temporaryCart.removeFromCart(newItemToBeRemoved, quantity);
							} else {
								break;
							}
							break;
						case "C":
							HashMap<String, Integer> itemsToPull = temporaryCart.checkout();
							Iterator<String> processCartItemsItr = itemsToPull.keySet().iterator();
							JSONObject backOrderedItems = new JSONObject();
							JSONObject takeHomeItems = new JSONObject();

							while(processCartItemsItr.hasNext()){
								String targetItemSku = processCartItemsItr.next();

								int[] pullResults = inventory.pullItemBySku(targetItemSku, itemsToPull.get(targetItemSku));
								if(pullResults[1] > 0){
									backOrderedItems.put(targetItemSku, pullResults[1]);
								}

								takeHomeItems.put(targetItemSku, pullResults[0]);
							}

							if(backOrderedItems.length() > 0){
								String orderId = orders.createNewOrderNumber(backOrderedItems);
								System.out.println("Purchase Receipt [Carryout]");
								System.out.println(takeHomeItems.toString());
								System.out.println(ConsoleColors.YELLOW);
								System.out.println("Purchase Receipt [BackOrder]");
								System.out.println(backOrderedItems.toString());
								System.out.println(ConsoleColors.GREEN);
							} else {
								System.out.println(ConsoleColors.CYAN_BACKGROUND);
								System.out.println(ConsoleColors.WHITE);
								System.out.println("Purchase Receipt - Carryout");
								System.out.println(temporaryCart.toString());
								System.out.println(ConsoleColors.GREEN);
							}
							break;
						case "e":
							newOrderFlag = false;
							break;
						default:
							System.out.println(ConsoleColors.RED);
							System.out.println("Invalid selection");
							System.out.println(ConsoleColors.GREEN);
					}
				}
				break;
			case 4:
				HashMap<String, Integer> remainingInventory = orders.reviewAllActiveOrders(inventory.printInventoryData());
				HashMap<String, Integer> updatedInventory = orders.reviewAllPendingOrders(remainingInventory);

				if(inventory.printInventoryData().equals(updatedInventory) == false) {
					inventory.updateInventory(updatedInventory);
				}
				break;
			case 5:
				while(true){
					System.out.println(ConsoleColors.GREEN);
					System.out.println("Enter the item sku(Enter e to exit): ");
					String itemSku = userInput.next();

					if(itemSku.equals("e")){
						break;
					}
					System.out.println("Enter the added quantity: ");
					int addedInventory = userInput.nextInt();
					inventory.pushItemBySku(itemSku, addedInventory);
				}
				break;
			case 6:
				while(true){
					System.out.println(ConsoleColors.GREEN);
					System.out.println("Enter the item sku(Enter e to exit): ");
					String itemSku = userInput.next();

					if(itemSku.equals("e")){
						break;
					}
					System.out.println("(1 - Enable ordering/2 - Diable ordering: ");
					int optionSelected = userInput.nextInt();

					switch (optionSelected){
						case 1:
							inventory.changeOrderingAvailability(itemSku, true);
							break;
						case 2:
							inventory.changeOrderingAvailability(itemSku, false);
							break;
						default:
							System.out.println(ConsoleColors.RED);
							System.out.println("Invalid selection");
							System.out.println(ConsoleColors.GREEN);
							break;
					}

				}
				break;
			case 7:
				while(true){
					System.out.println("Enter the item sku(Enter e to exit): ");
					String itemSku = userInput.next();
					if(itemSku.equals("e")){
						break;
					}

					System.out.println(inventory.checkInventoryBySku(itemSku));
				}
				break;
			case 8:

				break;
			default:
				System.out.println(ConsoleColors.RED);
				System.out.println("Invalid selection");
				System.out.println(ConsoleColors.RESET);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String filepath = "C:\\Users\\chris\\PycharmProjects\\cus720project\\pricingsheet.xlsx";
		try {
			inventory = InventoryManager.getInstance();
			inventory.loadInventoryFromExcel(filepath);
			while(true){
				menu();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
