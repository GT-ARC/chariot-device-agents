package org.octoprint.api.model;

public enum FileType {
	MODEL,MACHINECODE,FOLDER;
	
	@Override
	public String toString(){
		return this.name().toLowerCase();
	}
	
	public static org.octoprint.api.model.FileType findType(String t){
		org.octoprint.api.model.FileType result = null;
		
		if(t.equals(FOLDER.toString()))
		{
			result = FOLDER;
		}
		else if(t.equals(MACHINECODE.toString()))
		{
			result = MACHINECODE;
		}
		else
		{
			result = MODEL;
		}
		
		return result;
	}
}
