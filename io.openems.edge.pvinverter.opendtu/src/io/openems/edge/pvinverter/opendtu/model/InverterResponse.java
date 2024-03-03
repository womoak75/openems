package io.openems.edge.pvinverter.opendtu.model;

public class InverterResponse {
	public enum ResponseType {
		SUCCESS, ERROR
	};

	private ResponseType type;
	private String message;
	private int code;

	public InverterResponse() {
		this.type = ResponseType.SUCCESS;
		this.message = "";
		this.code = 1001;
	}

	public boolean isOK() {
		return ResponseType.SUCCESS.equals(this.type);
	}

	public ResponseType getType() {
		return type;
	}

	public void setType(ResponseType type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
}
