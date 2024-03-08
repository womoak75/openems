package io.openems.edge.pvinverter.opendtu;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.meter.api.SinglePhase;
import io.openems.edge.pvinverter.opendtu.api.OpenDTUCommunication;
import io.openems.edge.pvinverter.opendtu.model.InverterResponse;
import io.openems.edge.pvinverter.opendtu.model.InverterUnit;
import io.openems.edge.pvinverter.opendtu.model.InverterValue;
import io.openems.edge.pvinverter.opendtu.model.InverterValueInteger;
import io.openems.edge.pvinverter.opendtu.model.InverterValueLong;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel.InverterSetLimit;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterPhase;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUModel;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUModel.AC;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUModel.OpenDTUInverterModel;

public class OpenDTUInverter {

	public interface OpenDTUInverterObserver {
		public void update(OpenDTUInverterEvent event, OpenDTUInverter inverter);
	}

	public enum OpenDTUInverterEvent {
		DATA_UPDATE, LIMIT_UPDATE
	}

	private final Logger logger = LoggerFactory.getLogger(OpenDTUInverter.class);
	private final Config config;
	private Object inverterModelLock = new Object();
	private Set<OpenDTUInverterObserver> observer;
	private OpenDTUInverterModel inverterModel;
	private OpenDTUInverterLimit inverterLimit;
	private OpenDTUCommunication communication;
	private int cycle;

	public OpenDTUInverter(Config config, OpenDTUCommunication communication) {
		this.config = config;
		this.communication = communication;
		this.inverterLimit = new OpenDTUInverterLimit();
		this.observer = new HashSet<>();
	}

	public void init() {
		logger.warn("init() -> do setup");
	}

	public boolean isInitialized() {
		synchronized (inverterModelLock) {
			return this.inverterModel != null;
		}
	}

	public void register(OpenDTUInverterObserver o) {
		this.observer.add(o);
	}

	public void unregister(OpenDTUInverterObserver o) {
		this.observer.remove(o);
	}

	private void notiyObserver(final OpenDTUInverterEvent event) {
		this.observer.stream().forEach(o -> o.update(event, this));
	}

	public void update(OpenDTUModel inverterModel) {
		logger.debug("update OpenDTUModel: {}", inverterModel);
		if (inverterModel == null)
			return;
		var inverter = inverterModel.getInverter(this.config.inverterSerial());
		if (inverter == null)
			return;
		logger.debug("update OpenDTUModel: Inverter {}", inverter);
		synchronized (inverterModelLock) {
			this.inverterModel = inverter;
		}
		notiyObserver(OpenDTUInverterEvent.DATA_UPDATE);
	}

	public void update(InverterResponse response) {
		if (response.isOK()) {
			requestActPowerLimit();
		} else {
			logger.warn("inverter set limit failed");
			this.inverterLimit.update(null);
		}
	}

	public void update(OpenDTUInverterLimitModel inverterLimitModel) {
		logger.debug("update OpenDTUInverterLimitModel: {}", inverterLimitModel);
		if (inverterLimitModel == null)
			return;
		var inverterLimit = inverterLimitModel.getInverterLimit(this.config.inverterSerial());
		if (inverterLimit == null)
			return;

		logger.debug("update OpenDTUInverterLimitModel: Inverter {}", inverterLimit);
		this.inverterLimit.update(inverterLimit);
	}

	private boolean hasSinglePhase() {
		synchronized (inverterModelLock) {
			return (this.inverterModel != null && this.inverterModel.getAc().size() == 1);
		}
	}

	private boolean hasThreePhase() {
		synchronized (inverterModelLock) {
			return (this.inverterModel != null && this.inverterModel.getAc().size() == 3);
		}
	}

	public OpenDTUInverterPhase getPhase(SinglePhase phase) throws OpenemsException {
		if (!isInitialized())
			return null;
		synchronized (inverterModelLock) {
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

	public OpenDTUInverterLimit getInverterLimit() {
		return this.inverterLimit;
	}

	private void startPolling() {
		this.communication.requestInverterData(this::update);
	}

	private void requestActPowerLimit() {
		this.logger.debug("request active power limit");
		this.communication.requestInverterLimitData(this::update);
	}

	/**
	 * set production limit in percent
	 * 
	 * @param value (0 - 100)
	 */
	public void setLimit(Integer value, Consumer<OpenDTUInverterLimit> callback) {
		logger.debug("setLimit: {}%", value);
		if (this.inverterLimit.isPending()) {
			logger.warn("limit request pending - no new request possible");
			return;
		}
		this.inverterLimit.addStatusCallback(callback);
		var limit = new InverterSetLimit(this.config.inverterSerial());
		limit.setLimit_value(toLimit(value));
		this.communication.setInverterLimit(limit, this::update);
	}

	private int toLimit(Integer value) {
		int intLimit = value == null ? 100 : value.intValue();
		if (intLimit < 0)
			intLimit = 0;
		if (intLimit > 100)
			intLimit = 100;
		return intLimit;
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
		synchronized (inverterModelLock) {
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
		synchronized (inverterModelLock) {
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

	public String getSerial() {
		synchronized (inverterModelLock) {
			return this.inverterModel == null ? null : this.inverterModel.getSerial();
		}
	}

	public void cycle() {
		if (nextCycle() == 0) {
			if (this.inverterLimit.isPending()) {
				requestActPowerLimit();
			}
		}
	}

	private int nextCycle() {
		cycle++;
		cycle %= this.config.cycleInterval();
		return cycle;
	}

}
