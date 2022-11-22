package com.sjumasters.cus720project;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class InventoryManager {
	private static Timestamp lastUpdated;
	private static int updateInterval;
	private static HashMap<String, Item> inventory = new HashMap<String, Item>();
	private static String dataFilePath;
	
	private static InventoryManager instance;
	
	private InventoryManager() {
		
	}
	
	public static InventoryManager getInstance() {
		if(instance == null) {
			instance = new InventoryManager();
		}
		
		return instance; 
	}
	
	public void changeOrderingAvailability(String sku, boolean newstate) {
		Item targetItem = (Item)inventory.get(sku);
		targetItem.setCanOrder(newstate);
		inventory.put(sku, targetItem);
		lastUpdated = new Timestamp(System.currentTimeMillis());
	}
	
	public JSONObject printInventoryCommonNames() {
		Iterator<String> inventorySkuItr = inventory.keySet().iterator();
		JSONObject inventoryJson = new JSONObject();
		while(inventorySkuItr.hasNext()) {
			Item temp = (Item)inventory.get(inventorySkuItr.next());
			inventoryJson.put(temp.sku, temp.commonName);
		}

		return inventoryJson;
	}

	public void updateInventory(HashMap<String, Integer> updatedInventory){
		Iterator<String> itemSkus = updatedInventory.keySet().iterator();
		System.out.println(itemSkus.toString());
		if(updatedInventory.isEmpty() == false) {
			while (itemSkus.hasNext()) {
				String itemSku = itemSkus.next();
				Item tempItem = inventory.get(itemSku);
				tempItem.setOnHand(updatedInventory.get(itemSku));
				inventory.put(itemSku, tempItem);
			}
		}
	}

	public HashMap<String, Integer> printInventoryData() {
		Iterator<String> inventorySkuItr = inventory.keySet().iterator();
		HashMap<String, Integer> inventoryJson = new HashMap<String, Integer>();
		while(inventorySkuItr.hasNext()) {
			Item temp = (Item)inventory.get(inventorySkuItr.next());
			inventoryJson.put(temp.sku, temp.onHand);
		}

		return inventoryJson;
	}
	
	public void loadInventoryFromExcel(String filepath) {
		dataFilePath = filepath;
		System.out.println("Inventory data load started");
		
		try {
			FileInputStream excelFile = new FileInputStream(new File(filepath));
			System.out.println("File found");
			XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
			System.out.println("Loading workbook");
			XSSFSheet sheet = workbook.getSheetAt(0);
			System.out.println("Extracting inventory sheet");
			Iterator<Row> rowIterator = sheet.iterator();
			
			while(rowIterator.hasNext()) {
				Row row = rowIterator.next();
				
				if(row.getCell(0).getStringCellValue().contains("SKU")) {
					
				} else {
					String cellSku = row.getCell(0).getStringCellValue();
					System.out.println("Processing SKU: " + cellSku);
					String cellCommonName = row.getCell(1).getStringCellValue();
					String[] cellImageLinks;
							
					if (row.getCell(2) == null) {
						cellImageLinks = new String[1];
						cellImageLinks[0] = "None";
					} else {
						cellImageLinks = row.getCell(2).getStringCellValue().split("\n");
					}
					int cellOnHand = Integer.parseInt(row.getCell(3).getStringCellValue());
					int cellOnBackOrder = Integer.parseInt(row.getCell(4).getStringCellValue());
					boolean cellCanOrder = row.getCell(5).getBooleanCellValue();
					double cellCurrentPrice = row.getCell(6).getNumericCellValue();
					Item newItem = new Item(cellSku, cellCommonName, cellImageLinks, cellOnHand, cellOnBackOrder, cellCanOrder, cellCurrentPrice);
					inventory.put(cellSku, newItem);
				}
			}
			
			System.out.println("Inventory has been loaded");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Missing inventory file: " + filepath + " does not exist");
		}
		
		lastUpdated = new Timestamp(System.currentTimeMillis());
	}
	
	public void saveInventoryToExcel() {
		
		lastUpdated = new Timestamp(System.currentTimeMillis());
	}
	
	public JSONObject checkInventoryByTerm(String searchTerm) {
		Iterator<String> skuItr = inventory.keySet().iterator();
		JSONObject returnBlock = new JSONObject();
		
		while(skuItr.hasNext()) {
			String testString = skuItr.next();
			
			if(((Item)inventory.get(testString)).commonName.contains(searchTerm)) {
				returnBlock.append(testString, ((Item)inventory.get(testString)).onHand);
			}
		}
		
		return returnBlock;
	}
	
	public JSONObject checkInventoryBySku(String sku) {
		Iterator<String> skuItr = inventory.keySet().iterator();
		JSONObject returnBlock = new JSONObject();
		
		while(skuItr.hasNext()) {
			String testString = skuItr.next();
			
			if(testString.contains(sku)) {
				returnBlock.append(testString, ((Item)inventory.get(testString)).onHand);
			}
		}
		
		return returnBlock;
	}
	
	public int[] pullItemBySku(String sku, int quantity) {
		int[] values = { 0, 0 };
		
		Item targetItem = (Item)inventory.get(sku);
		int backOrdered = targetItem.pull(quantity);
		
		if(backOrdered == 0) {
			values[0] = quantity;
			inventory.put(sku, targetItem);
			lastUpdated = new Timestamp(System.currentTimeMillis());
			return values;
		}
		
		if(backOrdered == -1) {
			return values;
		} else {
			values[0] = quantity - backOrdered;
			values[1] = backOrdered;
			inventory.put(sku, targetItem);
			lastUpdated = new Timestamp(System.currentTimeMillis());
			return values;
		}
	}
	
	
	public void pushItemBySku(String sku, int quantity) {
		Item targetItem = (Item)inventory.get(sku);
		targetItem.addItems(quantity);
		inventory.put(sku, targetItem);
		lastUpdated = new Timestamp(System.currentTimeMillis());
	}
	
	
}
