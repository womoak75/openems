package io.openems.edge.pvinverter.opendtu;

import org.junit.Test;

import io.openems.common.types.ChannelAddress;
import io.openems.edge.bridge.http.dummy.DummyBridgeHttpFactory;
import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.common.test.ComponentTest;
import io.openems.edge.common.test.DummyConfigurationAdmin;
import io.openems.edge.pvinverter.api.ManagedSymmetricPvInverter;
import io.openems.edge.pvinverter.opendtu.OpenDTUTestUtils.OpenDTUBridgeHttp;

public class OpenDTUTest {

	private final OpenDTUTestUtils testUtils = new OpenDTUTestUtils();
	private final OpenDTUConfig config = testUtils.createConfig();

	@Test
	public void test() throws Exception {
		var limitJson = testUtils.readFromResource("resources/limitdata.json");
		final DummyBridgeHttpFactory bridgeHttpFactory = testUtils.createHttpBridgeFactory();
		final OpenDTUBridgeHttp bridgeHttp = testUtils.createHttpBridge(limitJson);
		final ChannelAddress powerLimitChannel = new ChannelAddress(config.id(),
				ManagedSymmetricPvInverter.ChannelId.ACTIVE_POWER_LIMIT.id());
		;

		bridgeHttpFactory.setBridgeHttp(bridgeHttp);
		var openDtu = new OpenDTUImpl();
		var componentTest = new ComponentTest(openDtu) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("httpBridgeFactory", bridgeHttpFactory) //
				.activate(config);

		componentTest.next(new TestCase() //
				.output(powerLimitChannel, null));

	}

}
