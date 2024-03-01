package io.openems.edge.pvinverter.opendtu;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import io.openems.edge.meter.api.SinglePhase;

@ObjectClassDefinition(//
		name = "io.openems.edge.pvinerter.opendtu", //
		description = "")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "opendtu0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	String webconsole_configurationFactory_nameHint() default "io.openems.edge.pvinerter.opendtu [{id}]";
	
	@AttributeDefinition(name = "Inverter serial", description = "serial of inverter")
	String inverterSerial() default "";
	
	@AttributeDefinition(name = "Hostname", description = "Ip/Hostname of openDTU")
	String openDtuHost() default "localhost";

	@AttributeDefinition(name = "Port", description = "Port of openDTU")
	int openDtuPort() default 80;
	
	@AttributeDefinition(name = "Protocol", description = "http / https")
	String openDtuSchema() default "http";
	
	@AttributeDefinition(name = "Username", description = "Username for authorization")
	String openDtuUser() default "admin";
	
	@AttributeDefinition(name = "Password", description = "Username for authorization")
	String openDtuPass() default "admin";

	@AttributeDefinition(name = "cycleInverval", description = "should be executed every n cycle")
	int cycleInterval() default 5;

	@AttributeDefinition(name = "Phase", description = "Phase which the inverter is connected to")
	SinglePhase phase();

}