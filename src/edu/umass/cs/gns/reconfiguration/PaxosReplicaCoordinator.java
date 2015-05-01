package edu.umass.cs.gns.reconfiguration;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.umass.cs.gns.gigapaxos.PaxosManager;
import edu.umass.cs.gns.nio.IntegerPacketType;
import edu.umass.cs.gns.nio.InterfaceJSONNIOTransport;
import edu.umass.cs.gns.nio.JSONMessenger;
import edu.umass.cs.gns.util.MyLogger;
import edu.umass.cs.gns.util.Stringifiable;

public class PaxosReplicaCoordinator<NodeIDType> extends
		AbstractReplicaCoordinator<NodeIDType> {

	private final PaxosManager<NodeIDType> paxosManager;
	public static final Logger log = Logger.getLogger(Reconfigurator.class
			.getName());

	public PaxosReplicaCoordinator(InterfaceReplicable app, NodeIDType myID,
			Stringifiable<NodeIDType> unstringer,
			InterfaceJSONNIOTransport<NodeIDType> niot) {
		super(app, (JSONMessenger<NodeIDType>) niot);
		this.paxosManager = new PaxosManager<NodeIDType>(myID, unstringer,
				niot, this);
	}

	@Override
	public Set<IntegerPacketType> getRequestTypes() {
		return this.app.getRequestTypes();
	}

	@Override
	public boolean coordinateRequest(InterfaceRequest request)
			throws IOException, RequestParseException {
		// this.sendAllLazy(request);
		return this.coordinateRequest(request.getServiceName(), request);
	}

	// in case paxosGroupID is not the same as the name in the request
	public boolean coordinateRequest(String paxosGroupID,
			InterfaceRequest request) throws RequestParseException {
		String proposee = this.propose(paxosGroupID, request);
		log.log(Level.INFO,
				MyLogger.FORMAT[6],
				new Object[] {
						this,
						(proposee != null ? "paxos-coordinated"
								: "failed to paxos-coordinate"),
						request.getRequestType(), " to ", paxosGroupID, ":",
						request });
		return proposee != null;
	}

	private String propose(String paxosID, InterfaceRequest request) {
		String proposee = null;
		if (request instanceof InterfaceReconfigurableRequest)
			proposee = this.paxosManager.proposeStop(paxosID, request
					.toString(),
					(short) ((InterfaceReconfigurableRequest) request)
							.getEpochNumber());
		else
			proposee = this.paxosManager.propose(paxosID, request.toString());
		return proposee;
	}

	public boolean createReplicaGroup(String groupName, int epoch,
			String state, Set<NodeIDType> nodes) {
		log.info(this + " creating paxos instance " + groupName + ":" + epoch
				+ (state != null ? " with initial state " + state : ""));
		if (!this.paxosManager.existsOrHigher(groupName, (short) epoch))
			this.paxosManager.createPaxosInstance(groupName, (short) epoch,
					nodes, this, state);
		return true;
	}

	public String toString() {
		return this.getClass().getSimpleName() + getMyID();
	}

	@Override
	public Set<NodeIDType> getReplicaGroup(String serviceName) {
		if (this.paxosManager.isStopped(serviceName))
			return null;
		return this.paxosManager.getPaxosNodeIDs(serviceName);
	}

	/*
	 * FIXME: Needed only for reconfiguring reconfigurators, which is not yet
	 * implemented. We also need PaxosManager support for deleting a paxos
	 * group.
	 */
	// @Override
	public void deleteReplicaGroup(String serviceName, int epoch) {
		this.paxosManager.deletePaxosInstance(serviceName, (short) epoch);
	}

	@Override
	public void deleteReplicaGroup(String serviceName) {
		throw new RuntimeException("Method not implemented");
	}

	@Override
	public String getFinalState(String name, int epoch) {
		String state = this.paxosManager.getFinalState(name, (short) epoch);
		log.info(this.getMyID()
				+ " received request for epoch final state "
				+ name
				+ ":"
				+ epoch
				+ "; returning: "
				+ state
				+ (state == null ? " paxos instance stopped is "
						+ this.paxosManager.isStopped(name)
						+ " and epoch final state is "
						+ this.paxosManager.getFinalState(name, (short) epoch)
						: ""));
		return state;
	}
	
	protected void forceCheckpoint(String paxosID) {
		this.paxosManager.forceCheckpoint(paxosID);
	}

	@Override
	public boolean deleteFinalState(String name, int epoch) {
		/*
		 * Will also delete one previous version. Sometimes, a node can miss a
		 * drop epoch that arrived even before it created that epoch, in which
		 * case, it would end up trying hard and succeeding at creating the
		 * epoch that just got dropped by using the previous epoch final state
		 * if it is available locally. So it is best to delete that final state
		 * as well so that the late, zombie epoch creation eventually fails.
		 * 
		 * Note: Usually deleting lower epochs in addition to the specified
		 * epoch is harmless. There is at most one lower epoch final state at a
		 * node anyway.
		 */
		return this.paxosManager.deleteFinalState(name, (short) epoch, 1);
	}
}
