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

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class XFeatureMapping implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	private int rationaleID;
	private RationaleElementType rationaleType;
	private XFeatureNodeType nodeType;
	private String nodeName;
	private int parent;

	public XFeatureMapping(int rationaleID,
			RationaleElementType rationaleType, XFeatureNodeType nodeType,
			String nodeName, int pid) {
		super();
		this.rationaleID = rationaleID;
		this.rationaleType = rationaleType;
		this.nodeType = nodeType;
		this.nodeName = nodeName;
		this.parent = pid;
	}
	
	public XFeatureMapping()
	{
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRationaleID() {
		return rationaleID;
	}

	public void setRationaleID(int rationaleID) {
		this.rationaleID = rationaleID;
	}

	public RationaleElementType getRationaleType() {
		return rationaleType;
	}

	public void setRationaleType(RationaleElementType rationaleType) {
		this.rationaleType = rationaleType;
	}

	public XFeatureNodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(XFeatureNodeType nodeType) {
		this.nodeType = nodeType;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int pid) {
		this.parent = pid;
	}

	/**
	 * Save the history information to the database
	 * @param parent - the element the history is for
	 * @param ptype - the element's type
	 * @return the unique ID for the history entry
	 */
	public int toDatabase()
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
//		DateFormat.SHORT).format(dateStamp));
		
		try {
			stmt = conn.createStatement(); 
			String ratSt = new Integer(rationaleID).toString();	
			String pidSt = new Integer(parent).toString();
			
			//make sure the mapping is not there already! The rationale id is what makes the
			//mapping unique since the same node in the feature model can translate into several
			//alternatives in the rationale (because of cardinality)
			String checkPresence = "SELECT id FROM xfeaturemapping where ratid="
				+ ratSt + " and rattype = '" + rationaleType.toString() + "'";
			rs = stmt.executeQuery(checkPresence);
			if (!rs.next())
			{
				String newQuestSt = "INSERT INTO xfeaturemapping "+
				"(ratid, rattype, nodename, nodetype, parent) " +
				"VALUES (" +
				ratSt + ", '" +
				rationaleType.toString() + "', '" +
				nodeName + "', '" +
				nodeType.toString() + "', " +
				pidSt + ")";
				
//				System.out.println(newQuestSt);
				stmt.execute(newQuestSt); 
//				***			System.out.println("query ok?");	
			}
			else
			{
				System.out.println("Mapping already in database?");
			}
			
		rs = stmt.executeQuery(checkPresence);

		if (rs.next()) {
			ourid = rs.getInt("id");
			rs.close();
		} else {
			ourid = -1;
		}
		this.id = ourid;
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "XFeatureMapping.toDatabase", "SQL error");
		}
		
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
		return ourid;	
		
	}		
	
	/**
	 * Read in the element from the database, given the rationale id and type
	 * @param name - the assumption name
	 */
	public void fromDatabase(RationaleElementType rtype, int rID)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		
		String findQuery = ""; 		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			stmt = conn.createStatement();
			
			findQuery = "SELECT *  FROM " +
			"xfeaturemapping where rationaletype = '" +
			rtype.toString() + "'" + 
			" and ratid = '" + new Integer(rID).toString() + "'";
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next())
			{
				
				id = rs.getInt("id");
				rationaleType = rtype;
				rationaleID = rID;
				nodeType = XFeatureNodeType.fromString(rs.getString("nodetype"));
				nodeName = rs.getString("nodename");
				parent = rs.getInt("parent");
				rs.close();
				
				
				
			}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "XFeatureMapping.fromDatabase(type,ID)", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
			
		}
		
	}	

	/**
	 * Read in the element from the database, given the id 
	 * @param xID - the ID
	 */
	public void fromDatabase(int xID)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		
		String findQuery = ""; 		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			stmt = conn.createStatement();
			
			findQuery = "SELECT *  FROM " +
			"xfeaturemapping where id = " + new Integer(xID).toString();
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next())
			{
				
				id = rs.getInt("id");
				rationaleType = RationaleElementType.fromString(rs.getString("rattype"));
				rationaleID = rs.getInt("ratid");
				nodeType = XFeatureNodeType.fromString(rs.getString("nodetype"));
				nodeName = rs.getString("nodename");
				parent = rs.getInt("parent");
				rs.close();
				
				
				
			}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "XFeatureMapping.fromDatabase(type,ID)", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
			
		}
		
	}	

}
