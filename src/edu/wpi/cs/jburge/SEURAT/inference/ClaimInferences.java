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
 * Performs any necessary inferences when a claim is deleted or modified.
 * @author jburge
 */
public class ClaimInferences {
	
	/**
	 * Empty constructor
	 */
	public ClaimInferences() {
	}
	
	/**
	 * Update the claim and return any status changes
	 * @param ourClaim - the claim that has been modified
	 * @return a vector of status updates that need to be displayed
	 */
	public Vector<RationaleStatus> updateClaim(Claim ourClaim)
	{
		Vector<RationaleStatus> newStatus = new Vector<RationaleStatus>();
		//a claim has changed - so what inferences must be re-computed?
		//first, find what arguments are affected and evaluate them
		
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
			"arguments where argtype = 'Claim' and " +
			"claim = " + ourClaim.getID();
//			***			 System.out.println(findQuery);
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
			RationaleDB.reportError(ex, "ClaimInferences.updateClaim", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
			
		}
		
		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(ourClaim.getID(), ourClaim.getName(), RationaleElementType.CLAIM);
		
		return newStatus;
		
	} 	
	
}
