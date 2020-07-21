package de.gtarc.chariot.orserviceagent.utils;

import org.json.simple.JSONObject;

public class GenericAction {
	
	public int actionID;
	public String actionName;
	public String actionCategory;
	public String actionMethod;
	public String actionMessage;
	public String actionDescription;
	public int associatedWith;
	
	public GenericAction(int id, String name, String category, String method, String message, String description, int associatedWith) {
		this.actionID = id;
		this.actionName = name;
		this.actionCategory = category;
		this.actionMessage = message;
		this.actionMethod = method;
		this.actionDescription = description;
		this.associatedWith = associatedWith;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("actionName", this.actionName);
		object.put("actionCategory", this.actionCategory);
		object.put("actionMethod", this.actionMethod);
		object.put("actionMessage", this.actionMessage);
		object.put("actionDescription", this.actionDescription);
		return object;
	}
}
