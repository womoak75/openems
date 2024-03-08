package io.openems.edge.pvinverter.opendtu.api;

import java.util.function.Consumer;

import io.openems.edge.pvinverter.opendtu.model.InverterResponse;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel.InverterSetLimit;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUModel;

public interface OpenDTUCommunication {

	void requestInverterData(Consumer<OpenDTUModel> callback);

	void requestInverterLimitData(Consumer<OpenDTUInverterLimitModel> callback);

	void setInverterLimit(InverterSetLimit limit, Consumer<InverterResponse> callback);

}