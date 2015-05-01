package edu.umass.cs.gns.reconfiguration.examples.noop;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import edu.umass.cs.gns.nio.IntegerPacketType;
import edu.umass.cs.gns.nio.JSONMessenger;
import edu.umass.cs.gns.reconfiguration.InterfaceReconfigurable;
import edu.umass.cs.gns.reconfiguration.InterfaceReplicable;
import edu.umass.cs.gns.reconfiguration.InterfaceRequest;
import edu.umass.cs.gns.reconfiguration.InterfaceReconfigurableRequest;
import edu.umass.cs.gns.reconfiguration.Reconfigurator;
import edu.umass.cs.gns.reconfiguration.RequestParseException;
import edu.umass.cs.gns.reconfiguration.examples.AppRequest;

/**
 * @author V. Arun
 */
public class NoopApp implements
InterfaceReplicable, InterfaceReconfigurable {

	private static final String DEFAULT_INIT_STATE = "";
	
	private class AppData {
		final String name;
		final int epoch;
		String state=DEFAULT_INIT_STATE;
		AppData(String name, int epoch, String state) {this.name=name; this.epoch=epoch; this.state=state;}
		void setState(String state) {this.state = state;}
		String getState() {return this.state;}
	}
	private final int myID;
	private final HashMap<String, AppData> appData = new HashMap<String,AppData>();
	private final HashMap<String, AppData> prevEpochFinal = new HashMap<String,AppData>();
	private JSONMessenger<Integer> messenger;

	public NoopApp(int id) {
		this.myID = id;
	}
	
	// Need a messenger mainly to send back responses to the client.
	protected void setMessenger(JSONMessenger<Integer> msgr) {
		this.messenger = msgr;
	}

	// FIXME: return response to client
	@Override
	public boolean handleRequest(InterfaceRequest request, boolean doNotReplyToClient) {
		try {
			switch ((AppRequest.PacketType)(request.getRequestType())) {
			case DEFAULT_APP_REQUEST:
				return processRequest((NoopAppRequest)request);
			default:
				break;
			}
		} catch (RequestParseException rpe) {
			rpe.printStackTrace();
		}
		return false;
	}
	
	private boolean processRequest(NoopAppRequest request) {
		if (request.isStop())
			return processStopRequest(request);
		AppData data = this.appData.get(request.getServiceName());
		if (data == null) {
			System.out.println("App-" + myID + " has no record for "
					+ request.getServiceName() + " for " + request);
			return false;
		}
		assert (data != null);
		data.setState(request.getValue());
		this.appData.put(request.getServiceName(), data);
		System.out.println("App-" + myID + " wrote " + data.name + ":"
				+ data.epoch + " with state " + data.getState());
		sendResponse(request);
		return true;
	}
	
	private void sendResponse(NoopAppRequest request) {
		if (this.messenger == null || request.getEntryReplica()!=this.myID)
			return;
		InetSocketAddress sockAddr = new InetSocketAddress(
				request.getSenderAddress(), request.getSenderPort());
		try {
			this.messenger.sendToAddress(sockAddr, request.toJSONObject());
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	private boolean processStopRequest(NoopAppRequest request) {
		AppData data = this.appData.remove(request.getServiceName());
		if (data == null)
			return false;
		// else
		this.prevEpochFinal.put(request.getServiceName(), data);
		System.out.println("App-" + myID + " stopped " + data.name + ":"
				+ (data.epoch) + " with state " + data.getState());
		return true;
	}

	@Override
	public InterfaceRequest getRequest(String stringified)
			throws RequestParseException {
		NoopAppRequest request = null;
		try {
			request = new NoopAppRequest(new JSONObject(stringified));
		} catch (JSONException je) {
			Reconfigurator.log.info(myID + " unable to parse request "
					+ stringified);
			throw new RequestParseException(je);
		}
		return request;
	}

	private static AppRequest.PacketType[] types = {AppRequest.PacketType.DEFAULT_APP_REQUEST, AppRequest.PacketType.APP_COORDINATION};

	@Override
	public Set<IntegerPacketType> getRequestTypes() {
		return new HashSet<IntegerPacketType>(Arrays.asList(types));
	}

	@Override
	public boolean handleRequest(InterfaceRequest request) {
		return this.handleRequest(request, false);
	}

	@Override
	public String getState(String name) {
		//throw new RuntimeException("Method not yet implemented");
		AppData data = this.appData.get(name);
		return (data!=null ? data.getState() : ((data = this.prevEpochFinal.get(name))!=null ? data.getState() : null));
	}

	@Override
	public boolean updateState(String name, String state) {
		AppData data = this.appData.get(name);
		/* If no previous state, set epoch to initial epoch,
		 * otherwise putInitialState will be called.
		 */
		if(data==null) data = new AppData(name, 0, state);
		this.appData.put(name, data);
		return true;
	}

	@Override
	public InterfaceReconfigurableRequest getStopRequest(String name, int epoch) {
		return new NoopAppRequest(name, epoch, (int)(Math.random()*Integer.MAX_VALUE), "", AppRequest.PacketType.DEFAULT_APP_REQUEST, true);
	}

	@Override
	public String getFinalState(String name, int epoch) {
		AppData state = this.prevEpochFinal.get(name);
		if(state!=null && state.epoch==epoch) return state.getState();
		else return null;
	}

	@Override
	public void putInitialState(String name, int epoch, String state) {
		System.out.println("App-"+this.myID+" created record " + name+":"+epoch+ ":"+state);
		AppData data = new AppData(name, epoch, state);
		this.appData.put(name, data);
	}

	@Override
	public boolean deleteFinalState(String name, int epoch) {
		AppData data = this.appData.get(name);
		if(data!=null && data.epoch==epoch) {
			this.appData.remove(name);
			return true;
		}
		return false;
	}

	@Override
	public Integer getEpoch(String name) {
		AppData data = this.appData.get(name);
		if(data!=null) {return data.epoch;}
		return null;
	}
}
