package edu.wpi.cs.jburge.SEURAT.rationaleData;

import instrumentation.DataLog;

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
import edu.wpi.cs.jburge.SEURAT.editors.EditAlternative;
import edu.wpi.cs.jburge.SEURAT.inference.AlternativeInferences;

import SEURAT.events.RationaleElementUpdateEventGenerator;
import SEURAT.events.RationaleUpdateEvent;

/**
 * Defines an Alternative (alternatives are alternative methods of addressing a
 * design decision)
 * 
 * @author burgeje
 * 
 */
public class Alternative extends RationaleElement implements Serializable {
	// class variables

	/**
	 * Auto generated
	 */
	private static final long serialVersionUID = 4699219499868590420L;
	// instance variables
	/**
	 * The evaluations "score" for the alternative
	 */
	double evaluation;
	/**
	 * The status of the alternative (selected, at issue, etc.)
	 */
	AlternativeStatus status;
	/**
	 * The artifacts associated with the alternative
	 */
	Vector<String> artifacts;
	/**
	 * The ID (database ID) of the parent (decision)
	 */
	int parent;
	/**
	 * The designer who proposed the alternative
	 */
	Designer designer;
	/**
	 * The contingency level for the alternative. Typically a %. This is from
	 * the Orca modifications and is not usually used in SEURAT.
	 */
	Contingency contingency;
	/**
	 * The type of the parent
	 */
	RationaleElementType ptype;
	/**
	 * A vector containing all arguments about the alternative
	 */
	Vector<Argument> arguments;
	/**
	 * The arguments against the alternative
	 */
	Vector<Argument> argumentsAgainst;
	/**
	 * The arguments for the alternative (supporting)
	 */
	Vector<Argument> argumentsFor;
	/**
	 * Arguments that specify requirements for a specific alternative -
	 * dependency and conflict relationships. These only affect the evaluation
	 * if the alternative required is selected.
	 */
	Vector<Argument> relationships;
	/**
	 * Decisions that need to be made if this alternative is selected.
	 */
	Vector<Decision> subDecisions;
	/**
	 * Questions that have to be answered in order to evaluate the alternative
	 */
	Vector<Question> questions;

	/**
	 * If this alternative is generated from pattern, patterID is the id for that pattern. If not, patternID = -1;
	 */
	int patternID;

	private RationaleElementUpdateEventGenerator<Alternative> m_eventGenerator = new RationaleElementUpdateEventGenerator<Alternative>(
			this);

	/**
	 * Our constructor. Initially our status is "at issue"
	 * 
	 */
	public Alternative() {
		super();
		status = AlternativeStatus.ATISSUE;
		argumentsAgainst = new Vector<Argument>();
		argumentsFor = new Vector<Argument>();
		relationships = new Vector<Argument>();
		subDecisions = new Vector<Decision>();
		questions = new Vector<Question>();
		artifacts = new Vector<String>();
		arguments = new Vector<Argument>();
		patternID = -1;
	}

	public Element toXML(Document ratDoc) {
		Element altE;
		RationaleDB db = RationaleDB.getHandle();
		String altID = db.getRef(id);
		if (altID == null) {
			altID = db.addRef(id);
			altE = ratDoc.createElement("DR:alternative");
			altE.setAttribute("id", altID);
			altE.setAttribute("name", name);
			Float tempf = new Float(evaluation);
			altE.setAttribute("evaluation", tempf.toString());
			altE.setAttribute("status", status.toString());

			// save our description
			Element descE = ratDoc.createElement("DR:description");
			// set the reference contents
			Text descText = ratDoc.createTextNode(description);
			descE.appendChild(descText);
			altE.appendChild(descE);

			Enumeration args = arguments.elements();
			while (args.hasMoreElements()) {
				Argument arg = (Argument) args.nextElement();
				altE.appendChild(arg.toXML(ratDoc));
			}

			Enumeration quests = questions.elements();
			while (quests.hasMoreElements()) {
				Question quest = (Question) quests.nextElement();
				altE.appendChild(quest.toXML(ratDoc));
			}

			Enumeration decs = subDecisions.elements();
			while (decs.hasMoreElements()) {
				Decision dec = (Decision) decs.nextElement();
				altE.appendChild(dec.toXML(ratDoc));
			}

			// finally, the history

			Element ourHist = ratDoc.createElement("DR:history");
			Enumeration hist = history.elements();
			while (hist.hasMoreElements()) {
				History his = (History) hist.nextElement();
				ourHist.appendChild(his.toXML(ratDoc));
			}
			altE.appendChild(ourHist);
		} else {
			altE = ratDoc.createElement("altref");
			// set the reference contents
			Text text = ratDoc.createTextNode(altID);
			altE.appendChild(text);
		}
		return altE;
	}

	public RationaleElementType getElementType() {
		return RationaleElementType.ALTERNATIVE;
	}

	public double getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(double ev) {
		evaluation = ev;
	}

	public int getParent() {
		return parent;
	}

	public RationaleElementType getPtype() {
		return ptype;
	}

	public AlternativeStatus getStatus() {
		return status;
	}

	public void setStatus(AlternativeStatus stat) {
		status = stat;
	}

	public Contingency getContingency() {
		return contingency;
	}

	public void setContingency(Contingency contingency) {
		this.contingency = contingency;
	}

	public Designer getDesigner() {
		return designer;
	}

	public void setDesigner(Designer designer) {
		this.designer = designer;
	}

	public Vector getArgumentsFor() {
		return argumentsFor;
	}

	public Vector getArgumentsAgainst() {
		return argumentsAgainst;
	}

	public Vector getRelationships() {
		return relationships;
	}

	public Vector getSubDecisions() {
		return subDecisions;
	}

	public Vector getQuestions() {
		return questions;
	}

	public void addArgumentFor(Argument alt) {
		argumentsFor.addElement(alt);
	}

	public void addArgumentAgainst(Argument alt) {
		argumentsAgainst.addElement(alt);
	}

	public void addArgument(Argument alt) {
		if ((alt.type == ArgType.ADDRESSES) || (alt.type == ArgType.SUPPORTS)
				|| (alt.type == ArgType.SATISFIES)) {
			addArgumentFor(alt);
		} else if ((alt.type == ArgType.VIOLATES)
				|| (alt.type == ArgType.DENIES)) {
			addArgumentAgainst(alt);
		} else {
			relationships.addElement(alt);
		}
	}

	public void addSubDecision(Decision dec) {
		subDecisions.addElement(dec);
	}

	public void addQuestion(Question quest) {
		questions.addElement(quest);
	}

	public void delArgumentFor(Argument alt) {
		argumentsFor.remove(alt);
	}

	public void delArgumentAgainst(Argument alt) {
		argumentsAgainst.remove(alt);
	}

	public void delSubDecision(Decision dec) {
		subDecisions.remove(dec);
	}

	public void delQuestion(Question quest) {
		questions.remove(quest);
	}

	public int getPatternID() {
		return patternID;
	}

	public void setPatternID(int patternID) {
		this.patternID = patternID;
	}

	/**
	 * Generic function to get a list of sub-elements of a particular type -
	 * decisions, arguments, or questions.
	 * 
	 * @param type
	 *            - the type of sub-element to be returned.
	 */
	public Vector getList(RationaleElementType type) {
		if (type.equals(RationaleElementType.DECISION)) {
			return subDecisions;
		} else if (type.equals(RationaleElementType.ARGUMENT)) {
			return getAllArguments();
		} else if (type.equals(RationaleElementType.QUESTION)) {
			return questions;
		} else {
			return null;
		}
	}

	/**
	 * Get all our arguments
	 * 
	 * @return a list of arguments
	 */
	public Vector<Argument> getAllArguments() {
		Vector<Argument> args = new Vector<Argument>();
		args.addAll(argumentsFor);
		args.addAll(argumentsAgainst);
		args.addAll(relationships);
		return args;
	}

	public double getImportanceVal() {
		if (status == AlternativeStatus.ADOPTED) {
			return 1.0;
		} else {
			return 0.0;
		}
	}

	public Vector getArtifacts() {
		return artifacts;
	}

	public double evaluate() {
		return evaluate(true);
	}

	/**
	 * Based on the arguments for and against our alternative, calculate the
	 * argument score. This also involves going to the DB to see if there is
	 * anyone who refers to this alternative in an argument (pre-supposing or
	 * opposing)
	 * 
	 * @return the evaluation score. High is good.
	 */
	public double evaluate(boolean pSave) {
		double result = 0.0;
		Enumeration args = getAllArguments().elements();
		while (args.hasMoreElements()) {
			Argument arg = (Argument) args.nextElement();
			// System.out.println("relevant argument: " + arg.toString());
			result += arg.evaluate();
		}

		// should we take into account anyone pre-supposing or opposing us?
		RationaleDB db = RationaleDB.getHandle();
		Vector dependent = db.getDependentAlternatives(this, ArgType.OPPOSES);
		Iterator depI = dependent.iterator();
		while (depI.hasNext()) {
			Alternative depA = (Alternative) depI.next();
			if (depA.getStatus() == AlternativeStatus.ADOPTED) {
				result += -10; // assume amount is MAX
			}
		}
		dependent = db.getDependentAlternatives(this, ArgType.PRESUPPOSES);
		depI = dependent.iterator();
		while (depI.hasNext()) {
			Alternative depA = (Alternative) depI.next();
			if (depA.getStatus() == AlternativeStatus.ADOPTED) {
				result += 10; // assume amount is MAX
			}
		}

		// System.out.println("setting our evaluation = " + new
		// Double(result).toString());
		setEvaluation(result);

		if (pSave) {
			// /
			// TODO This was added to get the integrated editors
			// updating correctly, however would be better suited
			// in a seperate function which is then refactored
			// outward so other places in SEURAT use it.
			Connection conn = db.getConnection();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = conn.createStatement();

				// TODO Function call here is really hacky, see
				// the function inDatabase()
				if (inDatabase()) {
					String updateParent = "UPDATE alternatives "
						+ "SET evaluation = "
						+ new Double(evaluation).toString() + " WHERE "
						+ "id = " + this.id + " ";
					stmt.execute(updateParent);

					// Broadcast Notification That Score Has Been Recomputed
					m_eventGenerator.Updated();
				} else {
					// If The RationaleElement Wasn't In The Database There
					// Is No Reason To Attempt To Save The Evaluation Score
					// Or Broadcast A Notification About It
				}
			} catch (Exception e) {
				System.out.println("Exception while saving evaluation score to database");
			} finally {
				RationaleDB.releaseResources(stmt, rs);
			}
		}

		return result;
	}

	public Vector getOntEntriesFor() {
		Iterator argI = argumentsFor.iterator();
		return getOntEntryArgs(argI);

	}

	public Vector getOntEntriesAgainst() {
		Iterator argI = argumentsAgainst.iterator();
		return getOntEntryArgs(argI);

	}

	public Vector<OntEntry> getOntEntryArgs(Iterator argI) {
		Vector<OntEntry> ourOntEntries = new Vector<OntEntry>();
		while (argI.hasNext()) {
			Argument ourArg = (Argument) argI.next();
			if (ourArg.getClaim() != null) {
				ourOntEntries.add(ourArg.getClaim().getOntology());
			}
		}

		return ourOntEntries;

	}

	/**
	 * Gets alternatives that have a particular type of relationship with this
	 * alternative.
	 * 
	 * @param typeWanted
	 *            - the type of argument relationship
	 * @return a list of relevant alternatives
	 */
	public Vector<Alternative> getAlts(ArgType typeWanted) {
		Vector<Alternative> presup = new Vector<Alternative>();
		Iterator argI;
		if ((typeWanted == ArgType.PRESUPPOSES)
				|| (typeWanted == ArgType.OPPOSES)) {
			// System.out.println("Relationships: " + relationships.size());
			argI = relationships.iterator();
		}
		/*
		 * else if (typeWanted == ArgType.PRESUPPOSEDBY) { argI =
		 * argumentsFor.iterator(); }
		 */
		else {
			argI = argumentsAgainst.iterator();
		}
		while (argI.hasNext()) {
			Argument ourArg = (Argument) argI.next();
			Alternative preAlt = ourArg.getAlternative();
			// System.out.println("pre arg type = " +
			// ourArg.getType().toString());
			if (preAlt != null) {
				// System.out.println("pre arg type = " +
				// ourArg.getType().toString());
				if (ourArg.getType() == typeWanted) {
					presup.add(preAlt);
				}
			}
		}

		return presup;
	}

	/**
	 * Get all the arguments that refer to claims. This is used to check for
	 * tradeoff violations.
	 */
	public Vector<Claim> getClaims() {
		Vector<Claim> ourClaims = new Vector<Claim>();
		Iterator argI;

		argI = this.getAllArguments().iterator();
		while (argI.hasNext()) {
			Claim nextClaim = ((Argument) argI.next()).getClaim();
			if (nextClaim != null) {
				ourClaims.add(nextClaim);
			}
		}
		return ourClaims;
	}

	/**
	 * Save our alternative to the database.
	 * 
	 * @param parentID
	 *            - the parent of the alternative
	 * @param ptype
	 *            - the parent type
	 * @return the ID (from the DB) of our alternative
	 */
	public int toDatabase(int parentID, RationaleElementType ptype) {
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		// Update Event To Inform Subscribers Of Changes
		// To Rationale
		RationaleUpdateEvent l_updateEvent;

		int ourid = 0;

		// find out if this requirement is already in the database
		Statement stmt = null;
		ResultSet rs = null;

		String updateC;
		if (contingency == null)
			updateC = "null";
		else
			updateC = new Integer(contingency.getID()).toString();

		evaluation = this.evaluate(false); // might as well make sure we are up
		// to date!

		try {
			stmt = conn.createStatement();

			if (inDatabase(parentID, ptype)) {
				String updateParent = "UPDATE alternatives R "
					+ "SET R.parent = " + new Integer(parentID).toString()
					+ ", R.ptype = '" + ptype.toString() + "', R.name = '"
					+ RationaleDBUtil.escape(this.name)
					+ "', R.description = '"
					+ RationaleDBUtil.escape(this.description)
					+ "', R.status = '" + status.toString()
					+ "', R.evaluation = "
					+ new Double(evaluation).toString()
					+ ", R.designType = " + updateC
					+ ", R.patternID = " + new Integer(this.patternID).toString()
					+ " WHERE " + "R.id = "
					+ this.id + " ";
				// System.out.println(updateParent);
				stmt.execute(updateParent);

				l_updateEvent = m_eventGenerator.MakeUpdated();
				// return ourid;
			} else {

				// now, we have determined that the requirement is new
				String parentSt;
				if (this.parent < 0) {
					parentSt = "NULL";
				} else {
					parentSt = new Integer(this.parent).toString();
				}

				String updateD;

				if (designer == null)
					updateD = "null";
				else
					updateD = new Integer(designer.getID()).toString();

				String newAltSt = "INSERT INTO Alternatives "
					+ "(name, description, status, ptype, parent, evaluation, designer, designType, patternID) "
					+ "VALUES ('" + RationaleDBUtil.escape(this.name)
					+ "', '" + RationaleDBUtil.escape(this.description)
					+ "', '" + this.status.toString() + "', '"
					+ ptype.toString() + "', " + parentSt + ", "
					+ new Double(evaluation).toString() + ", " + updateD
					+ "," + updateC + ", " + new Integer(this.patternID).toString() + ")";
				// *** System.out.println(newAltSt);
				stmt.execute(newAltSt);

				l_updateEvent = m_eventGenerator.MakeCreated();
			}

			// in either case, we want to update any sub-requirements in case
			// they are new!
			// now, we need to get our ID
			String findQuery2 = "SELECT id FROM alternatives where name='"
				+ RationaleDBUtil.escape(this.name) + "'";
			rs = stmt.executeQuery(findQuery2);

			if (rs.next()) {
				ourid = rs.getInt("id");
				rs.close();
			} else {
				ourid = -1;
			}
			this.id = ourid;

			Enumeration args = getAllArguments().elements();
			while (args.hasMoreElements()) {
				Argument arg = (Argument) args.nextElement();
				// System.out.println("Saving arg from alternative");
				arg.toDatabase(ourid, RationaleElementType.ALTERNATIVE);
			}

			Enumeration quests = questions.elements();
			while (quests.hasMoreElements()) {
				Question quest = (Question) quests.nextElement();
				quest.toDatabase(ourid, RationaleElementType.ALTERNATIVE);
			}

			Enumeration decs = subDecisions.elements();
			while (decs.hasMoreElements()) {
				Decision dec = (Decision) decs.nextElement();
				dec.toDatabase(ourid, RationaleElementType.ALTERNATIVE);
			}

			// finally, the history

			Enumeration hist = history.elements();
			while (hist.hasMoreElements()) {
				History his = (History) hist.nextElement();
				his.toDatabase(ourid, RationaleElementType.ALTERNATIVE);
			}

			m_eventGenerator.Broadcast(l_updateEvent);
		} catch (SQLException ex) {
			// handle any errors
			RationaleDB.reportError(ex, "Alternative.toDatabase", "Bad query");
		}

		finally {
			RationaleDB.releaseResources(stmt, rs);
		}

		return ourid;

	}

	/**
	 * this method is for move Requirement in RationaleExplorer. and newEle is
	 * fake parameter, it just helps to find the entry.
	 */
	public int toDatabase(int parentID, RationaleElementType ptype,boolean newEle) {
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		// Update Event To Inform Subscribers Of Changes
		// To Rationale
		RationaleUpdateEvent l_updateEvent;

		int ourid = 0;

		// find out if this requirement is already in the database
		Statement stmt = null;
		ResultSet rs = null;

		String updateC;
		if (contingency == null)
			updateC = "null";
		else
			updateC = new Integer(contingency.getID()).toString();

		evaluation = this.evaluate(false); // might as well make sure we are up
		// to date!

		try {
			stmt = conn.createStatement();
			// now, we have determined that the requirement is new

			String updateAltSt ="update alternatives " + "set parent="+ parentID + " ,ptype='" + ptype.toString()+ "' where id="+this.id+"";
			System.out.println(updateAltSt);
			stmt.execute(updateAltSt);

			l_updateEvent = m_eventGenerator.MakeCreated();

			// in either case, we want to update any sub-requirements in case
			// they are new!
			// now, we need to get our ID
			String findQuery2 = "SELECT id FROM alternatives where name='"
				+ RationaleDBUtil.escape(this.name) + "'";
			rs = stmt.executeQuery(findQuery2);

			if (rs.next()) {
				ourid = rs.getInt("id");
				rs.close();
			} else {
				ourid = -1;
			}
			this.id = ourid;

			Enumeration args = getAllArguments().elements();
			while (args.hasMoreElements()) {
				Argument arg = (Argument) args.nextElement();
				// System.out.println("Saving arg from alternative");
				arg.toDatabase(ourid, RationaleElementType.ALTERNATIVE);
			}

			Enumeration quests = questions.elements();
			while (quests.hasMoreElements()) {
				Question quest = (Question) quests.nextElement();
				quest.toDatabase(ourid, RationaleElementType.ALTERNATIVE);
			}

			Enumeration decs = subDecisions.elements();
			while (decs.hasMoreElements()) {
				Decision dec = (Decision) decs.nextElement();
				dec.toDatabase(ourid, RationaleElementType.ALTERNATIVE);
			}

			// finally, the history

			Enumeration hist = history.elements();
			while (hist.hasMoreElements()) {
				History his = (History) hist.nextElement();
				his.toDatabase(ourid, RationaleElementType.ALTERNATIVE);
			}

			m_eventGenerator.Broadcast(l_updateEvent);
		} catch (SQLException ex) {
			// handle any errors
			RationaleDB.reportError(ex, "Alternative.toDatabase", "Bad query");
		}

		finally {
			RationaleDB.releaseResources(stmt, rs);
		}

		return ourid;

	}

	public RationaleElement getParentElement() {
		return RationaleDB.getRationaleElement(this.parent, this.ptype);
	}

	/**
	 * Read in our alternative from the database
	 * 
	 * @param id
	 *            - the database ID
	 */
	public void fromDatabase(int id) {

		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		this.id = id;

		Statement stmt = null;
		ResultSet rs = null;
		String findQuery = "";
		try {
			stmt = conn.createStatement();

			findQuery = "SELECT *  FROM " + "alternatives where id = "
			+ new Integer(id).toString();
			// *** System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);

			if (rs.next()) {
				name = RationaleDBUtil.decode(rs.getString("name"));
				rs.close();
				this.fromDatabase(name);
			}

		} catch (SQLException ex) {
			// handle any errors
			RationaleDB.reportError(ex, "Alternative.fromDatabase(int)",
					findQuery);
		} finally {
			RationaleDB.releaseResources(stmt, rs);
		}

	}

	/**
	 * Read in our alternative from the database.
	 * 
	 * @param name
	 *            - the alternative name
	 */
	public void fromDatabase(String name) {

		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		this.name = name;
		name = RationaleDBUtil.escape(name);

		Statement stmt = null;
		ResultSet rs = null;
		String findQuery = "";
		try {
			stmt = conn.createStatement();
			findQuery = "SELECT *  FROM " + "alternatives where name = '"
			+ name + "'";
			// *** System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);

			if (rs.next()) {
				id = rs.getInt("id");
				description = RationaleDBUtil.decode(rs
						.getString("description"));
				ptype = RationaleElementType.fromString(rs.getString("ptype"));
				parent = rs.getInt("parent");
				// enabled = rs.getBoolean("enabled");
				status = (AlternativeStatus) AlternativeStatus.fromString(rs
						.getString("status"));
				evaluation = rs.getFloat("evaluation");
				patternID = rs.getInt("patternID");

				try {
					int desID = rs.getInt("designer");

					if (rs.wasNull())
						throw new SQLException();

					designer = new Designer();
					designer.fromDatabase(desID);
				} catch (SQLException ex) {
					designer = null; // nothing...
				}

				try {
					int contID = rs.getInt("designType");

					if (rs.wasNull())
						throw new SQLException();

					contingency = new Contingency();
					contingency.fromDatabase(contID);
				} catch (SQLException ex) {
					contingency = null; // nothing...
				}

				// need to read in the rest - recursive routines?
				// Now, we need to get the lists of arguments for and against
				// first For
				String findFor = "SELECT id FROM "
					+ RationaleDBUtil.escapeTableName("arguments")
					+ " where ptype = 'Alternative' and " + "parent = "
					+ new Integer(this.id).toString() + " and "
					+ "(type = 'Supports' or " + "type = 'Addresses' or "
					+ "type = 'Satisfies' or "
					+ "type = 'Pre-supposed-by')";
				// *** System.out.println(findFor);
				rs = stmt.executeQuery(findFor);
				Vector<Integer> aFor = new Vector<Integer>();
				Vector<Integer> aAgainst = new Vector<Integer>();
				Vector<Integer> aRel = new Vector<Integer>();
				while (rs.next()) {
					aFor.addElement(new Integer(rs.getInt("id")));
				}
				rs.close();

				// Now, the arguments against
				String findAgainst = "SELECT id FROM "
					+ RationaleDBUtil.escapeTableName("arguments")
					+ " where ptype = 'Alternative' and " + "parent = "
					+ new Integer(this.id).toString() + " and "
					+ "(type = 'Denies' or " + "type = 'Violates' or "
					+ "type = 'Opposed-by')";
				// *** System.out.println(findAgainst);
				rs = stmt.executeQuery(findAgainst);

				while (rs.next()) {
					aAgainst.addElement(new Integer(rs.getInt("id")));
				}
				rs.close();

				// Now, any other useful relationships
				// Now, the arguments against
				String findRel = "SELECT id FROM "
					+ RationaleDBUtil.escapeTableName("arguments")
					+ " where ptype = 'Alternative' and " + "parent = "
					+ new Integer(this.id).toString() + " and "
					+ "(type = 'Opposed' or " + "type = 'Pre-supposes')";
				// *** System.out.println(findRel);
				rs = stmt.executeQuery(findRel);

				while (rs.next()) {
					aRel.addElement(new Integer(rs.getInt("id")));
				}
				rs.close();

				// ..Treat non-affiliated arguments as relationships (for now)
				String findUnafil = "SELECT id FROM Arguments where "
					+ "ptype = 'Alternative' and " + "parent = "
					+ new Integer(this.id).toString() + " and "
					+ "(type = 'NONE' )";
				// ** System.out.println(findUnafil);
				rs = stmt.executeQuery(findUnafil);

				while (rs.next()) {
					aRel.addElement(new Integer(rs.getInt("id")));
				}
				rs.close();

				// Cleanup before loading arguments
				argumentsFor.removeAllElements();
				// Now that we have the IDs, create the arguments
				Enumeration args = aFor.elements();
				while (args.hasMoreElements()) {
					Argument arg = new Argument();
					arg.fromDatabase(((Integer) args.nextElement()).intValue());
					if (arg.getParent() == this.id)
						argumentsFor.add(arg);
					else {
						System.out.println("argparent = " + arg.getParent()
								+ "not equal" + this.id);
					}
				}

				// Cleanup before loading arguments
				argumentsAgainst.removeAllElements();
				args = aAgainst.elements();
				while (args.hasMoreElements()) {
					Argument arg = new Argument();
					arg.fromDatabase(((Integer) args.nextElement()).intValue());
					if (arg.getParent() == this.id)
						argumentsAgainst.add(arg);
				}

				// Cleanup before loading arguments
				relationships.removeAllElements();
				args = aRel.elements();
				while (args.hasMoreElements()) {
					Argument arg = new Argument();
					arg.fromDatabase(((Integer) args.nextElement()).intValue());
					if (arg.getParent() == this.id)
						relationships.add(arg);
				}

				Vector<String> decNames = new Vector<String>();
				String findQuery2 = "SELECT name from DECISIONS where "
					+ "ptype = '"
					+ RationaleElementType.ALTERNATIVE.toString()
					+ "' and parent = " + new Integer(id).toString();
				// *** System.out.println(findQuery2);
				rs = stmt.executeQuery(findQuery2);
				while (rs.next()) {
					decNames.add(RationaleDBUtil.decode(rs.getString("name")));
				}
				Enumeration decs = decNames.elements();

				// Cleanup before loading subdecisions
				subDecisions.removeAllElements();
				while (decs.hasMoreElements()) {
					Decision subDec = new Decision();
					subDec.fromDatabase((String) decs.nextElement());
					subDecisions.add(subDec);
				}

				// need to do questions too
				Vector<String> questNames = new Vector<String>();
				String findQuery3 = "SELECT name from QUESTIONS where "
					+ "ptype = '"
					+ RationaleElementType.ALTERNATIVE.toString()
					+ "' and parent = " + new Integer(id).toString();
				// *** System.out.println(findQuery3);
				rs = stmt.executeQuery(findQuery3);
				while (rs.next()) {
					questNames
					.add(RationaleDBUtil.decode(rs.getString("name")));
				}
				Enumeration quests = questNames.elements();
				// Cleanup before loading questions
				questions.removeAllElements();
				while (quests.hasMoreElements()) {
					Question quest = new Question();
					quest.fromDatabase((String) quests.nextElement());
					questions.add(quest);
				}

				// Last, but not least, look for any associations
				String findQuery4 = "SELECT artName from ASSOCIATIONS where "
					+ "alternative = " + Integer.toString(id);
				rs = stmt.executeQuery(findQuery4);

				// Cleanup before loading artifacts
				artifacts.removeAllElements();
				while (rs.next()) {
					artifacts.add(rs.getString("artName"));
				}

				// no, not last - need history too
				String findQuery5 = "SELECT * from HISTORY where ptype = 'Alternative' and "
					+ "parent = " + Integer.toString(id);
				// *** System.out.println(findQuery5);
				rs = stmt.executeQuery(findQuery5);

				// Cleanup before loading history
				history.removeAllElements();

				while (rs.next()) {
					History nextH = new History();
					nextH.setStatus(rs.getString("status"));
					nextH.setReason(RationaleDBUtil.decode(rs
							.getString("reason")));
					nextH.dateStamp = rs.getTimestamp("date");
					// nextH.dateStamp = rs.getDate("date");
					history.add(nextH);
				}
			}
		} catch (SQLException ex) {
			// handle any errors
			RationaleDB.reportError(ex, "Alternative.fromDatabase(String)",
			"Error in a query");
		} finally {
			RationaleDB.releaseResources(stmt, rs);
		}

	}

	/*
	 * public boolean display() { Frame lf = new Frame(); AlternativeGUI ar =
	 * new AlternativeGUI(lf, this, false); ar.show(); return ar.getCanceled();
	 * }
	 */
	/**
	 * Used to edit our alternative - invokes the editor display
	 * 
	 * @param disp
	 *            - points back to the display
	 * @return - true if cancelled by the user
	 */
	public boolean display(Display disp) {
		EditAlternative ar = new EditAlternative(disp, this, false);
		// System.out.println("this after = " + this.getStatus().toString());
		// System.out.println(ar.getCanceled());
		String msg = "Edited alternative " + this.getName() + " "
		+ ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		return ar.getCanceled(); // can I do this?

	}

	/**
	 * Used to bring up an editor and create a new alternative
	 * 
	 * @param disp
	 *            - points back to the display
	 * @param parent
	 *            - the parent element
	 * @return true if cancelled by the user
	 */
	public boolean create(Display disp, RationaleElement parent) {
		// System.out.println("create alternative");
		if (parent != null) {
			this.parent = parent.getID();
			this.ptype = parent.getElementType();
		}
		EditAlternative ar = new EditAlternative(disp, this, true);
		return ar.getCanceled(); // can I do this?
	}

	/**
	 * Used to set the parent data of the rationale element without brigning up
	 * the edit alternative GUI (in conjunction with the new editor GUI).
	 * 
	 * @param parent
	 */
	public void setParent(RationaleElement parent) {
		if (parent != null) {
			this.parent = parent.getID();
			this.ptype = parent.getElementType();
		}
	}

	/**
	 * Delete our alternative from the database. This will only happen if there
	 * are no dependencies (no arguments, questions, or sub-decisions)
	 * 
	 * @return true if sucessful
	 */
	public boolean delete() {
		// need to have a way to inform if delete did not happen
		// can't delete if there are dependencies...
		if ((this.argumentsAgainst.size() > 0)
				|| (this.argumentsFor.size() > 0)
				|| (this.relationships.size() > 0)
				|| (this.questions.size() > 0)
				|| (this.subDecisions.size() > 0)) {
			MessageDialog.openError(new Shell(), "Delete Error",
			"Can't delete when there are sub-elements.");

			return true;
		}

		if (this.artifacts.size() > 0) {
			MessageDialog.openError(new Shell(), "Delete Error",
			"Can't delete when code is associated!");
			return true;
		}
		RationaleDB db = RationaleDB.getHandle();

		// are there any dependencies on this item?
		if (db.getDependentAlternatives(this).size() > 0) {
			MessageDialog.openError(new Shell(), "Delete Error",
			"Can't delete when there are depencencies.");
			return true;
		}

		m_eventGenerator.Destroyed();

		db.deleteRationaleElement(this);
		return false;

	}

	/**
	 * Update our status by inferenging over the rationale
	 * 
	 * @return new status values
	 */
	public Vector<RationaleStatus> updateStatus() {
		AlternativeInferences inf = new AlternativeInferences();
		Vector<RationaleStatus> newStat = inf.updateAlternative(this);
		return newStat;
	}

	/**
	 * Update status when the element is deleted.
	 * 
	 * @return new status values
	 */
	public Vector<RationaleStatus> updateOnDelete() {
		AlternativeInferences inf = new AlternativeInferences();
		Vector<RationaleStatus> newStat = inf.updateOnDelete(this);
		return newStat;
	}

	/**
	 * Is this alternative enabled? For alternatives, an alternative is enabled
	 * if it has been Adopted (Selected)
	 * 
	 * @return true if selected
	 */
	public boolean getEnabled() {
		if (status == AlternativeStatus.ADOPTED) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Read in an alternative from an XML representation
	 * 
	 * @param altN
	 *            - the alternative in XML
	 */
	public void fromXML(Element altN) {
		this.fromXML = true;
		RationaleDB db = RationaleDB.getHandle();

		// add idref ***from the XML***
		String idref = altN.getAttribute("id");

		// get our name
		name = altN.getAttribute("name");

		// get our status
		status = AlternativeStatus.fromString(altN.getAttribute("status"));

		// get our evaluation
		evaluation = Float.parseFloat(altN.getAttribute("evaluation"));

		Node descN = altN.getFirstChild();
		// get the description
		// the text is actually the child of the element, odd...
		Node descT = descN.getFirstChild();
		if (descT instanceof Text) {
			Text text = (Text) descT;
			String data = text.getData();
			setDescription(data);
		}

		// and last....
		db.addRef(idref, this); // important to use the ref from the XML file!

		Element child = (Element) descN.getNextSibling();

		while (child != null) {

			String nextName;

			nextName = child.getNodeName();
			// here we check the type, then process
			if (nextName.compareTo("DR:argument") == 0) {
				Argument arg = new Argument();
				db.addArgument(arg);
				addArgument(arg);
				arg.fromXML(child);
			} else if (nextName.compareTo("DR:question") == 0) {
				Question quest = new Question();
				db.addQuestion(quest);
				addQuestion(quest);
				quest.fromXML(child);
			} else if (nextName.compareTo("DR:decisionproblem") == 0) {
				Decision dec = new Decision();
				db.addDecision(dec);
				addSubDecision(dec);
				dec.fromXML(child);
			} else if (nextName.compareTo("DR:history") == 0) {
				historyFromXML(child);
			} else if (nextName.compareTo("argref") == 0) {
				Node childRef = child.getFirstChild(); // now, get the text
				// decode the reference
				Text refText = (Text) childRef;
				String stRef = refText.getData();
				addArgument((Argument) db.getRef(stRef));

			} else if (nextName.compareTo("decref") == 0) {
				Node childRef = child.getFirstChild(); // now, get the text
				// decode the reference
				Text refText = (Text) childRef;
				String stRef = refText.getData();
				addSubDecision((Decision) db.getRef(stRef));

			} else if (nextName.compareTo("questref") == 0) {
				Node childRef = child.getFirstChild(); // now, get the text
				// decode the reference
				Text refText = (Text) childRef;
				String stRef = refText.getData();
				addQuestion((Question) db.getRef(stRef));

			} else {
				System.out.println("unrecognized element under alternative!");
			}

			child = (Element) child.getNextSibling();
		}
	}

	/**
	 * Forwarding Function Which Makes The Assumption THat The Two Parameters
	 * Are Ignored.
	 * 
	 * @return
	 */
	private boolean inDatabase() {
		return inDatabase(0, null);
	}

	/**
	 * Check if our element is already in the database. The check is different
	 * if you are reading it in from XML because you can do a query on the name.
	 * Otherwise you can't because you run the risk of the user having changed
	 * the name.
	 * 
	 * @param parentID
	 *            the parent ID
	 * @param ptype
	 *            the parent type
	 * @return true if in the database already
	 */
	private boolean inDatabase(int parentID, RationaleElementType ptype) {
		boolean found = false;
		String findQuery = "";

		if (fromXML) {
			RationaleDB db = RationaleDB.getHandle();
			Connection conn = db.getConnection();

			// find out if this alternative is already in the database
			Statement stmt = null;
			ResultSet rs = null;

			try {
				stmt = conn.createStatement();
				findQuery = "SELECT id, parent FROM alternatives where name='"
					+ this.name + "'";
				System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery);

				if (rs.next()) {
					int ourid;
					ourid = rs.getInt("id");
					this.id = ourid;
					found = true;
				}
			} catch (SQLException ex) {
				// handle any errors
				RationaleDB
				.reportError(ex, "Alternative.inDatabase", findQuery);
			} finally {
				RationaleDB.releaseResources(stmt, rs);
			}
		}
		// If we aren't reading it from the XML, just check the ID
		// checking the name like above won't work because the user may
		// have modified the name!
		else if (this.getID() >= 0) {
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

	/**
	 * Method to add an artifact to this alternative and publish an event to the
	 * same effect. (Doesn't actually save the alternative to the DB, because
	 * artifacts are stored in the associations table.)
	 * 
	 * @param artName
	 *            - artifact name
	 */
	public void addArtifact(String artName) {
		artifacts.add(artName);
		RationaleUpdateEvent l_updateEvent = m_eventGenerator.MakeUpdated();
		l_updateEvent.setTag("artifacts");
		m_eventGenerator.Broadcast(l_updateEvent);
	}

	/**
	 * Method to publish an event that this alternative has been affected by
	 * artifact removal. (Doesn't actually change the alternative, as we have no
	 * way of knowing how many artifacts were removed and also
	 * artifacts/associations are stored separately in the DB.)
	 */
	public void removeArtifact() {
		RationaleUpdateEvent l_updateEvent = m_eventGenerator.MakeUpdated();
		l_updateEvent.setTag("artifacts");
		m_eventGenerator.Broadcast(l_updateEvent);
	}

	/**
	 * 
	 * @return
	 */
	public ArrayList<Decision> savePatternSubDecisions(){
		ArrayList<Decision> addedDecisions = new ArrayList<Decision>();

		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		Statement stmt = null; 
		ResultSet rs = null; 		
		System.out.println("Get here");
		Vector<PatternDecision> pds = new Vector<PatternDecision>();
		Pattern pattern = new Pattern();
		String thePatternName = this.getName();
		if(thePatternName.contains("~")){
			thePatternName = thePatternName.substring(0, thePatternName.indexOf("~"));
		}

		System.out.println("Get here!");
		pattern.fromDatabase(thePatternName);		
		pds = pattern.getSubDecisions();
		Enumeration enu = pds.elements();
		while(enu.hasMoreElements()){
			PatternDecision patternDecision = (PatternDecision)enu.nextElement();

			String theNewDecisionName = patternDecision.getName();
			while(RationaleDBUtil.isExist(theNewDecisionName, "Decisions")){
				theNewDecisionName = theNewDecisionName + "~";
			}

			String update = "INSERT INTO decisions (name, description, type, status, subdecreq, phase, ptype, parent) VALUES (" 
				+ "'" + theNewDecisionName + "',"
				+ "'" + patternDecision.getDescription() + "',"
				+ "'" + patternDecision.getType().toString() + "',"
				+ "'" + patternDecision.getStatus().toString() + "',"
				+ "'No',"
				+ "'" + patternDecision.getPhase().toString() + "',"
				+ "'Alternative',"
				+ this.id + ")";
			//					"(SELECT name, description, type, status, phase, subdecreq, ptype, parent, subsys, designer" +
			//					" FROM patterndecisions WHERE name = '" + name + "')";			
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(update);
				//				System.out.println("Get here!!!");
				//				String getLastID = "SELECT LAST_INSERT_ID()";
				//				rs = stmt.executeQuery(getLastID);
				//				Vector<Integer> decisionIDs = new Vector<Integer>();
				//				if(rs.next()){
				//					//String theID = rs.getString("LAST_INSERT_ID()");
				//					decisionIDs.add((rs.getInt("LAST_INSERT_ID()")));
				//				}
				//				for(int k=0;k < decisionIDs.size();k++){
				//					int decisionID = (Integer)decisionIDs.get(k);
				//					Decision decision = new Decision();
				//					decision.fromDatabase(decisionID);
				//					
				//					System.out.println(decisionID);
				//
				//					update = "UPDATE decisions SET ptype = 'Alternative', parent = " + this.id + " WHERE id=" + decisionID;
				//					System.out.println(update);
				//					stmt.executeUpdate(update);
				//					decision.fromDatabase(decisionID);
				//addedDecisions.add(decision);

				//generate alternatives for each of the subdecisions
				//					PatternDecision pd = new PatternDecision();
				//					pd.fromDatabase(name);
				System.out.println("Get here!!!!!");
				String findQuery = "SELECT patternID FROM pattern_decision WHERE decisionID = "
					+ patternDecision.getID()
					+ " and parentType = 'Decision'";
				System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery);
				Vector<String> names = new Vector<String>();
				while(rs.next()){
					Pattern candidate = new Pattern();
					candidate.fromDatabase(rs.getInt("patternID"));
					names.add(candidate.getName());
				}
				for(int j=0;j<names.size();j++){					
					String patternName = (String)names.get(j);
					Pattern thePattern = new Pattern();
					thePattern.fromDatabase(patternName);
					while(RationaleDBUtil.isExist(patternName, "Alternatives")){
						patternName = patternName + "~";
					}
					Alternative alt = new Alternative();
					alt.setName(patternName);
					alt.setPatternID(thePattern.getID());
					Decision newCreatedDecision = new Decision();
					newCreatedDecision.fromDatabase(theNewDecisionName);
					alt.generateFromPattern(newCreatedDecision, 1);

					//						String insertPattern = "INSERT INTO alternativepatterns (name, status, evaluation, ptype, parent) VALUES ('" + 
					//						(String)names.get(j) + "', 'At_Issue', 0, 'Decision', " + decisionID + ")";
					//						System.out.println(insertPattern);
					//						stmt.execute(insertPattern);
				}
				//}
			} catch (SQLException e) {
				RationaleDB.reportError(e, "Alternative.savePatternDecisions", "Bad update");
			}			

		}
		this.fromDatabase(this.name);

		return addedDecisions;
	}

	/**
	 * Given an alternative, find its parent's alternative.
	 * Decision
	 *  --> Alternative (return)
	 *    --> Decision
	 *       --> Alternative (alt)
	 * @param alt The alternative to start searching
	 * @return null if not found or cannot be retrieved, object of interest otherwise
	 */
	private Alternative getParentAlternative(Alternative alt){
		Alternative toReturn = null;
		Decision parentDecision = new Decision();
		parentDecision.fromDatabase(alt.getParent());
		if (parentDecision.getID() < 0 || 
				parentDecision.getPtype() != RationaleElementType.ALTERNATIVE) return null;
		toReturn = new Alternative();
		toReturn.fromDatabase(parentDecision.getParent());
		if (toReturn.getID() < 0) return null;
		return toReturn;
	}

	/**
	 * Find the worst tactic-pattern fit score.
	 * This might needs to be changed if tactic-tactic relationship is entered into the system.
	 * @param decision
	 * @param t
	 * @param recursiveParent
	 * @return
	 */
	private int findWorstTacticPatternScore(Decision decision, Tactic t, boolean recursiveParent){
		if (decision.getPtype() != RationaleElementType.ALTERNATIVE){
			//If this tactic is on the top of the decision tree, no compatibility issue can be found.
			return 0;
		}
		Alternative parentAlternative = (Alternative) decision.getParentElement();
		Pattern p = new Pattern();
		p.fromDatabase(parentAlternative.getPatternID());
		TacticPattern tp = new TacticPattern();
		tp.fromDatabase(TacticPattern.combineNames(p.getName(), t.getName()));
		if (tp.getID() < 0) return 0;
		//TODO: Tactic-tactic relationship....
		int currScore = tp.getOverallScore();

		while (recursiveParent){
			parentAlternative = getParentAlternative(parentAlternative);
			if (parentAlternative == null) break;
			p.fromDatabase(parentAlternative.getPatternID());
			tp.fromDatabase(TacticPattern.combineNames(p.getName(), t.getName()));
			if (tp.getID() < 0) break;
			//TODO: Tactic-tactic relationship
			currScore = tp.getOverallScore();
		}

		return currScore;

	}

	/**
	 * Generate sub-arguments for the given tactic ID under given decision
	 * @param decision
	 * @param tacticID
	 * @param recursiveParent true if wishes to scan for parent pattern
	 * @return
	 */
	public boolean generateFromTactic(Decision decision, int tacticID, boolean considerParentPattern, boolean recursiveParent){
		Tactic t = new Tactic();
		t.fromDatabase(tacticID);
		if (t.getID() < 0) return false;
		setDescription("This alternative is generated on the basis of tactic " + t.getName());
		setStatus(AlternativeStatus.ATISSUE);
		setParent(decision);

		toDatabase(decision.getID(), RationaleElementType.DECISION);
		fromDatabase(getName());
		RationaleDB db = RationaleDB.getHandle();
		Vector<Requirement> nfrs = db.getNFRs();
		boolean isCategoryMatched = false;
		boolean[] isNegQAMatched = new boolean[t.getBadEffects().size()];
		for (int i = 0; i < isNegQAMatched.length; i++){
			isNegQAMatched[i] = false;
		}

		for (int i = 0; i < nfrs.size(); i++){
			//Contribution matching on negative side of the tactic.
			Iterator<OntEntry> badI = t.getBadEffects().iterator();
			while (badI.hasNext()){
				OntEntry cur = badI.next();

				Iterator<OntEntry> curChildrenI = db.getOntologyElements(cur.getName()).iterator();
				while (curChildrenI.hasNext()){
					OntEntry curChild = curChildrenI.next();
					if (curChild.equals(nfrs.get(i).getOntology())){
						isNegQAMatched[i] = true;
						Argument arg = new Argument();
						String argName = "Requirement Violation - " + curChild.getName();
						while(RationaleDBUtil.isExist(argName, "Arguments")){
							argName = argName + "~";
						}
						arg.setName(argName);
						arg.setRequirement(nfrs.get(i));
						arg.setType(ArgType.VIOLATES);
						arg.setPlausibility(Plausibility.CERTAIN);
						arg.setAmount(10);
						arg.setImportance(Importance.ESSENTIAL);
						arg.setParent(this);
						arg.toDatabase(id, RationaleElementType.ALTERNATIVE);
					}
				}
			}

			if (nfrs.get(i).getOntology().equals(t.getCategory())){
				Argument arg = new Argument();
				String argName = "Requirement Satisfication - " + t.getCategory().getName();
				while(RationaleDBUtil.isExist(argName, "Arguments")){
					argName = argName + "~";
				}
				arg.setName(argName);
				arg.setRequirement(nfrs.get(i));
				arg.setType(ArgType.SATISFIES);
				arg.setPlausibility(Plausibility.CERTAIN);
				arg.setAmount(10);
				arg.setImportance(Importance.ESSENTIAL);
				arg.setParent(this);
				arg.toDatabase(id, RationaleElementType.ALTERNATIVE);
				isCategoryMatched = true;
			}
		}
		


		//Add arguments/claims
		if (!isCategoryMatched){
			Argument arg = new Argument();
			Claim c = new Claim();
			String claimName = "Positive Quality Attribute - " + t.getCategory().getName();
			while(RationaleDBUtil.isExist(claimName, "Claims")){
				claimName = claimName + "~";
			}
			c.setDirection(Direction.IS);
			arg.setType(ArgType.SUPPORTS);
			c.setName(claimName);
			c.setImportance(Importance.DEFAULT);
			c.setOntology(t.getCategory());
			c.setEnabled(true);
			c.toDatabase();

			arg.setClaim(c);
			while(RationaleDBUtil.isExist(claimName, "Arguments")){
				claimName = claimName + "~";
			}
			arg.setName(claimName);
			arg.setPlausibility(Plausibility.HIGH);
			arg.setDescription("");
			arg.setAmount(5);
			arg.setImportance(Importance.DEFAULT);
			arg.setParent(this);	
			arg.toDatabase(id, RationaleElementType.ALTERNATIVE);
		}
		for (int i = 0; i < isNegQAMatched.length; i++){
			Argument arg = new Argument();
			Claim c = new Claim();
			String claimName = "Negative Quality Attribute - " + t.getBadEffects().get(i).getName();
			while(RationaleDBUtil.isExist(claimName, "Claims")){
				claimName = claimName + "~";
			}
			c.setDirection(Direction.NOT);
			arg.setType(ArgType.DENIES);
			c.setName(claimName);
			c.setImportance(Importance.DEFAULT);
			c.setOntology(t.getBadEffects().get(i));
			c.setEnabled(true);
			c.toDatabase();

			arg.setClaim(c);
			while(RationaleDBUtil.isExist(claimName, "Arguments")){
				claimName = claimName + "~";
			}
			arg.setName(claimName);
			arg.setPlausibility(Plausibility.HIGH);
			arg.setDescription("");
			arg.setAmount(5);
			arg.setImportance(Importance.DEFAULT);
			arg.setParent(this);	
			arg.toDatabase(id, RationaleElementType.ALTERNATIVE);
		}
		//Add impact score.
		if (considerParentPattern){
			int score = findWorstTacticPatternScore(decision, t, recursiveParent);
			if (score > 0){
				Argument arg = new Argument();
				Claim c = new Claim();
				String claimName = "Tactic Compatible";
				c.fromDatabase(claimName);
				if (c.getID() < 0){
					c.setDirection(Direction.NOT);
					c.setName(claimName);
					c.setImportance(Importance.DEFAULT);
					OntEntry claimOnt = new OntEntry();
					claimOnt.fromDatabase(TacticPattern.CHANGEONTNAME);
					if (claimOnt.getID() < 0){
						//Create a new quality attribute
						OntEntry ontParent = new OntEntry();
						ontParent.fromDatabase(1);
						claimOnt.setName(TacticPattern.CHANGEONTNAME);
						claimOnt.setEnabled(true);
						claimOnt.setImportance(Importance.LOW);
						claimOnt.toDatabase(ontParent.getID());
					}
					c.setOntology(claimOnt);
					c.setEnabled(true);
					c.toDatabase();
				}
				arg.setClaim(c);
				arg.setType(ArgType.DENIES);

				while(RationaleDBUtil.isExist(claimName, "Arguments")){
					claimName = claimName + "~";
				}
				arg.setName(claimName);
				arg.setPlausibility(Plausibility.HIGH);
				arg.setDescription("");
				arg.setAmount(score);
				arg.setImportance(Importance.DEFAULT);
				arg.setParent(this);	
				arg.toDatabase(id, RationaleElementType.ALTERNATIVE);
			}
		}
		//Evaluate
		fromDatabase(name);
		evaluate(true);
		fromDatabase(name);
		return true;
	}
	
	/**
	 * This method provides backward compatability with Wang's original code.
	 * @param decision
	 * @param matchingMethod
	 * @return
	 */
	public boolean generateFromPattern(Decision decision, int matchingMethod){
		return generateFromPattern(decision, matchingMethod, new ArrayList<Pattern>());
	}

	/**
	 * This method creates requirement and arguments from the pattern that is selected under the decision.
	 * <br>
	 * This method is RECURSIVE. After generation of all arguments, it will generate the children of the pattern decision.
	 * After that, it will generate all sub-decisions of the current alternative.
	 * <br>
	 * Let D_1, D_2, ..., D_j be the decisions of the pattern. Let p_i_j be the j's pattern for decision D_i.
	 * BASE CASE 1: For each m: A := |{p_m_n: for each n, p_m_n NOT IN visited}| = 0
	 * Recursion: For each element p_m_n in A, it will call generateFromPattern for parent decision D_m.
	 * @param decision
	 * @param matchingMethod 0 = exact matching, 1 = contribution matching, 2 = not matching
	 * @return
	 */
	public boolean generateFromPattern(Decision decision, int matchingMethod, ArrayList<Pattern> visited){
		boolean isCompleted = false;
		if(getPatternID() != -1){
			//Find the pattern, and mark this pattern as "Visited".
			Pattern pattern = new Pattern();
			pattern.fromDatabase(this.getPatternID());
			visited.add(pattern);
			
			setDescription("This alternative is generated on the basis of pattern " + pattern.getName());
			setStatus(AlternativeStatus.ATISSUE);
			setParent(decision);

			toDatabase(decision.getID(), RationaleElementType.DECISION);
			fromDatabase(getName());
			if(pattern.getOntEntries()!=null && pattern.getOntEntries().size() != 0){
				RationaleDB db = RationaleDB.getHandle();
				Vector<Requirement> nfrs = db.getNFRs();

				for(int i=0; i<pattern.getOntEntries().size(); i++){
					OntEntry oe = pattern.getOntEntries().get(i);
					//whether satisfied by this pattern
					//matchingMethod == 2 is the "not" matching method.
					if(matchingMethod != 2){
						boolean isMatched = false;
						ArrayList<Requirement> matchedRequirements = new ArrayList<Requirement>();
						for (int k=0; k<nfrs.size(); k++){
							//matchingMethod == 0 is exact matching method
							if(matchingMethod == 0){
								if(nfrs.get(k).getOntology().getName().equals(oe.getName())){
									isMatched = true;
									matchedRequirements.add(nfrs.get(k));
								}
							}
							//matchingMethod == 1 is the contribution matching method
							else if(matchingMethod == 1){
								Vector<OntEntry> ontList = db.getOntologyElements(oe.getName());
								Enumeration<OntEntry> ontChildren = ontList.elements();
								while (ontChildren.hasMoreElements()){
									OntEntry ont = (OntEntry)ontChildren.nextElement();
									if(ont.getName().compareTo(nfrs.get(k).getOntology().getName()) == 0){
										isMatched = true;
										matchedRequirements.add(nfrs.get(k));
									}
								}
							}
						}
						if(isMatched && matchedRequirements.size() != 0){
							for(Requirement matchedR: matchedRequirements){
								Argument argu = new Argument();
								String arguName = "";
								if(pattern.getPosiOnts().contains(oe)){
									arguName = "Requirement Satisfaction - " + oe.getName();
									argu.setType(ArgType.SATISFIES);

								}else{
									arguName = "Requirement Violation - " + oe.getName();
									argu.setType(ArgType.VIOLATES);
								}
								while(RationaleDBUtil.isExist(arguName, "Arguments")){
									arguName = arguName + "~";
								}
								argu.setName(arguName);
								argu.setRequirement(matchedR);
								argu.setPlausibility(Plausibility.CERTAIN);
								argu.setDescription("");
								argu.setAmount(10);
								argu.setImportance(Importance.ESSENTIAL);
								argu.setParent(this);

								argu.toDatabase(this.getID(), RationaleElementType.ALTERNATIVE);
							}									
						}else{
							Argument argu = new Argument();
							Claim claim = new Claim();
							String claimName = "";
							if(pattern.getPosiOnts().contains(oe)){
								claimName = "Positive Quality Attribute - " + oe.getName();
								claim.setDirection(Direction.IS);
								argu.setType(ArgType.SUPPORTS);
							}else{
								claimName = "Negative Quality Attribute - " + oe.getName();
								claim.setDirection(Direction.NOT);
								argu.setType(ArgType.DENIES);
							}								
							while(RationaleDBUtil.isExist(claimName, "Claims")){
								claimName = claimName + "~";
							}
							claim.setName(claimName);

							claim.setImportance(Importance.DEFAULT);
							claim.setOntology(oe);
							claim.setEnabled(true);
							claim.toDatabase();

							argu.setClaim(claim);
							while(RationaleDBUtil.isExist(claimName, "Arguments")){
								claimName = claimName + "~";
							}
							argu.setName(claimName);
							argu.setPlausibility(Plausibility.HIGH);
							argu.setDescription("");
							argu.setAmount(5);
							argu.setImportance(Importance.DEFAULT);
							argu.setParent(this);

							argu.toDatabase(this.getID(), RationaleElementType.ALTERNATIVE);
						}
					}else{
						//no matching, arguments only
						Argument argu = new Argument();
						Claim claim = new Claim();
						String claimName = "";
						if(pattern.getPosiOnts().contains(oe)){
							claimName = "Positive Quality Attribute - " + oe.getName();
							claim.setDirection(Direction.IS);
							argu.setType(ArgType.SUPPORTS);
						}else{
							claimName = "Negative Quality Attribute - " + oe.getName();
							claim.setDirection(Direction.NOT);
							argu.setType(ArgType.DENIES);
						}								
						while(RationaleDBUtil.isExist(claimName, "Claims")){
							claimName = claimName + "~";
						}
						claim.setName(claimName);

						claim.setImportance(Importance.ESSENTIAL);
						claim.setOntology(oe);
						claim.setEnabled(true);
						claim.toDatabase();

						argu.setClaim(claim);

						while(RationaleDBUtil.isExist(claimName, "Arguments")){
							claimName = claimName + "~";
						}
						argu.setName(claimName);
						argu.setPlausibility(Plausibility.HIGH);
						argu.setDescription("");
						argu.setAmount(5);
						argu.setImportance(Importance.ESSENTIAL);
						argu.setParent(this);

						argu.toDatabase(this.getID(), RationaleElementType.ALTERNATIVE);
					}
				}
			}
			
			//Generate Sub-decisions...
			Iterator<PatternDecision> subdecI = pattern.getSubDecisions().iterator();
			while (subdecI.hasNext()){
				PatternDecision curDec = subdecI.next();
				Decision subDec = new Decision();
				subDec.setParent(this);
				subDec.setPtype(RationaleElementType.ALTERNATIVE);
				subDec.setName(curDec.getName());
				subDec.setDescription(curDec.getDescription());
				subDec.setDesigner(curDec.getDesigner());
				subDec.setPhase(curDec.getPhase());
				subDec.setEnabled(curDec.getEnabled());
				subDec.setStatus(curDec.getStatus());
				subDec.setType(curDec.getType());
				this.addSubDecision(subDec);
				subDec.toDatabase(getID(), RationaleElementType.ALTERNATIVE);
				
				Iterator<Pattern> subpatternI = curDec.getCandidatePatterns().iterator();
				while (subpatternI.hasNext()){
					Pattern curSubPattern = subpatternI.next();
					
					if (!visited.contains(curSubPattern)){
						//Does not contain the pattern. This means we will create a new alternative.
						Alternative subAlt = new Alternative();
						subAlt.setPatternID(curSubPattern.getID());
						subAlt.setName(curSubPattern.getName());
						subAlt.generateFromPattern(subDec, matchingMethod, visited);
					}
				}
			}
			

			isCompleted = true;
			this.fromDatabase(this.name);
			this.evaluate(true);
			this.fromDatabase(this.name);
		}

		return isCompleted;
	}
	
	public boolean isUMLAssociated(){
		if (!inDatabase()) return false;
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		try{
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM DIAGRAM_ALTERNATIVE WHERE alt_id = " + id;
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()){
				return true;
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public void broadcastUpdate(){
		RationaleUpdateEvent l_updateEvent = m_eventGenerator.MakeUpdated();
		m_eventGenerator.Broadcast(l_updateEvent);
	}
	
	/**
	 * Removes the association from the database.
	 */
	public void disAssociateUML(){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		try{
			Statement stmt = conn.createStatement();
			String dm = "DELETE FROM DIAGRAM_PATTERNELEMENTS WHERE "
					+ "alt_id = " + id;
			stmt.execute(dm);
			dm = "DELETE FROM DIAGRAM_ALTERNATIVE WHERE alt_id = " + id;
			stmt.execute(dm);
		} catch (SQLException e){
			e.printStackTrace();
		}
		
		broadcastUpdate();
	}
}
