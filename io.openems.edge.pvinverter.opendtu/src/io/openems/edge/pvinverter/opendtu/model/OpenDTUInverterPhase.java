package io.openems.edge.pvinverter.opendtu.model;

@SuppressWarnings("rawtypes")
public record OpenDTUInverterPhase(InverterValue power, //
		InverterValue voltage, //
		InverterValue current, //
		InverterValue frequency, //
		InverterValue powerFactor, //
		InverterValue reactivPower, //
		InverterValue energy //
) {
}