package io.openems.edge.pvinverter.opendtu.model;

public enum InverterUnit {
	NONE(""), V("V"), A("A"), W("W"), kW("kW"), kWh("kWh"), PERCENT("%"), Hz("Hz"), VAR("var"), Wh("Wh"),
	DEGREE_C("°C");

	InverterUnit(String unitString) {
		this.unitString = unitString;
	}

	String unitString;

	public static InverterUnit toUnit(String unitString) {
		for (var value : values()) {
			if (value.unitString.equals(unitString))
				return value;
		}
		return InverterUnit.NONE;
	}
};
