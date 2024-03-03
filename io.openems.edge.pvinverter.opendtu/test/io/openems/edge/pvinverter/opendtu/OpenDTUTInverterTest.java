package io.openems.edge.pvinverter.opendtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.openems.edge.meter.api.SinglePhase;
import io.openems.edge.pvinverter.opendtu.model.InverterUnit;
import io.openems.edge.pvinverter.opendtu.util.OpenDTUConverter;

public class OpenDTUTInverterTest {

	private final OpenDTUTestUtils testUtils = new OpenDTUTestUtils();
	private final OpenDTUConverter converter = new OpenDTUConverter();
	private final Config config = testUtils.createConfig();

	@Test
	public void testInverterDataAndLimit() throws Exception {
		var dataJson = testUtils.readFromResource("resources/inverterdata.json");
		var limitJson = testUtils.readFromResource("resources/limitdata.json");

		var bridgeHttp = testUtils.createHttpBridge(dataJson, limitJson);

		var inverter = new OpenDTUInverter(config, converter);
		inverter.setBridgeHttp(bridgeHttp);
		inverter.init();
		assertFalse(inverter.isInitialized());
		assertNull(inverter.getActiveLimit());
		assertNull(inverter.getMaxPower());
		assertNull(inverter.getPower());
		assertNull(inverter.getPhase(SinglePhase.L1));
		assertNull(inverter.getPhase(SinglePhase.L2));
		assertNull(inverter.getPhase(SinglePhase.L3));
		inverter.start();
		bridgeHttp.callSubscriptionEndpoint(testUtils.getInverterDataEndpoint(), dataJson);
		assertTrue(inverter.isInitialized());
		assertEquals(Integer.valueOf(40), inverter.getActiveLimit());
		assertEquals(InverterUnit.W, inverter.getMaxPower().getUnit());
		assertEquals(Integer.valueOf(1500), inverter.getMaxPower().toIntegerValue());
		assertEquals(InverterUnit.W, inverter.getPower().getUnit());
		assertEquals(Integer.valueOf(23), inverter.getPower().toIntegerValue());
		assertNotNull(inverter.getPhase(SinglePhase.L1));
		assertNotNull(inverter.getPhase(SinglePhase.L1).power().toIntegerValue());
		assertEquals(223, inverter.getPhase(SinglePhase.L1).voltage().toIntegerValue().intValue());
		assertEquals(0, inverter.getPhase(SinglePhase.L1).current().toIntegerValue().intValue());
		assertEquals(0, inverter.getPhase(SinglePhase.L1).powerFactor().toIntegerValue().intValue());
		assertEquals(19.5, inverter.getPhase(SinglePhase.L1).reactivPower().toDoubleValue().doubleValue(), 0.001);
		assertNull(inverter.getPhase(SinglePhase.L2));
		assertNull(inverter.getPhase(SinglePhase.L3));
	}

}
