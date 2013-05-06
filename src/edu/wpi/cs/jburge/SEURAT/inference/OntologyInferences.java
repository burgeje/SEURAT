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
		Vector<String> reqNames = new Vector<String>();
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
		String findQuery = "";
		//	boolean error = false;
		
		//check claims
		try {
			stmt = conn.createStatement();
			
			findQuery = "SELECT name  FROM " +
			"claims where " +
			"ontology = " + entry.getID();
//			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			while (rs.next())
			{
				claimNames.add(RationaleDBUtil.decode(rs.getString("name")));
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
			RationaleDB.reportError(ex, "OntologyInferences.updateOntEntry",
					findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
		//Now, do the same thing for requirements
		try {
			stmt = conn.createStatement();
			
			findQuery = "SELECT name  FROM " +
			"requirements where " +
			"ontology = " + entry.getID();
//			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			while (rs.next())
			{
				reqNames.add(RationaleDBUtil.decode(rs.getString("name")));
			}
			
			Enumeration requirements = reqNames.elements();
			while (requirements.hasMoreElements())
			{
				Requirement ourreq = new Requirement();
				ourreq.fromDatabase((String) requirements.nextElement());
				//We can pretty much assume that the importance has changed so to be
				//safe, we'll go with the direct update of the argument
				//we need to update any arguments that this requirement is in. 
				Vector<Argument> argsUsing = RationaleDB.getDependentArguments(ourreq.getID());
				Iterator argI = argsUsing.iterator();
				Vector<RationaleStatus> argStatus = new Vector<RationaleStatus>(); 
				while (argI.hasNext())
				{
					Argument arg = (Argument) argI.next();				
					//for now, don't get the status change - we're hoping this will do what we need
					argStatus = arg.updateStatus();
				}
				newStatus.addAll(argStatus);
			}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "OntologyInferences.updateOntEntry",
					findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(entry.getID(), entry.getName(), RationaleElementType.ONTENTRY);
		
		return newStatus;
		
	} 
	
}
