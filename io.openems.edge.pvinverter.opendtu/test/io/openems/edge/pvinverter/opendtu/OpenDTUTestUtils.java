package io.openems.edge.pvinverter.opendtu;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.common.utils.JsonUtils;
import io.openems.edge.bridge.http.api.BridgeHttp;
import io.openems.edge.bridge.http.api.BridgeHttp.Endpoint;
import io.openems.edge.bridge.http.dummy.DummyBridgeHttpFactory;

public class OpenDTUTestUtils {

	public static abstract class OpenDTUBridgeHttp implements BridgeHttp {

		protected abstract void callSubscriptionEndpoint(Endpoint endpoint, JsonObject json);

		protected abstract void setResponse(Endpoint endpoint, JsonObject jsonResponse);
	}

	public final String COMPONENT_ID = "opendtu0";
	public final String inverterSerial = "116666666666";
	public final OpenDTUConfig config = createConfig();
	public final OpenDtuEndpoints endpoints = createEndpoints(config);

	public JsonObject readFromResource(String fileName) throws Exception {
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
		return JsonUtils.parseToJsonObject(buffer.toString());
	}

	public JsonObject createLimitResponse(boolean b) throws Exception {
		return JsonUtils.parseToJsonObject(
				"{\"type\":\"" + (b ? "success" : "error") + "\",\"message\":\"Settings saved!\",\"code\":1001}");
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

	public OpenDTUBridgeHttp createHttpBridge(final JsonObject limitJson) throws Exception {
		return new OpenDTUBridgeHttp() {

			public final Map<String, JsonObject> jsonResponses = new HashMap<>();
			{
				jsonResponses.put(toKey(endpoints.getLimitEndpoint().toEndpoint()), limitJson);
				jsonResponses.put(toKey(endpoints.getLimitSetEndpoint((String) null).toEndpoint()),
						createLimitResponse(true));
			}
			public final Map<String, CycleEndpoint> cycleEndpoints = new HashMap<>();

			private String toKey(Endpoint endpoint) {
				return endpoint.url() + endpoint.method();
			}

			@Override
			public void subscribeCycle(CycleEndpoint endpoint) {
				this.cycleEndpoints.put(toKey(endpoint.endpoint()), endpoint);
			}

			public void callSubscriptionEndpoint(Endpoint endpoint, JsonObject json) {
				var cycleEndpoint = this.cycleEndpoints.get(toKey(endpoint));
				if (cycleEndpoint != null) {
					cycleEndpoint.result().accept(json.toString());
				}
			}

			@Override
			public void subscribeTime(TimeEndpoint endpoint) {

			}

			@Override
			public CompletableFuture<JsonElement> requestJson(Endpoint endpoint) {
				JsonObject response = this.jsonResponses.get(toKey(endpoint));
				if (response != null) {
					return CompletableFuture.completedFuture(response);
				} else {
					return null;
				}
			}

			@Override
			protected void setResponse(Endpoint endpoint, JsonObject responseJson) {
				this.jsonResponses.put(toKey(endpoint), responseJson);
			}

			@Override
			public CompletableFuture<String> request(Endpoint endpoint) {
				// TODO Auto-generated method stub
				return null;
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

	public JsonObject createLimitRespone(int i) throws Exception {
		String json = "{\n" + "  \"" + this.config.inverterSerial() + "\": {\n" + "    \"limit_relative\": " + i + ",\n"
				+ "    \"max_power\": 1500,\n" + "    \"limit_set_status\": \"Ok\"\n" + "  }\n" + "}";
		return JsonUtils.parse(json).getAsJsonObject();
	}

	public Endpoint getInverterLimitSetEndoint() {
		return this.endpoints.getLimitSetEndpoint((String) null).toEndpoint();
	}

}
