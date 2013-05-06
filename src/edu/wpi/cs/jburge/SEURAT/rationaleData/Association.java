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
 * Created on Nov 6, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Contains the information associating an alternative with the code that
 * implements it. This class is now saved and restored to the database and
 * reflects the data elements in the database table.  However, its from
 * and toDatabase methods are not used in all database save/restore operations.
 * @author jburge
 */
public class Association {
	
	/**
	 * The alternative
	 */
	int alt;
	/**
	 * The Eclipse format artifact identifier
	 */
	String artifact;
	/**
	 * The file name for the resource
	 */
	String resource;
	/**
	 * The name of the artifact
	 */
	String artName;
	/**
	 * The message displayed indicating the alternative associated
	 */
	String msg;
	
	/**
	 * Auto-generated empty constructor.
	 */
	public Association() {
		super();
	}
	
	/**
	 * Constructor with all of the attributes
	 */
	public Association(int alt, String artifact, String resource, String artName, String msg) {
		super();
		this.alt = alt;
		this.artifact = artifact;
		this.resource = resource;
		this.artName = artName;
		this.msg = msg;
	}
	
	/**
	 * @return int
	 */
	public int getAlt() {
		return alt;
	}
	
	/**
	 * @return String
	 */
	public String getResource() {
		return resource;
	}
	
	/**
	 * @return String
	 */
	public String getArtifact() {
		return artifact;
	}
	
	/**
	 * @return String
	 */
	public String getArtName() {
		return artName;
	}
	
	/**
	 * @return String
	 */
	public String getMsg() {
		return msg;
	}
	
	/**
	 * Sets the alt.
	 * @param alt The alt to set
	 */
	public void setAlt(int alt) {
		this.alt = alt;
	}
	
	/**
	 * Sets the artifact.
	 * @param artifact The artifact to set
	 */
	public void setArtifact(String artifact) {
		this.artifact = artifact;
	}	
	
	/**
	 * Sets the resource.
	 * @param resource The resource to set
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}
	
	/**
	 * Sets the artName.
	 * @param artName The artName to set
	 */
	public void setArtName(String artName) {
		this.artName = artName;
	}
	
	/**
	 * Sets the msg.
	 * @param msg The msg to set
	 */
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	/**
	 * Gets the association from the database, given the artifact name.
	 */
	public void fromDatabase(String artName) {
		this.artName = artName;
		
		System.out.println(artName);
		
		RationaleDB db = RationaleDB.getHandle();
		Statement stmt = null; 
		ResultSet rs = null; 
		Connection conn = db.getConnection();
		
		try {
			stmt = conn.createStatement();
			String findQuery = "SELECT * from " + RationaleDBUtil.escapeTableName("associations") +
			" where artname = '" + artName + "'";
			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next())
			{
				this.alt = rs.getInt("alternative");
				this.artifact = rs.getString("artifact");
				this.resource = rs.getString("artresource");
				this.msg = RationaleDBUtil.decode(rs.getString("assocmessage"));
			} else {
				System.out.println("didn't find artname " + artName + " ?");
				this.alt = -1;
			}
		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex, "Assumption.fromDatabase(String)", "Error in a query"); 
		} finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
	}
	
	/**
	 * Saves our association to the database given the artifact name it was previously known by.
	 */
	public void toDatabase(String oldArtName) {
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
		
//		***		System.out.println("Saving to the database");
		
		try {
			stmt = conn.createStatement(); 
			
			// TODO- refactoring, add an inDatabase method, and use the from/to database
			// methods for all association database-related tasks
			//if (inDatabase(parent,ptype))
			//{
				String updateParent = "UPDATE " + RationaleDBUtil.escapeTableName("associations") +
				" R " + "SET R.artifact = '" +
				this.artifact + "', " +
				"R.artresource = '" +
				this.resource + "', " +
				"R.artname = '" +
				this.artName +
				"' WHERE " +
				"R.alternative = " + this.alt + " and R.artname = '" + 
				oldArtName + "'";
//				System.out.println(updateParent);
				stmt.execute(updateParent);
				
			//}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Association.toDatabase()", "SQL Error");
		}
		
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
	}
}
