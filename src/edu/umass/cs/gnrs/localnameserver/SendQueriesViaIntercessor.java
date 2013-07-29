package edu.umass.cs.gnrs.localnameserver;

//import java.util.ArrayList;

import edu.umass.cs.gnrs.main.GNS;
import edu.umass.cs.gnrs.main.StartLocalNameServer;
import edu.umass.cs.gnrs.nameserver.NameRecordKey;
import edu.umass.cs.gnrs.packet.DNSPacket;
import edu.umass.cs.gnrs.packet.DNSRecordType;
import edu.umass.cs.gnrs.packet.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimerTask;

//import edu.umass.cs.gnrs.nameserver.NameRecordKey;
//import edu.umass.cs.gnrs.util.Util;

public class SendQueriesViaIntercessor // extends Thread
{
//	private static ExponentialDistribution exponentialDistribution;
//	public SendQueriesViaIntercessor()
//	{
////		super("SendQueriesViaIntercessor");
//		if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("Starting thread 2.Lookup rate : " + StartLocalNameServer.lookupRate);
//		
//	}

	public static void schdeduleAllQueries() {
		if (LocalNameServer.lookupTrace == null)
		{
			if (StartLocalNameServer.debugMode) GNS.getLogger().fine("Lookup trace is null. SendQueriesViaIntercessor thread quitting.");
			return;
		}
		if (StartLocalNameServer.debugMode) GNS.getLogger().fine("Send query intercessor started. Number of queries. "
				+ LocalNameServer.lookupTrace.size());
//		exponentialDistribution = new ExponentialDistribution(StartLocalNameServer.lookupRate );
		double delay = 0;

		long expectedDurationSec = (long) ((LocalNameServer.lookupTrace.size() * 
				StartLocalNameServer.lookupRate)/1000);
		String msg = "SendQueryStart StartTime " + delay + 
				" Expected-Duration " + expectedDurationSec + 
				" Number-Queries " + LocalNameServer.lookupTrace.size();

		GNS.getStatLogger().fine(msg);
		if (StartLocalNameServer.debugMode) GNS.getLogger().fine(msg);
		int count = 0;
		for( String name : LocalNameServer.lookupTrace) {
			count++;
			LocalNameServer.experimentSendRequestTimer.schedule(new SendQueryIntercessorTask(count, name), (long) delay);
			delay += StartLocalNameServer.lookupRate;//exponentialDistribution.exponential();
			if (StartLocalNameServer.debugMode) GNS.getLogger().fine(" Send query scheduled: count " + count + " delay = " + delay);
		}
		
		if (StartLocalNameServer.debugMode) GNS.getLogger().fine("Final delay = " + delay/1000 + " Expected-duration " + expectedDurationSec);
	}
	
}



class SendQueryIntercessorTask extends TimerTask {

	int count;
    String name;
	public SendQueryIntercessorTask(int count, String name1) {
		this.count = count;
        this.name = name1;
	}
	
	@Override
	public void run() {

		if (StartLocalNameServer.tinyQuery)
			LNSSendTinyQuery.sendQuery(name, count);
		else {
            int id = count;

            Header header = new Header(id, DNSRecordType.QUERY, DNSRecordType.RCODE_NO_ERROR);
            DNSPacket queryRecord = new DNSPacket(header, name, NameRecordKey.EdgeRecord);
            JSONObject json;
            try {
                json = queryRecord.toJSONObjectQuestion();
                LNSListener.demultiplexLNSPackets(json);
            } catch (JSONException e) {
                e.printStackTrace();

            }

//			LocalNameServer.myInter.sendQueryNoWait(name, NameRecordKey.EdgeRecord.getName());
        }
//		if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("Intercessor sending query number " + count + " for name " + name);
	}
	
}

//	public void run()
//	{
//		if (LocalNameServer.lookupTrace == null)
//		{
//			if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("Lookup trace is null. SendQueriesViaIntercessor thread quitting.");
//			return;
//		}
//
//		if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("Send query intercessor started. Number of queries. "
//				+ LocalNameServer.lookupTrace.size());
//
//		long startTime = System.currentTimeMillis();
//
//		long expectedDurationSec = (LocalNameServer.lookupTrace.size() * 
//				StartLocalNameServer.lookupRate)/1000;
//		String msg = "SendQueryStart StartTime " + startTime + 
//				" Expected-Duration " + expectedDurationSec + 
//				" Number-Queries " + LocalNameServer.lookupTrace.size();
//
//		GNRS.getStatLogger().fine(msg);
//		if (StartLocalNameServer.debugMode) GNRS.getLogger().fine(msg);
//
//
//		int count = 0;
//		ArrayList<Long> queryTimes = new ArrayList<Long>();
//		long tStart = 0;
//		try {
//			for( String name : LocalNameServer.lookupTrace) {
//				if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("Starting to send next query.");
//				if (queryTimes.size() == 0) {
//					if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("Populating this thread.");
//					tStart = System.currentTimeMillis();
//					long timeBetweenQuery = 0;
//					for (int i = 0; i < 1000; i++) {
//						timeBetweenQuery += Util.round(exponentialDistribution.exponential());
//						queryTimes.add(timeBetweenQuery);
//					}
//				}
//				count++;
//				if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("Sending Query. count = "+ (count) +" name = "+ name);
//				if( !LocalNameServer.workloadContainsName( name ) ) {
//					if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("GNRS logger does not contain name.");
//					continue;
//				}
//
//				//Time (ms) between events selected from an exponential distribution	
//
//				boolean success = LocalNameServer.myInter.sendQueryNoWait(name, "edgeRecord");
//				
//				//			if (!success) 
//				//			{
//				//				if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("Msg send failed. count = "+ (count) +"  name = "+ name);
//				//			}
//				//			else
//				//			{
//				//				if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("Msg send success. count = "+ (count) +"  name = "+ name);
//				//			}
//
//				//			int timeBetweenQuery = Util.round( exponentialDistribution.exponential() );
//
//
//				long tNow = System.currentTimeMillis();
//				long sleepTime = queryTimes.get(0) - (tNow - tStart);
//				long delay = (tNow - startTime) - (StartLocalNameServer.lookupRate * count);
//				if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("SendQueryTimeDiff. count = "+ count +
//						" sleepTime = "+ sleepTime + " delay = " + delay);
//
//				//			if (timeBetweenQuery < tUsed) 
//				//			delay += tUsed - timeBetweenQuery;			
//				//			timeBetweenQuery -= (tUsed);
//				//			if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("Sending Query. count = "+ (count) +
//				//					" waiting = "+ timeBetweenQuery);
//
//				if (sleepTime  > 0)  
//				{
//					try {
//						Thread.sleep(sleepTime);
//					} catch (InterruptedException e)
//					{
//						e.printStackTrace();
//					}
//				}
//				if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("After sleep.");
//				queryTimes.remove(0);
//				if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("Removed query time.");
//			}
//
//			long endTime = System.currentTimeMillis();
//			long actualDurationSec = (endTime - startTime)/1000;
//
//			msg = "SendQueryEnd EndTime " + endTime + 
//					" Expected-Duration " + expectedDurationSec + 
//					" Actual-Duration " + actualDurationSec;
//
//			GNRS.getStatLogger().fine(msg);
//			if (StartLocalNameServer.debugMode) GNRS.getLogger().fine(msg);
//
//
//		} catch (Exception e) {
//			if (StartLocalNameServer.debugMode) GNRS.getLogger().fine("EXCEPTION in Send Query Via Intercessor: Mesage" + e.getMessage());
//			StringBuilder sb = new StringBuilder("EXCEPTION STACK TRACE.");
//			for (StackTraceElement s: e.getStackTrace()){
//				sb.append(s.toString() + "\n");
//			}
//			if (StartLocalNameServer.debugMode) GNRS.getLogger().fine(sb.toString());
//		}
//	}
//}

