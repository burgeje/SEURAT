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
 * Designer class
 */

package edu.wpi.cs.jburge.SEURAT.rationaleData;

import instrumentation.DataLog;

import java.io.*;

import java.sql.Connection; 
import java.sql.SQLException; 
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import SEURAT.events.RationaleElementUpdateEventGenerator;
import SEURAT.events.RationaleUpdateEvent;

import edu.wpi.cs.jburge.SEURAT.editors.EditDesigner;

/**
 * The Designer refers to the person who entered some element of the rationale although
 * it could also be used to refer to who is responsible. This was added for
 * the ORCA extensions and needs more work to be complete.
 * @author burgeje
 *
 */
public class Designer extends RationaleElement implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7720911570930140048L;
	// instance variables
	//TBD...
	/**
	 * The designer's position within the corporation. This probably should
	 * be changed to refer to the CorpPosType but is just a string for now.
	 */
	String corpPosition;
	/**
	 * The designer's position/role on the project. This is different from
	 * corporation position - for example, a senior engineer might be a team leader
	 * or they could be a product designer, or...
	 */
	String projPosition;
	
	/**
	 * How much experience does the designer have at their current job (would
	 * position make more sense?)
	 */
	int exprHere;
	/**
	 * How much experience do they have total.
	 */
	int exprTotal;
	/**
	 * What are their areas of expertise?
	 */
	Vector<AreaExp> expertise;

	private RationaleElementUpdateEventGenerator<Designer> m_eventGenerator = 
		new RationaleElementUpdateEventGenerator<Designer>(this);
	
	public String getCorpPosition() {
		return corpPosition;
	}
	
	public void setCorpPosition(String corpPosition) {
		this.corpPosition = corpPosition;
	}
	
	public Vector getExpertise() {
		return expertise;
	}
	
	public void setExpertise(Vector<AreaExp> expertise) {
		this.expertise = expertise;
	}
	
	public void addArea(AreaExp area)
	{
		this.expertise.add(area);
	}
	public int getExprHere() {
		return exprHere;
	}
	
	public void setExprHere(int exprHere) {
		this.exprHere = exprHere;
	}
	
	public int getExprTotal() {
		return exprTotal;
	}
	
	public void setExprTotal(int exprTotal) {
		this.exprTotal = exprTotal;
	}
	
	public String getProjPosition() {
		return projPosition;
	}
	
	public void setProjPosition(String projPosition) {
		this.projPosition = projPosition;
	}
	
	/** 
	 * Our constructor
	 *
	 */
	public Designer()
	{
		super();
		expertise = new Vector<AreaExp>();
		corpPosition = "";
		projPosition = "";
		
		
	} 
	
	public RationaleElementType getElementType()
	{
		return RationaleElementType.DESIGNER;
	}
	
	/**
	 * Save our designer to the database.
	 * @return the id
	 */
	public int toDatabase()
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		int ourid = 0;

		// Update Event To Inform Subscribers Of Changes
		// To Rationale
		RationaleUpdateEvent l_updateEvent;
		
		//find out if this requirement is already in the database
		Statement stmt = null; 
		ResultSet rs = null; 
		
//		***		System.out.println("Saving to the database");
		
		try {
			stmt = conn.createStatement(); 
			/*
			 String findQuery = "SELECT id  FROM Contingencies where name='" +
			 this.name + "'";
			 //***			 System.out.println(findQuery);
			  rs = stmt.executeQuery(findQuery);
			  */ 
			
			
			/*
			 if (rs.next())
			 {
			 */
			if (this.id >= 0)
			{
//				***				System.out.println("already there");
//				ourid = rs.getInt("id");
				String updateAssump = "UPDATE DesignerProfiles A " +
				"SET A.name = '" + RationaleDBUtil.escape(this.name) +
				"', A.corpPosition = '" + RationaleDBUtil.escape(this.corpPosition) +
				"', A.projPosition = '" + RationaleDBUtil.escape(this.projPosition) +
				"', A.exprHere = " + this.exprHere +
				", A.exprTotal = " + this.exprTotal +
				" WHERE " +
				"A.id = " + this.id + " ";
//				System.out.println(updateAssump);
				stmt.execute(updateAssump);
				
				l_updateEvent = m_eventGenerator.MakeUpdated();
			}
			else 
			{
				
				//now, we have determined that the Designer is new
				
				String newArgSt = "INSERT INTO DesignerProfiles " +
				"(name, corpPosition, projPosition, exprHere, exprTotal) " +
				"VALUES ('" +
				RationaleDBUtil.escape(this.name) + 
				"', '" + RationaleDBUtil.escape(this.corpPosition) +
				"', '" + RationaleDBUtil.escape(this.projPosition) +
				"', " + this.exprHere +
				", " + this.exprTotal + ")";
				
//				***			   System.out.println(newArgSt);
				stmt.execute(newArgSt); 
				
				l_updateEvent = m_eventGenerator.MakeCreated();
			}
			//now, we need to get our ID
			String findQuery2 = "SELECT id FROM DesignerProfiles where name='" +
			this.name + "'";
			rs = stmt.executeQuery(findQuery2); 
//			***			System.out.println(findQuery2);
			
			if (rs.next())
			{
				ourid = rs.getInt("id");
				rs.close();
			}
			else
			{
				ourid = -1;
			}
			this.id = ourid;
			
			m_eventGenerator.Broadcast(l_updateEvent);
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Designer.toDatabase", "SQL error");
		}
		
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
		return ourid;	
		
	}	
	
	/**
	 * Read our designer from the database, given their ID
	 * @param id - the unique id
	 */
	public void fromDatabase(int id)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		this.id = id;
		
		Statement stmt = null; 
		ResultSet rs = null; 
		String findQuery = "";
		try {
			stmt = conn.createStatement();
			
			findQuery = "SELECT *  FROM " +
			"DesignerProfiles where id = " +
			new Integer(id).toString();
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next())
			{
				name = RationaleDBUtil.decode(rs.getString("name"));
				rs.close();
				this.fromDatabase(name);
			}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Designer.fromDatabase(int)", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
	}		
	
	/**
	 * Read in our decision from the database, given its name
	 * @param name - the decision name
	 */
	public void fromDatabase(String name)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		this.name = name;
		name = RationaleDBUtil.escape(name);
		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			stmt = conn.createStatement();
			String findQuery; 
			findQuery = "SELECT *  FROM " +
			"DesignerProfiles where name = '" +
			name + "'";
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			if (rs != null)
			{
				while (rs.next())
				{
					id = rs.getInt("id");
					corpPosition = RationaleDBUtil.decode(rs.getString("corpPosition"));
					projPosition = RationaleDBUtil.decode(rs.getString("projPosition"));
					exprHere = rs.getInt("exprHere");
					exprTotal = rs.getInt("exprTotal");
				}
				rs.close();
				
				//also need to read in the areas too
				String findQuery3 = "SELECT * from AreaExp WHERE " +
				"des = " + new Integer(id).toString();
				
//				***				   System.out.println(findQuery3);
				rs = stmt.executeQuery(findQuery3);
				if (rs != null)
				{
					while (rs.next())
					{
						int areaID = rs.getInt("id");
						AreaExp exA = new AreaExp();
						exA.fromDatabase(areaID);
						this.addArea(exA);
					}
					rs.close();
				}
				
			}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Designer.fromDatabase(String)", "SQL Error");
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
	}	
	
	/**
	 * Create a new designer by bringing up the designer editor.
	 * @param disp - points to the display
	 * @param parent - the parent element of the designer
	 * @return true if the user cancels
	 */
	public boolean create(Display disp, RationaleElement parent)
	{
//		System.out.println("create Designer");
		
		EditDesigner ar = new EditDesigner(disp, this, true);
		String msg = "Edited Designer " + this.getName() + " " + ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		return ar.getCanceled(); //can I do this?
	}
	
	/**
	 * Displays the designer using the designer editor.
	 * @param disp - points back to the display
	 * @return true if the user cancels
	 */
	public boolean display(Display disp)
	{
		EditDesigner ar = new EditDesigner(disp, this, false);
		return ar.getCanceled(); //can I do this?
		
	}
	
	/**
	 * Designers have no status.
	 */
	public Vector<RationaleStatus> updateStatus()
	{
//		DesignerInferences inf = new ContingencyInferences();
		//	Vector newStat = inf.updateAssumption( this);
		//return newStat;
		return null;
	}
}
