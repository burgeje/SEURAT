/*
 * Created on Jan 15, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.inference;

import java.sql.*;
import java.util.Iterator;
import java.util.Vector;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Handles inferencing (evaluation, error checking, ...) at the alternative
 * level.
 * @author jburge
 */
public class AlternativeInferences {
	
	
	/**
	 * Constant indicating that two arguments disagree with each other
	 */
	static int DISAGREE = 1;
	/**
	 * Constant indicating that two arguments are the same (this is usually an error)
	 */
	static int AGREE = 2;
	/**
	 * Constant indicating that two arguments contradict each other
	 */
	static int DIFFERENT = 3;
	
	public AlternativeInferences() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Updates the rationale status when an alternative is deleted. This 
	 * will affect any decisions that the alternative was a solution for
	 * @param alt - the alternative that has been removed from the database.
	 * @return - a vector giving the status updates that need to be displayed
	 */
	public Vector<RationaleStatus> updateOnDelete(Alternative alt) {
		
		Vector<RationaleStatus> newStatus = new Vector<RationaleStatus>();
		DecisionInferences inf = new DecisionInferences();
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
		String findQuery = "";
		//boolean error = false;
		try {
			stmt = conn.createStatement();
			
			
			if (alt.getPtype() == RationaleElementType.DECISION)
			{
				findQuery = "SELECT name  FROM " +
				"decisions where " +
				"id = " + alt.getParent();
//				***				 System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery);
				while (rs.next())
				{
					Decision dec = new Decision();
					dec.fromDatabase(RationaleDB.decode(rs.getString("name")));
					Vector<RationaleStatus> results = inf.updateDecisionStatus(dec);
					if (results != null)
					{
						newStatus.addAll(results);
					}
				}
			} 	
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "AlternativeInferences.UpdateOnDelete",
					findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);			
		}
		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(alt.getID(), alt.getName(), RationaleElementType.ALTERNATIVE);
		
		return newStatus;
	}
	
	/**
	 * Performs updates needed when something has happened that might affect the
	 * status of an alternative. This could mean an argument has changed, or some other
	 * rationale element that connects to an alterantive (like a question or requirement)
	 * @param alt - the alternative that needs updating
	 * @return a vector of the new alternative status that needs to be displayed
	 */
	public Vector<RationaleStatus> updateAlternative(Alternative alt) {
		String findQuery = ""; 
		Vector<RationaleStatus> newStatus  = new Vector<RationaleStatus>();
		alt.evaluate(); //this is actually done when it is re-written...
//		System.out.println("Saving alternative after update");
		alt.toDatabase(alt.getParent(), alt.getPtype());
		
		//lets check to see if we have any unanswered questions
//		check to see if the selected alternative has any questions!
		Iterator ourQuestions = alt.getQuestions().iterator();
		while (ourQuestions.hasNext())
		{
			Question nextQ = (Question) ourQuestions.next();
			if (nextQ.getStatus() != QuestionStatus.ANSWERED)
			{
				String problem = "Question '" + nextQ.getName() + "' about alt is unanswered"; 
				RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.WARNING, problem, 
						RationaleElementType.ALTERNATIVE, new java.util.Date(), alt.getID(),
						RationaleStatusType.UNANSWERED_ALT_QUEST);
				newStatus.add(stat);					
//				***			System.out.println(problem);
			}
		}	
		DecisionInferences inf = new DecisionInferences();
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
		//boolean error = false;
		try {
			stmt = conn.createStatement();
			
			
			if (alt.getPtype() == RationaleElementType.DECISION)
			{
				findQuery = "SELECT name  FROM " +
				"decisions where " +
				"id = " + alt.getParent();
//				***				 System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery);
				while (rs.next())
				{
					Decision dec = new Decision();
					dec.fromDatabase(RationaleDB.decode(rs.getString("name")));
					Vector<RationaleStatus> results = inf.updateDecisionStatus(dec);
					if (results != null)
					{
						newStatus.addAll(results);
					}
				}
			}
			
			//are we pre-supposed by anyone? opposed-by anyone? if we are,
			//maybe they need to be updated.
			
			Vector<RationaleStatus> relResults = null;
			Iterator relI = db.getDependentAlternatives(alt).iterator();
			while (relI.hasNext())
			{
//				System.out.println("found some dependencies");
				Alternative relAlt = (Alternative) relI.next();
				//even if not selected, we will want to update the evaluation
//				if (relAlt.getStatus() == AlternativeStatus.ADOPTED)
//				{
				AlternativeInferences altInf = new AlternativeInferences();
				relResults = altInf.updateAlternative(relAlt);
//				}
			}
			if (relResults != null)
			{
				newStatus.addAll(relResults);
			}			 		
			
			//need to check for contradictory arguments
			//first, for the same argument on opposing sides
			Iterator argAI = alt.getArgumentsAgainst().iterator();
			Iterator argFI = alt.getArgumentsFor().iterator();
			while (argAI.hasNext())
			{
				Argument argA = (Argument) argAI.next();
				while (argFI.hasNext())
				{
					Argument argF = (Argument) argFI.next();
					int agreement = compareArguments(argA, argF);
					
					if (agreement != DIFFERENT)
					{
						String problem = "The same, or opposite argument, both supports and "+
						" opposes alt '" + alt.getName() + "'"; 
						RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.ERROR, problem, 
								RationaleElementType.ALTERNATIVE, new java.util.Date(), alt.getID(),
								RationaleStatusType.CONTRADICTORY_ARGUMENTS);
						newStatus.add(stat);
					}
					
					// Not entirely clear on why this is commented out - this must have
					// caused false errors.
					/*		 			if (agreement == AGREE)
					 {
					 //flag this as an error!
					  String problem = "The same, or similar argument, both supports and "+
					  " opposes alternative " + alt.getName(); 
					  RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.ERROR, problem, 
					  RationaleElementType.ALTERNATIVE, new java.util.Date(), alt.getID(),
					  RationaleStatusType.CONTRADICTORY_ARGUMENTS);
					  newStatus.add(stat);					
					  
					  }
					  else if ((agreement == DISAGREE) && 
					  (argF.getCategory() == ArgCategory.REQUIREMENT))
					  {
					  //flag this as an error!
					   String problem = "Contradictory arguments for alternative " + alt.getName(); 
					   RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.ERROR, problem, 
					   RationaleElementType.ALTERNATIVE, new java.util.Date(), alt.getID(),
					   RationaleStatusType.CONTRADICTORY_ARGUMENTS);
					   newStatus.add(stat);
					   } */
					
				} 
			}
			
			//now check for opposing arguments on the same side...
			Iterator argI1 = alt.getArgumentsFor().iterator();
			Iterator argI2 = alt.getArgumentsFor().iterator();
			if (argI2.hasNext())
			{
				argI2.next(); //step to the second one	
			}
			while (argI2.hasNext())
			{
				Argument a1 = (Argument) argI1.next();
				Argument a2 = (Argument) argI2.next();
				int agreement = compareArguments(a1, a2);
				if (agreement == DISAGREE)
				{
					//flag as an error
					String problem = "Contradictory arguments appear on the same side for '" + alt.getName() + "'"; 
					RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.ERROR, problem, 
							RationaleElementType.ALTERNATIVE, new java.util.Date(), alt.getID(),
							RationaleStatusType.CONTRADICTORY_ARGUMENTS);
					newStatus.add(stat);
				}
				else if (agreement == AGREE)
				{
					String problem = "Duplicate arguments found regarding " +
					" alt '" + alt.getName() + "'"; 
					RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.WARNING, problem, 
							RationaleElementType.ALTERNATIVE, new java.util.Date(), alt.getID(),
							RationaleStatusType.DUPLICATE_ARGUMENTS);
					newStatus.add(stat);
				}
				
				
			}
			//need to clear out any requirement violations if we are
			//not selected (decision infs. won't catch this)
			if (alt.getStatus() != AlternativeStatus.ADOPTED)
			{
				System.out.println("checking for requirement violations");
				Vector argA = alt.getArgumentsAgainst();
				argAI = argA.iterator();
				while (argAI.hasNext())
				{
					Argument rarg = (Argument) argAI.next();
					if (rarg.getRequirement() != null)
					{
						Requirement req = rarg.getRequirement();
						if ((req.getStatus() != ReqStatus.REJECTED) && 
//								(req.getStatus() != ReqStatus.DEFERRED) &&
								(req.getStatus() != ReqStatus.RETRACTED) &&
								(req.getEnabled()))
						{
							RequirementInferences reqInf = new RequirementInferences();
							newStatus.addAll(reqInf.updateRequirement(req));
						}
						
					}
				}
			}
			
		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex, "AlternativeInferences.updateAlternative",
					findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(alt.getID(), alt.getName(), RationaleElementType.ALTERNATIVE);
		
		return newStatus;
	}
	
	/**
	 * Compares two arguments (for the same alternative) to find out if they
	 * are different, identical, or contradictory (for example - if an alternative
	 * was given arguments saying it was "Safe" and "NOT Safe" those arguments
	 * would contradict)
	 * @param arg1 - first argument being compared
	 * @param arg2 - second argument being compared
	 * @return - status value describing difference 
	 */
	private int compareArguments(Argument arg1, Argument arg2)
	{
		int result = DIFFERENT;
		if (arg1.getCategory() != arg2.getCategory())
		{
			return DIFFERENT;
		}
		if (arg1.getCategory() == ArgCategory.CLAIM)
		{
			String ont1 = arg1.getClaim().getOntology().getName();
			String ont2 = arg2.getClaim().getOntology().getName();
			if (ont1.compareTo(ont2) != 0)
			{
				return DIFFERENT;
			}
			else
			{
				if (arg1.getClaim().getDirection() == arg2.getClaim().getDirection())
				{
					result = AGREE;
				}
				else
				{
					result = DISAGREE;
				}
			}
		}
		else if (arg1.getCategory() == ArgCategory.REQUIREMENT)
		{
			String req1 = arg1.getRequirement().getName();
			String req2 = arg2.getRequirement().getName();
			if (req1.compareTo(req2) != 0)
			{
				result = DIFFERENT;
			}
			else
			{
				if (arg1.getType() == arg2.getType())
				{
					result = AGREE;
				}
				else if ((arg1.getType() != ArgType.VIOLATES) &&
						(arg2.getType() != ArgType.VIOLATES))
				{
					//if one addresses and the other supports we call it agree
					result = AGREE;
				}
				else
				{
					result = DISAGREE;
//					System.out.println("requirement arguments disagree");
				}
			}
		}
		else if (arg1.getCategory() == ArgCategory.ASSUMPTION)
		{
			String asm1 = arg1.getAssumption().getName();
			String asm2 = arg2.getAssumption().getName();
			if (asm1.compareTo(asm2) == 0)
			{
				result = AGREE;
			}
			else
			{
				result = DIFFERENT;
			}
		}
		else if (arg1.getCategory() == ArgCategory.ALTERNATIVE)
		{
			String alt1 = arg1.getAlternative().getName();
			String alt2 = arg2.getAlternative().getName();
			if (alt1.compareTo(alt2) == 0)
			{
				result = AGREE;
			}
			else
			{
				result = DIFFERENT;
			}
		}
		return result;
	}
	
}
