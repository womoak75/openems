package io.openems.edge.pvinverter.opendtu;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
		name = "io.openems.edge.pvinerter.opendtu", //
		description = "")
@interface Config {

	@AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
	String id() default "io.openems.edge.pvinerter.opendtu0";

	@AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
	String alias() default "";

	@AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
	boolean enabled() default true;

	String webconsole_configurationFactory_nameHint() default "io.openems.edge.pvinerter.opendtu [{id}]";
	
	String inverterSerial() default "";
	
	String openDtuHost() default "localhost";
	
	int openDtuPort() default 80;
	
	String openDtuSchema() default "http";
		
	String openDtuUser() default "admin";
	
	String openDtuPass() default "admin";

}