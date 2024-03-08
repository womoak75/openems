package io.openems.edge.pvinverter.opendtu;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.utils.JsonUtils;
import io.openems.edge.pvinverter.opendtu.model.InverterUnit;
import io.openems.edge.pvinverter.opendtu.model.InverterValueDouble;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel.InverterLimit.InverterLimitStatus;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel.InverterSetLimit;
import io.openems.edge.pvinverter.opendtu.model.OpenDTUInverterLimitModel.InverterSetLimit.LimitType;
import io.openems.edge.pvinverter.opendtu.util.OpenDTUConverter;;

public class OpenDTUConverterTest {

	@Test
	public void testOpenDTUModelHM1500() throws OpenemsNamedException {
		String json = "{\n" + "  \"inverters\": [\n" + "    {\n" + "      \"serial\": \"116666666666\",\n"
				+ "      \"name\": \"hm1500\",\n" + "      \"order\": 0,\n" + "      \"data_age\": 11670,\n"
				+ "      \"poll_enabled\": false,\n" + "      \"reachable\": false,\n" + "      \"producing\": false,\n"
				+ "      \"limit_relative\": 100,\n" + "      \"limit_absolute\": 1500,\n" + "      \"AC\": {\n"
				+ "        \"0\": {\n" + "          \"Power\": {\n" + "            \"v\": 0,\n"
				+ "            \"u\": \"W\",\n" + "            \"d\": 1\n" + "          },\n"
				+ "          \"Voltage\": {\n" + "            \"v\": 223.8999939,\n" + "            \"u\": \"V\",\n"
				+ "            \"d\": 1\n" + "          },\n" + "          \"Current\": {\n" + "            \"v\": 0,\n"
				+ "            \"u\": \"A\",\n" + "            \"d\": 2\n" + "          },\n"
				+ "          \"Frequency\": {\n" + "            \"v\": 49.97999954,\n" + "            \"u\": \"Hz\",\n"
				+ "            \"d\": 2\n" + "          },\n" + "          \"PowerFactor\": {\n"
				+ "            \"v\": 0,\n" + "            \"u\": \"\",\n" + "            \"d\": 3\n" + "          },\n"
				+ "          \"ReactivePower\": {\n" + "            \"v\": 19.5,\n" + "            \"u\": \"var\",\n"
				+ "            \"d\": 1\n" + "          }\n" + "        }\n" + "      },\n" + "      \"DC\": {\n"
				+ "        \"0\": {\n" + "          \"name\": {\n" + "            \"u\": \"panel4\"\n"
				+ "          },\n" + "          \"Power\": {\n" + "            \"v\": 0.100000001,\n"
				+ "            \"u\": \"W\",\n" + "            \"d\": 1\n" + "          },\n"
				+ "          \"Voltage\": {\n" + "            \"v\": 12.10000038,\n" + "            \"u\": \"V\",\n"
				+ "            \"d\": 1\n" + "          },\n" + "          \"Current\": {\n"
				+ "            \"v\": 0.01,\n" + "            \"u\": \"A\",\n" + "            \"d\": 2\n"
				+ "          },\n" + "          \"YieldDay\": {\n" + "            \"v\": 190,\n"
				+ "            \"u\": \"Wh\",\n" + "            \"d\": 0\n" + "          },\n"
				+ "          \"YieldTotal\": {\n" + "            \"v\": 233.9459991,\n"
				+ "            \"u\": \"kWh\",\n" + "            \"d\": 3\n" + "          },\n"
				+ "          \"Irradiation\": {\n" + "            \"v\": 0.044052865,\n" + "            \"u\": \"%\",\n"
				+ "            \"d\": 3,\n" + "            \"max\": 227\n" + "          }\n" + "        },\n"
				+ "        \"1\": {\n" + "          \"name\": {\n" + "            \"u\": \"panel3\"\n"
				+ "          },\n" + "          \"Power\": {\n" + "            \"v\": 0.200000003,\n"
				+ "            \"u\": \"W\",\n" + "            \"d\": 1\n" + "          },\n"
				+ "          \"Voltage\": {\n" + "            \"v\": 12.10000038,\n" + "            \"u\": \"V\",\n"
				+ "            \"d\": 1\n" + "          },\n" + "          \"Current\": {\n"
				+ "            \"v\": 0.02,\n" + "            \"u\": \"A\",\n" + "            \"d\": 2\n"
				+ "          },\n" + "          \"YieldDay\": {\n" + "            \"v\": 174,\n"
				+ "            \"u\": \"Wh\",\n" + "            \"d\": 0\n" + "          },\n"
				+ "          \"YieldTotal\": {\n" + "            \"v\": 69.77899933,\n"
				+ "            \"u\": \"kWh\",\n" + "            \"d\": 3\n" + "          },\n"
				+ "          \"Irradiation\": {\n" + "            \"v\": 0.083333336,\n" + "            \"u\": \"%\",\n"
				+ "            \"d\": 3,\n" + "            \"max\": 240\n" + "          }\n" + "        },\n"
				+ "        \"2\": {\n" + "          \"name\": {\n" + "            \"u\": \"panel2\"\n"
				+ "          },\n" + "          \"Power\": {\n" + "            \"v\": 0.200000003,\n"
				+ "            \"u\": \"W\",\n" + "            \"d\": 1\n" + "          },\n"
				+ "          \"Voltage\": {\n" + "            \"v\": 12.10000038,\n" + "            \"u\": \"V\",\n"
				+ "            \"d\": 1\n" + "          },\n" + "          \"Current\": {\n"
				+ "            \"v\": 0.01,\n" + "            \"u\": \"A\",\n" + "            \"d\": 2\n"
				+ "          },\n" + "          \"YieldDay\": {\n" + "            \"v\": 162,\n"
				+ "            \"u\": \"Wh\",\n" + "            \"d\": 0\n" + "          },\n"
				+ "          \"YieldTotal\": {\n" + "            \"v\": 160.2870026,\n"
				+ "            \"u\": \"kWh\",\n" + "            \"d\": 3\n" + "          },\n"
				+ "          \"Irradiation\": {\n" + "            \"v\": 0.041666668,\n" + "            \"u\": \"%\",\n"
				+ "            \"d\": 3,\n" + "            \"max\": 480\n" + "          }\n" + "        },\n"
				+ "        \"3\": {\n" + "          \"name\": {\n" + "            \"u\": \"panel1\"\n"
				+ "          },\n" + "          \"Power\": {\n" + "            \"v\": 0.300000012,\n"
				+ "            \"u\": \"W\",\n" + "            \"d\": 1\n" + "          },\n"
				+ "          \"Voltage\": {\n" + "            \"v\": 12.10000038,\n" + "            \"u\": \"V\",\n"
				+ "            \"d\": 1\n" + "          },\n" + "          \"Current\": {\n"
				+ "            \"v\": 0.029999999,\n" + "            \"u\": \"A\",\n" + "            \"d\": 2\n"
				+ "          },\n" + "          \"YieldDay\": {\n" + "            \"v\": 185,\n"
				+ "            \"u\": \"Wh\",\n" + "            \"d\": 0\n" + "          },\n"
				+ "          \"YieldTotal\": {\n" + "            \"v\": 102.012001,\n" + "            \"u\": \"kWh\",\n"
				+ "            \"d\": 3\n" + "          },\n" + "          \"Irradiation\": {\n"
				+ "            \"v\": 0.062500007,\n" + "            \"u\": \"%\",\n" + "            \"d\": 3,\n"
				+ "            \"max\": 480\n" + "          }\n" + "        }\n" + "      },\n" + "      \"INV\": {\n"
				+ "        \"0\": {\n" + "          \"Power DC\": {\n" + "            \"v\": 0.800000012,\n"
				+ "            \"u\": \"W\",\n" + "            \"d\": 1\n" + "          },\n"
				+ "          \"YieldDay\": {\n" + "            \"v\": 711,\n" + "            \"u\": \"Wh\",\n"
				+ "            \"d\": 0\n" + "          },\n" + "          \"YieldTotal\": {\n"
				+ "            \"v\": 566.0240479,\n" + "            \"u\": \"kWh\",\n" + "            \"d\": 3\n"
				+ "          },\n" + "          \"Temperature\": {\n" + "            \"v\": 6.900000095,\n"
				+ "            \"u\": \"°C\",\n" + "            \"d\": 1\n" + "          },\n"
				+ "          \"Efficiency\": {\n" + "            \"v\": 0,\n" + "            \"u\": \"%\",\n"
				+ "            \"d\": 3\n" + "          }\n" + "        }\n" + "      },\n" + "      \"events\": 4\n"
				+ "    }\n" + "  ],\n" + "  \"total\": {\n" + "    \"Power\": {\n" + "      \"v\": 0,\n"
				+ "      \"u\": \"W\",\n" + "      \"d\": 0\n" + "    },\n" + "    \"YieldDay\": {\n"
				+ "      \"v\": 909,\n" + "      \"u\": \"Wh\",\n" + "      \"d\": 0\n" + "    },\n"
				+ "    \"YieldTotal\": {\n" + "      \"v\": 714.3240356,\n" + "      \"u\": \"kWh\",\n"
				+ "      \"d\": 3\n" + "    }\n" + "  },\n" + "  \"hints\": {\n" + "    \"time_sync\": false,\n"
				+ "    \"radio_problem\": false,\n" + "    \"default_password\": false\n" + "  }\n" + "}";
		var jsonObj = JsonUtils.parse(json);
		var converter = new OpenDTUConverter();
		var model = converter.toInverterModel(jsonObj);

		assertEquals(1, model.getInverters().size());
		var inverter = model.getInverters().get(0);
		assertEquals("116666666666", inverter.getSerial());
		assertEquals("hm1500", inverter.getName());
		assertEquals(0, inverter.getOrder());
		assertEquals(11670, inverter.getDataAge());
		assertFalse(inverter.isPollEnabled());
		assertFalse(inverter.isReachable());
		assertFalse(inverter.isProducing());
		assertEquals(100, inverter.getLimitRelative());
		assertEquals(1500, inverter.getLimitAbsolute());
		var aclist = inverter.getAc();
		assertEquals(1, aclist.size());
		var ac0 = aclist.get(0);
		assertEquals(0, ac0.getNr());
		assertEquals(new InverterValueDouble(0.0, InverterUnit.W, 1), ac0.getPower());
		assertEquals(new InverterValueDouble(223.8999939, InverterUnit.V, 1), ac0.getVoltage());
		assertEquals(new InverterValueDouble(0.0, InverterUnit.A, 2), ac0.getCurrent());
		assertEquals(new InverterValueDouble(49.97999954, InverterUnit.Hz, 2), ac0.getFrequency());
		assertEquals(new InverterValueDouble(0.0, InverterUnit.NONE, 3), ac0.getPowerFactor());
		assertEquals(new InverterValueDouble(19.5, InverterUnit.VAR, 1), ac0.getReactivPower());
		var dclist = inverter.getDc();
		assertEquals(4, dclist.size());
		var dc0 = dclist.get(0);
		assertEquals(0, dc0.getNr());
		assertEquals("panel4", dc0.getName());
		assertEquals(new InverterValueDouble(0.100000001, InverterUnit.W, 1), dc0.getPower());
		assertEquals(new InverterValueDouble(12.10000038, InverterUnit.V, 1), dc0.getVoltage());
		assertEquals(new InverterValueDouble(0.01, InverterUnit.A, 2), dc0.getCurrent());
		assertEquals(new InverterValueDouble(190d, InverterUnit.Wh, 0), dc0.getYieldDay());
		assertEquals(new InverterValueDouble(233.9459991, InverterUnit.kWh, 3), dc0.getYieldTotal());
		assertEquals(new InverterValueDouble(0.044052865, InverterUnit.PERCENT, 3, 227), dc0.getIrradiation());
		var dc1 = dclist.get(1);
		assertEquals(1, dc1.getNr());
		assertEquals("panel3", dc1.getName());
		assertEquals(new InverterValueDouble(0.200000003, InverterUnit.W, 1), dc1.getPower());
		assertEquals(new InverterValueDouble(12.10000038, InverterUnit.V, 1), dc1.getVoltage());
		assertEquals(new InverterValueDouble(0.02, InverterUnit.A, 2), dc1.getCurrent());
		assertEquals(new InverterValueDouble(174d, InverterUnit.Wh, 0), dc1.getYieldDay());
		assertEquals(new InverterValueDouble(69.77899933, InverterUnit.kWh, 3), dc1.getYieldTotal());
		assertEquals(new InverterValueDouble(0.083333336, InverterUnit.PERCENT, 3, 240), dc1.getIrradiation());
		var dc2 = dclist.get(2);
		assertEquals(2, dc2.getNr());
		assertEquals("panel2", dc2.getName());
		assertEquals(new InverterValueDouble(0.200000003, InverterUnit.W, 1), dc2.getPower());
		assertEquals(new InverterValueDouble(12.10000038, InverterUnit.V, 1), dc2.getVoltage());
		assertEquals(new InverterValueDouble(0.01, InverterUnit.A, 2), dc2.getCurrent());
		assertEquals(new InverterValueDouble(162d, InverterUnit.Wh, 0), dc2.getYieldDay());
		assertEquals(new InverterValueDouble(160.2870026, InverterUnit.kWh, 3), dc2.getYieldTotal());
		assertEquals(new InverterValueDouble(0.041666668, InverterUnit.PERCENT, 3, 480), dc2.getIrradiation());
		var dc3 = dclist.get(3);
		assertEquals(3, dc3.getNr());
		assertEquals("panel1", dc3.getName());
		assertEquals(new InverterValueDouble(0.300000012, InverterUnit.W, 1), dc3.getPower());
		assertEquals(new InverterValueDouble(12.10000038, InverterUnit.V, 1), dc3.getVoltage());
		assertEquals(new InverterValueDouble(0.029999999, InverterUnit.A, 2), dc3.getCurrent());
		assertEquals(new InverterValueDouble(185d, InverterUnit.Wh, 0), dc3.getYieldDay());
		assertEquals(new InverterValueDouble(102.012001, InverterUnit.kWh, 3), dc3.getYieldTotal());
		assertEquals(new InverterValueDouble(0.062500007, InverterUnit.PERCENT, 3, 480), dc3.getIrradiation());
		var inv = inverter.getInv();
		assertEquals(new InverterValueDouble(0.800000012, InverterUnit.W, 1), inv.getPowerDc());
		assertEquals(new InverterValueDouble(6.900000095, InverterUnit.DEGREE_C, 1), inv.getTemperature());
		assertEquals(new InverterValueDouble(711d, InverterUnit.Wh, 0), inv.getYieldDay());
		assertEquals(new InverterValueDouble(566.0240479, InverterUnit.kWh, 3), inv.getYieldTotal());
		assertEquals(new InverterValueDouble(0d, InverterUnit.PERCENT, 3), inv.getEfficiency());
		var total = model.getTotal();
		assertEquals(new InverterValueDouble(0d, InverterUnit.W, 0), total.getPower());
		assertEquals(new InverterValueDouble(909d, InverterUnit.Wh, 0), total.getYieldDay());
		assertEquals(new InverterValueDouble(714.3240356, InverterUnit.kWh, 3), total.getYieldTotal());
		var hints = model.getHints();
		assertFalse(hints.isTimeSync());
		assertFalse(hints.isDefaultPassword());
		assertFalse(hints.isRadioProblem());

	}

	@Test
	public void testOpenDTULimitModel() throws OpenemsNamedException {
		var json = "{\n" + "  \"116666666666\": {\n" + "    \"limit_relative\": 40,\n" + "    \"max_power\": 1500,\n"
				+ "    \"limit_set_status\": \"Ok\"\n" + "  },\n" + "  \"112666666666\": {\n"
				+ "    \"limit_relative\": 100,\n" + "    \"max_power\": 350,\n"
				+ "    \"limit_set_status\": \"Failure\"\n" + "  }\n" + "}";
		var jsonObj = JsonUtils.parse(json);
		var converter = new OpenDTUConverter();
		var model = converter.toInverterLimitModel(jsonObj);

		assertEquals(2, model.getInverterLimits().size());

		var hm1500 = model.getInverterLimit("116666666666");
		assertEquals(40, hm1500.getLimitRelative().intValue());
		assertEquals(1500, hm1500.getMaxPower().intValue());
		assertEquals(InverterLimitStatus.Ok, hm1500.getLimitSetStatus());

		var hm350 = model.getInverterLimit("112666666666");
		assertEquals(100, hm350.getLimitRelative().intValue());
		assertEquals(350, hm350.getMaxPower().intValue());
		assertEquals(InverterLimitStatus.Failure, hm350.getLimitSetStatus());
	}

	@Test
	public void testOpenDTUSetLimitModel() {
		var model = new InverterSetLimit("116666666666");
		var converter = new OpenDTUConverter();
		var json = converter.toInverterLimitJson(model);

		assertEquals(116666666666l, json.get("serial").getAsLong());
		assertEquals(100, json.get("limit_value").getAsInt());
		assertEquals(LimitType.RelativNonPersistent.toLimit(), json.get("limit_type").getAsInt());

		model.setLimit_value(50);
		model.setLimit_type(LimitType.RelativPersistent);
		json = converter.toInverterLimitJson(model);

		assertEquals("116666666666", json.get("serial").getAsString());
		assertEquals(50, json.get("limit_value").getAsInt());
		assertEquals(LimitType.RelativPersistent.toLimit(), json.get("limit_type").getAsInt());

	}

	@Test
	public void testInverteSetLimitErrorResponse() throws OpenemsNamedException {
		var json = "{\"type\":\"error\",\"message\":\"something went wrong!\",\"code\":1000}";
		var jsonObj = JsonUtils.parse(json);
		var converter = new OpenDTUConverter();
		var model = converter.toInverterSetLimitResponse(jsonObj);

		assertFalse(model.isOK());
		assertEquals("something went wrong!", model.getMessage());
		assertEquals(1000, model.getCode());
	}

	@Test
	public void testInverteSetLimitOKResponse() throws OpenemsNamedException {
		var json = "{\"type\":\"success\",\"message\":\"Settings saved!\",\"code\":1001}";
		var jsonObj = JsonUtils.parse(json);
		var converter = new OpenDTUConverter();
		var model = converter.toInverterSetLimitResponse(jsonObj);

		assertTrue(model.isOK());
		assertEquals("Settings saved!", model.getMessage());
		assertEquals(1001, model.getCode());
	}
}
