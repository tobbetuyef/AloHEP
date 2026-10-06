package org.hepforge.alohep.database;

import java.util.LinkedHashMap;

public class ParticleData 
{
	public static enum Type {LINEAR, CIRCLE}
	private Type type = Type.CIRCLE;
	private boolean typeVisible = false;
	private LinkedHashMap<String, AcceleratorData> accelerators;
	
	public ParticleData()
	{
		accelerators = new LinkedHashMap<String, AcceleratorData>();
		
	}
	public void put(String key, AcceleratorData acceleratorData)
	{
		accelerators.put(key, acceleratorData);
	}
	public LinkedHashMap<String, AcceleratorData> getAccelerators() {
		return accelerators;
	}
	public void setAccelerators(LinkedHashMap<String, AcceleratorData> accelerators) {
		this.accelerators = accelerators;
	}
	public void setLinear()
	{
		setType(Type.LINEAR);
	}
	public void setCircle()
	{
		setType(Type.CIRCLE);
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public boolean isTypeVisible() {
		return typeVisible;
	}
	public void setTypeVisible(boolean typeVisible) {
		this.typeVisible = typeVisible;
	}
	
}
