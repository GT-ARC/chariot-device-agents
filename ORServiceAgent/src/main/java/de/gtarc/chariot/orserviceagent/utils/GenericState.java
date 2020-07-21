package de.gtarc.chariot.orserviceagent.utils;

import org.json.simple.JSONObject;

public class GenericState<T> {

	public int stateID;
	public String stateName;
	public String stateCategory;
	public T stateValue;
	public String stateDescription;
	
	public GenericState(int id, String name, String category, T value, String description) {
		this.stateID = id;
		this.stateName = name;
		this.stateCategory = category;
		this.stateValue = value;
		this.stateDescription = description;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSON() {
		JSONObject object = new JSONObject();
		object.put("stateName", this.stateName);
		object.put("stateCategory", this.stateCategory);
		object.put("stateValue", this.stateValue);
		object.put("stateDescription", this.stateDescription);
		return object;
	}
}