/*
 * Requirements class
 */

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

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
		
		//find out if this question is already in the database
		Statement stmt = null; 
		ResultSet rs = null; 
		
//***		System.out.println("Saving to the database");

		try {
			 stmt = conn.createStatement(); 
			 /*
			 String findQuery = "SELECT id, parent FROM questions where name='" +
				this.name + "'";
			 System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery); 

			if (rs.next())
			{
//***				System.out.println("already there");
				ourid = rs.getInt("id");
				int prevParent = rs.getInt("parent");

				rs.close();
				
				if (parent > 0)
				{
				*/
				if (this.id >= 0)
				{
					String updateParent = "UPDATE questions " +
						"SET name = '" +
					 	RationaleDB.escape(this.name) + "', " +
						"description = '" +
					 	RationaleDB.escape(this.description) + "', " +
					 	"answer = '" +
					 	RationaleDB.escape(this.answer) + "', " +
					 	"proc = '" +
					 	RationaleDB.escape(this.procedure) + "', " +
					 	"status = '" +
					 	status.toString() + "', " +
					   "parent = " + new Integer(parent).toString() + ", " +
					   "ptype = '" + ptype.toString() +
						"' WHERE " +
					   "id = " + this.id + " " ;
//					   System.out.println(updateParent);
					stmt.execute(updateParent);
				}
/*				else
				{
					System.out.println("parent is zero");
				}
				*/
//			} 
	
		else 
		{
		
			//now, we have determined that the question is new
			String parentRSt = new Integer(parent).toString();		
			String newQuestSt = "INSERT INTO Questions "+
			   "(name, description, status, proc, answer, ptype, parent) " +
			   "VALUES ('" +
			   RationaleDB.escape(this.name) + "', '" +
			   RationaleDB.escape(this.description) + "', '" +
			   this.status.toString() + "', '" +
			   RationaleDB.escape(this.procedure) + "', '" +
			   RationaleDB.escape(this.answer) + "', '" +
			   ptype.toString() + "', " +
			   parentRSt + ")";

//***			   System.out.println(newQuestSt);
			stmt.execute(newQuestSt); 
			
		}
		//in either case, we want to update any sub-requirements in case
		//they are new!
			//now, we need to get our ID
			String findQuery2 = "SELECT id FROM questions where name='" +
			   RationaleDB.escape(this.name) + "'";
			rs = stmt.executeQuery(findQuery2); 

		   if (rs.next())
		   {
			   ourid = rs.getInt("id");
			   rs.close();
		   }
		   else
		   {
			ourid = 0;
		   }
		   this.id = ourid;
		   
		   //finally, the history
			
		   Enumeration hist = history.elements();
		   while (hist.hasMoreElements())
		   {
			   History his = (History) hist.nextElement();
			   his.toDatabase(ourid, RationaleElementType.QUESTION);
		   }
		

		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Question.toDatabase(int, type)", "SQL Error");
	   }
   	   
	   finally { 
		   RationaleDB.releaseResources(stmt, rs);
		   }
		   
		return ourid;	
 
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
		name = RationaleDB.escape(name);
		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			 stmt = conn.createStatement();
			 String findQuery; 
				 findQuery = "SELECT *  FROM " +
				 "Questions where name = '" +
				 name + "'";
// ***			System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery);
			 
	 
			 if (rs.next())
			 {
				
				id = rs.getInt("id");
				description = RationaleDB.decode(rs.getString("description"));
				answer = RationaleDB.decode(rs.getString("answer"));
				procedure = RationaleDB.decode(rs.getString("proc"));
				status = (QuestionStatus) QuestionStatus.fromString(rs.getString("status"));
				ptype = (RationaleElementType) RationaleElementType.fromString(rs.getString("ptype"));
				parent = rs.getInt("parent");
				rs.close();	
						
		 }
		 
		  //no, not last - need history too
		  String findQuery5 = "SELECT * from HISTORY where ptype = 'Question' and " +
			"parent = " + Integer.toString(id);
//***			  System.out.println(findQuery5);
		  rs = stmt.executeQuery(findQuery5);
		  while (rs.next())
		  {
			  History nextH = new History();
			  nextH.setStatus(rs.getString("status"));
			  nextH.setReason(RationaleDB.decode(rs.getString("reason")));
			  nextH.dateStamp = rs.getTimestamp("date");
//			  nextH.dateStamp = rs.getDate("date");
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
		//no downward dependencies for questions!
		RationaleDB db = RationaleDB.getHandle();
		db.deleteRationaleElement(this);
		return false;
		
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
	
	public int toDatabaseXML(int parent, RationaleElementType ptype)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		int ourid = 0;
		
		//find out if this question is already in the database
		Statement stmt = null; 
		ResultSet rs = null; 
		
		System.out.println("Saving to the database");

		try {
			 stmt = conn.createStatement(); 
			 String findQuery = "SELECT id, parent FROM questions where name='" +
				this.name + "'";
			 System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery); 

			if (rs.next())
			{
				System.out.println("already there");
				ourid = rs.getInt("id");
				int prevParent = rs.getInt("parent");

				rs.close();
				if ((prevParent <= 0) && (parent > 0))
				{
					String updateParent = "UPDATE questions D " +
					   "SET D.parentr = " + new Integer(parent).toString() +
					   "D.ptype = '" + ptype.toString() +
						"' WHERE " +
					   "D.id = " + ourid + " " ;
					   System.out.println(updateParent);
					stmt.execute(updateParent);
				}
			}
	
		else 
		{
		
			//now, we have determined that the requirement is new
			String parentRSt = new Integer(parent).toString();		
			String newQuestSt = "INSERT INTO Questions "+
			   "(name, description, status, proc, answer, ptype, parent) " +
			   "VALUES ('" +
			   RationaleDB.escape(this.name) + "', '" +
			   RationaleDB.escape(this.description) + "', '" +
			   this.status.toString() + "', '" +
			   RationaleDB.escape(this.procedure) + "', '" +
			   RationaleDB.escape(this.answer) + "', '" +
			   ptype.toString() + "', " +
			   parentRSt + ")";

			   System.out.println(newQuestSt);
			stmt.execute(newQuestSt); 
			
		}
		//in either case, we want to update any sub-requirements in case
		//they are new!
			//now, we need to get our ID
			String findQuery2 = "SELECT id FROM questions where name='" +
			   this.name + "'";
			rs = stmt.executeQuery(findQuery2); 

		   if (rs.next())
		   {
			   ourid = rs.getInt("id");
			   rs.close();
		   }
		   else
		   {
			ourid = 0;
		   }
		   


		} catch (SQLException ex) {
	   // handle any errors 
	   System.out.println("SQLException: " + ex.getMessage()); 
	   System.out.println("SQLState: " + ex.getSQLState()); 
	   System.out.println("VendorError: " + ex.getErrorCode()); 
	   }
   	   
	   finally { 
		   // it is a good idea to release
		   // resources in a finally{} block 
		   // in reverse-order of their creation 
		   // if they are no-longer needed 

		   if (rs != null) { 
			   try {
				   rs.close(); 
			   } catch (SQLException sqlEx) { // ignore 
			   } 

			   rs = null; 
		   }
    
		   if (stmt != null) { 
			   try { 
				   stmt.close(); 
			   } catch (SQLException sqlEx) { // ignore
				   } 

			   stmt = null; 
		   } 
		   }
		   
		return ourid;	
 
	}	
}

