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
			"arguments where argtype = 'Assumption' and " +
			"assumption = " + assm.getID();
//			***	 		 System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			while (rs.next())
			{
				argNames.add(RationaleDBUtil.decode(rs.getString("name")));
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

