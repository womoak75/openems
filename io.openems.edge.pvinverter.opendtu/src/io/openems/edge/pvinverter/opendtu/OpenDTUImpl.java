package io.openems.edge.pvinverter.opendtu;

import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.propertytypes.EventTopics;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.edge.bridge.http.api.BridgeHttp;
import io.openems.edge.bridge.http.api.BridgeHttpFactory;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.meter.api.MeterType;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "io.openems.edge.pvinerter.opendtu", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE	 //
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE, //
})
public class OpenDTUImpl extends AbstractOpenemsComponent implements OpenDTU, OpenemsComponent, EventHandler {

	private final Logger logger = LoggerFactory.getLogger(OpenDTUImpl.class);
	
	@Reference
	private ConfigurationAdmin cm;
	
	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	private BridgeHttpFactory httpBridgeFactory;
	private BridgeHttp httpBridge;

	private OpenDtuEndpoints api;
	private OpenDTUConverter converter;

	public OpenDTUImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				OpenDTU.ChannelId.values() //
		);
	}
	
	@Activate
	private void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.api = new OpenDtuEndpoints(config);
		this.converter = new OpenDTUConverter();
		this.httpBridge = this.httpBridgeFactory.get();

		if (!this.isEnabled()) {
			return;
		}
		this.httpBridge.subscribeJsonEveryCycle(api.getInverterLiveDataUrl(config.inverterSerial()), (json,ex) -> {
			if(ex!=null) {
				update(this.converter.toInverterModel(json));
			} else {
				logger.error("getInverterLiveData:", ex);
				update((OpenDTUModel)null);
			}
		});
		this.httpBridge.requestJson(api.getLimitEndpoint()).whenComplete((json,ex)->{
			if(ex!=null) {
				update(this.converter.toInverterLimitModel(json));
			} else {
				logger.error("getInverterLimit:", ex);
				update((OpenDTUInverterLimitModel)null);
			}
		});
	}

	private void update(OpenDTUInverterLimitModel inverterLimitModel) {
		// actual inverter limit
		logger.warn("implement me! {}",inverterLimitModel);
	}

	private void update(OpenDTUModel openDtuModel) {
		// inverter live data update
		if(openDtuModel!=null)
			this._setActiveProductionEnergy(openDtuModel.getTotal().getPower().getValueAsLong());
		else
			this._setActiveProductionEnergy(null);
	}

	@Deactivate
	protected void deactivate() {
		if (this.httpBridge != null) {
			this.httpBridgeFactory.unget(this.httpBridge);
			this.httpBridge = null;
		}
		super.deactivate();
	}

	@Override
	public void handleEvent(Event event) {
		if (!this.isEnabled()) {
			return;
		}
		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE:
			// TODO: fill channels
			break;
		}
	}

	@Override
	public String debugLog() {
		return "OpenDTUImpl";
	}

	@Override
	public MeterType getMeterType() {
		return MeterType.PRODUCTION;
	}
}
