/*
 * Created on Jan 15, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.inference;

import java.sql.*;
import java.util.Vector;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Performs any necessary inferences when a question is changed.
 * @author jburge
 */
public class QuestionInferences {

	/**
	 * Empty constructor
	 */
	public QuestionInferences() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Update the rationale status based on the change.
	 * @param quest - the question to update
	 * @return a vector of any status changes that need to be displayed
	 */
	public Vector<RationaleStatus> updateQuestion(Question quest) {
		Vector<RationaleStatus> newStatus = new Vector<RationaleStatus>();
		quest.toDatabase(quest.getParent(), quest.getPtype());
		
		DecisionInferences decinf = new DecisionInferences();
		AlternativeInferences altinf = new AlternativeInferences();
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
	//	boolean error = false;
		try {
			 stmt = conn.createStatement();
			 String findQuery; 
			 
			 if (quest.getPtype() == RationaleElementType.DECISION)
			 {
				 findQuery = "SELECT name  FROM " +
				 "decisions where " +
				 "id = " + quest.getParent();
//***				 System.out.println(findQuery);
				 rs = stmt.executeQuery(findQuery);
 				 while (rs.next())
				 {
					 Decision dec = new Decision();
					 dec.fromDatabase(RationaleDB.decode(rs.getString("name")));
					 Vector<RationaleStatus> results = decinf.updateDecisionStatus(dec);
					 if (results != null)
					 {
						newStatus.addAll(results);
					 }
				 }
			 }
			 else if (quest.getPtype() == RationaleElementType.ALTERNATIVE)
			 {
				findQuery = "SELECT name  FROM " +
				"alternatives where " +
				"id = " + quest.getParent();
//***				System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery);
				while (rs.next())
				{
					Alternative alt = new Alternative();
					alt.fromDatabase(RationaleDB.decode(rs.getString("name")));
					Vector<RationaleStatus> results = altinf.updateAlternative(alt);
					if (results != null)
					{
					   newStatus.addAll(results);
					}
				}			 	
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
		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(quest.getID(), quest.getName(), RationaleElementType.QUESTION);
	
		return newStatus;
	}
	
	public Vector<RationaleStatus> updateOnDelete(Question quest) {
		Vector<RationaleStatus> newStatus = new Vector<RationaleStatus>();

		DecisionInferences decinf = new DecisionInferences();
		AlternativeInferences altinf = new AlternativeInferences();
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
	//	boolean error = false;
		try {
			 stmt = conn.createStatement();
			 String findQuery; 
			 
			 if (quest.getPtype() == RationaleElementType.DECISION)
			 {
				 findQuery = "SELECT name  FROM " +
				 "decisions where " +
				 "id = " + quest.getParent();
//***				 System.out.println(findQuery);
				 rs = stmt.executeQuery(findQuery);
				 while (rs.next())
				 {
					 Decision dec = new Decision();
					 dec.fromDatabase(RationaleDB.decode(rs.getString("name")));
					 Vector<RationaleStatus> results = decinf.updateDecisionStatus(dec);
					 if (results != null)
					 {
						newStatus.addAll(results);
					 }
				 }
			 }
			 else if (quest.getPtype() == RationaleElementType.ALTERNATIVE)
			 {
				findQuery = "SELECT name  FROM " +
				"alternatives where " +
				"id = " + quest.getParent();
//***				System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery);
				while (rs.next())
				{
					Alternative alt = new Alternative();
					alt.fromDatabase(RationaleDB.decode(rs.getString("name")));
					Vector<RationaleStatus> results = altinf.updateAlternative(alt);
					if (results != null)
					{
					   newStatus.addAll(results);
					}
				}			 	
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
		   
		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(quest.getID(), quest.getName(), RationaleElementType.QUESTION);

		return newStatus;
	}


}
