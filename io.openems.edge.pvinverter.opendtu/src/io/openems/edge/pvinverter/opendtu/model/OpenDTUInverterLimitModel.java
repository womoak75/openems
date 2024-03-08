package io.openems.edge.pvinverter.opendtu.model;

import java.util.HashMap;
import java.util.Map;

public class OpenDTUInverterLimitModel {
	public static class InverterSetLimit {
		public enum LimitType {
			AbsolutNonPersistent(0), // 0x0000, // 0
			RelativNonPersistent(1), // 0x0001, // 1
			AbsolutPersistent(266), // 0x0100, // 256
			RelativPersistent(257); // = 0x0101 // 257

			LimitType(int i) {
				this.t = i;
			}

			public int toLimit() {
				return t;
			}

			private int t;
		}

		private long serial;
		private LimitType limit_type;
		private int limit_value;

		public InverterSetLimit(String serial) {
			this(Long.parseLong(serial));
		}

		public InverterSetLimit(long serial) {
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

		public long getSerial() {
			return serial;
		}
	}

	public static class InverterLimit {
		public enum InverterLimitStatus {
			Unknown, Ok, Pending, Failure
		};

		private String serial;
		private Integer limitRelative;
		private Integer maxPower;
		private InverterLimitStatus limitSetStatus;

		public InverterLimit(String serial, Integer limitRelative, Integer maxPower,
				InverterLimitStatus limitSetStatus) {
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

		public Integer getLimitRelative() {
			return limitRelative;
		}

		public void setLimitRelative(Integer limitRelative) {
			this.limitRelative = limitRelative;
		}

		public Integer getMaxPower() {
			return maxPower;
		}

		public void setMaxPower(Integer maxPower) {
			this.maxPower = maxPower;
		}

		public InverterLimitStatus getLimitSetStatus() {
			return limitSetStatus;
		}

		public void setLimitSetStatus(InverterLimitStatus limitSetStatus) {
			this.limitSetStatus = limitSetStatus;
		}

		@Override
		public String toString() {
			return "InverterLimit [serial=" + serial + ", limitRelative=" + limitRelative + ", maxPower=" + maxPower
					+ ", limitSetStatus=" + limitSetStatus + "]";
		}

	}

	private Map<String, InverterLimit> inverterLimits = new HashMap<>();

	public OpenDTUInverterLimitModel() {
	}

	public void addInverterLimit(InverterLimit inverterLimit) {
		this.inverterLimits.put(inverterLimit.getSerial(), inverterLimit);
	}

	public InverterLimit getInverterLimit(String serial) {
		return this.inverterLimits.get(serial);
	}

	public Map<String, InverterLimit> getInverterLimits() {
		return this.inverterLimits;
	}

	public void setInverterLimits(Map<String, InverterLimit> inverterLimits) {
		this.inverterLimits.clear();
		this.inverterLimits.putAll(inverterLimits);
	}

	@Override
	public String toString() {
		return "OpenDTUInverterLimitModel [inverterLimits=" + inverterLimits + "]";
	}

}
