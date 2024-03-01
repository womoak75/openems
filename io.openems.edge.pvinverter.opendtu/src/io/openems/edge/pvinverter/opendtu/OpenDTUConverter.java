package io.openems.edge.pvinverter.opendtu;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.edge.pvinverter.opendtu.OpenDTUInverterLimitModel.InverterLimit;
import io.openems.edge.pvinverter.opendtu.OpenDTUInverterLimitModel.InverterSetLimit;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.AC;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.DC;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.Hints;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.INV;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.InverterResponse;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.InverterResponse.ResponseType;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.InverterValue.InverterUnit;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.InverterValueDouble;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.InverterValueLong;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.OpenDTUInverter;
import io.openems.edge.pvinverter.opendtu.OpenDTUModel.Total;

public class OpenDTUConverter {

	public OpenDTUModel toInverterModel(JsonElement json) {
		if(json==null 
			|| !json.isJsonObject() 
			|| !json.getAsJsonObject().has("inverters") 
			|| json.getAsJsonObject().get("inverters").getAsJsonArray().size() != 1)
			return null;
		var openDTUModel = new OpenDTUModel();
		var inverterObject = json.getAsJsonObject().get("inverters").getAsJsonArray();
		addInverters(openDTUModel,inverterObject);
		var totalObject = json.getAsJsonObject().get("total").getAsJsonObject();
		addTotal(openDTUModel,totalObject);
		var hintsObject = json.getAsJsonObject().get("hints").getAsJsonObject();
		addHints(openDTUModel,hintsObject);
		return openDTUModel;
	}
	
	private void addInverters(OpenDTUModel openDTUModel, JsonArray inverterArray) {
		inverterArray.forEach(entry -> {
			var inverter = new OpenDTUInverter(entry.getAsJsonObject().get("name").getAsString(),entry.getAsJsonObject().get("serial").getAsString());
			addInverter(inverter,entry.getAsJsonObject());
			openDTUModel.addInverter(inverter);
		});
	}
	
	private void addHints(OpenDTUModel inverterModel, JsonObject hintsObject) {
		var hints = new Hints();
		hints.setTimeSync(hintsObject.get("time_sync").getAsBoolean());
		hints.setRadioProblem(hintsObject.get("radio_problem").getAsBoolean());
		hints.setDefaultPassword(hintsObject.get("default_password").getAsBoolean());
		inverterModel.setHints(hints);
	}

	private void addInverter(OpenDTUInverter inverterModel, JsonObject inverterObject) {
		inverterModel.setOrder(inverterObject.get("order").getAsInt());
		inverterModel.setDataAge(inverterObject.get("data_age").getAsInt());
		inverterModel.setPollEnabled(inverterObject.get("poll_enabled").getAsBoolean());
		inverterModel.setReachable(inverterObject.get("reachable").getAsBoolean());
		inverterModel.setProducing(inverterObject.get("producing").getAsBoolean());
		inverterModel.setLimitRelative(inverterObject.get("limit_relative").getAsInt());
		inverterModel.setLimitAbsolute(inverterObject.get("limit_absolute").getAsInt());
		addACChannels(inverterModel,inverterObject.get("AC").getAsJsonObject());
		addDCChannels(inverterModel,inverterObject.get("DC").getAsJsonObject());
		addInv(inverterModel,inverterObject.get("INV").getAsJsonObject());	
	}

	private void addTotal(OpenDTUModel inverterModel, JsonObject totalObject) {
		var total = new Total();
		total.setPower(getDoubleValue("Power",totalObject));
		total.setYieldDay(getDoubleValue("YieldDay",totalObject));
		total.setYieldTotal(getDoubleValue("YieldTotal",totalObject));
		inverterModel.setTotal(total);
	}

	private void addInv(OpenDTUInverter inverterModel, JsonObject inverterObject) {
		var invObject = inverterObject.get("0").getAsJsonObject();
		INV inv = new INV();
		inv.setPowerDc(getDoubleValue("Power DC",invObject));
		inv.setYieldDay(getDoubleValue("YieldDay",invObject));
		inv.setYieldTotal(getDoubleValue("YieldTotal",invObject));
		inv.setTemperature(getDoubleValue("Temperature",invObject));
		inv.setEfficiency(getDoubleValue("Efficiency",invObject));
		inverterModel.setInv(inv);
	}

	private void addDCChannels(OpenDTUInverter inverterModel, JsonObject inverterObject) {
		inverterObject.entrySet().stream().forEach(entry -> {
			addDCChannel(inverterModel,entry.getKey(), entry.getValue().getAsJsonObject());
		});
	}
	
	private void addDCChannel(OpenDTUInverter inverterModel, String key, JsonObject dcObject) {
		var dc = new DC(Integer.parseInt(key));
		dc.setPower(getDoubleValue("Power",dcObject));
		dc.setCurrent(getDoubleValue("Current",dcObject));
		dc.setName(dcObject.get("name").getAsJsonObject().get("u").getAsString());
		dc.setVoltage(getDoubleValue("Voltage",dcObject));
		dc.setYieldDay(getDoubleValue("YieldDay",dcObject));
		dc.setYieldTotal(getDoubleValue("YieldTotal",dcObject));
		dc.setIrradiation(getDoubleValueMax("Irradiation",dcObject));
		inverterModel.addDC(dc);
	}

	private void addACChannels(OpenDTUInverter inverterModel, JsonObject inverterObject) {
		inverterObject.entrySet().stream().forEach(entry -> {
			addACChannel(inverterModel,entry.getKey(), entry.getValue().getAsJsonObject());
		});
		
	}

	private void addACChannel(OpenDTUInverter inverterModel, String key, JsonObject acObject) {
		AC ac = new AC(Integer.parseInt(key));
		ac.setPower(getDoubleValue("Power",acObject));
		ac.setCurrent(getDoubleValue("Current",acObject));
		ac.setFrequency(getDoubleValue("Frequency",acObject));
		ac.setVoltage(getDoubleValue("Voltage",acObject));
		ac.setPowerFactor(getDoubleValue("PowerFactor",acObject));
		ac.setReactivPower(getDoubleValue("ReactivePower",acObject));
		inverterModel.addAC(ac);
	}
	
	private InverterValueDouble getDoubleValueMax(String valueName, JsonObject valueObject) {
		var value = valueObject.get(valueName).getAsJsonObject();
		var unit = InverterUnit.toUnit(value.get("u").getAsString());
		return new InverterValueDouble(value.get("v").getAsDouble(),unit,value.get("d").getAsInt(),value.get("max").getAsInt());
	}
	
	private InverterValueLong getLongValue(String valueName, JsonObject valueObject) {
		var value = valueObject.get(valueName).getAsJsonObject();
		var unit = InverterUnit.toUnit(value.get("u").getAsString());
		return new InverterValueLong(value.get("v").getAsLong(),unit,value.get("d").getAsInt());
	}
	
	private InverterValueDouble getDoubleValue(String valueName, JsonObject valueObject) {
		var value = valueObject.get(valueName).getAsJsonObject();
		var unit = InverterUnit.toUnit(value.get("u").getAsString());
		return new InverterValueDouble(value.get("v").getAsDouble(),unit,value.get("d").getAsInt());
	}

	public OpenDTUInverterLimitModel toInverterLimitModel(JsonElement json) {
		if(json==null || !json.isJsonObject())
			return null;
		var limitobject = json.getAsJsonObject();
		var limitModel = new OpenDTUInverterLimitModel();
		limitobject.entrySet().stream().forEach(entry -> {
			var entryObject = entry.getValue().getAsJsonObject();
		limitModel.addInverterLimit(
				new InverterLimit(entry.getKey(), 
				entryObject.get("limit_relative").getAsInt(),
				entryObject.get("max_power").getAsInt(),
				"Ok".equals(entryObject.get("limit_set_status").getAsString())?true:false));
		});
		return limitModel;
	}
	
	public InverterResponse toInverterSetLimitResponse(JsonElement json) {
		var response = new InverterResponse();
		if(!"success".equals(json.getAsJsonObject().get("type").getAsString()))
			response.setType(ResponseType.ERROR);
		response.setMessage(json.getAsJsonObject().get("message").getAsString());
		response.setCode(json.getAsJsonObject().get("code").getAsInt());
		return response;
	}
	
	public JsonObject toInverterLimitJson(InverterSetLimit model) {
		var json = new JsonObject();
		json.addProperty("serial", model.getSerial());
		json.addProperty("limit_type", model.getLimit_type().toLimit());
		json.addProperty("limit_value", model.getLimit_value());
		return json;
	}
	
}
