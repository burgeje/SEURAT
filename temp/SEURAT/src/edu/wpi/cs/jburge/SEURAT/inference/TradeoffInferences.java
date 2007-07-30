/*
 * Created on Jan 12, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.inference;

import java.util.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Performs any inferences needed when a tradeoff is modified.
 * @author jburge
 */
public class TradeoffInferences {

	/**
	 * Empty constructor
	 */
	public TradeoffInferences() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Update the status of a tradeoff. This involves checking to see if a tradeoff
	 * or a co-occurrence has been voilated
	 * @param ourTrade - the tradeoff or co-occurrence being checked
	 * @return a vector of status updates to display
	 */
	public Vector<RationaleStatus> updateTradeoffStatus(Tradeoff ourTrade)
	{
		Vector<RationaleStatus> statV = new Vector<RationaleStatus>();	
		RationaleDB db = RationaleDB.getHandle();
		Vector<Decision> allDecs = db.getAllDecisions();
		Iterator decI = allDecs.iterator();
		DecisionInferences inf = new DecisionInferences();
		while (decI.hasNext())
		{
			Decision ourDec = (Decision) decI.next();
			//check to see if this tradeoff applies to our decision
			if (inf.checkTradeoff(ourDec, ourTrade))
			{
				//if yes, update all the status for the decision	
				Vector<RationaleStatus> uStat = inf.updateDecisionStatus(ourDec);
				if (uStat != null)
				{
					statV.addAll(uStat);
				}
			}
//			UpdateManager manager = UpdateManager.getHandle();
//			manager.addUpdate(ourDec.getID(), ourDec.getName(), RationaleElementType.DECISION);

		}
//		System.out.println("tradeoff status ln = " + statV.size());
		return statV;
	}
		


}

