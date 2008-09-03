

/*
 * AltConstRel class
 * This class describes the relationship between the designer and an
 * area of expertise. The two attributes are the design component 
 * (design product entry) and the level.
 */

package edu.wpi.cs.jburge.SEURAT.rationaleData;


import instrumentation.DataLog;

import java.util.*;
import java.io.*;

import java.sql.Connection; 
import java.sql.SQLException; 
import java.sql.Statement;
import java.sql.ResultSet;

import org.eclipse.swt.widgets.Display;

import SEURAT.events.RationaleElementUpdateEventGenerator;
import SEURAT.events.RationaleUpdateEvent;

import edu.wpi.cs.jburge.SEURAT.editors.EditAltConstRel;

/**
 * The data structure describing the relationship between an alternative and
 * a constraint
 * @author burgeje
 *
 */
public class AltConstRel extends RationaleElement implements Serializable 
{
	// class variables
	
	/**
	 * version ID needed for serialization
	 */
	private static final long serialVersionUID = -4980728648617057858L;
	// instance variables
	
	/**
	 * The constraint
	 */
	Constraint constr;
	/**
	 * The alternative
	 */
	Alternative alt;
	/**
	 * The amount referred to by the constraint (ex: weight constraint of 5 pounds)
	 */
	float amount;
	/**
	 * The unit associated with the constraint amount (ex: pounds)
	 */
	String units;

	private RationaleElementUpdateEventGenerator<AltConstRel> m_eventGenerator = 
		new RationaleElementUpdateEventGenerator<AltConstRel>(this);
	
	/**
	 * The constructor.
	 */
	public AltConstRel()
	{
		super();
		units = "";
		constr = new Constraint();
	}
	
	/**
	 * Get the units
	 * @return units
	 */
	public String getUnits() {
		return units;
	}
	
	/**
	 * Set the units
	 * @param units - the units
	 */
	public void setUnits(String units) {
		this.units = units;
	}
	
	/**
	 * Get the type
	 * @return type
	 */
	public RationaleElementType getElementType()
	{
		return RationaleElementType.ALTCONSTREL;
	}
	
	/**
	 * Get the amount
	 * @return amount
	 */
	public float getAmount() {
		return amount;
	}
	
	/**
	 * Set the amount
	 * @param amount - the amount
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	/**
	 * Get the associated constraint
	 * @return the constraint
	 */
	public Constraint getConstr() {
		return constr;
	}
	
	/**
	 * Set our constraint
	 * @param constr - the constraint
	 */
	public void setConstr(Constraint constr) {
		this.constr = constr;
	}
	
	/**
	 * Get the alternative from the relationship
	 * @return - the alternative
	 */
	public Alternative getAlt() {
		return alt;
	}
	
	/**
	 * Set our alternative
	 * @param alt - the alternative
	 */
	public void setAlt(Alternative alt)
	{
		this.alt = alt;
	}
	
	/**
	 * Save our relationship to the database. 
	 * @param pid - the alternative ID
	 * @return the ID for our relationship (primary key)
	 */
	public int toDatabase(int pid)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		String updateOnt = "";
		int ourid = 0;

		// Update Event To Inform Subscribers Of Changes
		// To Rationale
		RationaleUpdateEvent l_updateEvent;
		
		//find out if this requirement is already in the database
		Statement stmt = null; 
		ResultSet rs = null; 
		
//		System.out.println("Saving to the database");
		
		try {
			stmt = conn.createStatement(); 
			if (this.id >= 0)
			{				
				//now, update it with the new information
				updateOnt = "UPDATE AltConstRel " +
				"SET name = '" + RationaleDBUtil.escape(this.name) + 
				"', alternative = " +
				pid + ", " +
				"constr  = " +
				constr.getID() + ", " +
				"amount = " + 
				this.amount +
				", units = '" +
				this.units + 
				"' WHERE " +
				"alternative = " + alt.getID() + " AND constr = " + 
				constr.getID() + ";";
				System.out.println(updateOnt);
				stmt.execute(updateOnt);
				
				ourid = this.id;
				
				l_updateEvent = m_eventGenerator.MakeUpdated();
			}
			else if (pid == 0)
			{
				//now, update it with the new information
				updateOnt = "UPDATE AltConstRel " +
				"SET name = '" + RationaleDBUtil.escape(this.name) + 
				"' units = '" +
				RationaleDBUtil.escape(this.units) +
				"' amount = " +
				this.amount +
				" WHERE " +
				"id = " + 
				this.getID() + ";";
				System.out.println(updateOnt);
				stmt.execute(updateOnt);

				ourid = this.id;
				
				l_updateEvent = m_eventGenerator.MakeUpdated();
			}
			else 
			{
				
				//now, we have determined that the ontolgy entry is new
				
				updateOnt = "INSERT INTO AltConstRel " +
				"(name, alternative, constr, amount, units) " +
				"VALUES ('" +
				RationaleDBUtil.escape(name) + "', " +
				alt.getID() + ", " +
				constr.getID() + ", " +
				amount + ", '" +
				RationaleDBUtil.escape(units) + "')"; 
				
				System.out.println(updateOnt);
				stmt.executeUpdate(updateOnt, Statement.RETURN_GENERATED_KEYS); 

				// TODO This code has not been tested
				ResultSet generatedKey = null;
				generatedKey = stmt.getGeneratedKeys();
				generatedKey.next();
				ourid = generatedKey.getInt(1);
				
				l_updateEvent = m_eventGenerator.MakeCreated();				
			}
			
			m_eventGenerator.Broadcast(l_updateEvent);
		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex, "Writing AltConstRel to DB", updateOnt);
		}
		
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
		return ourid;
	}	
	
	/**
	 * Given an ID, get the relationship from the database.
	 * @param id - our ID
	 */
	public void fromDatabase(int id)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		this.id = id;
		
		Statement stmt = null; 
		ResultSet rs = null;
		String findQuery = "";
//		boolean error = false;
		try {
			stmt = conn.createStatement();
			
			findQuery = "SELECT *  FROM " +
			"AltConstRel where id = " +
			new Integer(id).toString();
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next())
			{
				name = RationaleDBUtil.decode(rs.getString("name"));
				this.fromDatabase(name);
			}
			
		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex, "AltConstRel fromDB 2", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
			
		}
		
	}	
	
	/**
	 * Given the relationship name, get the relationship from the database
	 * @param name - the relationship name
	 */
	public void fromDatabase(String name)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
//		***		System.out.println("ont name = " + name);
		
		this.name = name;
		name = RationaleDBUtil.escape(name);
		String findQuery = "";
		Statement stmt = null; 
		ResultSet rs = null; 
//		boolean error = false;
		try {
			stmt = conn.createStatement();
			findQuery = "SELECT *  FROM " +
			"AltConstRel where name = '" +
			name + "'";
			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
//			int ontologyID;
			
			if (rs.next())
			{
				
				id = rs.getInt("id"); //ID equals the alternative
				name = RationaleDBUtil.decode(rs.getString("name"));
				amount = rs.getFloat("amount");
				units = RationaleDBUtil.decode(rs.getString("units"));
				constr = new Constraint();
				constr.fromDatabase(rs.getInt("constr"));
				alt = new Alternative();
				alt.fromDatabase(rs.getInt("alternative"));
				
				
			}
			
		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex, "reading AltConstRel from DB", findQuery); 
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
	}	
	
	/*	public boolean display()
	 {
	 Frame lf = new Frame();
	 AltConstRelGUI ar = new AltConstRelGUI(lf, this, null, false);
	 ar.show();
	 return ar.getCanceled();
	 } */
	
	/**
	 * Used to display the editor for this relationship. This is how you
	 * edit an existing relationship.
	 * @param disp - the display pointer
	 * @return true if the user cancels the edits
	 */
	public boolean display(Display disp)
	{
		EditAltConstRel ar = new EditAltConstRel(disp, this, null, false);
		String msg = "Edited AltConstRel " + this.getName() + " " + ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		return ar.getCanceled(); //can I do this?
		
	}
	
	/**
	 * Creates a new relationship by bringing up the editor.
	 * @param disp - points back to the display
	 * @param parent - the parent alternative for our relationship 
	 */
	public boolean create(Display disp, RationaleElement parent)
	{
		System.out.println("create AltConstRel");
		System.out.println("id = " + parent.getID());
		
		EditAltConstRel ar = new EditAltConstRel(disp, this, (Alternative) parent, true);
		System.out.println("name in create = " + this.getName());
//		((Alternative) parent).toDatabase();
		return ar.getCanceled(); //can I do this?
	}
	
	/**
	 * We don't use this yet. 
	 */
	public Vector<RationaleStatus> updateStatus()
	{
		//need to replace with real content!
//		OntologyInferences inf = new OntologyInferences();
//		Vector newStat = inf.updateConstraint( this);
		
//		return newStat;
		return null;
	}
}
