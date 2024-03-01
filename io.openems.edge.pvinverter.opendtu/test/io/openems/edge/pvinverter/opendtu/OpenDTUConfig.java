package io.openems.edge.pvinverter.opendtu;

import io.openems.common.test.AbstractComponentConfig;
import io.openems.edge.meter.api.SinglePhase;
import io.openems.edge.pvinverter.opendtu.Config;

@SuppressWarnings("all")
public class OpenDTUConfig extends AbstractComponentConfig implements Config {

	protected static class Builder {
		private String id;
		private String inverterSerial;
		private String openDtuHost;
		private int openDtuPort;
		private String openDtuSchema;
		private String openDtuUser;
		private String openDtuPass;
		private int cycleInterval;
		private SinglePhase phase;

		private Builder() {
		}

		public Builder setId(String id) {
			this.id = id;
			return this;
		}

		public Builder setInverterSerial(String inverterSerial) {
			this.inverterSerial = inverterSerial;
			return this;
		}

		public Builder setOpenDtuHost(String openDtuHost) {
			this.openDtuHost = openDtuHost;
			return this;
		}

		public Builder setOpenDtuPort(int openDtuPort) {
			this.openDtuPort = openDtuPort;
			return this;
		}

		public Builder setOpenDtuSchema(String openDtuSchema) {
			this.openDtuSchema = openDtuSchema;
			return this;
		}

		public Builder setOpenDtuUser(String openDtuUser) {
			this.openDtuUser = openDtuUser;
			return this;
		}

		public Builder setOpenDtuPass(String openDtuPass) {
			this.openDtuPass = openDtuPass;
			return this;
		}
		
		public Builder setCycleInterval(int cycleInterval) {
			this.cycleInterval = cycleInterval;
			return this;
		}
		
		public Builder setPhase(SinglePhase phase) {
			this.phase = phase;
			return this;
		}
		

		public OpenDTUConfig build() {
			return new OpenDTUConfig(this);
		}

	}

	/**
	 * Create a Config builder.
	 * 
	 * @return a {@link Builder}
	 */
	public static Builder create() {
		return new Builder();
	}

	private final Builder builder;

	private OpenDTUConfig(Builder builder) {
		super(Config.class, builder.id);
		this.builder = builder;
	}

	@Override
	public String inverterSerial() {
		return this.builder.inverterSerial;
	}

	@Override
	public String openDtuHost() {
		return this.builder.openDtuHost;
	}

	@Override
	public int openDtuPort() {
		return this.builder.openDtuPort;
	}

	@Override
	public String openDtuSchema() {
		return this.builder.openDtuSchema;
	}

	@Override
	public String openDtuUser() {
		return this.builder.openDtuUser;
	}

	@Override
	public String openDtuPass() {
		return this.builder.openDtuPass;
	}
	
	@Override
	public int cycleInterval() {
		return this.builder.cycleInterval;
	}
	
	@Override
	public SinglePhase phase() {
		return this.builder.phase;
	}

}