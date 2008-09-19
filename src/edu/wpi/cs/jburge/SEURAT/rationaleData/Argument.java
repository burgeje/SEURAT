/*
 * Argument class
 */

package edu.wpi.cs.jburge.SEURAT.rationaleData;

import instrumentation.DataLog;

import java.util.*;
import java.io.*;  

import java.sql.Connection; 
import java.sql.SQLException; 
import java.sql.Statement;
import java.sql.ResultSet;

import org.eclipse.swt.widgets.Display;
import org.w3c.dom.*;

import SEURAT.events.RationaleElementUpdateEventGenerator;
import SEURAT.events.RationaleUpdateEvent;

import edu.wpi.cs.jburge.SEURAT.editors.EditArgument;
import edu.wpi.cs.jburge.SEURAT.inference.ArgumentInferences;

/**
 * Defines the structure for the arguments stored in the rationale
 * @author burgeje
 *
 */
public class Argument extends RationaleElement implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6496727760462256889L;
	// class variables

	// instance variables
	/**
	 * The type (direction) of argument
	 */
	ArgType type;
	/**
	 * Who we are arguing about
	 */
	int parent;
	/**
	 * The type of element we are arguing about
	 */
	RationaleElementType ptype; 
	/**
	 * The category - arguments can refer to claims, requirements, assumptions, 
	 * or other arguments.
	 */
	ArgCategory category;
	/**
	 * How confident are we that this argument is right?
	 */
	Plausibility plausibility;
	/**
	 * How important is this argument when making the decision?
	 */
	Importance importance;
	/**
	 * By how much does this argument apply? For example, if we arguing that
	 * something is scalable - how scalable?
	 */
	int amount;
	/**
	 * Who is making the argument
	 */
	Designer designer;
	/**
	 * If the argument is about an assumption, we keep the assumption here
	 */
	Assumption assumption;
	/**
	 * If the argument is about an alternative, we keep it here
	 */
	Alternative alternative;
	/**
	 * If the argument is about a claim, we keep it here
	 */
	Claim	claim;
	/**
	 * If the argument is about a requirement, we keep it here
	 */
	Requirement requirement;
	/**
	 * Any arguments for the validity of this argument
	 */
	Vector<Argument> argumentsFor;
	/**
	 * Any arguments against the validity of this argument
	 */
	Vector<Argument> argumentsAgainst;
	/**
	 * Any questions about this argument that need to be answered
	 */
	Vector<Question> questions;
	/**
	 * All the arguments relating to this argument
	 */
	Vector<Argument> arguments;

	private RationaleElementUpdateEventGenerator<Argument> m_eventGenerator = 
		new RationaleElementUpdateEventGenerator<Argument>(this);

	/**
	 * Constructor
	 *
	 */
	public Argument()
	{
		super();
		argumentsFor = new Vector<Argument>();
		argumentsAgainst = new Vector<Argument>();
		questions = new Vector<Question>();
		arguments = new Vector<Argument>();
	} 
	public Element toXML(Document ratDoc)
	{
		Element argE = ratDoc.createElement("DR:argument");
		RationaleDB db = RationaleDB.getHandle();
		String argID = db.getRef(id);
		if (argID == null)
		{
			argID = db.addRef(id);
		}
		argE.setAttribute("id", argID);
		argE.setAttribute("name", name);
		argE.setAttribute("argtype", type.toString());
		if (!importance.toString().equals(Importance.DEFAULT.toString()))
			argE.setAttribute("importance", importance.toString());
		argE.setAttribute("plausibility", plausibility.toString());
		Integer testi = new Integer(amount);
		argE.setAttribute("amount", testi.toString());

		//save our description
		Element descE = ratDoc.createElement("DR:description");
		//set the reference contents
		Text descText = ratDoc.createTextNode(description);
		descE.appendChild(descText);
		argE.appendChild(descE);


		//now the hard part - what is our argument about?
		if (assumption != null)
		{
			argE.appendChild(assumption.toXML(ratDoc));
		}
		else if (alternative != null)
		{
			argE.appendChild(alternative.toXML(ratDoc));
		}
		else if (claim != null)
		{
			argE.appendChild(claim.toXML(ratDoc));
		}
		else if (requirement != null)
		{
			argE.appendChild(requirement.toXML(ratDoc));
		}

		//any arguments?
		Enumeration args = arguments.elements();
		while (args.hasMoreElements())
		{
			Argument arg = (Argument) args.nextElement();
			argE.appendChild(arg.toXML(ratDoc));
		}

		//finally - any questions?
		Enumeration quests = questions.elements();
		while (quests.hasMoreElements())
		{
			Question quest = (Question) quests.nextElement();
			argE.appendChild(quest.toXML(ratDoc));
		}

		return argE;
	}
	public RationaleElementType getElementType()
	{
		return RationaleElementType.ARGUMENT;
	}

	public void setType(ArgType ntype)
	{
		type = ntype;
	}

	public ArgType getType()
	{
		return type;
	}

	public Designer getDesigner() {
		return designer;
	}

	public void setDesigner(Designer designer) {
		this.designer = designer;
	}

	public ArgCategory getCategory()
	{
		return category;
	}
	public RationaleElementType getPtype()
	{
		return ptype;
	}

	public void setPlausibility(Plausibility pls)
	{
		plausibility = pls;
	}

	public Plausibility getPlausibility()
	{
		return plausibility;
	}

	public void setImportance(Importance imp)
	{
		importance = imp;
	}

	public Importance getImportance()
	{
		return importance;
	}

	public void setAmount (int amt)
	{
		amount = amt;
	}

	public void addArgument(Argument newArg)
	{
		arguments.addElement(newArg);
	}

	public Vector getArguments()
	{
		return arguments;
	}

	public int getAmount()
	{
		return amount;
	}

	public int getParent()
	{
		return parent;
	}
	public Vector getArgumentsFor()
	{
		return argumentsFor;
	}

	public Vector getArgumentsAgainst()
	{
		return argumentsAgainst;
	}

	public Vector getQuestions()
	{
		return questions;
	}

	/**
	 * Associate a requirement to our argument. We store the requirement,
	 * set our category to the correct value, and set the other possible 
	 * argument types to null.
	 * @param newr - the requirement
	 */
	public void setRequirement(Requirement newr)
	{
		requirement = newr;
		category = ArgCategory.REQUIREMENT;
		assumption = null;
		alternative = null;
		claim = null;
	}

	public Requirement getRequirement()
	{
		return requirement;
	}

	/**
	 * Associate an assumption to our argument. We store the assumption,
	 * set our category to the correct value, and set the other possible 
	 * argument types to null.
	 * @param newa - the assumption
	 */
	public void setAssumption(Assumption newa)
	{
		assumption = newa;
		category = ArgCategory.ASSUMPTION;
		requirement = null;
		alternative = null;
		claim = null;
	}

	public Assumption getAssumption()
	{
		return assumption;
	}

	/**
	 * Associate an alternative to our argument. We store the alternative, set
	 * our category to the correct value, and set the other possible argument
	 * types to null
	 * @param newalt - the alternative
	 */
	public void setAlternative(Alternative newalt)
	{
//		System.out.println("Setting type to alternative");
		alternative = newalt;
		category = ArgCategory.ALTERNATIVE;
		requirement = null;
		assumption = null;
		claim = null;
	}

	public Alternative getAlternative()
	{
		return alternative;
	}

	/**
	 * Associate a claim to our argument. We store the claim, set our
	 * category to the correct value, and set the other possible argument
	 * types to null
	 * @param clm - the claim
	 */
	public void setClaim(Claim clm)
	{
//		System.out.println("Setting type to claim");
		claim = clm;
		category = ArgCategory.CLAIM;
		alternative = null;
		requirement = null;
		assumption = null;
	}

	public Claim getClaim()
	{
		return claim;
	}
	/*	
	 public void Argument addArgument()
	 {
	 Argument newArgument = new Argument();
	 arguments.addElement(newArgument);
	 return newArgument;
	 }
	 */	

	public void addArgumentFor(Argument newArg)
	{
		argumentsFor.addElement(newArg);
	}

	public void addArgumentAgainst(Argument newArg)
	{
		argumentsAgainst.addElement(newArg);
	}

	public void addQuestion(Question newQuest)
	{
		questions.addElement(newQuest);
	}
	public void delArgumentFor(Argument newArg)
	{
		argumentsFor.remove(newArg);
	}

	public void delArgumentAgainst(Argument newArg)
	{
		argumentsAgainst.remove(newArg);
	}

	public void delQuestion(Question newQuest)
	{
		questions.remove(newQuest);
	}

	/**
	 * Get all our arguments - for and against
	 * @return a vector of arguments
	 */
	public Vector<Argument> getAllArguments()
	{
		Vector<Argument> all = new Vector<Argument>();
		all.addAll(argumentsFor);
		all.addAll(argumentsAgainst);
		return all;
	}

	/**
	 * Gets a list of elements associated with this argument. In this case,
	 * the two valid types are sub-arguments and questions.
	 * @param type - the type of rationale element we are interested in
	 */
	public Vector getList(RationaleElementType type)
	{
		if (type.equals(RationaleElementType.ARGUMENT))
		{
			return getAllArguments();
		}
		else if (type.equals(RationaleElementType.QUESTION))
		{
			return questions;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Return true if this argument is in support of an alternative
	 * @return true if supporting
	 */
	public boolean isFor()
	{
		if ((type == ArgType.SATISFIES) || (type == ArgType.ADDRESSES) || (type == ArgType.SUPPORTS))
		{
			return true;
		}
		else
		{
			return false;
		}

	}

	/**
	 * Return true if this argument is against an alternative
	 * @return true if arguing against
	 */
	public boolean isAgainst()
	{
		if ((type == ArgType.VIOLATES) || (type == ArgType.DENIES))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Evaluate the numerical score for this argument. This is used when evaluating
	 * the alternative that it argues
	 * @return the score
	 */
	public double evaluate()
	{
		double result;
		double impVal = 0.0;

//		if (assumption != null)
		if (category == ArgCategory.ASSUMPTION)
		{
//			***				System.out.println("Getting the assumption importance");
			if (importance == Importance.DEFAULT)
			{
				impVal = assumption.getImportanceVal();
			}
			else if (assumption.getEnabled())
			{
				impVal = importance.getValue();
			}
			else
			{
				impVal = 0.0;
			}
//			***				System.out.println("value is " + new Double(impVal).toString());
		}
//		else if (alternative != null)
		else if (category == ArgCategory.ALTERNATIVE)
		{
			/*		if (importance == Importance.DEFAULT)
			 {
			 impVal = alternative.getImportanceVal();
			 }

			 else  */

			if (alternative.getStatus()== AlternativeStatus.ADOPTED)
			{
				if (this.type == ArgType.OPPOSES)
				{
					impVal = 1.0; //want a strong argument against
				}
				else
				{
					impVal = 0.0; //no effect
				}
			}
			else
			{
				if (this.type == ArgType.PRESUPPOSES)
				{
					impVal = -1.0; //this will treat it as an argument against
				}
				else
				{
					impVal = 0.0; //no effect

				}

			} 
		}
//		else if (claim != null)
		else if (category == ArgCategory.CLAIM)
		{
			if (importance == Importance.DEFAULT)
			{
				impVal = claim.getImportanceVal();
			}
			else if (claim.getEnabled())
			{
				impVal = importance.getValue();
			}
			else
			{
				impVal = 0.0;
			}
		}
//		else if (requirement != null)
		else if (category == ArgCategory.REQUIREMENT)
		{
//			***				System.out.println("req imp");
			if (importance != Importance.DEFAULT)
			{
				impVal = importance.getValue();
			}
			else 
			{
				if ((requirement.getStatus() != ReqStatus.REJECTED) &&
						(requirement.getStatus() != ReqStatus.RETRACTED) &&
						(requirement.getEnabled()))
				{
					impVal = requirement.getImportanceVal();
					//changed to now allow importance values for requirements
//					impVal = 1.0; //requirements are always essential!
				}
				else
				{
					impVal = 0.0; //ignore if requirement is not valid
				}
			}

		}



		result = amount * impVal;

		if ((type == ArgType.DENIES) || 
//				(type == ArgType.OPPOSEDBY) ||
				(type == ArgType.OPPOSES) ||
				(type == ArgType.VIOLATES))
		{
			result *= -1; //want to subtract
		}
		return result;
	}
	/*
	 public double evaluate()
	 {
	 double result;
	 double impVal = 0.0;

	 //the trick is getting the importance
	  if (importance == Importance.DEFAULT)
	  {
	  if (assumption != null)
	  {
	  impVal = assumption.getImportanceVal();
	  System.out.println("no default with assumptions?");
	  }
	  else if (alternative != null)
	  {
	  System.out.println("alt");
	  impVal = alternative.getImportanceVal();
	  }
	  else if (claim != null)
	  {
	  System.out.println("claim imp");
	  impVal = claim.getImportanceVal();
	  }
	  else if (requirement != null)
	  {
	  System.out.println("req imp");
	  impVal = 1.0; //requirements are always essential!
	  }

	  }
	  else
	  {
	  impVal = importance.getValue();
	  }

	  result = amount * impVal;

	  if ((type == ArgType.DENIES) || 
	  (type == ArgType.OPPOSEDBY) ||
	  (type == ArgType.OPPOSES) ||
	  (type == ArgType.VIOLATES))
	  {
	  result *= -1; //want to subtract
	  }
	  return result;
	  } */

	/**
	 * Save our argument to the database
	 * @param parent - the parent of the argument (what it argues)
	 * @param ptype - the parent's type
	 * @return the database ID for our argument
	 */
	public int toDatabase(int parent, RationaleElementType ptype)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		// Update Event To Inform Subscribers Of Changes
		// To Rationale
		RationaleUpdateEvent l_updateEvent;

		int ourid = 0;

		//find out if this requirement is already in the database
		Statement stmt = null; 
		ResultSet rs = null; 

//		***		System.out.println("Saving to the database");

		try {
			stmt = conn.createStatement(); 


			String updateD;
			if (designer == null)
				updateD = "null";
			else
				updateD = new Integer(designer.getID()).toString();


			if (inDatabase(parent,ptype))
			{
				String updateParent = "UPDATE arguments D " +
				"SET D.parent = " + new Integer(parent).toString() +
				", D.ptype = '" + ptype.toString() +
				"', D.name = '" + RationaleDBUtil.escape(name) +
				"', D.description = '" + RationaleDBUtil.escape(description) +
				"', D.type = '" + type.toString() +
				"', D.plausibility = '" + plausibility.toString() +
				"', D.importance = '" + importance.toString() +
				"', D.amount = " + new Integer(amount).toString() +
				" WHERE " +
				"D.id = " + this.id + " " ;
//				System.out.println(updateParent);
				stmt.execute(updateParent);

				l_updateEvent = m_eventGenerator.MakeUpdated();
			}
			else 
			{

				//now, we have determined that the requirement is new
				String parentRSt = new Integer(parent).toString();

				String newArgSt;

				//If this is a newly imported candidate, some of this information will
				//not exist. 
				if (type == ArgType.NONE)
				{
					newArgSt = "INSERT INTO Arguments " +
					"(name, description, type, plausibility, importance, amount, ptype, parent, designer) " +
					"VALUES ('" +
					RationaleDBUtil.escape(this.name) + "', '" +
					RationaleDBUtil.escape(this.description) + "', '" +
					this.type.toString() + "', '" +
					this.plausibility.toString() + "', '" +
					this.importance.toString() + "', " +
					new Integer(this.amount).toString() + ", '" +
					ptype.toString() + "', " +
					parentRSt + ", " +
					updateD + ")";					
				}
				else
				{
					newArgSt = "INSERT INTO Arguments " +
					"(name, description, type, plausibility, importance, amount, ptype, parent, designer) " +
					"VALUES ('" +
					RationaleDBUtil.escape(this.name) + "', '" +
					RationaleDBUtil.escape(this.description) + "', '" +
					this.type.toString() + "', '" +
					this.plausibility.toString() + "', '" +
					this.importance.toString() + "', " +
					new Integer(this.amount).toString() + ", '" +
					ptype.toString() + "', " +
					parentRSt + ", " +
					updateD + ")";

				}

//				System.out.println(newArgSt);
				stmt.execute(newArgSt); 

				l_updateEvent = m_eventGenerator.MakeCreated();
			}
			//in either case, we want to update any sub-requirements in case
			//they are new!
			//now, we need to get our ID
			String findQuery2 = "SELECT id FROM arguments where name='" +
			RationaleDBUtil.escape(this.name) + "' and parent = " + this.parent;
			rs = stmt.executeQuery(findQuery2); 
//			***			System.out.println(findQuery2);

			if (rs.next())
			{
				ourid = rs.getInt("id");
				rs.close();
			}
			else
			{
				ourid = -1;
			}

			this.id = ourid;

//			System.out.println("category = " + category.toString());
			//now, we need to either find, or create, our argument subject
			//now the hard part - what is our argument about?
//			if (assumption != null)
			if (category == ArgCategory.ASSUMPTION)
			{
				//create the assumption
				int asmid = assumption.toDatabase();
				String setReq = "UPDATE Arguments A " +
				"SET A.assumption = " + new Integer(asmid).toString() +
				", A.argtype = '" + ArgCategory.ASSUMPTION.toString() +
				"' WHERE A.id = " + new Integer(ourid).toString() +
				" ";   
//				System.out.println(setReq);
				stmt.execute(setReq);
			}
//			else if (alternative != null)
			else if (category == ArgCategory.ALTERNATIVE)
			{
				//get the ID for our alternative (presumably it exists)
				String findAlt = "SELECT id from Alternatives where name = '" +
				RationaleDBUtil.escape(alternative.getName()) + "'";
				rs = stmt.executeQuery(findAlt); 
//				***			 System.out.println(findAlt);
				int altid = 0;
				if (rs.next())
				{
					altid = rs.getInt("id");
					rs.close();
				}
				String setReq = "UPDATE Arguments A " +
				"SET A.alternative = " + new Integer(altid).toString() +
				", A.argtype = '" + ArgCategory.ALTERNATIVE.toString() +
				"' WHERE A.id = " + new Integer(ourid).toString() +
				" ";   
//				System.out.println(setReq);
				stmt.execute(setReq);

			}
			else if (category == ArgCategory.CLAIM)
//				else if (claim != null)
			{
				//create the claim - if it exists, this returns the id
				int clmid = claim.toDatabase();
				String setReq = "UPDATE Arguments A " +
				"SET A.claim = " + new Integer(clmid).toString() +
				", A.argtype = '" + ArgCategory.CLAIM.toString() +
				"' WHERE A.id = " + new Integer(ourid).toString() +
				" ";   
//				System.out.println(setReq);
				stmt.execute(setReq);


			}
			else if (category == ArgCategory.REQUIREMENT)
//				else if (requirement != null)
			{
				//get the ID for the requirement
				String findReq = "SELECT id from Requirements where name = '" +
				RationaleDBUtil.escape(requirement.getName()) + "'";
				rs = stmt.executeQuery(findReq); 
//				***				System.out.println(findReq);
				int reqid = 0;
				if (rs.next())
				{
					reqid = rs.getInt("id");
					rs.close();
				}
				String setReq = "UPDATE Arguments A " +
				"SET A.requirement = " + new Integer(reqid).toString() +
				", A.argtype = '" + ArgCategory.REQUIREMENT.toString() +
				"' WHERE A.id = " + new Integer(ourid).toString() +
				" ";   		
				stmt.execute(setReq);
//				System.out.println(setReq);
			}

			Enumeration args = getAllArguments().elements();
			while (args.hasMoreElements()) {
				Argument arg = (Argument) args.nextElement();
				arg.toDatabase(ourid, arg.getPtype());
			}

			Enumeration quests = questions.elements();
			while (quests.hasMoreElements()) {
				Question quest = (Question) quests.nextElement();
				quest.toDatabase(ourid, RationaleElementType.ARGUMENT);
			}

			m_eventGenerator.Broadcast(l_updateEvent);
		} 
		catch (RuntimeException ex)
		{
			System.out.println(ex.getMessage());
			System.out.println(ex.getStackTrace());
			throw(ex);
		}
		catch (SQLException ex) {
			RationaleDB.reportError(ex, "Argument.toDatabase", "SQL Error");
		}


		finally { 
			RationaleDB.releaseResources(stmt, rs);

		}

		return ourid;	

	}	

	public RationaleElement getParentElement()
	{
		return RationaleDB.getRationaleElement(this.parent, this.ptype);
	}

	/**
	 * Given the argument name, get it from the database
	 * @param name - the argument name
	 */
	public void fromDatabase(String name)
	{

		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		this.name = name;

		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			stmt = conn.createStatement();
			String findQuery; 
			findQuery = "SELECT *  FROM " +
			"arguments where name = '" +
			RationaleDBUtil.escape(name) + "'";
			rs = stmt.executeQuery(findQuery);
//			***			 System.out.println("Getting our argument contents");
//			System.out.println(findQuery);

			if (rs.next())
			{	 				
				id = rs.getInt("id");
				this.fromDatabase(id);
			}

		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex, "Argument.fromDatabase(String)", "Error reading argument");
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}

	}	

	/**
	 * Given the argument ID, get it from the database
	 * @param theID - the argument ID
	 */
	public void fromDatabase(int theID)
	{

		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		this.id = theID;

		Statement stmt = null; 
		ResultSet rs = null; 
		//need to figure out our ids... 
		int claimid = 0;
		int altid = 0;
		int assumpid = 0;
		int reqid = 0;

		//assume all components are null!
		alternative = null;
		claim = null;
		requirement = null;
		assumption = null;

		try {
			stmt = conn.createStatement();
			String findQuery; 
			findQuery = "SELECT *  FROM " +
			"arguments where id = " +
			theID;
			rs = stmt.executeQuery(findQuery);
//			System.out.println("Getting our argument contents");
//			System.out.println(findQuery);

			if (rs.next())
			{				
				name = RationaleDBUtil.decode(rs.getString("name"));
				description = RationaleDBUtil.decode(rs.getString("description"));
				ptype = RationaleElementType.fromString(rs.getString("ptype"));
				parent = rs.getInt("parent");
//				enabled = rs.getBoolean("enabled");
				String typeStr = rs.getString("type");
				if (typeStr != null)
				{
					type = ArgType.fromString(typeStr);
				}
				else
				{
					type = ArgType.NONE;
				}
				String catStr = rs.getString("argtype");
				if (catStr != null)
				{
					category = ArgCategory.fromString(catStr);
				}
				else
				{
					category = ArgCategory.NONE;
				}
				amount = rs.getInt("amount");
				importance = (Importance) Importance.fromString(rs.getString("importance"));
				plausibility = (Plausibility) Plausibility.fromString(rs.getString("plausibility"));
				try {
					int desID = rs.getInt("designer");

					if( rs.wasNull() )
						throw new SQLException();

					designer = new Designer();
					designer.fromDatabase(desID);
				} catch (SQLException ex)
				{
					designer = null; //nothing...
				}

				if (category == ArgCategory.CLAIM)
				{
					claimid = rs.getInt("claim");
				}
				else if (category == ArgCategory.ALTERNATIVE)
				{
					altid = rs.getInt("alternative");
				}
				else if (category == ArgCategory.ASSUMPTION)
				{
					assumpid = rs.getInt("assumption");
				}
				else if (category == ArgCategory.REQUIREMENT)
				{
					reqid = rs.getInt("requirement");
				}
				//need to read in the rest - recursive routines?
				//Now, we need to get the lists of arguments for and against
				//first For
				String findFor = "SELECT name FROM Arguments where " +
				"ptype = 'Argument' and " +
				"parent = " + 
				new Integer(this.id).toString() + " and " +
				"(type = 'Supports' or " +
				"type = 'Addresses' or " +
				"type = 'Satisfies' or " +
				"type = 'Pre-supposed-by')";
//				***				System.out.println(findFor);
				rs = stmt.executeQuery(findFor); 
				Vector<String> aFor = new Vector<String>();
				Vector<String> aAgainst = new Vector<String>();
				while (rs.next())
				{
					aFor.addElement(RationaleDBUtil.decode(rs.getString("name")));
				}
				rs.close();

				//Now, the arguments against
				String findAgainst = "SELECT name FROM Arguments where " +
				"ptype = 'Argument' and " +
				"parent = " + 
				new Integer(this.id).toString() + " and " +
				"(type = 'Denies' or " +
				"type = 'Violates' or " +
				"type = 'Opposed-by')";
				rs = stmt.executeQuery(findAgainst); 

				while (rs.next())
				{
					aAgainst.addElement(RationaleDBUtil.decode(rs.getString("name")));
				}
				rs.close();	

				//Now that we have the names, create the arguments
				Enumeration args = aFor.elements();
				while (args.hasMoreElements())
				{
					Argument arg = new Argument();
					arg.fromDatabase((String) args.nextElement());
					argumentsFor.add(arg);
				}
				args = aAgainst.elements();
				while (args.hasMoreElements())
				{
					Argument arg = new Argument();
					arg.fromDatabase((String) args.nextElement());
					argumentsAgainst.add(arg);
				}

				//now, figure out the rest

				if (category == ArgCategory.REQUIREMENT)
				{
					String reqName;
					String findQuery2 = "SELECT name from REQUIREMENTS where " +
					"id = " + new Integer(reqid).toString();
					rs = stmt.executeQuery(findQuery2);	
					if (rs.next())				
					{
						reqName = RationaleDBUtil.decode(rs.getString("name"));
						requirement = new Requirement();
						requirement.fromDatabase(reqName);
					}
				}

				//is the argument a claim?
				else if (category == ArgCategory.CLAIM)
				{
					String claimName;
					String findQuery2 = "SELECT name from CLAIMS where " +
					"id = " + new Integer(claimid).toString();
					rs = stmt.executeQuery(findQuery2);	
					if (rs.next())				
					{
						claimName = RationaleDBUtil.decode(rs.getString("name"));
						claim = new Claim();
						claim.fromDatabase(claimName);
					}
				}

				else if (category == ArgCategory.ALTERNATIVE)
				{
					String altName;
					String findQuery2 = "SELECT name from ALTERNATIVES where " +
					"id = " + new Integer(altid).toString();
					rs = stmt.executeQuery(findQuery2);
					if (rs.next())				
					{
						altName = RationaleDBUtil.decode(rs.getString("name"));
						alternative = new Alternative();
						alternative.fromDatabase(altName);
					}					
				}

				else if (category == ArgCategory.ASSUMPTION)
				{
					String assumpName;
					String findQuery2 = "SELECT name from ASSUMPTIONS where " +
					"id = " + new Integer(assumpid).toString();
					rs = stmt.executeQuery(findQuery2);	
					if (rs.next())				
					{
						assumpName = RationaleDBUtil.decode(rs.getString("name"));
						assumption = new Assumption();
						assumption.fromDatabase(assumpName);
					}
				}

			}

		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex, "Argument.fromDatabase(int)", "Query error");
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);

		}

	}	

	/**
	 * Delete our argument. This requires checking for any dependencies. If the
	 * argument is about a claim or an assumption we need to see who else refers
	 * to that claim or assumption. If no one else, we delete them too so we don't get
	 * orphaned rationale elements in the database that no one is using.
	 */
	public boolean delete()
	{
		RationaleDB db = RationaleDB.getHandle();

		m_eventGenerator.Destroyed();

		if (alternative != null)
		{
			//if we pre-suppose someone, then we can delete the argument
			//but if they use an argument that they are pre-supposed by us, that 
			//will be a problem... we probably shouldn't allow the pre-supposed-by
			//argument at all!
			//ditto for opposes...
		}
		else if (claim != null)
		{
			int argCount = db.countArgReferences(claim);
			if (argCount == 1)
			{
				db.deleteRationaleElement(claim);
			}
		}
		else if (assumption != null)
		{
			int argCount = db.countArgReferences(assumption);
			if (argCount == 1)
			{
				db.deleteRationaleElement(assumption);
			}
		}
		db.deleteRationaleElement(this);
		return false;		
	}

	/*	
	 public boolean display()
	 {
	 Frame lf = new Frame();
	 ArgumentGUI ar = new ArgumentGUI(lf, this, false);
	 ar.show();
	 return ar.getCanceled();
	 }
	 */	
	/**
	 * Bring up the editor to display the contents of our argument
	 * @param disp - points to the display
	 * @return true if the user cancels from the editor
	 */
	public boolean display(Display disp)
	{
		EditArgument ar = new EditArgument(disp, this, false);
		String msg = "Edited argument " + this.getName() + " " + ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		return ar.getCanceled(); //can I do this?

	}

	/**
	 * Create a new argument by starting up the editor
	 * @param disp - points to the display
	 * @param parent - the parent of our argument (who we are arguing about)
	 * @return true if the user cancels from the editor
	 */
	public boolean create(Display disp, RationaleElement parent)
	{
//		System.out.println("create argument");
		this.parent = parent.getID();
		this.ptype = parent.getElementType();
		//check the type of the parent
		if (parent instanceof Argument)
		{
			Argument parg = (Argument) parent;
			if (parg.getCategory() == ArgCategory.CLAIM)
			{
				this.setClaim(parg.getClaim());				
			}
			else if (parg.getCategory() == ArgCategory.ALTERNATIVE)
			{
				this.setAlternative(parg.getAlternative());				
			}
			else if (parg.getCategory() == ArgCategory.ASSUMPTION)
			{
				this.setAssumption(parg.getAssumption());
			}
			else if (parg.getCategory() == ArgCategory.REQUIREMENT)
			{
				this.setRequirement(parg.getRequirement());				
			}
			this.setType(ArgType.DENIES);
			this.setAmount(parg.getAmount());
			this.setImportance(parg.getImportance());
			this.setPlausibility(parg.getPlausibility());
			this.setCategory(parg.getCategory());
			parg.addArgumentAgainst(this);
		}
		EditArgument ar = new EditArgument(disp, this, true);
		return ar.getCanceled(); //can I do this?
	}

	/**
	 * Used to set the parent data of the rationale element without
	 * brigning up the edit alternative GUI (in conjunction with the
	 * new editor GUI).
	 * @param parent
	 */
	public void setParent(RationaleElement parent) {
		if (parent != null)
		{
			this.parent = parent.getID();
			this.ptype = parent.getElementType();
		}
	}

	/**
	 * Inference over the argument to update any status information that has 
	 * been changed.
	 * @return any new status elements
	 */
	public Vector<RationaleStatus> updateStatus()
	{
		ArgumentInferences inf = new ArgumentInferences();
		Vector<RationaleStatus> newStat = inf.updateArgument( this, false);
		return newStat;
	}

	/**
	 * Inference over the argument to update status when an argment is deleted.
	 * @return any new status elements
	 */
	public Vector<RationaleStatus> updateOnDelete()
	{
		ArgumentInferences inf = new ArgumentInferences();
		Vector<RationaleStatus> newStat = inf.updateArgument( this, true);
		return newStat;
	}

	public void setCategory(ArgCategory category) {
		this.category = category;
	}

	/**
	 * Read in our argument from XML
	 * @param argN - the XML element containing our argument
	 */
	public void fromXML(Element argN)
	{
		this.fromXML = true;
		RationaleDB db = RationaleDB.getHandle();

		//add idref ***from the XML***
		String idref = argN.getAttribute("id");

		//get our name
		name = argN.getAttribute("name");

		//get our type
		type = ArgType.fromString(argN.getAttribute("argtype"));

		//get our status
		plausibility = Plausibility.fromString(argN.getAttribute("plausibility"));

		//get importance
		importance = Importance.fromString(argN.getAttribute("importance"));

		//get our artifact
		amount = Integer.parseInt(argN.getAttribute("amount"));

		Node descN = argN.getFirstChild();
		//get the description
		//the text is actually the child of the element, odd...
		Node descT = descN.getFirstChild();
		if (descT instanceof Text) 
		{
			Text text = (Text) descT;
			String data = text.getData();
			setDescription(data);
		}

		//and last....
		db.addRef(idref, this);	//important to use the ref from the XML file!

		Element child = (Element) descN.getNextSibling();
		String nextName;

		while (child != null)
		{


			nextName = child.getNodeName();
			//here we check the type, then process
			if (nextName.compareTo("DR:claim") == 0)
			{
				claim = new Claim();
				claim.fromXML(child);
				db.addClaim(claim);
			}
			else if (nextName.compareTo("DR:requirement") == 0)
			{
				requirement = new Requirement();
				requirement.fromXML(child);
				db.addRequirement(requirement);

			}
			else if (nextName.compareTo("DR:assumption") == 0)
			{
				assumption = new Assumption();
				assumption.fromXML(child);
				db.addAssumption(assumption);

			}
			else if (nextName.compareTo("DR:argument") == 0)
			{
				Argument arg = new Argument();
				db.addArgument(arg);
				addArgument(arg);
				arg.fromXML(child);

			}
			else if (nextName.compareTo("DR:question") == 0)
			{
				Question quest = new Question();
				db.addQuestion(quest);
				addQuestion(quest);
				quest.fromXML(child);

			}
			else if (nextName.compareTo("DR:alternative") == 0)
			{
				alternative = new Alternative();
				alternative.fromXML(child);
				db.addAlternative(alternative);

			}
			else if (nextName.compareTo("reqref") == 0)
			{
				Node childRef = child.getFirstChild(); //now, get the text
				//decode the reference
				Text refText = (Text) childRef;
				String stRef = refText.getData();
				requirement = (Requirement)db.getRef(stRef);
			}
			else if (nextName.compareTo("clmref") == 0)
			{
				Node childRef = child.getFirstChild(); //now, get the text
				//decode the reference
				Text refText = (Text) childRef;
				String stRef = refText.getData();
				claim = (Claim)db.getRef(stRef);

			}
			else if (nextName.compareTo("assref") == 0)
			{
				Node childRef = child.getFirstChild(); //now, get the text
				//decode the reference
				Text refText = (Text) childRef;
				String stRef = refText.getData();
				assumption = (Assumption)db.getRef(stRef);

			}
			else if (nextName.compareTo("argref") == 0)
			{
				Node childRef = child.getFirstChild(); //now, get the text
				//decode the reference
				Text refText = (Text) childRef;
				String stRef = refText.getData();
				addArgument( (Argument)db.getRef(stRef));

			}
			else if (nextName.compareTo("questref") == 0)
			{
				Node childRef = child.getFirstChild(); //now, get the text
				//decode the reference
				Text refText = (Text) childRef;
				String stRef = refText.getData();
				addQuestion( (Question)db.getRef(stRef));

			}
			else if (nextName.compareTo("altref") == 0)
			{
				Node childRef = child.getFirstChild(); //now, get the text
				//decode the reference
				Text refText = (Text) childRef;
				String stRef = refText.getData();
				alternative = (Alternative)db.getRef(stRef);

			}
			else
			{
				System.out.println("unrecognized element under argument!");
			}
			child = (Element) child.getNextSibling();
		}
	}

	/**
	 * Check if our element is already in the database. The check is different
	 * if you are reading it in from XML because you can do a query on the name.
	 * Otherwise you can't because you run the risk of the user having changed the
	 * name.
	 * @param parentID the parent ID
	 * @param ptype the parent type
	 * @return true if in the database already
	 */
	private boolean inDatabase(int parentID, RationaleElementType ptype)
	{
		boolean found = false;
		String findQuery = "";

		if (fromXML)
		{
			RationaleDB db = RationaleDB.getHandle();
			Connection conn = db.getConnection();

			//find out if this argument is already in the database
			Statement stmt = null; 
			ResultSet rs = null; 

			try {
				stmt = conn.createStatement(); 
				findQuery = "SELECT id, parent FROM arguments where name='" +
				this.name + "'";
				System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery); 

				if (rs.next())
				{
					int ourid;
					ourid = rs.getInt("id");
					this.id = ourid;
					found = true;
				}
			}
			catch (SQLException ex) {
				// handle any errors 
				RationaleDB.reportError(ex, "Argument.inDatabase", findQuery); 
			}
			finally { 
				RationaleDB.releaseResources(stmt, rs);
			}
		}
		//If we aren't reading it from the XML, just check the ID
		//checking the name like above won't work because the user may 
		//have modified the name!
		else if (this.getID() >= 0)
		{
			found = true;
		}
		return found;
	}
	public void setParent(int parent) {
		this.parent = parent;
	}

	public void setPtype(RationaleElementType ptype) {
		this.ptype = ptype;
	}
}
