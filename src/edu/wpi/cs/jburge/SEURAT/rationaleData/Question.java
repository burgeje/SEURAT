/*	This code belongs to the SEURAT project as written by Dr. Janet Burge
    Copyright (C) 2013  Janet Burge

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package edu.wpi.cs.jburge.SEURAT.rationaleData;

import instrumentation.DataLog;

import java.io.*;

import java.sql.Connection; 
import java.sql.SQLException; 
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;
import org.w3c.dom.*;

import SEURAT.events.RationaleElementUpdateEventGenerator;
import SEURAT.events.RationaleUpdateEvent;
import edu.wpi.cs.jburge.SEURAT.editors.EditQuestion;
import edu.wpi.cs.jburge.SEURAT.inference.QuestionInferences;

/**
 * Defines the Question rationale element. Questions are things that need to be
 * answered before a decision can be made. Questions can be attached to decisions,
 * alternatives, or arguments
 * @author burgeje
 *
 */
public class Question extends RationaleElement implements Serializable
{
	// class variables
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 650069251407164381L;
	// instance variables
	/**
	 * The type of element the question is about
	 */
	RationaleElementType ptype;
	/**
	 * The element the question is about
	 */
	int parent;
	/**
	 * The answer to the question
	 */
	String answer;
	/**
	 * The procedure for getting an answer. This might be who should be asked
	 * or it might be an actual procedure that needs to be executed.
	 */
	String procedure;
	/**
	 * Is the question answered or not?
	 */
	QuestionStatus status;		//don't know if maybe this should be an integer?

	private RationaleElementUpdateEventGenerator<Question> m_eventGenerator = 
		new RationaleElementUpdateEventGenerator<Question>(this);
	
	/**
	 * Our constructor
	 *
	 */
	public Question()
	{
		super();
		status = QuestionStatus.UNANSWERED;
		answer = new String("");
		procedure = new String("");
	} 
	public Element toXML(Document ratDoc)
	{
		Element questE;
		RationaleDB db = RationaleDB.getHandle();
		String questID = db.getRef(id);
		if (questID != null)
		{		
			//this should never be the case but just in case...
			questE = ratDoc.createElement("questref");
			//set the reference contents
			Text text = ratDoc.createTextNode(questID);
			questE.appendChild(text);
		}
		else 
		{
			questE  = ratDoc.createElement("DR:question");
			questID = db.addRef(id);
			questE.setAttribute("id", questID);
			questE.setAttribute("name", name);
			questE.setAttribute("status", status.toString());
			System.out.println(name);
			
			//save our description
			Element descE = ratDoc.createElement("DR:description");
			//set the reference contents
			Text descText = ratDoc.createTextNode(description);
			descE.appendChild(descText);
			questE.appendChild(descE);
						
			Element procE = ratDoc.createElement("DR:procedure");
			Text pt = ratDoc.createTextNode(procedure);
			procE.appendChild(pt);
			questE.appendChild(procE);
			Element ansE = ratDoc.createElement("DR:answer");
			Text at = ratDoc.createTextNode(answer);
			ansE.appendChild(at);
			questE.appendChild(ansE);	

			
			//finally, the history
			
			Element ourHist = ratDoc.createElement("DR:history");
			Enumeration hist = history.elements();
			while (hist.hasMoreElements())
			{
				History his = (History) hist.nextElement();
				ourHist.appendChild(his.toXML(ratDoc));
			}
			questE.appendChild(ourHist);
		}	
		return questE;
	}
	public RationaleElementType getElementType()
	{
		return RationaleElementType.QUESTION;
	}
	
	public String getAnswer()
	{
		return answer;
	}
	public void setAnswer(String answ)
	{
		answer = answ;
	}
	
	public String getProcedure()
	{
		return procedure;
	}
	public void setProcedure(String pro)
	{
		procedure = pro;
	}
	
	public QuestionStatus getStatus()
	{
		return status;
	}
	public void setStatus(QuestionStatus stat)
	{
		status = stat;
	}
	
	public void setParent(int parent)
	{
		this.parent = parent;
	}
	
	public int getParent()
	{
		return this.parent;
	}
	
	public void setPtype (RationaleElementType ptype)
	{
		this.ptype = ptype;
	}
	
	public RationaleElementType getPtype()
	{
		return this.ptype;
	}
	
	/**
	 * Save our question to the database
	 * @param parent - the element we are asking about
	 * @param ptype - the element's type
	 * @return the ID of our question
	 */
	public int toDatabase(int parent, RationaleElementType ptype)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		int ourid = 0;

		// Update Event To Inform Subscribers Of Changes
		// To Rationale
		RationaleUpdateEvent l_updateEvent;
		
		//find out if this question is already in the database
		Statement stmt = null; 
		ResultSet rs = null; 
		
//		***		System.out.println("Saving to the database");
		
		try {
			stmt = conn.createStatement(); 
			
			if (inDatabase(parent,ptype))
			{
				String updateParent = "UPDATE questions " +
				"SET name = '" +
				RationaleDBUtil.escape(this.name) + "', " +
				"description = '" +
				RationaleDBUtil.escape(this.description) + "', " +
				"answer = '" +
				RationaleDBUtil.escape(this.answer) + "', " +
				"proc = '" +
				RationaleDBUtil.escape(this.procedure) + "', " +
				"status = '" +
				status.toString() + "', " +
				"parent = " + new Integer(parent).toString() + ", " +
				"ptype = '" + ptype.toString() +
				"' WHERE " +
				"id = " + this.id + " " ;
//				System.out.println(updateParent);
				stmt.execute(updateParent);
				
				l_updateEvent = m_eventGenerator.MakeUpdated();
			}
			
			else 
			{
				
				//now, we have determined that the question is new
				String parentRSt = new Integer(parent).toString();		
				String newQuestSt = "INSERT INTO Questions "+
				"(name, description, status, proc, answer, ptype, parent) " +
				"VALUES ('" +
				RationaleDBUtil.escape(this.name) + "', '" +
				RationaleDBUtil.escape(this.description) + "', '" +
				this.status.toString() + "', '" +
				RationaleDBUtil.escape(this.procedure) + "', '" +
				RationaleDBUtil.escape(this.answer) + "', '" +
				ptype.toString() + "', " +
				parentRSt + ")";
				
//				***			   System.out.println(newQuestSt);
				stmt.execute(newQuestSt); 
				
				l_updateEvent = m_eventGenerator.MakeCreated();
			}
			//in either case, we want to update any sub-requirements in case
			//they are new!
			//now, we need to get our ID
			String findQuery2 = "SELECT id FROM questions where name='" +
			RationaleDBUtil.escape(this.name) + "'";
			rs = stmt.executeQuery(findQuery2); 
			
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
			
			//finally, the history
			
			Enumeration hist = history.elements();
			while (hist.hasMoreElements())
			{
				History his = (History) hist.nextElement();
				his.toDatabase(ourid, RationaleElementType.QUESTION);
			}
			
			m_eventGenerator.Broadcast(l_updateEvent);
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Question.toDatabase(int, type)", "SQL Error");
		}
		
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
		return ourid;	
		
	}	
	
	public void fromDatabase(int id){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		try {
			Statement stmt = conn.createStatement();
			String query = "SELECT name from Questions where id = " + id;
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()){
				this.id = id;
				fromDatabase(RationaleDBUtil.decode(rs.getString("name")));
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		
	}
	/**
	 * Read in our question from the database
	 * @param name - the question name (the question itself)
	 */	
	public void fromDatabase(String name)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		this.name = name;
		name = RationaleDBUtil.escape(name);
		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			stmt = conn.createStatement();
			String findQuery; 
			findQuery = "SELECT *  FROM " +
			"Questions where name = '" +
			name + "'";
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			
			if (rs.next())
			{
				
				id = rs.getInt("id");
				description = RationaleDBUtil.decode(rs.getString("description"));
				answer = RationaleDBUtil.decode(rs.getString("answer"));
				procedure = RationaleDBUtil.decode(rs.getString("proc"));
				status = (QuestionStatus) QuestionStatus.fromString(rs.getString("status"));
				ptype = (RationaleElementType) RationaleElementType.fromString(rs.getString("ptype"));
				parent = rs.getInt("parent");
				rs.close();	
				
			}
			
			//no, not last - need history too
			String findQuery5 = "SELECT * from HISTORY where ptype = 'Question' and " +
			"parent = " + Integer.toString(id);
//			***			  System.out.println(findQuery5);
			rs = stmt.executeQuery(findQuery5);
			while (rs.next())
			{
				History nextH = new History();
				nextH.setStatus(rs.getString("status"));
				nextH.setReason(RationaleDBUtil.decode(rs.getString("reason")));
				nextH.dateStamp = rs.getTimestamp("date");
//				nextH.dateStamp = rs.getDate("date");
				history.add(nextH);
			}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Question.fromDatabase(String)", "SQL Error");
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
	}	
	/*	
	 public boolean display()
	 {
	 Frame lf = new Frame();
	 QuestionGUI ar = new QuestionGUI(lf, parent, ptype, this, false);
	 ar.show();
	 return ar.getCanceled();
	 } */
	
	/**
	 * Bring up the question in the editor.
	 * @param disp - points to the display
	 * @return true if the user cancels
	 */
	public boolean display(Display disp)
	{
		EditQuestion ar = new EditQuestion(disp, this, false);
		String msg = "Edited question " + this.getName() + " " + ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
//		System.out.println("this after = " + this.getStatus().toString());
//		System.out.println(ar.getCanceled());
		return ar.getCanceled(); //can I do this?
		
	}
	
	/**
	 * Create a new question using the question editor
	 * @param disp - points to the display
	 * @param parent - the element we are asking about
	 * @return true if the user cancels
	 */
	public boolean create(Display disp, RationaleElement parent)
	{
//		System.out.println("create question");
		this.parent = parent.getID();
		this.ptype = parent.getElementType();
		EditQuestion ar = new EditQuestion(disp, this, true);
		return ar.getCanceled(); 
	}
	
	/**
	 * Delete our question. There are no sub-elements so it is quite
	 * straightforward
	 */
	public boolean delete()
	{
		m_eventGenerator.Destroyed();
		
		//no downward dependencies for questions!
		RationaleDB db = RationaleDB.getHandle();
		db.deleteRationaleElement(this);
		return false;
		
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
	 * Update the status of our question
	 * @return new status values
	 */
	public Vector<RationaleStatus> updateStatus()
	{
		QuestionInferences inf = new QuestionInferences();
		Vector<RationaleStatus> newStat = inf.updateQuestion(this);
		return newStat;
	}
	
	/**
	 * Make any inferences needed to check for changes from deleting the
	 * question
	 * @return status changes
	 */
	public Vector<RationaleStatus> updateOnDelete()
	{
		QuestionInferences inf = new QuestionInferences();
		Vector<RationaleStatus> newStat = inf.updateOnDelete(this);
		return newStat;
	}
	
	public void fromXML(Element qN) {
		
		this.fromXML = true;
		
		RationaleDB db = RationaleDB.getHandle();
		
		//add idref ***from the XML***
		String idref = qN.getAttribute("id");
		
		//get our name
		name = qN.getAttribute("name");
		System.out.println(name);
		
		//get our status
		status = QuestionStatus.fromString(qN.getAttribute("status"));
		System.out.println(status.toString());
		
		Node descN = qN.getFirstChild();
		System.out.println(descN.getNodeName());
		//get the description
		//the text is actually the child of the element, odd...
		Node descT = descN.getFirstChild();
		if (descT instanceof Text) {
			Text text = (Text) descT;
			String data = text.getData();
			setDescription(data);
		}
		
		//and last....
		db.addRef(idref, this); //important to use the ref from the XML file!
		
		Element child = (Element) descN.getNextSibling();
		System.out.println(child.getNodeName());
		//get the procedure
		//the text is actually the child of the element, odd...
		Node nextT = child.getFirstChild();
		if (nextT instanceof Text) {
			Text text = (Text) nextT;
			String data = text.getData();
			System.out.println("thinks it found a procedure");
			setProcedure(data);
		}
		
		child = (Element) child.getNextSibling();
		System.out.println(child.getNodeName());
		//get the answer
		//the text is actually the child of the element, odd...
		nextT = child.getFirstChild();
		if (nextT instanceof Text) {
			Text text = (Text) nextT;
			String data = text.getData();
			System.out.println(data);
			setAnswer(data);
		}
		
		child = (Element) child.getNextSibling();
		if (child != null)
		{
			String nextName;
			
			nextName = child.getNodeName();
			System.out.println(child.getNodeName());
			//here we check the type, then process
			if (nextName.compareTo("DR:history") == 0)
			{
				historyFromXML(child);
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
			
			//find out if this question is already in the database
			Statement stmt = null; 
			ResultSet rs = null; 
			
			try {
				stmt = conn.createStatement(); 
				findQuery = "SELECT id, parent FROM questions where name='" +
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
				RationaleDB.reportError(ex, "Question.inDatabase", findQuery); 
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
	
}

