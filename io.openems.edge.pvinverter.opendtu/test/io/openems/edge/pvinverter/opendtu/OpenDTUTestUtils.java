package io.openems.edge.pvinverter.opendtu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import io.openems.edge.bridge.http.api.BridgeHttp;
import io.openems.edge.bridge.http.api.BridgeHttp.Endpoint;
import io.openems.edge.bridge.http.dummy.DummyBridgeHttpFactory;

public class OpenDTUTestUtils {

	public static abstract class OpenDTUBridgeHttp implements BridgeHttp {

		protected abstract void callSubscriptionEndpoint(Endpoint endpoint, String json);
	}

	public final String COMPONENT_ID = "opendtu0";
	public final String inverterSerial = "116666666666";
	public final OpenDTUConfig config = createConfig();
	public final OpenDtuEndpoints endpoints = createEndpoints(config);

	public String readFromResource(String fileName) throws IOException {
		InputStream ioStream = OpenDTUTest.class.getClassLoader().getResourceAsStream(fileName);

		if (ioStream == null) {
			throw new IllegalArgumentException(fileName + " is not found");
		}
		final var buffer = new StringBuilder();
		try (InputStreamReader isr = new InputStreamReader(ioStream); BufferedReader br = new BufferedReader(isr);) {
			String line;
			while ((line = br.readLine()) != null) {
				buffer.append(line);
			}
			ioStream.close();
		}
		return buffer.toString();
	}

	public OpenDTUConfig createConfig() {
		return OpenDTUConfig.create() //
				.setId(COMPONENT_ID) //
				.setInverterSerial(inverterSerial) //
				.build();
	}

	public OpenDtuEndpoints createEndpoints(OpenDTUConfig config) {
		return new OpenDtuEndpoints(config);
	}

	public boolean isInverterLimitEndpoint(Endpoint endpoint) {
		return this.endpoints.getLimitEndpoint().toEndpoint().url().equals(endpoint.url());
	}

	public boolean isInverterDataEndpoint(Endpoint endpoint) {
		return this.endpoints.getInverterLiveDataEndpoint(this.config.inverterSerial()).toEndpoint().url()
				.equals(endpoint.url());
	}

	public OpenDTUBridgeHttp createHttpBridge(String inverterDataJson, String inverterLimitJson) {
		return new OpenDTUBridgeHttp() {

			public final Map<String, CycleEndpoint> cycleEndpoints = new HashMap<>();

			@Override
			public void subscribeCycle(CycleEndpoint endpoint) {
				this.cycleEndpoints.put(endpoint.endpoint().url() + endpoint.endpoint().method(), endpoint);
			}

			public void callSubscriptionEndpoint(Endpoint endpoint, String json) {
				var cycleEndpoint = this.cycleEndpoints.get(endpoint.url() + endpoint.method());
				if (cycleEndpoint != null) {
					cycleEndpoint.result().accept(inverterDataJson);
				}
			}

			@Override
			public void subscribeTime(TimeEndpoint endpoint) {

			}

			@Override
			public CompletableFuture<String> request(Endpoint endpoint) {
				if (isInverterLimitEndpoint(endpoint)) {
					return CompletableFuture.completedFuture(inverterLimitJson);
				} else {
					return null;
				}
			}

		};
	}

	public DummyBridgeHttpFactory createHttpBridgeFactory()
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return new DummyBridgeHttpFactory();
	}

	public Endpoint getInverterDataEndpoint() {
		return this.endpoints.getInverterLiveDataEndpoint(this.config.inverterSerial()).toEndpoint();
	}
}
