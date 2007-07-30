/*
 * Created on Feb 1, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.inference;

import java.sql.*;
import java.util.*;
import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Performs any inferences needed when an ontology entry is modified
 * @author jburge
 *
 */
public class OntologyInferences {

	/**
	 * Empty constructor
	 */
	public OntologyInferences() {
	}
	
	/**
	 * Perform inferences when an ontology entry is modified. For example, if the
	 * importance is changed then any dependent arguments need to be re-evaluated.
	 * @param entry - the ontology entry that has been changed
	 * @return - a vector of status updates to display
	 */
	public Vector<RationaleStatus> updateOntEntry(OntEntry entry)
	{
		Vector<RationaleStatus> newStatus = new Vector<RationaleStatus>();
		//an OntEntry has changed - so what inferences must be re-computed?
		//first, find what arguments are affected and evaluate them
		
		Vector<String> claimNames = new Vector<String>();
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
	//	boolean error = false;
		try {
			 stmt = conn.createStatement();
			 String findQuery; 
				 findQuery = "SELECT name  FROM " +
				 "claims where " +
				 "ontology = " + entry.getID();
//			 System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery);
			 
			 while (rs.next())
			 {
				claimNames.add(RationaleDB.decode(rs.getString("name")));
			 }
			 
			 Enumeration claims = claimNames.elements();
			 while (claims.hasMoreElements())
			 {
				Claim ourclaim = new Claim();
				ourclaim.fromDatabase((String) claims.nextElement());
			 	
				ClaimInferences inf = new ClaimInferences();
				newStatus.addAll(inf.updateClaim(ourclaim));
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
		manager.addUpdate(entry.getID(), entry.getName(), RationaleElementType.ONTENTRY);

		 return newStatus;
	
	} 

}
