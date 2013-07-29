package edu.umass.cs.gnrs.packet;


import edu.umass.cs.gnrs.nameserver.NameRecordKey;
import edu.umass.cs.gnrs.util.JSONUtils;
import java.util.ArrayList;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddRecordPacket extends BasicPacket {

  private final static String REQUESTID = "reqID";
  private final static String LNSREQID = "lnreqID";
  private final static String NAME = "name";
  private final static String RECORDKEY = "recordkey";
  private final static String VALUE = "value";
  private final static String LOCALNAMESERVERID = "localID";
  private final static String PRIMARYNAMESERVERS = "primaryID";
  /** 
   * Unique identifier used by the entity making the initial request to confirm
   */
  private int requestID;
  /**
   * The ID the LNS uses to for bookeeping
   */
  private int LNSRequestID;
  /**
   * The key of the value key pair. For GNRS this will be EdgeRecord, CoreRecord or GroupRecord. *
   */
  private NameRecordKey recordKey;
  /**
   * Host/domain/device name *
   */
  private String name;
  /**
   * the value *
   */
  private ArrayList<String> value;
  /**
   * Id of local nameserver handling this request *
   */
  private int localNameServerID;
  
  /// this will be filled in by the local nameserver
  private Set<Integer> primaryNameServers;
  

  /**
   * ***********************************************************
   * Constructs a new AddRecordPacket with the given name and value.
   *
   * @param name Host/domain/device name
   * @param value
   * @param localNameServerID Id of local nameserver sending this request.
   * **********************************************************
   */
  public AddRecordPacket(int requestID, String name, NameRecordKey recordKey, ArrayList<String> value, int localNameServerID) {
    this.type = Packet.PacketType.ADD_RECORD_LNS;
    this.requestID = requestID;
    this.recordKey = recordKey;
    this.name = name;
    this.value = value;
    this.localNameServerID = localNameServerID;
  }

  /**
   * ***********************************************************
   * Constructs a new AddRecordPacket from a JSONObject
   *
   * @param json JSONObject that represents this packet
   * @throws JSONException **********************************************************
   */
  public AddRecordPacket(JSONObject json) throws JSONException {
    if (Packet.getPacketType(json) != Packet.PacketType.ADD_RECORD_LNS
            && Packet.getPacketType(json) != Packet.PacketType.ADD_RECORD_NS) {
      Exception e = new Exception("AddRecordPacket: wrong packet type " + Packet.getPacketType(json));
      e.printStackTrace();
    }

    this.type = Packet.getPacketType(json);
    this.requestID = json.getInt(REQUESTID);
    this.LNSRequestID = json.getInt(LNSREQID);
    this.recordKey = NameRecordKey.valueOf(json.getString(RECORDKEY));
    this.name = json.getString(NAME);
    this.value = JSONUtils.JSONArrayToArrayList(json.getJSONArray(VALUE));
    //this.value = json.getString(VALUE);
    this.localNameServerID = json.getInt(LOCALNAMESERVERID);
    this.primaryNameServers = JSONUtils.JSONArrayToSetInteger(json.getJSONArray(PRIMARYNAMESERVERS));
  }

  /**
   * ***********************************************************
   * Converts AddRecordPacket object to a JSONObject
   *
   * @return JSONObject that represents this packet
   * @throws JSONException **********************************************************
   */
  @Override
  public JSONObject toJSONObject() throws JSONException {
    JSONObject json = new JSONObject();
    Packet.putPacketType(json, getType());
    json.put(REQUESTID, getRequestID());
    json.put(LNSREQID, getLNSRequestID());
    json.put(RECORDKEY, getRecordKey().getName());
    json.put(NAME, getName());
    json.put(VALUE, new JSONArray(getValue()));
    json.put(LOCALNAMESERVERID, getLocalNameServerID());
    json.put(PRIMARYNAMESERVERS, new JSONArray(getPrimaryNameServers()));
    return json;
  }

  public int getRequestID() {
    return requestID;
  }

  public int getLNSRequestID() {
    return LNSRequestID;
  }

  public void setLNSRequestID(int LNSRequestID) {
    this.LNSRequestID = LNSRequestID;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
  
  /**
   * @return the recordKey
   */
  public NameRecordKey getRecordKey() {
    return recordKey;
  }

  /**
   * @return the value
   */
  public ArrayList<String> getValue() {
    return value;
  }

  /**
   * @return the primaryNameserverId
   */
  public int getLocalNameServerID() {
    return localNameServerID;
  }

  /**
   * @return the primaryNameServers
   */
  public Set<Integer> getPrimaryNameServers() {
    return primaryNameServers;
  }

  /**
   * @param primaryNameServers the primaryNameServers to set
   */
  public void setPrimaryNameServers(Set<Integer> primaryNameServers) {
    this.primaryNameServers = primaryNameServers;
  }
}
