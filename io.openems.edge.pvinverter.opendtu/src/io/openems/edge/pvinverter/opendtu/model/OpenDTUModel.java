package io.openems.edge.pvinverter.opendtu.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenDTUModel {
	public static class OpenDTUInverterModel {
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

		public OpenDTUInverterModel(String name, String serial) {
			this.name = name;
			this.serial = serial;
		}

		public OpenDTUInverterModel(String serial) {
			this.serial = serial;
		}

		public String getName() {
			return name;
		}

		public void getName(String name) {
			this.name = name;
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

		@Override
		public String toString() {
			return "OpenDTUInverterModel [name=" + name + ", serial=" + serial + ", order=" + order + ", dataAge="
					+ dataAge + ", pollEnabled=" + pollEnabled + ", reachable=" + reachable + ", producing=" + producing
					+ ", limitRelative=" + limitRelative + ", limitAbsolute=" + limitAbsolute + ", ac=" + ac + ", dc="
					+ dc + ", inv=" + inv + "]";
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
			this.power = new InverterValueDouble(0d, InverterUnit.W, 1);
			this.voltage = new InverterValueDouble(0d, InverterUnit.V, 1);
			this.current = new InverterValueDouble(0d, InverterUnit.A, 1);
			this.frequency = new InverterValueDouble(0d, InverterUnit.Hz, 1);
			this.powerFactor = new InverterValueDouble(0d, InverterUnit.NONE, 1);
			this.reactivPower = new InverterValueDouble(0d, InverterUnit.VAR, 1);
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

		@Override
		public String toString() {
			return "AC [nr=" + nr + ", power=" + power + ", voltage=" + voltage + ", current=" + current
					+ ", frequency=" + frequency + ", powerFactor=" + powerFactor + ", reactivPower=" + reactivPower
					+ "]";
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

		@Override
		public String toString() {
			return "DC [nr=" + nr + ", name=" + name + ", power=" + power + ", voltage=" + voltage + ", current="
					+ current + ", yieldDay=" + yieldDay + ", yieldTotal=" + yieldTotal + ", irradiation=" + irradiation
					+ "]";
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

		@Override
		public String toString() {
			return "INV [yieldDay=" + yieldDay + ", yieldTotal=" + yieldTotal + ", temperature=" + temperature
					+ ", efficiency=" + efficiency + ", powerDc=" + powerDc + "]";
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

		@Override
		public String toString() {
			return "Total [yieldDay=" + yieldDay + ", yieldTotal=" + yieldTotal + ", power=" + power + "]";
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

		@Override
		public String toString() {
			return "Hints [time_sync=" + time_sync + ", radio_problem=" + radio_problem + ", default_password="
					+ default_password + "]";
		}

	}

	private Map<String, OpenDTUInverterModel> inverters = new HashMap<>();
	private Total total;
	private Hints hints;

	public Total getTotal() {
		return total;
	}

	public void setTotal(Total total) {
		this.total = total;
	}

	public void setInverters(Map<String, OpenDTUInverterModel> inverters) {
		this.inverters.clear();
		this.inverters.putAll(inverters);
	}

	public void addInverter(OpenDTUInverterModel inverter) {
		this.inverters.put(inverter.getSerial(), inverter);
	}

	public List<OpenDTUInverterModel> getInverters() {
		return new ArrayList<>(this.inverters.values());
	}

	public OpenDTUInverterModel getInverter(String inverterSerial) {
		return this.inverters.get(inverterSerial);
	}

	public Hints getHints() {
		return hints;
	}

	public void setHints(Hints hints) {
		this.hints = hints;
	}

	@Override
	public String toString() {
		return "OpenDTUModel [inverters=" + inverters + ", total=" + total + ", hints=" + hints + "]";
	}

}
