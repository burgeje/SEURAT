/*
 * Requirements class
 */
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

import edu.wpi.cs.jburge.SEURAT.editors.EditRequirement;
import edu.wpi.cs.jburge.SEURAT.inference.RequirementInferences;
import org.w3c.dom.*;

import SEURAT.events.*;

/**
 * Defines the structure of a requirement.
 * 
 * @author burgeje
 * 
 */
public class Requirement extends RationaleElement implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8527639694978991643L;
	// class variables

	// instance variables
	/**
	 * Type of requirement: functional or non-functional (FR or NFR)
	 */
	ReqType m_type;
	/**
	 * Identifies the parent element
	 */
	int m_parent;
	/**
	 * The type of the parent. This should be an element type but there were
	 * issues with conversions.
	 */
	String m_ptype;
	/**
	 * String giving the artifact. This could be a requirement number. Right now
	 * this would need to be manually entered but eventually we would want to
	 * tie this into a requirements tool such as Requisite Pro (or whatever it
	 * is being replaced with...)
	 */
	String m_artifact;
	/**
	 * The status of the requirement (addressed, violated, satisfied, etc.)
	 */
	ReqStatus m_status;

	/**
	 * What ontology entry is the requirement referring to?
	 */
	OntEntry ontology;

	/**
	 * How important is the requirement?
	 */
	Importance importance;

	/**
	 * List of the names of arguments for this requirement
	 */
	Vector<String> m_argumentsAgainst;
	/**
	 * List of the names of arguments against this alternative
	 */
	Vector<String> m_argumentsFor;
	/**
	 * List of all the arguments relating to this requirement
	 */
	Vector<Argument> m_arguments;

	/**
	 * List of sub-requirements
	 */
	Vector<Requirement> m_requirements;

	private RationaleElementUpdateEventGenerator<Requirement> m_eventGenerator = new RationaleElementUpdateEventGenerator<Requirement>(
			this);

	/**
	 * Constructor. Sets the initial status to undecided and creates ampty
	 * vectors for the arguments for and against, and other arguments
	 * (dependencies), and for requirements (sub-requirements?)
	 * 
	 */
	public Requirement() {
		super();
		m_status = ReqStatus.UNDECIDED;
		m_argumentsAgainst = new Vector<String>();
		m_argumentsFor = new Vector<String>();
		m_arguments = new Vector<Argument>();
		m_requirements = new Vector<Requirement>();
	}

	/*
	 * public Requirement(String name) { this.name = name; }
	 */

	public Requirement(String newDescription, String newArtifact,
			ReqType newType) {
		description = newDescription;
		m_artifact = newArtifact;
		m_type = newType;
	}

	public Element toXML(Document ratDoc) {
		Element reqE = ratDoc.createElement("DR:requirement");
		RationaleDB db = RationaleDB.getHandle();
		String reqID = db.getRef(id);
		if (reqID != null) {
			reqE = ratDoc.createElement("reqref");
			// set the reference contents
			Text text = ratDoc.createTextNode(reqID);
			reqE.appendChild(text);
		} else {
			reqID = db.addRef(id);
			reqE.setAttribute("id", reqID);
			reqE.setAttribute("name", name);
			reqE.setAttribute("reqtype", m_type.toString());
			reqE.setAttribute("artifact", m_artifact);
			reqE.setAttribute("status", m_status.toString()); // change when we
			// set up "real"
			// status

			// save our description
			Element descE = ratDoc.createElement("DR:description");
			// set the reference contents
			Text descText = ratDoc.createTextNode(description);
			descE.appendChild(descText);
			reqE.appendChild(descE);

			// now set up our arguments, if any
			Enumeration args = m_arguments.elements();
			while (args.hasMoreElements()) {
				Argument arg = (Argument) args.nextElement();
				reqE.appendChild(arg.toXML(ratDoc));
			}

			// now, any sub-requirements
			Enumeration reqs = m_requirements.elements();
			while (reqs.hasMoreElements()) {
				Requirement req = (Requirement) reqs.nextElement();
				reqE.appendChild(req.toXML(ratDoc));
			}

			// finally, the history

			Element ourHist = ratDoc.createElement("DR:history");
			Enumeration hist = history.elements();
			while (hist.hasMoreElements()) {
				History his = (History) hist.nextElement();
				ourHist.appendChild(his.toXML(ratDoc));
			}
			reqE.appendChild(ourHist);
		}
		return reqE;
	}

	public RationaleElementType getElementType() {
		return RationaleElementType.REQUIREMENT;
	}

	public void fromDatabase(int reqID) {
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		String findQuery = "";
		this.id = reqID;

		Statement stmt = null;
		ResultSet rs = null;
		// boolean error = false;
		try {
			stmt = conn.createStatement();

			findQuery = "SELECT name FROM " + "requirements where id = "
			+ new Integer(reqID).toString();
			// *** System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);

			if (rs.next()) {
				name = RationaleDBUtil.decode(rs.getString("name"));
				rs.close();
				this.fromDatabase(name);
			}

		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Requirement.fromDatabase(int)",
					findQuery);
		} finally {
			RationaleDB.releaseResources(stmt, rs);
		}

	}

	public int getParent() {
		return m_parent;
	}

	public String getPtype() {
		return m_ptype;
	}

	public Vector getArgumentsFor() {
		return m_argumentsFor;
	}

	public Vector getArgumentsAgainst() {
		return m_argumentsAgainst;
	}

	public Vector getArguments() {
		return m_arguments;
	}

	public ReqType getType() {
		return m_type;
	}

	public void setParent(RationaleElement parent) {
		if (parent != null) {
			this.m_parent = parent.getID();
			this.m_ptype = parent.getElementType().toString();
		}
	}

	public void setParent(int parent) {
		this.m_parent = parent;
	}

	public void setPtype(RationaleElementType ptype) {
		if (ptype != null) {
			this.m_ptype = ptype.toString();
		} else {
			this.m_ptype = null;
		}

	}

	public boolean hasParent() {
		if (m_parent > 0) {
			return true;
		} else {
			return false;
		}
	}

	public void setType(ReqType newtype) {
		m_type = newtype;
	}

	public ReqStatus getStatus() {
		return m_status;
	}

	public void setStatus(ReqStatus newstatus) {
		m_status = newstatus;
	}

	public String getArtifact() {
		return m_artifact;
	}

	public void setArtifact(String newArtifact) {
		m_artifact = newArtifact;
	}

	public void addArgumentFor(String arg) {
		m_argumentsFor.addElement(arg);
	}

	public void addArgumentAgainst(String arg) {
		m_argumentsAgainst.addElement(arg);
	}

	public void addRequirement(Requirement newReq) {
		m_requirements.addElement(newReq);
	}

	public void deleteArgument(Argument newArg) {
		m_arguments.remove(newArg);
	}

	public void addArgument(Argument newArg) {
		m_arguments.addElement(newArg);
	}

	public void fromDatabase(String rName) {
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		this.name = rName; // don't forget our name!
		rName = RationaleDBUtil.escape(rName);

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = conn.createStatement();
			String findQuery = "SELECT * FROM REQUIREMENTS where name='"
				+ rName + "'";
			// *** System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);

			int ontologyID = 0;

			if (rs.next()) {
				this.id = rs.getInt("id");
				this.description = RationaleDBUtil.decode(rs
						.getString("description"));
				this.m_type = (ReqType) ReqType
				.fromString(rs.getString("type"));
				this.m_status = (ReqStatus) ReqStatus.fromString(rs
						.getString("status"));
				this.m_artifact = rs.getString("artifact");
				this.m_parent = rs.getInt("parent");
				this.m_ptype = rs.getString("ptype");
				importance = (Importance) Importance.fromString(rs
						.getString("importance"));
				ontologyID = rs.getInt("ontology");
				this.enabled = (rs.getString("enabled").compareTo("True") == 0);

			}

			rs.close();

			// Find the ontology entry if it exists
			if (ontologyID > 0) {
				String findOntology = "SELECT name FROM OntEntries where "
					+ "id = " + new Integer(ontologyID).toString();
				// *** System.out.println(findOntology);
				rs = stmt.executeQuery(findOntology);

				if (rs.next()) {
					String ontName = RationaleDBUtil.decode(rs
							.getString("name"));
					ontology = new OntEntry();
					ontology.fromDatabase(ontName);

				}
				rs.close();
			} else {
				ontology = null;
			}

			// Now, we need to get the lists of arguments for and against
			// first For
			String findFor = "SELECT name FROM ARGUMENTS where "
				+ "ptype = 'Requirement' and " + "parent = "
				+ new Integer(this.id).toString() + " and "
				+ "(type = 'Supports' or " + "type = 'Addresses' or "
				+ "type = 'Satisfies' or " + "type = 'Pre-Supposed-by')";
			// *** System.out.println(findFor);
			rs = stmt.executeQuery(findFor);

			m_argumentsFor.clear();
			while (rs.next()) {
				m_argumentsFor.addElement(RationaleDBUtil.decode(rs
						.getString("name")));
			}
			rs.close();

			// Now, the arguments against
			String findAgainst = "SELECT name FROM Arguments where "
				+ "ptype = 'Requirement' and " + "parent = "
				+ new Integer(this.id).toString() + " and "
				+ "(type = 'Denies' or " + "type = 'Violates' or "
				+ "type = 'Opposed-by')";
			// *** System.out.println(findAgainst);
			rs = stmt.executeQuery(findAgainst);
			m_argumentsAgainst.clear();
			while (rs.next()) {
				m_argumentsAgainst.addElement(RationaleDBUtil.decode(rs
						.getString("name")));
			}
			rs.close();

			// find history
			// no, not last - need history too
			String findQuery5 = "SELECT * from HISTORY where ptype = 'Requirement' and "
				+ "parent = " + Integer.toString(id);
			// *** System.out.println(findQuery5);
			rs = stmt.executeQuery(findQuery5);
			while (rs.next()) {
				History nextH = new History();
				nextH.setStatus(rs.getString("status"));
				nextH.setReason(RationaleDBUtil.decode(rs.getString("reason")));
				nextH.dateStamp = rs.getTimestamp("date");
				// nextH.dateStamp = rs.getDate("date");
				history.add(nextH);
			}

		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Requirement.fromDatabase(String)",
			"SQL Error");
		}

		finally {
			RationaleDB.releaseResources(stmt, rs);
		}

	}

	public int toDatabase(int parentID, RationaleElementType ptype) {
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		// Update Event To Inform Subscribers Of Changes
		// To Rationale
		RationaleUpdateEvent l_updateEvent;

		int ourid = this.id;

		// find out if this requirement is already in the database
		Statement stmt = null;
		ResultSet rs = null;

		String enabledStr;
		if (enabled)
			enabledStr = "True";
		else
			enabledStr = "False";

		try {
			stmt = conn.createStatement();

			if (inDatabase(parentID, ptype)) {
				int ontid;
				if (ontology != null) {

					// now we need up update our ontology entry, and that's it!
					String findQuery3 = "SELECT id FROM OntEntries where name='"
						+ RationaleDBUtil.escape(this.ontology.getName())
						+ "'";
					rs = stmt.executeQuery(findQuery3);
					// *** System.out.println(findQuery3);

					if (rs.next()) {
						ontid = rs.getInt("id");
						rs.close();
					} else {
						ontid = 0;
					}
				} else {
					ontid = 0;
				}

				String updateParent = "UPDATE Requirements " + "SET name = '"
				+ RationaleDBUtil.escape(this.name) + "', "
				+"parent="
				+new Integer(parent).toString()
				+ ",description = '"
				+ RationaleDBUtil.escape(this.description) + "', "
				+ "type = '" + this.m_type.toString() + "', "
				+ "status = '" + this.m_status.toString() + "', "
				+ "enabled = '" + enabledStr + "', " + "importance = '"
				+ this.importance.toString() + "', " + "ontology = "
				+ new Integer(ontid).toString() + " WHERE " + "id = "
				+ this.id + " ";

				// System.out.println(updateParent);
				stmt.execute(updateParent);

				l_updateEvent = m_eventGenerator.MakeUpdated();
			}
			// return ourid;
			else {

				// now, we have determined that the requirement is new
				String parentSt;
				String parentTSt;
				System.out.println("parent ID is " + parentID);
				if ((parentID < 0) || (ptype == null)) {
					parentSt = "NULL";
					parentTSt = "None";
				} else {
					parentSt = new Integer(parentID).toString();
					parentTSt = ptype.toString();
				}
				String newReqSt = "INSERT INTO Requirements "
					+ "(name, description, type, status, ptype, parent, importance, enabled) "
					+ "VALUES ('" + RationaleDBUtil.escape(this.name)
					+ "', '" + RationaleDBUtil.escape(this.description)
					+ "', '" + this.m_type.toString() + "', '"
					+ this.m_status.toString() + "', '" + parentTSt + "', "
					+ parentSt + ", '" + this.importance.toString()
					+ "', '" + enabledStr + "')";
				// System.out.println(newReqSt);
				stmt.execute(newReqSt);

				l_updateEvent = m_eventGenerator.MakeCreated();
			}

			// now, we need to get our ID
			String findQuery2 = "SELECT id FROM requirements where name='"
				+ RationaleDBUtil.escape(this.name) + "'";
			rs = stmt.executeQuery(findQuery2);

			if (rs.next()) {
				ourid = rs.getInt("id");
				rs.close();
			} else {
				ourid = -1;
			}
			this.id = ourid;

			if (ontology != null) {
				// now we need up update our ontology entry, and that's it!
				String findQuery3 = "SELECT id FROM OntEntries where name='"
					+ RationaleDBUtil.escape(this.ontology.getName()) + "'";
				rs = stmt.executeQuery(findQuery3);
				// *** System.out.println(findQuery3);
				int ontid;
				if (rs.next()) {
					ontid = rs.getInt("id");
					rs.close();
				} else {
					ontid = 0;
				}
				String updateOnt = "UPDATE Requirements R "
					+ "SET R.ontology = " + new Integer(ontid).toString()
					+ " WHERE " + "R.id = " + ourid + " ";
				// *** System.out.println(updateOnt);
				stmt.execute(updateOnt);
			}
			/*
			 * This should be done elsewhere??? no, this should be fixed... //in
			 * either case, we want to update any sub-requirements in case
			 * //they are new!
			 * 
			 * 
			 * Enumeration args = m_arguments.elements(); while
			 * (args.hasMoreElements()) { Argument arg = (Argument)
			 * args.nextElement(); arg.toDatabase(ourid,
			 * RationaleElementType.REQUIREMENT); }
			 * 
			 * //now, any sub-requirements Enumeration reqs =
			 * m_requirements.elements(); while (reqs.hasMoreElements()) {
			 * System.out.println("adding Sub-requirements");
			 * System.out.println(ourid); Requirement req = (Requirement)
			 * reqs.nextElement(); req.toDatabase(ourid,
			 * RationaleElementType.REQUIREMENT); }
			 */
			// finally, the history
			Enumeration hist = history.elements();
			while (hist.hasMoreElements()) {
				// System.out.println("printing history");
				History his = (History) hist.nextElement();
				his.toDatabase(ourid, RationaleElementType.REQUIREMENT);
				// System.out.println("printed history");
			}

			m_eventGenerator.Broadcast(l_updateEvent);
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Requirement.toDatabase", "SQL Error");
		}

		finally {
			RationaleDB.releaseResources(stmt, rs);

		}

		return ourid;

	}

	//this method is for move Requirement in RationaleExplorer. and newEle is fake parameter, it just helps to find the entry.
	public int toDatabase(int parentID, RationaleElementType ptype, boolean newEle) {
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		// Update Event To Inform Subscribers Of Changes
		// To Rationale
		RationaleUpdateEvent l_updateEvent;

		int ourid = this.id;

		Statement stmt = null;
		ResultSet rs = null;

		String enabledStr;
		if (enabled)
			enabledStr = "True";
		else
			enabledStr = "False";

		try {
			stmt = conn.createStatement();

			// now, we have determined that the requirement is new
			String parentSt;
			String parentTSt;
			System.out.println("parent ID is " + parentID);
			if ((parentID < 0) || (ptype == null)) {
				parentSt = "NULL";
				parentTSt = "None";
			} else {
				parentSt = new Integer(parentID).toString();
				parentTSt = ptype.toString();
			}
			String updateReqSt = "update Requirements " + "set parent="+ parentSt + " ,ptype='" + ptype.toString()+ "' where id="+this.id+"";
			System.out.println(updateReqSt);
			stmt.execute(updateReqSt);

			l_updateEvent = m_eventGenerator.MakeCreated();

			// now, we need to get our ID
			String findQuery2 = "SELECT id FROM requirements where name='"
				+ this.name + "'";
			rs = stmt.executeQuery(findQuery2);

			if (rs.next()) {
				ourid = rs.getInt("id");
				rs.close();
			} else {
				ourid = -1;
			}
			this.id = ourid;

			if (ontology != null) {
				// now we need up update our ontology entry, and that's it!
				String findQuery3 = "SELECT id FROM OntEntries where name='"
					+ RationaleDBUtil.escape(this.ontology.getName()) + "'";
				rs = stmt.executeQuery(findQuery3);
				// *** System.out.println(findQuery3);
				int ontid;
				if (rs.next()) {
					ontid = rs.getInt("id");
					rs.close();
				} else {
					ontid = 0;
				}
				String updateOnt = "UPDATE Requirements R "
					+ "SET R.ontology = " + new Integer(ontid).toString()
					+ " WHERE " + "R.id = " + ourid + " ";
				// *** System.out.println(updateOnt);
				stmt.execute(updateOnt);
			}
			m_eventGenerator.Broadcast(l_updateEvent);
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Requirement.toDatabase", "SQL Error");
		}

		finally {
			RationaleDB.releaseResources(stmt, rs);

		}

		return ourid;
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

			// find out if this argument is already in the database
			Statement stmt = null;
			ResultSet rs = null;

			try {
				stmt = conn.createStatement();
				findQuery = "SELECT id, parent FROM requirements where name='"
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
				.reportError(ex, "Requirement.inDatabase", findQuery);
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

	public boolean display(Display disp) {
		EditRequirement ar = new EditRequirement(disp, this, false);
		String msg = "Edited requirement " + this.getName() + " "
		+ ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		// System.out.println("this after = " + this.getStatus().toString());
		// System.out.println(ar.getCanceled());
		return ar.getCanceled(); // can I do this?

	}

	public boolean create(Display disp, RationaleElement parent) {
		// System.out.println("create requirement");
		if (parent != null) {
			this.m_parent = parent.getID();
			this.m_ptype = parent.getElementType().toString();
		} else {
			this.m_parent = 0;
		}
		EditRequirement ar = new EditRequirement(disp, this, true);
		return ar.getCanceled(); // can I do this?
	}

	/*
	 * public boolean create(RationaleElement parent) {
	 * System.out.println("create requirement"); if (parent != null) {
	 * this.m_parent = parent.getID(); this.m_ptype =
	 * parent.getElementType().toString(); } else { this.m_parent = 0; } Frame
	 * lf = new Frame(); RequirementGUI ar = new RequirementGUI(lf, this, true);
	 * ar.show(); return ar.getCanceled(); }
	 */

	public boolean delete() {
		m_eventGenerator.Destroyed();

		// need to have a way to inform if delete did not happen
		// can't delete if there are dependencies...
		if ((this.m_argumentsAgainst.size() > 0)
				|| (this.m_argumentsFor.size() > 0)) {
			MessageDialog.openError(new Shell(), "Delete Error",
			"Can't delete when there are sub-elements.");
			return true;
		}
		RationaleDB db = RationaleDB.getHandle();

		int argCount = db.countArgReferences(this);
		if (argCount > 0) {
			MessageDialog.openError(new Shell(), "Delete Error",
			"Can't delete when there are referring arguments.");
		}

		// are there any dependencies on this item?
		if (db.dependentAlternatives(this)) {
			MessageDialog.openError(new Shell(), "Delete Error",
			"Can't delete when there are dependencies.");
			return true;
		}
		db.deleteRationaleElement(this);
		return false;

	}

	public Vector<RationaleStatus> updateStatus() {
		RequirementInferences inf = new RequirementInferences();
		Vector<RationaleStatus> newStat = inf.updateRequirement(this);
		return newStat;

	}

	public Vector<RationaleStatus> updateOnDelete() {
		// if no dependencies, no one to update
		// wrong - need to update our own status...
		// return new Vector();
		return updateStatus();
	}

	public void fromXML(Element reqNode) {
		this.fromXML = true;

		RationaleDB db = RationaleDB.getHandle();

		// add idref ***from the XML***
		String idref = reqNode.getAttribute("id");

		// get our name
		name = reqNode.getAttribute("name");

		// get our type
		m_type = ReqType.fromString(reqNode.getAttribute("reqtype"));

		// get our status
		m_status = ReqStatus.fromString(reqNode.getAttribute("status"));

		// get our artifact
		m_artifact = reqNode.getAttribute("artifact");

		// and last....
		db.addRef(idref, this); // important to use the ref from the XML file!

		Node descN = reqNode.getFirstChild();
		// get the description
		// the text is actually the child of the element, odd...
		Node descT = descN.getFirstChild();
		if (descT instanceof Text) {
			Text text = (Text) descT;
			String data = text.getData();
			setDescription(data);
		}

		// now, we loop until all children are done.
		Element nextChild = (Element) reqNode.getFirstChild();
		String nextName;

		while (nextChild != null) {
			nextName = nextChild.getNodeName();
			// here we check the type, then process
			if (nextName.compareTo("DR:argument") == 0) {
				Argument nextArg = new Argument();
				db.addArgument(nextArg);
				addArgument(nextArg);
				nextArg.fromXML(nextChild);
			} else if (nextName.compareTo("DR:decision") == 0) {
				System.out.println("decision under requirement?");
			} else if (nextName.compareTo("DR:history") == 0) {
				History hist = new History();
				// why is this commented out???
				// updateHistory(hist);
				historyFromXML(nextChild);
			} else if (nextName.compareTo("DR:requirement") == 0) {
				Requirement subR = new Requirement();
				addRequirement(subR);
				db.addRequirement(subR);
				subR.fromXML(nextChild);
			} else if (nextName.compareTo("reqref") == 0) {
				Node childRef = nextChild.getFirstChild(); // now, get the text
				// decode the reference
				Text refText = (Text) childRef;
				String stRef = refText.getData();
				addRequirement((Requirement) db.getRef(stRef));
			} else if (nextName.compareTo("decref") == 0) {
				Node childRef = nextChild.getFirstChild(); // now, get the text
				// decode the reference
				Text refText = (Text) childRef;
				String stRef = refText.getData();
				// addDecision((Decision)db.getRef(stRef));

			}

			nextChild = (Element) nextChild.getNextSibling();
		}

	}

	public OntEntry getOntology() {
		return ontology;
	}

	/**
	 * Sets the ontology entry associated with the requirement. If there already
	 * is an ontology entry, the reference count is decremented before
	 * re-setting the entry. After setting the entry, the new entry's reference
	 * count is incremented.
	 * 
	 * @param ont
	 *            - the new ontology entry
	 */
	public void setOntology(OntEntry ont) {
		if (ontology != null) {
			ontology.decRefs();
		}
		ontology = ont;
		if (ont != null) {
			ont.incRefs();
		}
	}

	public Importance getImportance() {
		return importance;
	}

	/**
	 * Get the importance value for the requirement. If the importance is set to
	 * default, the importance needs to come from the ontology entry.
	 * 
	 * @return the importance
	 */
	public double getImportanceVal() {
		if (!enabled)
			return 0.0;

		if (importance == Importance.DEFAULT) {
			// *** System.out.println("getting importance from ontology");
			return (ontology.getImportance()).getValue();
		} else {
			// *** System.out.println("getting importance from ourself");
			return importance.getValue();
		}
	}

	public void setImportance(Importance importance) {
		this.importance = importance;
	}

}
