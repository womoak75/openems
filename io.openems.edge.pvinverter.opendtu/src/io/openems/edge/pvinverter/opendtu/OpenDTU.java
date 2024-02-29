package io.openems.edge.pvinverter.opendtu;

import io.openems.edge.common.channel.Doc;
import io.openems.edge.meter.api.ElectricityMeter;

public interface OpenDTU extends ElectricityMeter {
	public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
		;

		private final Doc doc;

		private ChannelId(Doc doc) {
			this.doc = doc;
		}

		@Override
		public Doc doc() {
			return this.doc;
		}
	}
}
