package io.openems.edge.pvinverter.opendtu;

import io.openems.edge.bridge.http.api.HttpMethod;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import io.openems.edge.bridge.http.api.BridgeHttp.Endpoint;

public class OpenDtuEndpoints {
	
	private String host;
	private int port;
	private String schema;
	private int connectTimeout;
	private int readTimeoute;
	private String user;
	private String pass;

	public OpenDtuEndpoints(Config config) {
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
		return schema+"://"+host+":"+port;
	}
	
	public Endpoint getInverterListEndpoint() {
		return new Endpoint(getBaseUrl()+"/api/inverter/list", HttpMethod.GET, this.connectTimeout, this.readTimeoute, null, authPropertiesMap());
	}
	
	public String getInverterLiveDataUrl(String inverterId) {
		return getBaseUrl()+"/api/livedata/status?inv="+inverterId;
	}
	
	public Endpoint getInverterLiveDataEndpoint(String inverterId) {
		return new Endpoint(getInverterLiveDataUrl(inverterId), HttpMethod.GET, this.connectTimeout, this.readTimeoute, null, propertiesMap());
	}
	
	public Endpoint getLimitEndpoint() {
		return new Endpoint(getBaseUrl()+"/api/limit/status", HttpMethod.GET, this.connectTimeout, this.readTimeoute, null, authPropertiesMap());
	}
	
	public Endpoint getLimitSetEndpoint(String inverterId, int limit) {
		var body = getLimitSetBody(inverterId, limit);
		return new Endpoint(getBaseUrl()+"/api/limit/config", HttpMethod.POST, this.connectTimeout, this.readTimeoute, body, authPropertiesMap());
	}
	
	private String getLimitSetBody(String inverterId, int percent) {
		return "data={\"serial\":\""+inverterId+"\",\"limit_type\":1, \"limit_value\":"+percent+"}";
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
