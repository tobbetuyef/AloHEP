package org.hepforge.alohep.database;

public class VariableData
{
	private String title;
	private String unit;
	
	public VariableData(String title, String unit)
	{
		this.title = title;
		this.unit = unit;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
