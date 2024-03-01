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
import io.openems.edge.pvinverter.opendtu.OpenDTUInverterLimitModel.InverterSetLimit;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.AC;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.InverterResponse;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.OpenDTUInverter;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "io.openems.edge.pvinverter.opendtu", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE	 //
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_BEFORE_PROCESS_IMAGE, //
})
public class OpenDTUImpl extends AbstractOpenemsComponent implements OpenDTU, ManagedSymmetricPvInverter, ElectricityMeter, OpenemsComponent, EventHandler {

	private final Logger logger = LoggerFactory.getLogger(OpenDTUImpl.class);
	
	@Reference
	private ConfigurationAdmin cm;
	
	@Reference(cardinality = ReferenceCardinality.MANDATORY)
	private BridgeHttpFactory httpBridgeFactory;
	private BridgeHttp httpBridge;

	private OpenDtuEndpoints api;
	private OpenDTUConverter converter;
	private Config config;

	private final AC nullAC;

	public OpenDTUImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				OpenDTU.ChannelId.values(), //
				ManagedSymmetricPvInverter.ChannelId.values(), //
				ElectricityMeter.ChannelId.values() //
		);
		this.nullAC = new AC(3);
	}
	
	@Activate
	private void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;
		this.api = new OpenDtuEndpoints(config);
		this.converter = new OpenDTUConverter();
		this.httpBridge = this.httpBridgeFactory.get();
		init();
		
		if (!this.isEnabled()) {
			return;
		}
		startInverterPolling();
		requestActPowerLimit();
	}
	
	private void startInverterPolling() {
		this.httpBridge.subscribeJsonCycle(config.cycleInterval(), api.getInverterLiveDataUrl(config.inverterSerial()), (json,ex) -> {
			if(ex==null) {
				update(this.converter.toInverterModel(json));
			} else {
				logger.error("getInverterLiveData:", ex);
				update((OpenDTUModel)null);
			}
		});
	}

	private void requestActPowerLimit() {
		this.httpBridge.requestJson(api.getLimitEndpoint()).whenComplete((json,ex)->{
			if(ex==null) {
				update(this.converter.toInverterLimitModel(json));
			} else {
				logger.error("getInverterLimit:", ex);
				update((OpenDTUInverterLimitModel)null);
			}
		});
	}
	
	private void init() {
		this._setActivePowerL1(0);
		this._setReactivePowerL1(0);
		this._setCurrentL1(0);
		this._setVoltageL1(0);
		this._setActiveProductionEnergyL1(0);
		this._setActivePowerL2(0);
		this._setActiveProductionEnergyL2(0);
		this._setActivePowerL3(0);
		this._setActiveProductionEnergyL3(0);
		this._setActivePower(0);
		this._setActiveProductionEnergy(0);
		this._setActivePowerLimit(0);
		this._setMaxApparentPower(0);
	}
	
	private void update(OpenDTUModel inverterModel) {
		var inverter = inverterModel.getInverter(this.config.inverterSerial());
		if(inverter==null) {
			logger.warn("no data for inverter "+this.config.inverterSerial());
			return;
		}
		var acs = inverter.getAc();
		// not sure about this
		// 1-phase hoymiles -> 1 element in ac list
		// and 3-phase models -> 3 elements in ac list ???
		if(acs.size()==1) {
			update1Phase(inverter);
		} else if(acs.size()==3) {
			update3Phase(inverter);
		} else {
			logger.warn("inverter data contains odd number of ac elements");
		}
	}

	private void update3Phase(OpenDTUInverter inverter) {
		logger.warn("3-phase inverter functionality untested!");
		var ac0 = inverter.getAc().get(0);
		var ac1 = inverter.getAc().get(1);
		var ac2 = inverter.getAc().get(2);
		// TODO: where/how to get energy / phase
		setPhase(SinglePhase.L1, ac0, 0l);
		setPhase(SinglePhase.L2, ac1, 0l);
		setPhase(SinglePhase.L3, ac2, 0l);
		this._setActivePower(
				ac0.getPower().getValueAsInt()+
				ac1.getPower().getValueAsInt()+
				ac2.getPower().getValueAsInt());
		this._setActiveProductionEnergy(inverter.getInv().getYieldDay().getValueAsInt());
	}
	
	private void setPhase(SinglePhase phase, AC ac, Long energy) {
		var power = ac.getPower().getValueAsInt();
		var reactivePower = ac.getReactivPower().getValueAsInt();
		var voltage = ac.getVoltage().getValueAsInt();
		var current = ac.getCurrent().getValueAsInt();
		switch(phase) {
			case L1:
				this._setActivePowerL1(power);
				this._setReactivePowerL1(reactivePower);
				this._setActiveProductionEnergyL1(energy);
				this._setCurrentL1(current);
				this._setVoltageL1(voltage);
				break;
			case L2:
				this._setActivePowerL2(power);
				this._setReactivePowerL2(reactivePower);
				this._setActiveProductionEnergyL2(energy);
				this._setCurrentL2(current);
				this._setVoltageL2(voltage);
				break;
			case L3:
				this._setActivePowerL3(power);
				this._setReactivePowerL3(reactivePower);
				this._setActiveProductionEnergyL3(energy);
				this._setCurrentL3(current);
				this._setVoltageL3(voltage);
				break;
			default:
		}
	}

	private void update1Phase(OpenDTUInverter inverter) {
		var ac = inverter.getAc().get(0);
		var power = ac.getPower().getValueAsInt();
		var energy = inverter.getInv().getYieldDay().getValueAsLong();
		logger.info("inverter[{}] power={}, energy={}",inverter.getName(),power,energy);
		switch(this.config.phase()) {
			case L1:
				setPhase(SinglePhase.L1, ac, energy);
				setPhase(SinglePhase.L2, nullAC, 0l);
				setPhase(SinglePhase.L3, nullAC, 0l);
				break;
			case L2:
				setPhase(SinglePhase.L1, nullAC, 0l);
				setPhase(SinglePhase.L2, ac, energy);
				setPhase(SinglePhase.L3, nullAC, 0l);
				break;
			case L3:
				setPhase(SinglePhase.L1, nullAC, 0l);
				setPhase(SinglePhase.L2, nullAC, 0l);
				setPhase(SinglePhase.L3, ac, energy);
				break;
			default:
		}
		this._setActivePower(power);
		this._setActiveProductionEnergy(energy);
	}

	private void update(OpenDTUInverterLimitModel inverterLimitModel) {
		var inverterLimit = inverterLimitModel.getInverterLimit(this.config.inverterSerial());
		this._setActivePowerLimit(inverterLimit.getLimitRelative());
		this._setMaxApparentPower(inverterLimit.getMaxPower());
	}
	
	private void setInverterLimit(int value) {
		var limit = new InverterSetLimit(this.config.inverterSerial());
		limit.setLimit_value(value);
		this.httpBridge.requestJson(api.getLimitSetEndpoint(this.converter.toInverterLimitJson(limit))).whenComplete((json,ex)->{
			if(ex==null) {
				handleSetLimitResponse(this.converter.toInverterSetLimitResponse(json));
			} else {
				logger.error("setInverterLimit:", ex);
			}
		});
	}

	private void handleSetLimitResponse(InverterResponse response) {
		if(!response.isOK()) {
			logger.warn("set limit request error: {} {}",response.getCode(),response.getMessage());
		}
		requestActPowerLimit();
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
	public void setActivePowerLimit(int value) throws OpenemsNamedException {
		setInverterLimit(value);
	}

	@Override
	public void setActivePowerLimit(Integer value) throws OpenemsNamedException {
		setInverterLimit(value);
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
