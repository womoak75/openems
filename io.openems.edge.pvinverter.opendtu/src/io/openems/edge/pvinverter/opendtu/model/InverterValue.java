package io.openems.edge.pvinverter.opendtu.model;

import java.util.Objects;

public class InverterValue<T extends Number> {

	private T value;
	private int d;
	private InverterUnit unit;
	private Integer max;

	public InverterValue(T value, InverterUnit unit, int d, Integer max) {
		this.value = value;
		this.unit = unit;
		this.d = d;
		this.max = max;
	}

	public InverterValue(T value, InverterUnit unit, int d) {
		this(value, unit, d, null);
	}

	public boolean hasMax() {
		return max != null;
	}

	public T getValue() {
		return value;
	}

	public Integer toIntegerValue() {
		return value.intValue();
	}

	public Double toDoubleValue() {
		return value.doubleValue();
	}

	public Long toLongValue() {
		return value.longValue();
	}

	public int getD() {
		return d;
	}

	public InverterUnit getUnit() {
		return unit;
	}

	public Integer getMax() {
		return max;
	}

	@Override
	public int hashCode() {
		return Objects.hash(d, max, unit, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
		InverterValue other = (InverterValue) obj;
		return d == other.d && Objects.equals(max, other.max) && unit == other.unit
				&& Objects.equals(value, other.value);
	}

	// hack :)
	public boolean isMilli() {
		return this.unit.unitString.startsWith("m");
	}

	// hack :)
	public boolean isKilo() {
		return this.unit.unitString.startsWith("k");
	}

	// hack :)
	public Integer toMilli() {
		if (isKilo()) {
			return Double.valueOf(value.doubleValue() * 1000000).intValue();
		} else if (isMilli()) {
			return value.intValue();
		} else {
			return Double.valueOf(value.doubleValue() * 1000).intValue();
		}
	}

	// hack :)
	public Integer toKilo() {
		if (isMilli()) {
			return Double.valueOf(value.doubleValue() / 1000000).intValue();
		} else if (isKilo()) {
			return value.intValue();
		} else {
			return Double.valueOf(value.doubleValue() / 1000).intValue();
		}
	}

	@Override
	public String toString() {
		return "InverterValue [" + value + " " + unit + (max != null ? ", max=" + max : "") + ", d=" + d + "]";
	}

}
