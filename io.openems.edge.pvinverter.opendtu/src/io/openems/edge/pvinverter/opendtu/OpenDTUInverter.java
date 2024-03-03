package io.openems.edge.pvinverter.opendtu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.bridge.http.api.BridgeHttp;
import io.openems.edge.meter.api.SinglePhase;
import io.openems.edge.pvinverter.opendtu.model.InverterResponse;
import io.openems.edge.pvinverter.opendtu.model.InverterUnit;
import io.openems.edge.pvinverter.opendtu.model.InverterValue;
import io.openems.edge.pvinverter.opendtu.model.InverterValueInteger;
import io.openems.edge.pvinverter.opendtu.model.InverterValueLong;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel.InverterLimit;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel.InverterSetLimit;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterPhase;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUModel;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUModel.AC;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUModel.OpenDTUInverterModel;
import io.openems.edge.pvinverter.opendtu.util.OpenDTUConverter;

public class OpenDTUInverter {

	private final Logger logger = LoggerFactory.getLogger(OpenDTUInverter.class);
	private final OpenDtuEndpoints apiEndpoints;
	private final Config config;
	private final Object inverterLock = new Object();
	private OpenDTUInverterModel inverterModel;
	private final Object limitLock = new Object();
	private InverterLimit inverterLimit;
	private BridgeHttp httpBridge;
	private final OpenDTUConverter converter;
	private boolean limitPending;

	public OpenDTUInverter(Config config, OpenDTUConverter converter) {
		this.config = config;
		this.converter = converter;
		this.apiEndpoints = new OpenDtuEndpoints(config);
	}

	public void init() {
		logger.warn("init() -> do setup");
	}

	public boolean isInitialized() {
		synchronized (inverterLock) {
			return this.inverterModel != null;
		}
	}

	public void update(OpenDTUModel inverterModel) {
		logger.debug("update OpenDTUModel: {}", inverterModel);
		if (inverterModel == null)
			return;
		var inverter = inverterModel.getInverter(this.config.inverterSerial());
		if (inverter != null) {
			synchronized (inverterLock) {
				logger.debug("update OpenDTUModel: Inverter {}", inverter);
				this.inverterModel = inverter;
			}
		} else {
			logger.warn("no data for inverter " + this.config.inverterSerial());
		}
	}

	public void update(InverterResponse response) {
		if (response.isOK()) {
			requestActPowerLimit();
		} else {
			logger.warn("inverter set limit failed");
		}
	}

	public void update(OpenDTUInverterLimitModel inverterLimitModel) {
		logger.debug("update OpenDTUInverterLimitModel: {}", inverterLimitModel);
		if (inverterLimitModel == null)
			return;
		var inverterLimit = inverterLimitModel.getInverterLimit(this.config.inverterSerial());
		if (inverterLimit != null) {
			synchronized (limitLock) {
				logger.debug("update OpenDTUInverterLimitModel: Inverter {}", inverterLimit);
				this.inverterLimit = inverterLimit;
			}
		} else {
			logger.warn("no limitdata for inverter " + this.config.inverterSerial());
		}
		setLimitPending(false);
	}

	private boolean hasSinglePhase() {
		synchronized (inverterLock) {
			return (this.inverterModel != null && this.inverterModel.getAc().size() == 1);
		}
	}

	private boolean hasThreePhase() {
		synchronized (inverterLock) {
			return (this.inverterModel != null && this.inverterModel.getAc().size() == 3);
		}
	}

	public OpenDTUInverterPhase getPhase(SinglePhase phase) throws OpenemsException {
		synchronized (inverterLock) {
			if (!isInitialized())
				return null;
			if (hasThreePhase()) {
				switch (phase) {
				case L1:
					return toInverterPhase(this.inverterModel.getAc().get(0));
				case L2:
					return toInverterPhase(this.inverterModel.getAc().get(1));
				case L3:
					return toInverterPhase(this.inverterModel.getAc().get(2));
				default:
					throw new OpenemsException("unknown phase");
				}
			} else if (hasSinglePhase()) {
				if (this.config.phase() == phase)
					return toInverterPhase(this.inverterModel.getAc().get(0));
				else
					return null;
			} else {
				return null;
			}
		}
	}

	private OpenDTUInverterPhase toInverterPhase(AC ac) {
		// not sure about 3phase models ... seems to be ok for hm series
		long energy = getProductionEnergy().toLongValue();
		if (hasThreePhase())
			energy /= 3;

		return new OpenDTUInverterPhase(ac.getPower(), ac.getVoltage(), ac.getCurrent(), ac.getFrequency(),
				ac.getPowerFactor(), ac.getReactivPower(), new InverterValueLong(energy, InverterUnit.Wh, 1));
	}

	public Integer getActiveLimit() {
		synchronized (limitLock) {
			if (this.inverterLimit != null) {
				return this.inverterLimit.getLimitRelative();
			}
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public InverterValue getMaxPower() {
		synchronized (limitLock) {
			if (this.inverterLimit != null) {
				return new InverterValueInteger(this.inverterLimit.getMaxPower(), InverterUnit.W, 1);
			}
		}
		return null;
	}

	public void setBridgeHttp(BridgeHttp httpBridge) {
		this.httpBridge = httpBridge;
	}

	public void unsetBridgeHttp(BridgeHttp httpBridge) {
		// do some cleanup?
		this.httpBridge = null;
	}

	private void startPolling() {
		this.httpBridge.subscribeJsonCycle(config.cycleInterval(),
				this.apiEndpoints.getInverterLiveDataEndpoint(this.config.inverterSerial()).toEndpoint().url(),
				(json, ex) -> {
					if (ex == null) {
						update(this.converter.toInverterModel(json));
					} else {
						logger.error("getInverterLiveData:", ex);
						update((OpenDTUModel) null);
					}
				});
	}

	private void requestActPowerLimit() {
		this.httpBridge.requestJson(this.apiEndpoints.getLimitEndpoint().toEndpoint()).whenComplete((json, ex) -> {
			if (ex == null) {
				update(this.converter.toInverterLimitModel(json));
			} else {
				logger.error("getInverterLimit:", ex);
				update((OpenDTUInverterLimitModel) null);
			}
		});
	}

	/**
	 * set production limit in percent
	 * 
	 * @param value (0 - 100)
	 */
	public void setLimit(Integer value) {
		logger.debug("setLimit: {}%", value);

		if (!isLimitPending()) {
			setLimitPending(true);
			var limit = new InverterSetLimit(this.config.inverterSerial());
			limit.setLimit_value(toLimit(value));
			var endpoint = this.apiEndpoints.getLimitSetEndpoint().setBody(this.converter.toInverterLimitJson(limit))
					.toEndpoint();
			this.httpBridge.requestJson(endpoint).whenComplete((json, ex) -> {
				if (ex == null) {
					update(this.converter.toInverterSetLimitResponse(json));
				} else {
					update((InverterResponse) null);
					logger.error("setInverterLimit:", ex);
				}
			});
		} else {
			logger.warn("limit request pending");
		}
	}

	private int toLimit(Integer value) {
		int intLimit = value == null ? 100 : value.intValue();
		if (intLimit < 0)
			intLimit = 0;
		if (intLimit > 100)
			intLimit = 100;
		return intLimit;
	}

	private void setLimitPending(boolean b) {
		this.limitPending = b;
	}

	private boolean isLimitPending() {
		return this.limitPending;
	}

	public void start() {
		requestActPowerLimit();
		startPolling();
	}

	/**
	 * power in W
	 * 
	 * @return InverterValue
	 */
	@SuppressWarnings("rawtypes")
	public InverterValue getPower() {
		synchronized (inverterLock) {
			if (this.inverterModel != null) {
				int power = 0;
				for (var ac : this.inverterModel.getAc()) {
					InverterValue p = ac.getPower();
					int i = p.toIntegerValue();
					if (p.isKilo()) {
						i *= 1000;
					} else if (p.isMilli()) {
						i /= 1000;
					}
					power += i;
				}
				return new InverterValueInteger(power, InverterUnit.W, 1);
			}
		}
		return null;
	}

	/**
	 * energy in Wh
	 * 
	 * @return InverterValue
	 */
	@SuppressWarnings("rawtypes")
	public InverterValue getProductionEnergy() {
		synchronized (inverterLock) {
			if (this.inverterModel != null) {
				// not sure about this ...
				long energy = 0;
				for (var dc : this.inverterModel.getDc()) {
					InverterValue e = dc.getYieldDay();
					long l = e.toLongValue();
					if (e.isKilo()) {
						l *= 1000;
					} else if (e.isMilli()) {
						l /= 1000;
					}
					energy += l;
				}
				return new InverterValueLong(energy, InverterUnit.Wh, 1);
			}
		}
		return null;
	}

	public void stop() {
		logger.warn("stop() -> do cleanup");
	}

}
