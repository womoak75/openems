package io.openems.common.jsonrpc.response;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.openems.common.jsonrpc.base.JsonrpcResponseSuccess;
import io.openems.common.jsonrpc.request.GetChannelsValuesRequest;
import io.openems.common.types.ChannelAddress;

/**
 * Represents a JSON-RPC Response for {@link GetChannelsValuesRequest}.
 * 
 * <pre>
 * {
 *   "jsonrpc": "2.0",
 *   "id": "UUID",
 *   "result": {
 *     [Edge-ID: string]: [{
 *       [Channel-Address: string]: number
 *     }]
 *   }
 * }
 * </pre>
 */
public class GetChannelsValuesResponse extends JsonrpcResponseSuccess {

	private final Table<String, ChannelAddress, JsonElement> values = HashBasedTable.create();

	public GetChannelsValuesResponse() {
		this(UUID.randomUUID());
	}

	public GetChannelsValuesResponse(UUID id) {
		super(id);
	}

	public void addValue(String edgeId, ChannelAddress channel, JsonElement value) {
		this.values.put(edgeId, channel, value);
	}

	@Override
	public JsonObject getResult() {
		JsonObject j = new JsonObject();
		for (Entry<String, Map<ChannelAddress, JsonElement>> row : this.values.rowMap().entrySet()) {
			String edgeId = row.getKey();
			Map<ChannelAddress, JsonElement> columns = row.getValue();
			JsonObject jEdge = new JsonObject();
			for (Entry<ChannelAddress, JsonElement> column : columns.entrySet()) {
				ChannelAddress channel = column.getKey();
				JsonElement value = column.getValue();
				jEdge.add(channel.toString(), value);
			}
			j.add(edgeId, jEdge);
		}
		return j;
	}

}