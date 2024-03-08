package io.openems.edge.pvinverter.opendtu;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonObject;

import io.openems.edge.bridge.http.api.BridgeHttp.Endpoint;
import io.openems.edge.bridge.http.api.HttpMethod;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUEndpoint;

public class OpenDtuEndpoints {

	private String host;
	private int port;
	private String schema;
	private int connectTimeout;
	private int readTimeout;
	private String user;
	private String pass;
	private Config config;

	public OpenDtuEndpoints(Config config) {
		this.config = config;
		this.host = config.openDtuHost();
		this.port = config.openDtuPort();
		this.schema = config.openDtuSchema();
		this.user = config.openDtuUser();
		this.pass = config.openDtuPass();
	}

	public OpenDtuEndpoints(String host, int port) {
		this.host = host;
		this.port = port;
		this.schema = "http";
	}

	private String basicAuthValue() {
		return "Basic " + Base64.getEncoder().encodeToString((user + ":" + pass).getBytes());
	}

	private String getBaseUrl() {
		return schema + "://" + host + ":" + port;
	}

	public OpenDTUEndpoint getInverterListEndpoint() {
		return new OpenDTUEndpoint(new Endpoint(getBaseUrl() + "/api/inverter/list", HttpMethod.GET,
				this.connectTimeout, this.readTimeout, null, authPropertiesMap()));
	}

	public String getInverterLiveDataUrl(String inverterId) {
		return getBaseUrl() + "/api/livedata/status?inv=" + inverterId;
	}

	public OpenDTUEndpoint getInverterLiveDataEndpoint() {
		return getInverterLiveDataEndpoint(this.config.inverterSerial());
	}

	public OpenDTUEndpoint getInverterLiveDataEndpoint(String inverterId) {
		return new OpenDTUEndpoint(new Endpoint(getInverterLiveDataUrl(inverterId), HttpMethod.GET, this.connectTimeout,
				this.readTimeout, null, propertiesMap()));
	}

	public OpenDTUEndpoint getLimitEndpoint() {
		return new OpenDTUEndpoint(new Endpoint(getBaseUrl() + "/api/limit/status", HttpMethod.GET, this.connectTimeout,
				this.readTimeout, null, authPropertiesMap()));
	}

	public OpenDTUEndpoint getLimitSetEndpoint(final JsonObject jsonObject) {
		return getLimitSetEndpoint(jsonObject.toString());
	}

	public OpenDTUEndpoint getLimitSetEndpoint(final String body) {
		return new OpenDTUEndpoint(new Endpoint(getBaseUrl() + "/api/limit/config", HttpMethod.POST,
				this.connectTimeout, this.readTimeout, body, authPropertiesMap()), body);
	}

	private Map<String, String> authPropertiesMap() {
		var map = propertiesMap();
		map.put("Authorization", basicAuthValue());
		return map;
	}

	private Map<String, String> propertiesMap() {
		return new HashMap<>();
	}

}
