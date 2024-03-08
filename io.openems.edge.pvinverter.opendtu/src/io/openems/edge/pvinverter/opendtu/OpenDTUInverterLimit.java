package io.openems.edge.pvinverter.opendtu;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import io.openems.edge.pvinverter.opendtu.model.InverterUnit;
import io.openems.edge.pvinverter.opendtu.model.InverterValue;
import io.openems.edge.pvinverter.opendtu.model.InverterValueInteger;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel.InverterLimit.InverterLimitStatus;

public class OpenDTUInverterLimit {

	public enum OpenDTUInverterLimitStatus {
		UNKNOWN, OK, PENDING, ERROR
	}

	private Integer limit;
	private Integer maxPower;
	private OpenDTUInverterLimitStatus status;
	private Set<Consumer<OpenDTUInverterLimit>> callbacks;

	public OpenDTUInverterLimit() {
		this.limit = null;
		this.maxPower = null;
		this.status = OpenDTUInverterLimitStatus.UNKNOWN;
		this.callbacks = new HashSet<>();
	}

	public OpenDTUInverterLimitStatus getStatus() {
		return status;
	}

	@SuppressWarnings("rawtypes")
	public InverterValue getMaxPower() {
		return new InverterValueInteger(this.maxPower, InverterUnit.W, 1);
	}

	public Integer getLimit() {
		return limit;
	}

	public void update(OpenDTUInverterLimitModel.InverterLimit limitUpdate) {
		var status = limitUpdate == null ? OpenDTUInverterLimitStatus.ERROR : toStatus(limitUpdate.getLimitSetStatus());
		switch (status) {
		case OK:
			this.limit = limitUpdate.getLimitRelative();
			this.maxPower = limitUpdate.getMaxPower();
			this.status = status;
			executeCallbacks();
			break;
		case ERROR:
			this.status = status;
			executeCallbacks();
			break;
		case PENDING:
			this.status = status;
			break;
		default:
			this.status = OpenDTUInverterLimitStatus.UNKNOWN;
			break;
		}

	}

	private void executeCallbacks() {
		var cbs = new HashSet<>(this.callbacks);
		this.callbacks.clear();
		cbs.stream().forEach(callback -> callback.accept(this));
	}

	private OpenDTUInverterLimitStatus toStatus(InverterLimitStatus limitSetStatus) {
		switch (limitSetStatus) {
		case Failure:
			return OpenDTUInverterLimitStatus.ERROR;
		case Ok:
			return OpenDTUInverterLimitStatus.OK;
		case Pending:
			return OpenDTUInverterLimitStatus.PENDING;
		default:
			return OpenDTUInverterLimitStatus.UNKNOWN;
		}
	}

	public boolean isLimit(Integer integer) {
		return (this.limit != null && this.limit.equals(integer));
	}

	public boolean isActive() {
		return (this.status == OpenDTUInverterLimitStatus.OK);
	}

	public boolean isPending() {
		return (this.status == OpenDTUInverterLimitStatus.PENDING);
	}

	public void addStatusCallback(Consumer<OpenDTUInverterLimit> callback) {
		this.callbacks.add(callback);
	}
}