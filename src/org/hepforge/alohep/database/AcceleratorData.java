package org.hepforge.alohep.database;

import java.util.LinkedHashMap;

public class AcceleratorData {

	private LinkedHashMap <String, Double> variables;
	
	public AcceleratorData()
	{
		variables = new LinkedHashMap <String, Double>();
		
	}
	public void put(String key, Double value)
	{
		variables.put(key, value);
	}
	public LinkedHashMap <String, Double> getVariables() {
		return variables;
	}
	public void setVariables(LinkedHashMap <String, Double> variables) {
		this.variables = variables;
	}
}
