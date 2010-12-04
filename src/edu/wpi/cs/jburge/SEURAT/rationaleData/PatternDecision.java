package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.*;
import java.io.*;

import java.sql.Connection; 
import java.sql.SQLException; 
import java.sql.Statement;
import java.sql.ResultSet;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.*;

import SEURAT.events.*;
import edu.wpi.cs.jburge.SEURAT.editors.EditDecision;
import edu.wpi.cs.jburge.SEURAT.editors.SelectConstraint;
import edu.wpi.cs.jburge.SEURAT.inference.DecisionInferences;

public class PatternDecision extends RationaleElement{

	//private static final long serialVersionUID = -4162618972367711832L;

	private DecisionType type;
	private Phase devPhase;
	private int parent;
	private RationaleElementType ptype;
	private boolean alts;
	private Designer designer;
	private Vector<Constraint> constraints;	
	private DecisionStatus status;
	private Vector<Alternative> alternatives;    //the decision will have alternatives *or* sub-decisions
	private Vector<PatternDecision> subDecisions;
	private Vector<Question> questions; 
	private Vector<Pattern> candidatePatterns;
	private int parentPattern; //this is used only to set the parentPattern during creation of patterndecision.
	//We don't need this attribute in the DB, we can clean it up later on.
	
	
	
	/**
	 * This is used for XML import...
	 */
	private Vector<Integer> subPatternsID;

	private RationaleElementUpdateEventGenerator<PatternDecision> m_eventGenerator = 
		new RationaleElementUpdateEventGenerator<PatternDecision>(this);

	public PatternDecision()
	{
		super();
		status = DecisionStatus.UNRESOLVED;
		alts = true;
		alternatives = new Vector<Alternative>();
		subDecisions = new Vector<PatternDecision>();
		questions = new Vector<Question>();
		constraints = new Vector<Constraint>();
		candidatePatterns = new Vector<Pattern>();
		subPatternsID = new Vector<Integer>();
	} 
	
	public Iterator<Integer> iteratorSubPatterns(){
		return subPatternsID.iterator();
	}

	public RationaleElementType getElementType()
	{
		return RationaleElementType.PATTERNDECISION;
	}

	public DecisionType getType()
	{
		return type;
	}
	public void setType(DecisionType dtp)
	{
		type = dtp;
	}

	public DecisionStatus getStatus()
	{
		return status;
	}
	public void setStatus(DecisionStatus stat)
	{
		status = stat;
	}
	public Phase getPhase()
	{
		return devPhase;
	}
	public void setPhase(Phase ph)
	{
		devPhase = ph;
	}

	public int getParent()
	{
		return parent;
	}

	public RationaleElementType getPtype()
	{
		return ptype;
	}
	public boolean getAlts()
	{
		return alts;
	}
	public void setAlts(boolean al)
	{
		alts = al;
	}
	public Vector getAlternatives()
	{
		return alternatives;
	}
	public Vector getSubDecisions()
	{
		return subDecisions;
	}
	public Vector getQuestions()
	{
		return questions;
	}
	public void addAlternative(Alternative alt)
	{
		alternatives.addElement(alt);
	}
	public void addSubDecision(PatternDecision dec)
	{
		subDecisions.addElement(dec);
	}
	public void addQuestion(Question quest)
	{
		questions.addElement(quest);
	}
	public void delAlternative(Alternative alt)
	{
		alternatives.remove(alt);
	}
	public void delSubDecision(Decision dec)
	{
		subDecisions.remove(dec);
	}
	public void delQuestion(Question quest)
	{
		questions.remove(quest);
	}	

	public Vector getConstraints() {
		return constraints;
	}

	public void addConstraint(Constraint con)
	{
		constraints.addElement(con);
	}
	public void setConstraints(Vector<Constraint> constraints) {
		this.constraints = constraints;
	}

	public Designer getDesigner() {
		return designer;
	}

	public void setDesigner(Designer designer) {
		this.designer = designer;
	}

	public Vector<Pattern> getCandidatePatterns() {
		return candidatePatterns;
	}

	public void setCandidatePatterns(Vector<Pattern> candidatePatterns) {
		this.candidatePatterns = candidatePatterns;
	}

	public void addCandidatePattern(Pattern pattern){
		this.candidatePatterns.add(pattern);
	}

	/**
	 * Gets a list of elements associated with this decision. These might
	 * be decisions, alternatives, or questions)
	 */
	public Vector getList(RationaleElementType type)
	{
		if (type.equals(RationaleElementType.PATTERNDECISION))
		{
			return subDecisions;
		}
		else if (type.equals(RationaleElementType.ALTERNATIVE))
		{
			return alternatives;
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
	 * This method saves the particular pattern decision to the database
	 * @param name Name of the pattern
	 * @param patternsID ID of the pattern
	 * @return 1 if successful, 0 if not successful.
	 */
	public int savePatterns(String name, String patternsID){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		String updateQuery = "";

		updateQuery = "Update decisions set patterns = '" + patternsID + "' where name = '" + name + "'";
		try {
			stmt = conn.createStatement();
			stmt.execute(updateQuery);
			return 1;
		} catch (SQLException e) {
			e.printStackTrace();
		}		

		return 0;
	}
	
	/**
	 * This method exports the single pattern decision instance to the XML.
	 * It MUST be called by an XML exporter, as this will not have a complete header.
	 * @param ratDoc The ratDoc generated by the XML exporter
	 * @return the SAX representation of the object.
	 */
	public Element toXML(Document ratDoc){
		Element decisionE;
		RationaleDB db = RationaleDB.getHandle();
		
		//Now, add pattern to doc
		String entryID = db.getRef(this);
		if (entryID == null){
			entryID = db.addPatternDecisionRef(this);
		}
		
		decisionE = ratDoc.createElement("DR:patternDecision");
		decisionE.setAttribute("rid", entryID);
		decisionE.setAttribute("name", name);
		decisionE.setAttribute("type", type.toString());
		decisionE.setAttribute("phase", devPhase.toString());
		decisionE.setAttribute("status", status.toString());
		//decisionE.setAttribute("artifact", artifact);
		
		Element descE = ratDoc.createElement("description");
		Text descText = ratDoc.createTextNode(description);
		descE.appendChild(descText);
		decisionE.appendChild(descE);
		
		//Add child pattern references...
		Iterator<Pattern> cpi = candidatePatterns.iterator();
		while (cpi.hasNext()){
			Pattern cur = cpi.next();
			Element curE = ratDoc.createElement("refChildPattern");
			Text curText = ratDoc.createTextNode("p" + new Integer(cur.getID()).toString());
			curE.appendChild(curText);
			decisionE.appendChild(curE);
		}
		
		return decisionE;
	}

	/**
	 * Save our decision to the database.
	 * @param parent - the parent of the decision
	 * @param ptype - the parent's type
	 * @return the unique ID
	 */
	public int toDatabase(int parent, RationaleElementType ptype)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		String updateQuery = "";
		int ourid = 0;

		RationaleUpdateEvent l_updateEvent;

		//find out if this requirement is already in the database
		Statement stmt = null; 
		ResultSet rs = null; 

		String subsReq = "No";
		if (!alts)
			subsReq = "Yes";

		try {
			stmt = conn.createStatement(); 

			if (inDatabase(parent,ptype))
			{
				//set up Designer update string
				String updateD;
				if (designer == null)
					updateD = "D.designer = null";
				else
					updateD = "D.designer = " + designer.getID();

				updateQuery = "UPDATE patterndecisions D " +
				"SET D.parent = " + new Integer(parent).toString() +
				", D.ptype = '" + ptype.toString() + 
				"', D.phase = '" + devPhase.toString() +
				"', D.description = '" + RationaleDBUtil.escape(description) +
				"', D.type = '" + type.toString() +
				"', D.name = '" + RationaleDBUtil.escape(name) +
				"', D.status = '" + status.toString() + 
				"', D.subdecreq = '" + subsReq +
				"', " + updateD +
				" WHERE " +
				"D.id = " + this.id + " " ;
				stmt.execute(updateQuery);

				l_updateEvent = m_eventGenerator.MakeUpdated();
			}
			else 
			{
				int id = RationaleDB.findAvailableID("PatternDecisions");
				
				String parentSt;
				String parentTSt;

				//now, we have determined that the decision is new
				if ((this.parent < 0) || (ptype == null))
				{
					parentSt = "NULL";
					parentTSt = "None";
				}
				else
				{
					parentSt = new Integer(this.parent).toString();
					parentTSt = ptype.toString();
				}
				String updateD;
				if (designer == null)
					updateD = "null";
				else
					updateD = new Integer(designer.getID()).toString();

				updateQuery = "INSERT INTO PatternDecisions "+
				"(id, name, description, type, status, phase, subdecreq, parent, ptype, designer) " +
				"VALUES (" + id + ", '" +
				RationaleDBUtil.escape(this.name) + "', '" +
				RationaleDBUtil.escape(this.description) + "', '" +
				this.type.toString() + "', '" +
				this.status.toString() + "', '" +
				this.devPhase.toString() + "', '" +
				subsReq + "', " +
				parentSt + ", '" +
				parentTSt + "', " +
				updateD + ")";

				stmt.execute(updateQuery); 

				/*
				//Now, associate with pattern.
				//Get id first
				updateQuery = "SELECT id FROM patterndecisions where name='" +
				RationaleDBUtil.escape(this.name) + "'";
				rs = stmt.executeQuery(updateQuery); 

				if (rs.next())
				{
					ourid = rs.getInt("id");
					rs.close();
					//We have found out our ID and the insert is a success.
					//get association set up.
					updateQuery = "INSERT into pattern_decision values (" + parent +
					"ourid" + "DECISION)";
				}
				
				//And now, we have patternID and patterndecisionID. We can insert into relationship entry
				updateQuery = "INSERT INTO pattern_decision values (" + parentPattern + ", " +
				ourid + ", " + "'Decision')";
				stmt.execute(updateQuery);
				*/
				l_updateEvent = m_eventGenerator.MakeCreated();	
			}
			//in either case, we want to update any sub-requirements in case
			//they are new!
			//now, we need to get our ID
			updateQuery = "SELECT id FROM patterndecisions where name='" +
			RationaleDBUtil.escape(this.name) + "'";
			rs = stmt.executeQuery(updateQuery); 

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

			Enumeration alts = alternatives.elements();
			while (alts.hasMoreElements())
			{
				Alternative alt = (Alternative) alts.nextElement();
//				System.out.println("Saving alternative from decision");
				alt.toDatabase(ourid, RationaleElementType.DECISION);
			}

			Enumeration decs = subDecisions.elements();
			while (decs.hasMoreElements())
			{
				Decision dec = (Decision) decs.nextElement();
				dec.toDatabase(ourid, RationaleElementType.DECISION);
			}

			Enumeration quests = questions.elements();
			while (quests.hasMoreElements())
			{
				Question quest = (Question) quests.nextElement();
				quest.toDatabase(ourid, RationaleElementType.DECISION);
			}

			//finally, the history

			Enumeration hist = history.elements();
			while (hist.hasMoreElements())
			{
				History his = (History) hist.nextElement();
				his.toDatabase(ourid, RationaleElementType.DECISION);
			}


			//need to update our relationships with the constraints
			Enumeration conkids = this.constraints.elements();
			while (conkids.hasMoreElements())
			{
				Constraint kid = (Constraint) conkids.nextElement();
				//if the parent ID is not zero, then update the parent-child relationship

				updateQuery = "SELECT * from ConDecRelationships WHERE " +
				"constr = " + new Integer(kid.getID()).toString() +
				" and decision = " + new Integer(ourid).toString();

				rs = stmt.executeQuery(updateQuery);
				if (rs.next())
				{
					rs.close();
				}
				else
				{
					String insertRel = "INSERT INTO ConDecRelationships (constr, decision) " +
					"VALUES (" +
					new Integer(kid.getID()).toString() + ", " +
					new Integer(ourid).toString() + ")";
					System.out.println(insertRel);
					stmt.execute(insertRel);
				}
				kid.toDatabase(ourid);
			} //checking parent

			m_eventGenerator.Broadcast(l_updateEvent);	
		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex,"Error in PatternDecision.toDatabase", updateQuery);

		}

		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}

		return ourid;	

	}	

	/**
	 * Get our alternative from the database, given its unique id
	 * @param decID - the decision ID
	 */
	public void fromDatabase(int decID)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		this.id = decID;

		Statement stmt = null; 
		ResultSet rs = null; 
		String findQuery = ""; 
		try {
			stmt = conn.createStatement();

			findQuery = "SELECT name  FROM " +
			"patterndecisions where id = " +
			new Integer(decID).toString();
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);

			if (rs.next())
			{
				name = RationaleDBUtil.decode(rs.getString("name"));
				rs.close();
				this.fromDatabase(name);
			}

		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex,"Error in PatternDecision.fromDatabase(1)", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}

	}

	public RationaleElement getParentElement()
	{
		return RationaleDB.getRationaleElement(this.parent, this.ptype);
	}

	public void deleteCandidatePattern(String name){

	}

	/**
	 * Get the decision from the database, given its name
	 * @param name the decision name
	 */
	public void fromDatabase(String name)
	{
		String findQuery = "";
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		this.name = name;
		name = RationaleDBUtil.escape(name);

		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			stmt = conn.createStatement();
			findQuery = "SELECT *  FROM " +
			"patterndecisions where name = '" +
			name + "'";
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);

			if (rs.next())
			{
				id = rs.getInt("id");
				description = RationaleDBUtil.decode(rs.getString("description"));
				type = (DecisionType) DecisionType.fromString(rs.getString("type"));
				devPhase = (Phase) Phase.fromString(rs.getString("phase"));
				ptype = RationaleElementType.fromString(rs.getString("ptype"));
				parent = rs.getInt("parent");
//				artifact = rs.getString("artifact");
//				enabled = rs.getBoolean("enabled");
				status = (DecisionStatus) DecisionStatus.fromString(rs.getString("status"));
				String subdecs = rs.getString("subdecreq");
				if (subdecs.compareTo("Yes") == 0)
				{
					alts = false;
				}
				else
				{
					alts = true;
				}

				try {
					int desID = rs.getInt("designer");
					designer = new Designer();
					designer.fromDatabase(desID);
				} catch (SQLException ex)
				{
					designer = null; //nothing...
				}


			}
			rs.close();
			//need to read in the rest - recursive routines?
			subDecisions.removeAllElements();
			alternatives.removeAllElements();
			if (!alts)
			{				
				Vector<String> decNames = new Vector<String>();
				findQuery = "SELECT name from PATTERNDECISIONS where " +
				"ptype = '" + RationaleElementType.DECISION.toString() +
				"' and parent = " + new Integer(id).toString();
//				***					System.out.println(findQuery2);
				rs = stmt.executeQuery(findQuery);	
				while (rs.next())				
				{
					decNames.add(RationaleDBUtil.decode(rs.getString("name")));
				}
				Enumeration decs = decNames.elements();
				while (decs.hasMoreElements())
				{
					PatternDecision subDec = new PatternDecision();
					subDec.fromDatabase((String) decs.nextElement());
					subDecisions.add(subDec);
				}

			}
			else
			{
				Vector<String> altNames = new Vector<String>();
				findQuery = "SELECT name from ALTERNATIVES where " +
				"ptype = '" + RationaleElementType.DECISION.toString() +
				"' and parent = " + new Integer(id).toString();
//				***					System.out.println(findQuery2);
				rs = stmt.executeQuery(findQuery);	
				while (rs.next())				
				{
					altNames.add(RationaleDBUtil.decode(rs.getString("name")));
				}
				Enumeration alts = altNames.elements();
				while (alts.hasMoreElements())
				{
					Alternative alt = new Alternative();
					alt.fromDatabase((String) alts.nextElement());
					alternatives.add(alt);
				}

			}

			//need to do questions too 
			Vector<String> questNames = new Vector<String>();
			findQuery = "SELECT name from QUESTIONS where " +
			"ptype = '" + RationaleElementType.DECISION.toString() +
			"' and parent = " + new Integer(id).toString();
//			***				System.out.println(findQuery3);
			rs = stmt.executeQuery(findQuery);
			while (rs.next())
			{
				questNames.add(RationaleDBUtil.decode(rs.getString("name")));
			}
			Enumeration quests = questNames.elements();
			questions.removeAllElements();
			while (quests.hasMoreElements())
			{
				Question quest = new Question();
				quest.fromDatabase((String) quests.nextElement());
				questions.add(quest);
			}

			//no, not last - need history too
			findQuery = "SELECT * from HISTORY where ptype = 'Decision' and " +
			"parent = " + Integer.toString(id);
//			***			  System.out.println(findQuery5);
			rs = stmt.executeQuery(findQuery);
			history.removeAllElements();
			while (rs.next())
			{
				History nextH = new History();
				nextH.setStatus(rs.getString("status"));
				nextH.setReason(RationaleDBUtil.decode(rs.getString("reason")));
				nextH.dateStamp = rs.getTimestamp("date");
//				nextH.dateStamp = rs.getDate("date");
				history.add(nextH);
			}


			//now, get our constraints
			findQuery = "SELECT * from ConDecRelationships WHERE " +
			"decision = " + new Integer(id).toString();

			rs = stmt.executeQuery(findQuery);
			constraints.removeAllElements();
			if (rs != null)
			{
				while (rs.next())
				{
					int ontID = rs.getInt("constr");
					Constraint cont = new Constraint();
					cont.fromDatabase(ontID);
					this.addConstraint(cont);
				}
				rs.close();
			}

			//now, candidate patterns
			findQuery = "SELECT * from pattern_decision WHERE parentType= 'Decision' and decisionID=" + this.id;
			rs = stmt.executeQuery(findQuery);
			if(rs != null){
				while(rs.next()){
					int patternID = rs.getInt("patternID");
					Pattern p = new Pattern();
					p.fromDatabase(patternID);
					this.addCandidatePattern(p);
				}
			}


		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex,"Error in PatternDecision.fromDatabase", findQuery);

		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}

	}


	/**
	 * Deletes a decision from SEURAT and the database. This only works
	 * if there aren't any associated alternatives, questions, or subdecisions.
	 */
	public boolean delete()
	{
		//need to have a way to inform if delete did not happen
		//can't delete if there are dependencies...
		if ((this.alternatives.size() > 0) ||
				(this.questions.size() > 0) ||
				(this.subDecisions.size() > 0))
		{
			MessageDialog.openError(new Shell(),	"Delete Error",	"Can't delete when there are sub-elements.");
			return true;
		}
		m_eventGenerator.Destroyed();

		RationaleDB db = RationaleDB.getHandle();

		db.deleteRationaleElement(this);
		return false;

	}

	/**
	 * Associates a constraint with the decision. This is done by bringing
	 * up the constraint selection display.
	 * @param disp - the constraint selection display
	 * @return the constraint associated
	 */
	public RationaleElement associateElement(Display disp)
	{
		Constraint newCont = null;
		SelectConstraint ar = new SelectConstraint(disp, true);
		newCont = ar.getSelConstraint();
		if (newCont != null)
		{
			addConstraint(newCont);
			this.toDatabase(this.parent, this.ptype);
		}
		return newCont;
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

	public void setParentID(int id)
	{	
		this.parent = id;
	}

	/**
	 * Reads in a decision stored in XML.
	 * @param decN - the XML element.
	 */
	public void fromXML(Element decN) {
		this.fromXML = true;

		RationaleDB db = RationaleDB.getHandle();
		
		String rid = decN.getAttribute("rid");
		id = Integer.parseInt(rid.substring(2));
		
		name = decN.getAttribute("name");
		
		type = DecisionType.fromString(decN.getAttribute("type"));
		
		devPhase = Phase.fromString(decN.getAttribute("phase"));
		
		status = DecisionStatus.fromString(decN.getAttribute("status"));
		
		Node child = decN.getFirstChild();
		importHelper(child);
		
		Node nextNode = child.getNextSibling();
		while (nextNode != null){
			importHelper(nextNode);
			nextNode = nextNode.getNextSibling();
		}
		
		db.addPatternDecisionFromXML(this);
		
	}
	
	/**
	 * This is just a helper function for the fromXML method.
	 * @param child
	 */
	public void importHelper(Node child){
		if (child.getFirstChild() instanceof Text){
			Text text = (Text) child.getFirstChild();
			String data = text.getData();
			if (child.getNodeName().equals("description")){
				setDescription(data);
			}
			if (child.getNodeName().equals("refChildPattern")){
				int decID = Integer.parseInt(data.substring(1));
				subPatternsID.add(decID);
			}
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

			//find out if this decision is already in the database
			Statement stmt = null; 
			ResultSet rs = null; 

			try {
				stmt = conn.createStatement(); 
				findQuery = "SELECT id, parent FROM patterndecisions where name='" +
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
				RationaleDB.reportError(ex, "PatternDecision.inDatabase", findQuery); 
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
	
	public int getParentPattern(){
		return parentPattern;
	}
	
	public void setParentPattern(int parent){
		parentPattern = parent;
	}
	
}
