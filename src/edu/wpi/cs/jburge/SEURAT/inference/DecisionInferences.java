/*
 * Created on Jan 12, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.inference;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Performs necessary inference when a decision or 
 * something dependent on a decision changes.
 * @author jburge
 */
public class DecisionInferences {

	/**
	 * Empty Constructor
	 */
	public DecisionInferences() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Updates the status of the decision. This includes - calculating support for all arguments (evaluation),
	 * checking for errors in the arguments for/against, and checking for tradeoff violations.
	 * @param ourDec - the decision being updated
	 * @return - a vector of status that needs to be displayed
	 */
	public Vector<RationaleStatus> updateDecisionStatus(Decision ourDec)
	{

		RationaleErrorLevel error = null;
		boolean selected = false;
		double evaluation = 0;
		double maxEvaluation = -99;
		Vector<RationaleStatus> ourStatus = new Vector<RationaleStatus>();

		//don't bother computing status for the base idem
		if (ourDec.getID() == 0)
		{
			return ourStatus;
		}
		//first, check to see if the decision has a selected alternative
		Enumeration alts = ourDec.getAlternatives().elements();
		if (alts == null)
		{
//			System.out.println("No alternatives?");
		}
		else
		{
			int selCount = 0;
			while (alts.hasMoreElements())
			{
				Alternative alt = (Alternative) alts.nextElement();
				if (alt.getStatus() == AlternativeStatus.ADOPTED)
				{
					selected = true;
					//in some cases, multiple selections are allowed
					if (selCount != 0)
					{
						if (alt.getEvaluation() > evaluation)
						{
							evaluation = alt.getEvaluation();
						}
					}
					else
					{
						evaluation = alt.getEvaluation();
					}
					selCount++;
				}
				if (alt.getEvaluation() > maxEvaluation)
				{
					maxEvaluation = alt.getEvaluation();
				}
			}
			if ((!selected) && (ourDec.getAlts()))
			{
				error = RationaleErrorLevel.ERROR;
				String problem = "No alternative selected";
				RationaleStatus stat = new RationaleStatus(error, problem, 
						RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
						RationaleStatusType.NONE_SELECTED );
				ourStatus.add(stat);
				ourDec.setStatus(DecisionStatus.UNRESOLVED);
				/*				boolean diff = stat.toDatabase(ourDec.getID());
				 if (diff)
				 ourStatus.add(stat);
				 else
				 System.out.println("status is not different?");
				 */
			}
			else if (!ourDec.getAlts())
			{
				if (ourDec.getSubDecisions().size() <= 0)
				{
					String problem = "Sub-decisions are required but missing";	
					RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.ERROR, problem, 
							RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
							RationaleStatusType.SUBDECISIONS_MISSING);
					ourStatus.add(stat);
					ourDec.setStatus(DecisionStatus.UNRESOLVED);
				}
			}
			//the alternative is selected
			else
			{
				if ((selCount > 1) && (ourDec.getType() == DecisionType.SINGLECHOICE))
				{
					String problem = "Multiple alternatives selected for the decision";	
					RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.ERROR, problem, 
							RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
							RationaleStatusType.MULTIPLE_SELECTION);
					ourStatus.add(stat);
				}
				ourDec.setStatus(DecisionStatus.RESOLVED);

				Alternative selAlt = ourDec.getSelected();	

				//check the evaluation
				if (evaluation < maxEvaluation)
				{
					String problem = "Alt '" + selAlt.getName() + "' selected but not the best";
					RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.WARNING, problem, 
							RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
							RationaleStatusType.LESS_SUPPORTED);
					ourStatus.add(stat);
					/*
					 if (diff)
					 ourStatus.add(stat);
					 else
					 System.out.println("status is not different?");
					 */					   				   
				}

				//perform inferences that look at the selected alternative

				//Make sure there are arguments for this alternative!
				//The arguments For should include anyone who we are pre-supposed by
				if (selAlt.getArgumentsFor().size() == 0)
				{

					//anyone against us?
					if (selAlt.getArgumentsAgainst().size() > 0)
					{
						//this is an error
						String problem = "Alt '" + selAlt.getName() + " selected for " + ourDec.getName() +
						"' has arguments against it but none for it.";	
						RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.ERROR, problem, 
								RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
								RationaleStatusType.SELECTED_ONLY_AGAINST);
						ourStatus.add(stat);	
					}
					else
					{
						//this is an warning
						String problem = "Alt '" + selAlt.getName() + " selected for " + ourDec.getName() +
						"' has no arguments in its favor.";	
						RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.WARNING, problem, 
								RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
								RationaleStatusType.SELECTED_NONE_FOR);
						ourStatus.add(stat);							
					}

				} //end checking arguments for

				//check to see if we have any requirements violations
				Vector argAgainst = selAlt.getArgumentsAgainst();
				Iterator againstI = argAgainst.iterator();
				while (againstI.hasNext())
				{
					Argument nArg = (Argument) againstI.next();
					if (nArg.getType() == ArgType.VIOLATES)
					{
						Requirement nreq = nArg.getRequirement();
						if ((nreq.getStatus() != ReqStatus.REJECTED) &&
								(nreq.getStatus() != ReqStatus.RETRACTED) &&
								(nreq.getEnabled()))
						{
							String problem = "Selected alt '" + selAlt.getName() + "' violates requirement " + nreq.getName();
							RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.ERROR, problem, 
									RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
									RationaleStatusType.ALT_REQ_VIOLATION);	
							ourStatus.add(stat);
							ourStatus.addAll(nreq.updateStatus());		
						}

					}
				}

				//Check to see if we need to update any other requirements
				Vector argFor = selAlt.getArgumentsFor();
				Iterator forI = argFor.iterator();
				while (forI.hasNext())
				{
					Argument nArg = (Argument) forI.next();
					if ((nArg.getType() == ArgType.ADDRESSES) ||
							(nArg.getType() == ArgType.SATISFIES))
					{
						Requirement nreq = nArg.getRequirement();
						if ((nreq.getStatus() != ReqStatus.REJECTED) &&
								(nreq.getStatus() != ReqStatus.RETRACTED) &&
								(nreq.getEnabled()))
						{
							ourStatus.addAll(nreq.updateStatus());		
						}

					}
				}				

				//check to see if the selected alternative pre-supposes
				//someone...

				Vector preSupposes = selAlt.getAlts(ArgType.PRESUPPOSES);
				if (preSupposes.size() > 0 )
				{
//					System.out.println("found presupposes!");
					//iterate through and make sure the alternative is selected!
					Iterator altI = preSupposes.iterator();
					while (altI.hasNext())
					{
						Alternative otherAlt = (Alternative) altI.next();
						if (otherAlt.getStatus() != AlternativeStatus.ADOPTED)
						{
							String problem = "Alt '" + selAlt.getName() + "' requires " +
							"non-selected alt '" + otherAlt.getName() + "'";	
							RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.ERROR, problem, 
									RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
									RationaleStatusType.PRESUPPOSED_NOTSEL);
							ourStatus.add(stat);							
						}
					}
				}
				//check to see if the selected alternative is opposed by someone
				Vector opposes = selAlt.getAlts(ArgType.OPPOSES);
				if (opposes.size() > 0 )
				{
					//iterate through and make sure the alternative is selected!
					Iterator altI = opposes.iterator();
					while (altI.hasNext())
					{
						Alternative otherAlt = (Alternative) altI.next();
						if (otherAlt.getStatus() == AlternativeStatus.ADOPTED)
						{
							String problem = "Alt '" + selAlt.getName() + "' is opposed by " +
							"selected alt '" + otherAlt.getName() + "'";	
							RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.ERROR, problem, 
									RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
									RationaleStatusType.OPPOSED_SEL);
							ourStatus.add(stat);							
						}
					}
				}

				//check to see if the selected alternative has any questions!
				Iterator ourQuestions = selAlt.getQuestions().iterator();
				while (ourQuestions.hasNext())
				{
					Question nextQ = (Question) ourQuestions.next();
					if (nextQ.getStatus() != QuestionStatus.ANSWERED)
					{
						String problem = "Question '" + nextQ.getName() + "' about selected alt is unanswered"; 
						RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.WARNING, problem, 
								RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
								RationaleStatusType.UNANSWERED_ALT_QUEST);
						ourStatus.add(stat);					
					}
				}				
				//Did check the tradeoffs here - moved to below...

				//check for any requirements violations? - make a specific routine
				//for this that checks for a specific decision.
				//go over all arguments
				//if argument points to a requirement...

			}

		}				

		//check all Tradeoffs - this is probably not real efficient... 
		RationaleDB db = RationaleDB.getHandle();
		Vector tradeoffs = db.getTradeoffData();
		Iterator tradeI = tradeoffs.iterator();
		while (tradeI.hasNext())
		{
			Tradeoff ourTrade = (Tradeoff) tradeI.next();
			Vector<RationaleStatus> newStat = this.updateTradeoff(ourDec, ourTrade);
			if (newStat != null)
			{
				ourStatus.addAll(newStat);
			}
		}
		//check for unanswered questions - here we don't care if there
		//is a selected alternative or not!
		Iterator ourQuestions = ourDec.getQuestions().iterator();
		while (ourQuestions.hasNext())
		{
			Question nextQ = (Question) ourQuestions.next();
			if (nextQ.getStatus() != QuestionStatus.ANSWERED)
			{
				String problem = "Question '" + nextQ.getName() + "' is unanswered"; 
				RationaleStatus stat = new RationaleStatus(RationaleErrorLevel.WARNING, problem, 
						RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
						RationaleStatusType.UNANSWERED_QUEST);
				ourStatus.add(stat);					
			}
		}
		//we put everyone examined in the update manager because maybe
		//the status changed from error to ok!
		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(ourDec.getID(), ourDec.getName(), RationaleElementType.DECISION);

		//is our parent a decision? if we are a sub-decision we might want
		//to update the parent status!
		if ((ourDec.getPtype() == RationaleElementType.DECISION) && (ourDec.getParent() != 0))
		{
			Decision parentDec = new Decision();
			parentDec.fromDatabase(ourDec.getParent());
			DecisionInferences nextInf = new DecisionInferences();
			Vector<RationaleStatus> parentStatus = nextInf.updateDecisionStatus(parentDec);
			if (parentStatus != null)
			{
				ourStatus.addAll(parentStatus);
			}

		} 
		if (ourStatus.size() > 0)
		{
			return ourStatus;
		}
		else
		{
			return null;
		}



	}

	/**
	 * Make any status updates needed when a decision is deleted
	 * @param ourDec - the decision that is being deleted
	 * @return a vector containing status updates to be displayed
	 */
	public Vector<RationaleStatus> updateOnDelete(Decision ourDec)
	{
		Vector<RationaleStatus> newStatus = new Vector<RationaleStatus>();
		DecisionInferences inf = new DecisionInferences();

		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		Statement stmt = null; 
		ResultSet rs = null; 
		String findQuery = "";
		//	boolean error = false;
		try {
			stmt = conn.createStatement();

			//what about our parents?
			if (ourDec.getPtype() == RationaleElementType.DECISION)
			{
				findQuery = "SELECT name  FROM " +
				"decisions where " +
				"id = " + ourDec.getParent();
				System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery);
				while (rs.next())
				{
					Decision dec = new Decision();
					dec.fromDatabase(RationaleDBUtil.decode(rs.getString("name")));
					Vector<RationaleStatus> results = inf.updateDecisionStatus(dec);
					if (results != null)
					{
						newStatus.addAll(results);
					}
				}
			} 	
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "DecisionInferences.updateOnDelete",
					findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}

		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(ourDec.getID(), ourDec.getName(), RationaleElementType.DECISION);
		return newStatus;
	}

	//This version looks at IS and NOT, rather than supports, denies
	public Vector<RationaleStatus> updateTradeoff(Decision ourDec, Tradeoff ourTrade)
	{
		Vector<RationaleStatus> ourStatus = new Vector<RationaleStatus>();

//		warn about any incomplete rationale!		Alternative alt = ourDec.getSelected();
		Vector<Alternative> altV = ourDec.getAlternatives();
		Iterator altI = altV.iterator();
		while (altI.hasNext())
		{
			Alternative alt = (Alternative) altI.next();
			Vector ourClaims = alt.getClaims();

			//figure out who is where
			boolean t1IS = false;
			boolean t2IS = false;
			boolean t1Present = false;
			boolean t2Present = false;
			boolean allFalse = true;

			//iterate through the For claims
			Iterator clmI = ourClaims.iterator();
			while (clmI.hasNext())
			{
				Claim ourClaim = (Claim) clmI.next();
				//do the ontologies match?
				if (ourClaim.getOntology().getName().compareTo(ourTrade.getOnt1().getName()) == 0)
				{
					t1IS = (ourClaim.getDirection() == Direction.IS);
					t1Present = true;
					allFalse = false;
				}
				else if (ourClaim.getOntology().getName().compareTo(ourTrade.getOnt2().getName()) == 0)
				{
					t2IS = (ourClaim.getDirection() == Direction.IS);
					t2Present = true;
					allFalse = false;
				}
			}


			if (!allFalse)
			{
//				System.out.println("match found for " + ourDec.getName());
//				System.out.println("t1 for = " + new Boolean(t1For).toString());
//				System.out.println("t1 against = " + new Boolean(t1Against).toString());
//				System.out.println("t2 for = " + new Boolean(t2For).toString());
//				System.out.println("t2 against" + new Boolean(t2Against).toString());	

				String errorMsg = "alt '" + alt.getName() + 
				"' violates tradeoff '" + ourTrade.getOnt1().getName() + " vs " +
				ourTrade.getOnt2().getName() + "'";
				RationaleErrorLevel error = null;
				RationaleStatusType stat = null;
				//First, are we a tradeoff?
				if (ourTrade.getTradeoff())
				{
					stat = RationaleStatusType.TRADE_VIOLATION;
					//first, check the cases that are always bad
					//this is when we actually contradict the tradeoff
					if ((t1IS == t2IS) && (t1Present && t2Present))
					{
						error = RationaleErrorLevel.ERROR;
						errorMsg = errorMsg + ": tradeoff is contradicted";

					}
					//then we look to see if there are problems in the 
					//"prime" direction
					else if ((t1Present) && (!t2Present))
					{
						error = RationaleErrorLevel.WARNING;
						errorMsg = errorMsg + ": second element is missing";
					}
					//in Symmetric cases, we also look for when the desired
					//tradeoff is not completely there. 
					else if (ourTrade.getSymmetric())
					{
						if ((t2Present) && (!t1Present))
						{
							error = RationaleErrorLevel.WARNING;
							errorMsg = errorMsg + ": first element is missing";
						}
					}

				} //end if Tradeoff
				//now, check the co-occurences
				else
				{
					stat = RationaleStatusType.CO_OCCURRENCE_VIOLATION;
					errorMsg = "alt '" + alt.getName() + 
					"' violates co-occurrence '" + ourTrade.getOnt1().getName() + " vs " +
					ourTrade.getOnt2().getName() + "'";

					//first, check the cases that are always bad - when the oppose
					if ((t1IS != t2IS) && (t1Present && t2Present))
					{
						error = RationaleErrorLevel.ERROR;
						errorMsg = errorMsg + ": co-occurrence is contradicted";
					}
					//then, check to see if the second is missing
					else if ((t1Present) && (!t2Present))
					{
						error = RationaleErrorLevel.WARNING;
						errorMsg = errorMsg + ": second element is missing";

					}
					else if (ourTrade.getSymmetric())
					{
						if ((t2Present) && (!t1Present))
						{	
							error = RationaleErrorLevel.WARNING;
							errorMsg = errorMsg + ": first element is missing";
						}
					}

				} //co-occurence
				if (error != null)
				{
//					System.out.println(errorMsg);
					RationaleStatus newStat = new RationaleStatus(RationaleErrorLevel.WARNING, errorMsg, 
							RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
							stat);
					ourStatus.add(newStat);

				}
			}
		}

		return ourStatus;
	}	
	/*	
	 public Vector updateTradeoff(Decision ourDec, Tradeoff ourTrade)
	 {
	 Vector ourStatus = new Vector();
	 Alternative alt = ourDec.getSelected();
	 Vector claimsFor = alt.getClaimsFor();
	 Vector claimsAgainst = alt.getClaimsAgainst();

	 //figure out who is where
	  boolean t1For = false;
	  boolean t2For = false;
	  boolean t1Against = false;
	  boolean t2Against = false;
	  boolean allFalse = true;

	  //iterate through the For claims
	   Iterator clmI = claimsFor.iterator();
	   while (clmI.hasNext())
	   {
	   OntEntry claim = (OntEntry) clmI.next();
	   if (claim.getName().compareTo(ourTrade.getOnt1().getName()) == 0)
	   {
	   t1For = true;
	   allFalse = false;
	   }
	   else if (claim.getName().compareTo(ourTrade.getOnt2().getName()) == 0) 
	   {
	   t2For = true;
	   allFalse = false;
	   }
	   }
	   clmI = claimsAgainst.iterator();
	   while (clmI.hasNext())
	   {
	   OntEntry claim = (OntEntry) clmI.next();
	   if (claim.getName().compareTo(ourTrade.getOnt1().getName()) == 0)
	   {
	   t1Against = true;
	   allFalse = false;
	   }
	   else if (claim.getName().compareTo(ourTrade.getOnt2().getName()) == 0) 
	   {
	   t2Against = true;
	   allFalse = false;
	   }
	   }

	   if (!allFalse)
	   {
	   System.out.println("match found for " + ourDec.getName());
	   System.out.println("t1 for = " + new Boolean(t1For).toString());
	   System.out.println("t1 against = " + new Boolean(t1Against).toString());
	   System.out.println("t2 for = " + new Boolean(t2For).toString());
	   System.out.println("t2 against" + new Boolean(t2Against).toString());	
	   }
	   String errorMsg = "alternative " + ourDec.getSelected().getName() + 
	   " violates tradeoff " + ourTrade.getOnt1().getName() + " vs " +
	   ourTrade.getOnt2().getName();
	   RationaleErrorLevel error = null;
	   RationaleStatusType stat = null;
	   //First, are we a tradeoff?
	    if (ourTrade.getTradeoff())
	    {
	    stat = RationaleStatusType.TRADE_VIOLATION;
	    //first, check the cases that are always bad
	     //this is when we actually contradict the tradeoff
	      if ((t1Against) && (t2Against))
	      {
	      error = RationaleErrorLevel.ERROR;
	      errorMsg = errorMsg + ": tradeoff is contradicted";

	      }
	      else if ((t1For) && (t2For))
	      {
	      error = RationaleErrorLevel.ERROR;
	      errorMsg = errorMsg + ": tradeoff is contradicted";

	      }
	      //then we look to see if there are problems in the 
	       //"prime" direction
	        else if ((t1Against) && (!t2For))
	        {
	        error = RationaleErrorLevel.WARNING;
	        errorMsg = errorMsg + ": second element is missing";
	        }
	        else if ((t1For) && (!t2Against))
	        {
	        System.out.println("missing found!");
	        error = RationaleErrorLevel.WARNING;
	        errorMsg = errorMsg + ": second element is missing";
	        }
	        //in Symmetric cases, we also look for when the desired
	         //tradeoff is not completely there. 
	          else if (ourTrade.getSymmetric())
	          {
	          if ((t2For) && (!t1Against))
	          {
	          error = RationaleErrorLevel.WARNING;
	          errorMsg = errorMsg + ": first element is missing";
	          }
	          else if ((t2Against) && (!t1For))
	          {
	          error = RationaleErrorLevel.WARNING;
	          errorMsg = errorMsg + ": first element is missing";
	          }

	          }

	          } //end if Tradeoff
	          //now, check the co-occurences
	           else
	           {
	           stat = RationaleStatusType.CO_OCCURRENCE_VIOLATION;
	           errorMsg = "alternative " + ourDec.getSelected().getName() + 
	           " violates co-occurrence " + ourTrade.getOnt1().getName() + " vs " +
	           ourTrade.getOnt2().getName();

	           //first, check the cases that are always bad - when the oppose
	            if ((t1For) && (t2Against))
	            {
	            error = RationaleErrorLevel.ERROR;
	            errorMsg = errorMsg + ": co-occurrence is contradicted";
	            }
	            else if ((t1Against) && (t2For))
	            {
	            error = RationaleErrorLevel.ERROR;
	            errorMsg = errorMsg + ": co-occurrence is contradicted";
	            }
	            //then, check to see if the second is missing
	             else if ((t1For) && (!t2For))
	             {
	             error = RationaleErrorLevel.WARNING;
	             errorMsg = errorMsg + ": second element is missing";

	             }
	             else if ((t1Against) && (!t2Against))
	             {
	             error = RationaleErrorLevel.WARNING;
	             errorMsg = errorMsg + ": second element is missing";

	             }
	             else if (ourTrade.getSymmetric())
	             {
	             if ((t2For) && (!t1For))
	             {	
	             error = RationaleErrorLevel.WARNING;
	             errorMsg = errorMsg + ": first element is missing";
	             }
	             else if ((t2Against) && (!t2Against))
	             {
	             error = RationaleErrorLevel.WARNING;
	             errorMsg = errorMsg + ": first element is missing";
	             }
	             }
	             } //co-occurence
	             if (error != null)
	             {
	             System.out.println(errorMsg);
	             RationaleStatus newStat = new RationaleStatus(RationaleErrorLevel.WARNING, errorMsg, 
	             RationaleElementType.DECISION, new java.util.Date(), ourDec.getID(),
	             stat);
	             ourStatus.add(newStat);

	             }

	             return ourStatus;
	             }
	 */
	public boolean checkTradeoff(Decision ourDec, Tradeoff ourTrade)
	{
		boolean tradeoffApplies = false;
		Vector<Alternative>  altV = ourDec.getAlternatives();
		Iterator<Alternative> altI = altV.iterator();
		
		while (altI.hasNext())
		{
			Alternative alt = altI.next();

		if (alt == null)
		{
			return false;
		}
		else
		{
			Vector ourClaims = alt.getClaims();

			//iterate through the For claims
			Iterator clmI = ourClaims.iterator();
//			System.out.println("testing trade " + ourTrade.getName());
//			System.out.println("against decision " + ourDec.getName());
//			System.out.println("and alternative " + alt.getName());
			while (clmI.hasNext())
			{
				Claim claim = (Claim) clmI.next();
				if ((claim.getOntology().getName()).compareTo(ourTrade.getOnt1().getName()) == 0)
				{
					tradeoffApplies = true;
				}
				else if ((claim.getOntology().getName()).compareTo(ourTrade.getOnt2().getName()) == 0) 
				{
					tradeoffApplies = true;
				}
			}
			if (tradeoffApplies)
			{
				//			System.out.println("Tradeoff applies to " + ourDec.getName());			
			}
			
		}
		}
			return tradeoffApplies;
	}


}

