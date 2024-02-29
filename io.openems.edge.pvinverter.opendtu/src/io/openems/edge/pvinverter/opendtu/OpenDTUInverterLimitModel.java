package io.openems.edge.pvinverter.opendtu;

import java.util.HashMap;
import java.util.Map;

public class OpenDTUInverterLimitModel {
	public static class InverterSetLimit {
		public enum LimitType {
			AbsolutNonPersistent(0), // 0x0000, // 0
			    RelativNonPersistent(1), // 0x0001, // 1
			    AbsolutPersistent(266), //0x0100, // 256
			    RelativPersistent(257); // = 0x0101 // 257
			LimitType(int i) {
				this.t = i;
			}
			public int toLimit() {
				return t;
			}
			private int t;
		}
		private String serial;
		private LimitType limit_type;
		private int limit_value;
		InverterSetLimit(String serial) {
			this.serial = serial;
			this.limit_type = LimitType.RelativNonPersistent;
			this.limit_value = 100;
		}
		public LimitType getLimit_type() {
			return limit_type;
		}
		public void setLimit_type(LimitType limit_type) {
			this.limit_type = limit_type;
		}
		public int getLimit_value() {
			return limit_value;
		}
		public void setLimit_value(int limit_value) {
			this.limit_value = limit_value;
		}
		public String getSerial() {
			return serial;
		}
	}

	public static class InverterLimit {
		private String serial;
		private int limitRelative;
		private int maxPower;
		private boolean limitSetStatus;
		
		public InverterLimit(String serial, int limitRelative, int maxPower, boolean limitSetStatus) {
			this.serial = serial;
			this.limitRelative = limitRelative;
			this.maxPower = maxPower;
			this.limitSetStatus = limitSetStatus;
		}

		public String getSerial() {
			return serial;
		}

		public void setSerial(String serial) {
			this.serial = serial;
		}

		public int getLimitRelative() {
			return limitRelative;
		}

		public void setLimitRelative(int limitRelative) {
			this.limitRelative = limitRelative;
		}

		public int getMaxPower() {
			return maxPower;
		}

		public void setMaxPower(int maxPower) {
			this.maxPower = maxPower;
		}

		public boolean isLimitSetStatus() {
			return limitSetStatus;
		}

		public void setLimitSetStatus(boolean limitSetStatus) {
			this.limitSetStatus = limitSetStatus;
		}
		
	}
	private Map<String,InverterLimit> inverterLimits = new HashMap<>();
	
	public OpenDTUInverterLimitModel() {	}

	public void addInverterLimit(InverterLimit inverterLimit) {
		this.inverterLimits.put(inverterLimit.getSerial(), inverterLimit);
	}
	
	public InverterLimit getInverterLimit(String serial) {
		return this.inverterLimits.get(serial);
	}
	
	public Map<String,InverterLimit> getInverterLimits() {
		return this.inverterLimits;
	}

	public void setInverterLimits(Map<String,InverterLimit> inverterLimits) {
		this.inverterLimits.clear();
		this.inverterLimits.putAll(inverterLimits);
	}

}
