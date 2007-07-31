/*
 * Created on Nov 12, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.inference;

import java.sql.*;
import java.util.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Performs inferences when an assumption is updated. For example, any arguments 
 * relating to that assumption may need changing.
 * @author jburge
 *
 */
public class AssumptionInferences {

	/**
	 * Empty constructor
	 *
	 */
	public AssumptionInferences() {
	}

	/**
	 * Updates any arguments that refer to this assumption
	 * @param assm - the assumption modified
	 * @return a vector giving status updates that need to be displayed
	 */
	public Vector<RationaleStatus> updateAssumption(Assumption assm)
	{
		Vector<RationaleStatus> newStatus = new Vector<RationaleStatus>();
		//an assumptione has changed - so what inferences must be re-computed?
		//first, find what arguments are affected and evaluate them
		
		//The names of all the affected arguments
		Vector<String> argNames = new Vector<String>();
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
		 String findQuery = "";
	//	boolean error = false;
		try {
			 stmt = conn.createStatement();

				 findQuery = "SELECT name  FROM " +
				 "arguments where argtype = 'assumption' and " +
				 "assumption = " + assm.getID();
//***	 		 System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery);
			 
			 while (rs.next())
			 {
			 	argNames.add(RationaleDB.decode(rs.getString("name")));
			 }
			 
			 Enumeration args = argNames.elements();
			 while (args.hasMoreElements())
			 {
			 	Argument arg = new Argument();
			 	arg.fromDatabase((String) args.nextElement());
			 	
			 	ArgumentInferences inf = new ArgumentInferences();
				newStatus.addAll(inf.updateArgument(arg, true));
			 }
			 
		} catch (SQLException ex) {
	   // handle any errors 
			RationaleDB.reportError(ex, "AssumptionInferences.updateAssumption",
					findQuery);
	   }
	   finally { 
		   RationaleDB.releaseResources(stmt, rs);
		   }
		   
		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(assm.getID(), assm.getName(), RationaleElementType.ASSUMPTION);

		 return newStatus;
	
	} 

}

