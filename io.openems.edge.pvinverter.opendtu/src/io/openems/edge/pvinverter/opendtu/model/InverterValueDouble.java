package io.openems.edge.pvinverter.opendtu.model;

public class InverterValueDouble extends InverterValue<Double> {

	public InverterValueDouble(Double value, InverterUnit unit, int d) {
		super(value, unit, d);
	}

	public InverterValueDouble(Double value, InverterUnit unit, int d, int max) {
		super(value, unit, d, max);
	}

	public Long getValueAsLong() {
		return getValue().longValue();
	}

	public Integer getValueAsInt() {
		return getValue().intValue();
	}

}
