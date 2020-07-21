package org.octoprint.api.model;

/**
 * Represents a 3d printer Axis (x,y,z) 
 * 
 * @author rweber
 */
public enum Axis {
	X("x"),
	Y("y"),
	Z("z");
	
	private String m_axis;
	Axis(String a){
		m_axis = a;
	}
	
	public String getAxis(){
		return m_axis;
	}
	
	/**
	 * A helper method to convert a passed in string value to an Axis enum 
	 * 
	 * @param t axis name as a string
	 * @return conversion of string to enum value
	 */
	public static org.octoprint.api.model.Axis getAxis(String t){
		if(t.toLowerCase().equals("x"))
		{
			return X;
		}
		else if(t.toLowerCase().equals("y"))
		{
			return Y;
		}
		else if(t.toLowerCase().equals("z"))
		{
			return Z;
		}
		
		return null;
	}
}
