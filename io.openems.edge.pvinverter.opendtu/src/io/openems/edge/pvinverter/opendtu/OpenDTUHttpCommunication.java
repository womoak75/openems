package io.openems.edge.pvinverter.opendtu;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;

import io.openems.edge.bridge.http.api.BridgeHttp;
import io.openems.edge.pvinverter.opendtu.api.OpenDTUCommunication;
import io.openems.edge.pvinverter.opendtu.model.InverterResponse;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel.InverterSetLimit;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUModel;
import io.openems.edge.pvinverter.opendtu.util.OpenDTUConverter;

public class OpenDTUHttpCommunication implements OpenDTUCommunication {

	private final Logger logger = LoggerFactory.getLogger(OpenDTUHttpCommunication.class);
	private BridgeHttp bridgeHttp;
	private OpenDtuEndpoints apiEndpoints;
	private OpenDTUConverter converter;
	private Config config;

	public OpenDTUHttpCommunication(Config config, BridgeHttp bridgeHttp) {
		this.bridgeHttp = bridgeHttp;
		this.config = config;
		this.apiEndpoints = new OpenDtuEndpoints(config);
		this.converter = new OpenDTUConverter();
	}

	@Override
	public void requestInverterData(final Consumer<OpenDTUModel> callback) {
		this.logger.debug("requestInverterData");
		this.bridgeHttp.subscribeJsonCycle(config.cycleInterval(),
				this.apiEndpoints.getInverterLiveDataEndpoint(this.config.inverterSerial()).toEndpoint().url(),
				(json, ex) -> {
					if (ex == null) {
						callback.accept(this.converter.toInverterModel(json));
					} else {
						logger.error("getInverterLiveData:", ex);
						callback.accept(null);
					}
				});
	}

	@Override
	public void requestInverterLimitData(final Consumer<OpenDTUInverterLimitModel> callback) {
		this.logger.debug("requestInverterLimitData");
		this.bridgeHttp.requestJson(this.apiEndpoints.getLimitEndpoint().toEndpoint()).whenComplete((json, ex) -> {
			if (ex == null) {
				callback.accept(this.converter.toInverterLimitModel(json));
			} else {
				logger.error("getInverterLimit:", ex);
				callback.accept(null);
			}
		});
	}

	@Override
	public void setInverterLimit(InverterSetLimit limit, Consumer<InverterResponse> callback) {
		var endpoint = this.apiEndpoints.getLimitSetEndpoint(toBody(limit)).toEndpoint();
		this.logger.debug("setInverterLimit {} {}", endpoint.url(), this.converter.toInverterLimitJson(limit));
		this.bridgeHttp.requestJson(endpoint).whenComplete((json, ex) -> {
			if (ex == null) {
				this.logger.debug("setInverterLimit response: {}", json);
				callback.accept(this.converter.toInverterSetLimitResponse(json));
			} else {
				this.logger.error("setInverterLimit exception:", ex);
				callback.accept((InverterResponse) null);
			}
		});
	}

	private String toBody(InverterSetLimit model) {
		return toBody(this.converter.toInverterLimitJson(model));
	}

	private String toBody(JsonObject json) {
		return "data=" + json.toString();
	}

}
