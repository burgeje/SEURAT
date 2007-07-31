
package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.util.*;
import java.io.*;

import java.sql.Connection;  
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.sql.Timestamp;

import org.w3c.dom.Element;

/**
 * Defines the structure of history information stored for rationale entities. 
 * The history keeps track of status changes (like when an alternative is rejected, and why).
 * @author burgeje
 *
 */
public class History implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1241985727138697071L;
	// instance variables
	
	/**
	 * The status of the element at the time of this history snapshot
	 */
	String status;
	/**
	 * The reason the status changed (provided by the user)
	 */
	String reason;
	/**
	 * The date/time of the change
	 */
	Date dateStamp;

	/**
	 * Flag indicating if this was read from XML
	 */
	boolean fromXML;
	/**
	 * Our constructor
	 *
	 */
	public History()
	{
		//this might be a good time to put on the date stamp?
		dateStamp = new Date(); //this will set date to the creation date/time
		fromXML = false;
	} 
	
	/**
	 * The alternative constructor
	 * @param stat - the status of the element
	 * @param reas - the reason its status has changed
	 */
	public History(String stat, String reas)
	{
		status = stat;
		reason = reas;
		//figure out date
		dateStamp = new Date();
	}

	public String getStatus()
	{
		return status;
	}
	public void setStatus(String stat)
	{
		status = stat;
	}
	
	public String getReason()
	{
		return reason;
	}
	
	public void setReason(String reas)
	{
		reason = reas;
	}
	
	public Date getDateStamp()
	{
		return dateStamp;
	}
	

	public String toString()
	{
		return dateStamp + ": " + "Status = " + status + "Reason: " + reason;
	}
	
	/**
	 * Save the history information to the database
	 * @param parent - the element the history is for
	 * @param ptype - the element's type
	 * @return the unique ID for the history entry
	 */
	public int toDatabase(int parent, RationaleElementType ptype)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		ResultSet rs = null; 
		
		int ourid = 0;
		
		//find out if this question is already in the database
		Statement stmt = null; 
//		ResultSet rs = null; 
		
//		System.out.println("Saving to the database");
//		System.out.println(DateFormat.getDateTimeInstance(DateFormat.SHORT,
//			DateFormat.SHORT).format(dateStamp));

		try {
			stmt = conn.createStatement(); 
			Timestamp ourTime = new Timestamp(dateStamp.getTime());
			String parentRSt = new Integer(parent).toString();	
			
			//make sure the history is not there already!
			String checkPresence = "SELECT * FROM History where " +
			   "ptype = '" + ptype.toString() + "' and " +
			   "parent = " + parentRSt + " and " +
			   "date = '" + ourTime.toString() + "'";
			rs = stmt.executeQuery(checkPresence);
			if (!rs.next())
			{
				String newQuestSt = "INSERT INTO History "+
				   "(ptype, parent, date, reason, status) " +
				   "VALUES ('" +
				   ptype.toString() + "', " +
				   parentRSt + ", '" +
				   ourTime.toString() + "', '" +
				   RationaleDB.escape(this.reason) + "', '" +
				   this.status + "')";

//				   System.out.println(newQuestSt);
				stmt.execute(newQuestSt); 
//	  ***			System.out.println("query ok?");	
			}

			
		ourid = 0; //no reason to keep ID around

		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "History.toDatabase", "SQL error");
	   }
   	   
	   finally { 
		   RationaleDB.releaseResources(stmt, rs);
		   }
		   
		return ourid;	
 
	}		

	/**
	 * Reads history information from XML
	 * @param hN - the XML element containing the history
	 */
	public void fromXML(Element hN)
	{
		this.fromXML = true;
		status = hN.getAttribute("status");
		reason = hN.getAttribute("reason");
		dateStamp = new Date(hN.getAttribute("datestamp"));
	}


}
