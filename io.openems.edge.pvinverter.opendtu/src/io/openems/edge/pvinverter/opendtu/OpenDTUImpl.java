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

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.edge.bridge.http.api.BridgeHttp;
import io.openems.edge.bridge.http.api.BridgeHttpFactory;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.meter.api.ElectricityMeter;
import io.openems.edge.meter.api.MeterType;
import io.openems.edge.meter.api.SinglePhase;
import io.openems.edge.pvinverter.api.ManagedSymmetricPvInverter;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterPhase;
import io.openems.edge.pvinverter.opendtu.util.OpenDTUConverter;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "io.openems.edge.pvinverter.opendtu", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE, //
})
public class OpenDTUImpl extends AbstractOpenemsComponent
		implements OpenDTU, ManagedSymmetricPvInverter, ElectricityMeter, OpenemsComponent, EventHandler {

	private final Logger logger = LoggerFactory.getLogger(OpenDTUImpl.class);
	private final OpenDTUConverter converter;

	@Reference
	private ConfigurationAdmin cm;

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	private BridgeHttpFactory httpBridgeFactory;
	private BridgeHttp httpBridge;

	@SuppressWarnings("unused")
	private Config config;

	private OpenDTUInverter inverter;

	public OpenDTUImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				OpenDTU.ChannelId.values(), //
				ManagedSymmetricPvInverter.ChannelId.values(), //
				ElectricityMeter.ChannelId.values() //
		);
		this.converter = new OpenDTUConverter();
	}

	@Activate
	private void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;
		this.httpBridge = this.httpBridgeFactory.get();
		this.inverter = new OpenDTUInverter(config, this.converter);
		this.inverter.setBridgeHttp(this.httpBridge);
		this.inverter.init();

		if (!this.isEnabled()) {
			return;
		}
		this.inverter.start();
	}

	@Deactivate
	protected void deactivate() {
		this.inverter.stop();
		if (this.httpBridge != null) {
			this.inverter.unsetBridgeHttp(httpBridge);
			this.httpBridgeFactory.unget(this.httpBridge);
			this.httpBridge = null;
		}
		this.inverter = null;
		super.deactivate();
	}

	@Override
	public void handleEvent(Event event) {
		if (!this.isEnabled()) {
			return;
		}
		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE:
			if (this.inverter.isInitialized()) {
				try {
					setPhase(SinglePhase.L1, this.inverter.getPhase(SinglePhase.L1));
					setPhase(SinglePhase.L2, this.inverter.getPhase(SinglePhase.L2));
					setPhase(SinglePhase.L3, this.inverter.getPhase(SinglePhase.L3));
					setTotal();
					setLimit();
				} catch (Exception e) {
					logger.error("setPhase: ", e);
				}
			}
			break;
		}

	}

	private void setTotal() {
		this._setActivePower(this.inverter.getPower().toIntegerValue());
		this._setActiveProductionEnergy(this.inverter.getProductionEnergy().toLongValue());
	}

	private void setLimit() {
		this._setActivePowerLimit(this.inverter.getActiveLimit());
		this._setMaxApparentPower(this.inverter.getMaxPower().toIntegerValue());
	}

	private void setPhase(SinglePhase phase, OpenDTUInverterPhase inverterPhase) {
		if (inverterPhase == null)
			return;
		switch (phase) {
		case L1:
			// all values seem to be milliX ?! is this true?
			this._setActivePowerL1(inverterPhase.power().toMilli());
			this._setReactivePowerL1(inverterPhase.reactivPower().toMilli());
			this._setActiveProductionEnergyL1(inverterPhase.energy().toMilli());
			this._setCurrentL1(inverterPhase.current().toMilli());
			this._setVoltageL1(inverterPhase.voltage().toMilli());
			break;
		case L2:
			this._setActivePowerL2(inverterPhase.power().toMilli());
			this._setReactivePowerL2(inverterPhase.reactivPower().toMilli());
			this._setActiveProductionEnergyL2(inverterPhase.energy().toMilli());
			this._setCurrentL2(inverterPhase.current().toMilli());
			this._setVoltageL2(inverterPhase.voltage().toMilli());
			break;
		case L3:
			this._setActivePowerL3(inverterPhase.power().toMilli());
			this._setReactivePowerL3(inverterPhase.reactivPower().toMilli());
			this._setActiveProductionEnergyL3(inverterPhase.energy().toMilli());
			this._setCurrentL3(inverterPhase.current().toMilli());
			this._setVoltageL3(inverterPhase.voltage().toMilli());
			break;
		default:
		}
	}

	@Override
	public void setActivePowerLimit(int value) throws OpenemsNamedException {
		this.inverter.setLimit(value);
	}

	@Override
	public void setActivePowerLimit(Integer value) throws OpenemsNamedException {
		this.inverter.setLimit(value);
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
