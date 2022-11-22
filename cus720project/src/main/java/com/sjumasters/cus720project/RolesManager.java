package com.sjumasters.cus720project;

import org.json.JSONObject;

public class RolesManager {
	private static RolesManager instance = null;
	private static JSONObject rolePermissionsMap;
	
	private RolesManager() {
		//TODO Load permissions map here
	}
	
	public static RolesManager getInstance() {
		if(instance == null) {
			instance = new RolesManager();
		} 
		
		return instance;
	}
	
	private void loadRolePermissions() {
		
	}
	
	public JSONObject getRolePermissions(int roleId) {
		
		return new JSONObject();
	}
}
