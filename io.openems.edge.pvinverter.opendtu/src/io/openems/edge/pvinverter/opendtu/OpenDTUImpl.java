package io.openems.edge.pvinverter.opendtu;

import java.util.function.BiConsumer;

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
import io.openems.edge.common.channel.IntegerWriteChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.meter.api.ElectricityMeter;
import io.openems.edge.meter.api.SinglePhase;
import io.openems.edge.pvinverter.api.ManagedSymmetricPvInverter;
import io.openems.edge.pvinverter.opendtu.OpenDTUInverter.OpenDTUInverterEvent;
import io.openems.edge.pvinverter.opendtu.OpenDTUInverter.OpenDTUInverterObserver;
import io.openems.edge.pvinverter.opendtu.api.OpenDTU;
import io.openems.edge.pvinverter.opendtu.api.OpenDTUCommunication;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterPhase;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "io.openems.edge.pvinverter.opendtu", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE //
})
public class OpenDTUImpl extends AbstractOpenemsComponent
		implements OpenDTU, ManagedSymmetricPvInverter, ElectricityMeter, OpenemsComponent, OpenDTUInverterObserver,
		EventHandler, BiConsumer<Value<Integer>, Value<Integer>> {

	private final Logger logger = LoggerFactory.getLogger(OpenDTUImpl.class);

	@Reference
	private ConfigurationAdmin cm;

	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	private BridgeHttpFactory httpBridgeFactory;
	private BridgeHttp httpBridge;

	@SuppressWarnings("unused")
	private Config config;

	private OpenDTUInverter inverter;
	private OpenDTUCommunication communication;
	final private IntegerWriteChannel powerLimitChannel;

	public OpenDTUImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				OpenDTU.ChannelId.values(), //
				ManagedSymmetricPvInverter.ChannelId.values(), //
				ElectricityMeter.ChannelId.values() //
		);
		powerLimitChannel = this.channel(ManagedSymmetricPvInverter.ChannelId.ACTIVE_POWER_LIMIT);
	}

	@Activate
	private void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;
		this.httpBridge = this.httpBridgeFactory.get();
		this.communication = new OpenDTUHttpCommunication(config, this.httpBridge);
		this.inverter = new OpenDTUInverter(config, this.communication);
		this.inverter.register(this);
		this.inverter.init();
		initInverterData();
		this.powerLimitChannel.onChange(this);

		if (!this.isEnabled()) {
			return;
		}
		this.inverter.start();
	}

	@Deactivate
	protected void deactivate() {
		this.inverter.stop();
		this.inverter.unregister(this);
		if (this.httpBridge != null) {
			this.httpBridgeFactory.unget(this.httpBridge);
			this.httpBridge = null;
		}
		this.powerLimitChannel.removeOnChangeCallback(this);
		this.inverter = null;
		this.communication = null;
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
				this.inverter.cycle();
			}
			break;
		}
	}

	@Override
	public void accept(Value<Integer> oldLimit, Value<Integer> newLimit) {
		logger.debug("POWERLIMIT onChange: {} - {}", oldLimit, newLimit);
		if (this.inverter.getInverterLimit().isPending()
				|| isActiveLimit(this.inverter.getInverterLimit().getLimit(), newLimit.get())) {
			// setChannelLimit(this.inverter.getInverterLimit());
			return;
		}
		try {
			setActivePowerLimit(newLimit.get());
		} catch (OpenemsNamedException e) {
			logger.error("exception wile setActivePowerLimit({})", newLimit);
			setChannelLimit(null);
		}
	}

	@Override
	public void update(OpenDTUInverterEvent event, OpenDTUInverter inverter) {
		if (event == OpenDTUInverterEvent.DATA_UPDATE)
			setInverterData();
	}

	private boolean isActiveLimit(Integer limit, Integer newLimit) {
		if (limit == null && newLimit == null)
			return true;
		if (limit != null && newLimit != null) {
			if (limit.equals(newLimit))
				return true;
		}
		return false;
	}

	private void initInverterData() {
		setChannelPhaseError(SinglePhase.L1);
		setChannelPhaseError(SinglePhase.L2);
		setChannelPhaseError(SinglePhase.L3);
		setChannelTotal(null);
		setChannelLimit(null);
	}

	private void setInverterData() {
		try {
			setChannelPhase(SinglePhase.L1, this.inverter.getPhase(SinglePhase.L1));
			setChannelPhase(SinglePhase.L2, this.inverter.getPhase(SinglePhase.L2));
			setChannelPhase(SinglePhase.L3, this.inverter.getPhase(SinglePhase.L3));
			setChannelTotal(this.inverter);
		} catch (Exception e) {
			logger.error("setPhase: ", e);
		}
	}

	private void setChannelTotal(OpenDTUInverter inv) {
		this._setActivePower(inv == null ? null : inv.getPower().toIntegerValue());
		this._setActiveProductionEnergy(inv == null ? null : inv.getProductionEnergy().toLongValue());
		this._setActiveConsumptionEnergy(null);
	}

	private void setChannelLimit(OpenDTUInverterLimit inverterLimit) {
		logger.debug("setChannelLimit {}", inverterLimit);
		this._setActivePowerLimit(inverterLimit == null ? null : inverterLimit.getLimit());
		this._setMaxApparentPower(inverterLimit == null ? null : inverterLimit.getMaxPower().toIntegerValue());
	}

	private void setChannelPhaseError(SinglePhase phase) {
		setPhase(true, phase, null);
	}

	private void setChannelPhase(SinglePhase phase, OpenDTUInverterPhase inverterPhase) {
		setPhase(false, phase, inverterPhase);
	}

	private void setPhase(boolean error, SinglePhase phase, OpenDTUInverterPhase inverterPhase) {
		if (inverterPhase == null)
			return;
		switch (phase) {
		case L1:
			// all values seem to be milliX ?! is this true?
			this._setActivePowerL1(error ? null : inverterPhase.power().toIntegerValue());
			this._setReactivePowerL1(error ? null : inverterPhase.reactivPower().toIntegerValue());
			this._setActiveProductionEnergyL1(error ? null : inverterPhase.energy().toIntegerValue());
			this._setActiveConsumptionEnergyL1(0);
			this._setCurrentL1(error ? null : inverterPhase.current().toIntegerValue());
			this._setVoltageL1(error ? null : inverterPhase.voltage().toIntegerValue());
			break;
		case L2:
			this._setActivePowerL2(error ? null : inverterPhase.power().toIntegerValue());
			this._setReactivePowerL2(error ? null : inverterPhase.reactivPower().toIntegerValue());
			this._setActiveProductionEnergyL2(error ? null : inverterPhase.energy().toIntegerValue());
			this._setActiveConsumptionEnergyL2(0);
			this._setCurrentL2(error ? null : inverterPhase.current().toIntegerValue());
			this._setVoltageL2(error ? null : inverterPhase.voltage().toIntegerValue());
			break;
		case L3:
			this._setActivePowerL3(error ? null : inverterPhase.power().toIntegerValue());
			this._setReactivePowerL3(error ? null : inverterPhase.reactivPower().toIntegerValue());
			this._setActiveProductionEnergyL3(error ? null : inverterPhase.energy().toIntegerValue());
			this._setActiveConsumptionEnergyL3(0);
			this._setCurrentL3(error ? null : inverterPhase.current().toIntegerValue());
			this._setVoltageL3(error ? null : inverterPhase.voltage().toIntegerValue());
			break;
		default:
		}
	}

	@Override
	public void setActivePowerLimit(int value) throws OpenemsNamedException {
		logger.debug("setActivePowerLimit: {}", value);
		this.inverter.setLimit(value, this::callback);
	}

	@Override
	public void setActivePowerLimit(Integer value) throws OpenemsNamedException {
		logger.debug("setActivePowerLimit: {}", value);
		this.inverter.setLimit(value, this::callback);
	}

	private void callback(OpenDTUInverterLimit limit) {
		logger.debug("OpenDTUInverterLimit.callback {}", limit);
		if (limit.isActive()) {
			setChannelLimit(limit);
		} else {
			setChannelLimit(null);
		}
	}

	@Override
	public String debugLog() {
		return "OpenDTUImpl";
	}

}
