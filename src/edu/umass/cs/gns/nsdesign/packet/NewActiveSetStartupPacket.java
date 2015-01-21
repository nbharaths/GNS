package edu.umass.cs.gns.nsdesign.packet;

import edu.umass.cs.gns.nsdesign.packet.Packet.PacketType;
import edu.umass.cs.gns.reconfiguration.InterfaceRequest;
import edu.umass.cs.gns.util.Stringifiable;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Random;
import java.util.Set;

/**
 * TODO write documentation for this class.
 *
 * @param <NodeIDType>
 */
public class NewActiveSetStartupPacket<NodeIDType> extends BasicPacket implements InterfaceRequest {

  private final static String ID = "nasID"; // new active set ID
  private final static String NAME = "name";
  private final static String PRIMARY_SENDER = "primarySender";
  private final static String ACTIVE_SENDER = "activeSender";
  private final static String NEW_ACTIVES = "newActives";
  private final static String OLD_ACTIVES = "oldActives";
  private final static String NEW_ACTIVE_VERSION = "newVersion";
  private final static String OLD_ACTIVE_VERSION = "oldVersion";
  private final static String PREVIOUS_VALUE = "previousValue";
  private final static String PREVIOUS_VALUE_CORRECT = "pvCorrect";

  /**
   * A unique ID to distinguish this packet at active replica
   */
  private int uniqueID;
  /**
   * name for which the proposal is being done.
   */
  private final String name;
  /**
   * primary node that sent this message
   */
  private final NodeIDType primarySender;
  /**
   * active node to which this message was sent
   */
  private NodeIDType activeSender;
  /**
   * current set of actives of this node.
   */
  private final Set<NodeIDType> newActives;
  /**
   * Previous set of actives of this node.
   */
  private final Set<NodeIDType> oldActives;
  /**
   * Version of the new set of actives.
   */
  private final short newActiveVersion;
  /**
   * Version of the old set of actives.
   */
  private final short oldActiveVersion;
  /**
   * Value at the end of previous epoch.
   */
  private String previousValue; // CAN BE NULL
  /**
   * Value at the end of previous epoch.
   */
  private boolean previousValueCorrect;

  /**
   *
   * @param name
   * @param primarySender
   * @param activeSender
   * @param newActives
   * @param oldActives
   * @param oldActiveVersion
   * @param newActiveVersion
   * @param type1
   * @param previousValue
   * @param previousValueCorrect
   */
  public NewActiveSetStartupPacket(String name,
          NodeIDType primarySender, NodeIDType activeSender, Set<NodeIDType> newActives,
          Set<NodeIDType> oldActives, short oldActiveVersion, short newActiveVersion,
          PacketType type1, String previousValue, boolean previousValueCorrect) {
    Random r = new Random();
    this.uniqueID = r.nextInt();
    this.name = name;
    this.type = type1;

    this.primarySender = primarySender;
    this.activeSender = activeSender;
    this.newActives = newActives;
    this.oldActives = oldActives;
    this.oldActiveVersion = oldActiveVersion;
    this.newActiveVersion = newActiveVersion;
    this.previousValue = previousValue;
    this.previousValueCorrect = previousValueCorrect;
  }

  public NewActiveSetStartupPacket(JSONObject json, Stringifiable<NodeIDType> unstringer) throws JSONException {

    this.type = Packet.getPacketType(json);
    this.uniqueID = json.getInt(ID);
    this.name = json.getString(NAME);
    this.primarySender = unstringer.valueOf(json.getString(PRIMARY_SENDER));
    this.activeSender = unstringer.valueOf(json.getString(ACTIVE_SENDER));

    this.newActives = unstringer.getValuesFromJSONArray(json.getJSONArray(NEW_ACTIVES));
    //this.newActives = Util.stringToSetOfNodeId(json.getString(NEW_ACTIVES));

    this.oldActives = unstringer.getValuesFromJSONArray(json.getJSONArray(OLD_ACTIVES));
    //this.oldActives = Util.stringToSetOfNodeId(json.getString(OLD_ACTIVES));

    this.oldActiveVersion = (short) json.getInt(OLD_ACTIVE_VERSION);
    this.newActiveVersion = (short) json.getInt(NEW_ACTIVE_VERSION);
    this.previousValue = json.has(PREVIOUS_VALUE) ? json.getString(PREVIOUS_VALUE) : null;
    this.previousValueCorrect = json.getBoolean(PREVIOUS_VALUE_CORRECT);
  }

  /**
   * JSON object that is implemented.
   *
   * @return
   * @throws org.json.JSONException
   */
  @Override
  public JSONObject toJSONObject() throws JSONException {
    JSONObject json = new JSONObject();
    Packet.putPacketType(json, getType());
    json.put(ID, uniqueID);
    json.put(NAME, name);
    json.put(PRIMARY_SENDER, primarySender);
    json.put(ACTIVE_SENDER, activeSender);

    //String actives = Util.setOfNodeIdToString(newActives);
    json.put(NEW_ACTIVES, newActives);

    //String old_actives = Util.setOfNodeIdToString(oldActives);
    json.put(OLD_ACTIVES, oldActives);

    json.put(NEW_ACTIVE_VERSION, this.newActiveVersion);
    json.put(OLD_ACTIVE_VERSION, this.oldActiveVersion);
    if (previousValue != null) {
      json.put(PREVIOUS_VALUE, previousValue);
    }
    json.put(PREVIOUS_VALUE_CORRECT, previousValueCorrect);
    return json;
  }

//  private String convertArrayToString(Set<NodeIDType> values) {
//    StringBuilder sb = new StringBuilder();
//    for (NodeIDType x : values) {
//      if (sb.length() == 0) {
//        sb.append(x.toString());
//      } else {
//        sb.append(":" + x.toString());
//      }
//    }
//    return sb.toString();
//  }
  public void setUniqueID(int uniqueID) {
    this.uniqueID = uniqueID;
  }

  public int getUniqueID() {
    return uniqueID;
  }

  public String getName() {
    return name;
  }

  public NodeIDType getSendingPrimary() {
    return primarySender;
  }

  public NodeIDType getSendingActive() {
    return activeSender;
  }

  /**
   *
   * @return
   */
  public Set<NodeIDType> getNewActiveNameServers() {
    return newActives;
  }

  /**
   *
   * @return
   */
  public Set<NodeIDType> getOldActiveNameServers() {
    return oldActives;
  }

  public short getOldActiveVersion() {
    return oldActiveVersion;
  }

  public short getNewActiveVersion() {
    return newActiveVersion;
  }

  public String getPreviousValue() {
    return previousValue;
  }

  public boolean getPreviousValueCorrect() {
    return previousValueCorrect;
  }

  public void changePacketTypeToForward() {
    setType(PacketType.NEW_ACTIVE_START_FORWARD);
  }

  public void changePacketTypeToResponse() {
    setType(PacketType.NEW_ACTIVE_START_RESPONSE);
  }

  public void changePacketTypeToConfirmation() {
    setType(PacketType.NEW_ACTIVE_START_CONFIRM_TO_PRIMARY);
  }

  public void changePacketTypeToPreviousValueRequest() {
    setType(PacketType.NEW_ACTIVE_START_PREV_VALUE_REQUEST);
  }

  public void changePacketTypeToPreviousValueResponse() {
    setType(PacketType.NEW_ACTIVE_START_PREV_VALUE_RESPONSE);
  }

  public void changeSendingActive(NodeIDType ID) {
    this.activeSender = ID;
  }

  public void changePreviousValue(String value) {
    this.previousValue = value;
  }

  public void changePreviousValueCorrect(boolean previousValueCorrect) {
    this.previousValueCorrect = previousValueCorrect;
  }

  // For InterfaceRequest
  @Override
  public String getServiceName() {
    return this.name;
  }

}
