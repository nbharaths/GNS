package edu.umass.cs.gns.reconfiguration.json.reconfigurationpackets;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.gns.nio.IntegerPacketType;
import edu.umass.cs.gns.reconfiguration.AbstractReconfiguratorDB;
import edu.umass.cs.gns.reconfiguration.RequestParseException;
import edu.umass.cs.gns.util.Stringifiable;
import edu.umass.cs.gns.util.Util;

public class ReconfigureRCNodeConfig<NodeIDType> extends
		BasicReconfigurationPacket<NodeIDType> {

	private static enum Keys {
		NEWLY_ADDED_NODES, NODE_ID, SOCKET_ADDRESS, DELETED_NODES
	};

	// used only in case of new RC node addition
	public final Map<NodeIDType, InetSocketAddress> newlyAddedNodes;
	public final Set<NodeIDType> deletedNodes;

	public ReconfigureRCNodeConfig(NodeIDType initiator, NodeIDType nodeID,
			InetSocketAddress sockAddr) {
		super(initiator,
				ReconfigurationPacket.PacketType.RECONFIGURE_RC_NODE_CONFIG,
				AbstractReconfiguratorDB.RecordNames.NODE_CONFIG.toString(), 0);
		(this.newlyAddedNodes = new HashMap<NodeIDType, InetSocketAddress>())
				.put(nodeID, sockAddr);
		this.deletedNodes = null;
	}

	public ReconfigureRCNodeConfig(JSONObject json,
			Stringifiable<NodeIDType> unstringer) throws JSONException {
		super(json, unstringer);
		this.newlyAddedNodes = this.arrayToMap(
				json.optJSONArray(Keys.NEWLY_ADDED_NODES.toString()),
				unstringer);
		this.deletedNodes = (json.has(Keys.DELETED_NODES.toString()) ? this
				.arrayToSet(json.getJSONArray(Keys.DELETED_NODES.toString()),
						unstringer) : null);
	}

	public JSONObject toJSONObjectImpl() throws JSONException {
		JSONObject json = super.toJSONObjectImpl();
		if (this.newlyAddedNodes != null)
			json.put(Keys.NEWLY_ADDED_NODES.toString(),
					this.mapToArray(newlyAddedNodes));
		if (this.deletedNodes != null)
			json.put(Keys.DELETED_NODES.toString(),
					this.setToArray(this.deletedNodes));

		return json;
	}

	@Override
	public IntegerPacketType getRequestType() throws RequestParseException {
		return ReconfigurationPacket.PacketType.RECONFIGURE_RC_NODE_CONFIG;
	}

	@Override
	public String getServiceName() {
		return AbstractReconfiguratorDB.RecordNames.NODE_CONFIG.toString();
	}

	public Set<NodeIDType> getAddedRCNodeIDs() {
		return (this.newlyAddedNodes != null ? new HashSet<NodeIDType>(
				this.newlyAddedNodes.keySet()) : null);
	}

	// Utility method for newly added nodes
	private Map<NodeIDType, InetSocketAddress> arrayToMap(JSONArray jArray,
			Stringifiable<NodeIDType> unstringer) throws JSONException {
		if (jArray == null || jArray.length() == 0)
			return null;
		Map<NodeIDType, InetSocketAddress> map = new HashMap<NodeIDType, InetSocketAddress>();
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject jElement = jArray.getJSONObject(i);
			assert (jElement.has(Keys.NODE_ID.toString()) && jElement
					.has(Keys.SOCKET_ADDRESS.toString()));
			map.put(unstringer.valueOf(jElement.getString(Keys.NODE_ID
					.toString())), Util.getInetSocketAddressFromString(jElement
					.getString(Keys.SOCKET_ADDRESS.toString())));
		}
		return map;
	}

	// Utility method for newly added nodes
	private JSONArray mapToArray(Map<NodeIDType, InetSocketAddress> map)
			throws JSONException {
		JSONArray jArray = new JSONArray();
		for (NodeIDType node : map.keySet()) {
			JSONObject jElement = new JSONObject();
			jElement.put(Keys.NODE_ID.toString(), node.toString());
			jElement.put(Keys.SOCKET_ADDRESS.toString(), map.get(node)
					.toString());
			jArray.put(jElement);
		}
		return jArray;
	}

	private Set<NodeIDType> arrayToSet(JSONArray jArray,
			Stringifiable<NodeIDType> unstringer) throws JSONException {
		if (jArray == null || jArray.length() == 0)
			return null;
		Set<NodeIDType> set = new HashSet<NodeIDType>();
		for (int i = 0; i < jArray.length(); i++) {
			set.add(unstringer.valueOf(jArray.getString(i)));
		}
		return set;
	}

	// need this to go from NodeIDType to String set
	private JSONArray setToArray(Set<NodeIDType> set) throws JSONException {
		if (set == null || set.isEmpty())
			return null;
		Set<String> stringSet = new HashSet<String>();
		for (NodeIDType node : set)
			stringSet.add(node.toString());
		return new JSONArray(stringSet);
	}

}
