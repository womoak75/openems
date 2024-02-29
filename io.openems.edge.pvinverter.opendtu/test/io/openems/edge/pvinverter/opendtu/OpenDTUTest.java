package io.openems.edge.pvinverter.opendtu;

import org.junit.Test;

import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.common.test.ComponentTest;

public class OpenDTUTest {

	private static final String COMPONENT_ID = "opendtu0";

	@Test
	public void test() throws Exception {
		new ComponentTest(new OpenDTUImpl()) //
				.activate(OpenDTUConfig.create() //
						.setId(COMPONENT_ID) //
						.build())
				.next(new TestCase());
	}

}
