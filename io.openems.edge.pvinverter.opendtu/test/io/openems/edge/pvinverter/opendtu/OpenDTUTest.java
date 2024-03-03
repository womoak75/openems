package io.openems.edge.pvinverter.opendtu;

import org.junit.Test;

import io.openems.edge.bridge.http.api.BridgeHttp;
import io.openems.edge.bridge.http.dummy.DummyBridgeHttpFactory;
import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.common.test.ComponentTest;
import io.openems.edge.common.test.DummyConfigurationAdmin;

public class OpenDTUTest {

	private final OpenDTUTestUtils testUtils = new OpenDTUTestUtils();
	private final OpenDTUConfig config = testUtils.createConfig();

	@Test
	public void test() throws Exception {
		String dataJson = testUtils.readFromResource("resources/inverterdata.json");
		var limitJson = testUtils.readFromResource("resources/limitdata.json");
		final DummyBridgeHttpFactory bridgeHttpFactory = testUtils.createHttpBridgeFactory();
		final BridgeHttp bridgeHttp = testUtils.createHttpBridge(dataJson, limitJson);
		bridgeHttpFactory.setBridgeHttp(bridgeHttp);
		var openDtu = new OpenDTUImpl();
		new ComponentTest(openDtu) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("httpBridgeFactory", bridgeHttpFactory) //
				.activate(config).next(new TestCase());
	}

}
