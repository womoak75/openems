package io.openems.edge.pvinverter.opendtu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.openems.edge.pvinverter.opendtu.OpenDTUModel.InverterValue.InverterUnit;

public class OpenDTUModel {
	public static class OpenDTUInverter {
		private String name;
		private String serial;
		int order;
		int dataAge;
		boolean pollEnabled;
		boolean reachable;
		boolean producing;
		int limitRelative;
		int limitAbsolute;
		private List<AC> ac = new ArrayList<>();
		private List<DC> dc = new ArrayList<>();
		private INV inv;

		public OpenDTUInverter(String name, String serial) {
			this.name = name;
			this.serial = serial;
		}
		public String getName() {
			return name;
		}
		public String getSerial() {
			return serial;
		}
		public int getOrder() {
			return order;
		}
		public void setOrder(int order) {
			this.order = order;
		}
		public int getDataAge() {
			return dataAge;
		}
		public void setDataAge(int dataAge) {
			this.dataAge = dataAge;
		}
		public boolean isPollEnabled() {
			return pollEnabled;
		}
		public void setPollEnabled(boolean pollEnabled) {
			this.pollEnabled = pollEnabled;
		}
		public boolean isReachable() {
			return reachable;
		}
		public void setReachable(boolean reachable) {
			this.reachable = reachable;
		}
		public boolean isProducing() {
			return producing;
		}
		public void setProducing(boolean producing) {
			this.producing = producing;
		}
		public int getLimitRelative() {
			return limitRelative;
		}
		public void setLimitRelative(int limitRelative) {
			this.limitRelative = limitRelative;
		}
		public int getLimitAbsolute() {
			return limitAbsolute;
		}
		public void setLimitAbsolute(int limitAbsolute) {
			this.limitAbsolute = limitAbsolute;
		}
		public void addAC(AC ac) {
			this.ac.add(ac);
		}
		public List<AC> getAc() {
			return ac;
		}
		public void addDC(DC dc) {
			this.dc.add(dc);
		}
		public List<DC> getDc() {
			return dc;
		}
		public INV getInv() {
			return inv;
		}
		public void setInv(INV inv) {
			this.inv = inv;
		}
	}
	public static class AC {
		private int nr;
		private InverterValueDouble power;
		private InverterValueDouble voltage;
		private InverterValueDouble current;
		private InverterValueDouble frequency;
		private InverterValueDouble powerFactor;
		private InverterValueDouble reactivPower;
		public AC(int nr) {
			this.nr = nr;
			this.power = new InverterValueDouble(0d,InverterUnit.W,1);
			this.voltage = new InverterValueDouble(0d,InverterUnit.V,1);
			this.current = new InverterValueDouble(0d,InverterUnit.A,1);
			this.frequency = new InverterValueDouble(0d,InverterUnit.Hz,1);
			this.powerFactor = new InverterValueDouble(0d,InverterUnit.NONE,1);
			this.reactivPower = new InverterValueDouble(0d,InverterUnit.VAR,1);
		}
		public int getNr() {
			return this.nr;
		}
		public InverterValueDouble getPower() {
			return power;
		}
		public void setPower(InverterValueDouble power) {
			this.power = power;
		}
		public InverterValueDouble getVoltage() {
			return voltage;
		}
		public void setVoltage(InverterValueDouble voltage) {
			this.voltage = voltage;
		}
		public InverterValueDouble getCurrent() {
			return current;
		}
		public void setCurrent(InverterValueDouble current) {
			this.current = current;
		}
		public InverterValueDouble getFrequency() {
			return frequency;
		}
		public void setFrequency(InverterValueDouble frequency) {
			this.frequency = frequency;
		}
		public InverterValueDouble getPowerFactor() {
			return powerFactor;
		}
		public void setPowerFactor(InverterValueDouble powerFactor) {
			this.powerFactor = powerFactor;
		}
		public InverterValueDouble getReactivPower() {
			return reactivPower;
		}
		public void setReactivPower(InverterValueDouble reactivPower) {
			this.reactivPower = reactivPower;
		}
		
	}
	public static class DC {
		private int nr;
		private String name;
		private InverterValueDouble power;
		private InverterValueDouble voltage;
		private InverterValueDouble current;
		private InverterValueDouble yieldDay;
		private InverterValueDouble yieldTotal;
		private InverterValueDouble irradiation;
		public DC(int nr) {
			this.nr = nr;
		}
		public int getNr() {
			return this.nr;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public InverterValueDouble getPower() {
			return power;
		}
		public void setPower(InverterValueDouble power) {
			this.power = power;
		}
		public InverterValueDouble getVoltage() {
			return voltage;
		}
		public void setVoltage(InverterValueDouble voltage) {
			this.voltage = voltage;
		}
		public InverterValueDouble getCurrent() {
			return current;
		}
		public void setCurrent(InverterValueDouble current) {
			this.current = current;
		}

		public InverterValueDouble getYieldDay() {
			return yieldDay;
		}

		public void setYieldDay(InverterValueDouble yieldDay) {
			this.yieldDay = yieldDay;
		}

		public InverterValueDouble getYieldTotal() {
			return yieldTotal;
		}

		public void setYieldTotal(InverterValueDouble yieldTotal) {
			this.yieldTotal = yieldTotal;
		}

		public InverterValueDouble getIrradiation() {
			return irradiation;
		}

		public void setIrradiation(InverterValueDouble irradiation) {
			this.irradiation = irradiation;
		}
	}
	public static class InverterValue<T> {
		public enum InverterUnit { 
			NONE(""), V("V"), A("A"), W("W"), kW("kW"), kWh("kWh"), PERCENT("%"), Hz("Hz"), VAR("var"), Wh("Wh"), DEGREE_C("°C");
			InverterUnit(String unitString) {
				this.unitString = unitString;
			}
			private String unitString;
			public static InverterUnit toUnit(String unitString) {
				for(var value: values()) {
					if(value.unitString.equals(unitString))
						return value;
				}
				return InverterUnit.NONE;
			}
		};
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
			this(value,unit,d,null);
		}

		public boolean hasMax() {
			return max!=null;	
		}
		public T getValue() {
			return value;
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
		
		
		
	}
	public static class InverterValueDouble extends InverterValue<Double> {

		public InverterValueDouble(Double value, InverterUnit unit, int d) {
			super(value, unit, d);
		}

		public InverterValueDouble(Double value, InverterUnit unit, int d, int max) {
			super(value,unit,d,max);
		}
		public Long getValueAsLong() {
			return getValue().longValue();
		}
		public Integer getValueAsInt() {
			return getValue().intValue();
		}
		
	}
	public static class InverterValueLong extends InverterValue<Long> {

		public InverterValueLong(Long value, InverterUnit unit, int d) {
			super(value, unit, d);
		}
		public Double getValueAsDouble() {
			return getValue().doubleValue();
		}
	}
	public static class INV {
		private InverterValueDouble yieldDay;
		private InverterValueDouble yieldTotal;
		private InverterValueDouble temperature;
		private InverterValueDouble efficiency;
		private InverterValueDouble powerDc;
		public InverterValueDouble getYieldDay() {
			return yieldDay;
		}
		public void setYieldDay(InverterValueDouble yieldDay) {
			this.yieldDay = yieldDay;
		}
		public InverterValueDouble getYieldTotal() {
			return yieldTotal;
		}
		public void setYieldTotal(InverterValueDouble yieldTotal) {
			this.yieldTotal = yieldTotal;
		}
		public InverterValueDouble getTemperature() {
			return temperature;
		}
		public void setTemperature(InverterValueDouble temperature) {
			this.temperature = temperature;
		}
		public InverterValueDouble getEfficiency() {
			return efficiency;
		}
		public void setEfficiency(InverterValueDouble efficiency) {
			this.efficiency = efficiency;
		}
		public InverterValueDouble getPowerDc() {
			return powerDc;
		}
		public void setPowerDc(InverterValueDouble powerDc) {
			this.powerDc = powerDc;
		}
	}
	public static class Total {
		private InverterValueDouble yieldDay;
		private InverterValueDouble yieldTotal;
		private InverterValueDouble power;
		public InverterValueDouble getYieldDay() {
			return yieldDay;
		}
		public void setYieldDay(InverterValueDouble yieldDay) {
			this.yieldDay = yieldDay;
		}
		public InverterValueDouble getYieldTotal() {
			return yieldTotal;
		}
		public void setYieldTotal(InverterValueDouble yieldTotal) {
			this.yieldTotal = yieldTotal;
		}
		public InverterValueDouble getPower() {
			return power;
		}
		public void setPower(InverterValueDouble power) {
			this.power = power;
		}
	}
	public static class Hints {
		private boolean time_sync;
		private boolean radio_problem;
		private boolean default_password;
		public boolean isTimeSync() {
			return time_sync;
		}
		public void setTimeSync(boolean time_sync) {
			this.time_sync = time_sync;
		}
		public boolean isRadioProblem() {
			return radio_problem;
		}
		public void setRadioProblem(boolean radio_problem) {
			this.radio_problem = radio_problem;
		}
		public boolean isDefaultPassword() {
			return default_password;
		}
		public void setDefaultPassword(boolean default_password) {
			this.default_password = default_password;
		}
		
	}

	private Map<String,OpenDTUInverter> inverters = new HashMap<>();
	private Total total;
	private Hints hints;
	
	public Total getTotal() {
		return total;
	}
	public void setTotal(Total total) {
		this.total = total;
	}
	public void setInverters(Map<String,OpenDTUInverter> inverters) {
		this.inverters.clear();
		this.inverters.putAll(inverters);
	}
	public void addInverter(OpenDTUInverter inverter) {
		this.inverters.put(inverter.getSerial(),inverter);
	}
	public List<OpenDTUInverter> getInverters() {
		return new ArrayList<>(this.inverters.values());
	}
	public OpenDTUInverter getInverter(String inverterSerial) {
		return this.inverters.get(inverterSerial);
	}
	public Hints getHints() {
		return hints;
	}
	public void setHints(Hints hints) {
		this.hints = hints;
	}

	public static class InverterResponse {
		enum ResponseType { SUCCESS, ERROR };
		private ResponseType type;
		private String message; 
		private int code;
		public InverterResponse() {
			this.type = ResponseType.SUCCESS;
			this.message = "";
			this.code = 1001;
		}
		public boolean isOK() {
			return ResponseType.SUCCESS.equals(this.type);
		}
		public ResponseType getType() {
			return type;
		}
		public void setType(ResponseType type) {
			this.type = type;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
	}
}
