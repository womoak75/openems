package io.openems.edge.pvinverter.opendtu.model;

import com.google.gson.JsonObject;

import io.openems.edge.bridge.http.api.BridgeHttp.Endpoint;

public class OpenDTUEndpoint {

	private Endpoint endpoint;
	private String body;

	public OpenDTUEndpoint(Endpoint endpoint) {
		this(endpoint, (String) null);
	}

	public OpenDTUEndpoint(Endpoint endpoint, JsonObject json) {
		this(endpoint, json.toString());
	}

	public OpenDTUEndpoint(Endpoint endpoint, String body) {
		this.endpoint = endpoint;
		this.body = body;
	}

	public OpenDTUEndpoint setBody(String body) {
		this.body = body;
		return this;
	}

	public OpenDTUEndpoint setBody(JsonObject json) {
		this.body = json.toString();
		return this;
	}

	public Endpoint toEndpoint() {
		return new Endpoint(this.endpoint.url(), //
				this.endpoint.method(), //
				this.endpoint.connectTimeout(), //
				this.endpoint.readTimeout(), //
				this.body, //
				this.endpoint.properties());
	}
}
