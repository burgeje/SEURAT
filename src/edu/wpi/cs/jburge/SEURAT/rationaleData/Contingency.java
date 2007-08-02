

/*
 * Contingency class
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

import edu.wpi.cs.jburge.SEURAT.editors.EditContingency;

/**
 * Defines a contingency. Contingencies, a term that NASA uses in spacecraft
 * design, indicate the % of safety margin used when determining things like the
 * weight of a spacecraft. This is a way of dealing with uncertainty and the
 * contingency level varies depending on how well known the problem is.
 * @author burgeje
 *
 */
public class Contingency extends RationaleElement implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2437383533065145294L;
	// instance variables
	
	/**
	 * The contingency amount
	 */
	float percentage;
	
	public Contingency()
	{
		super();
		
	} 
	
	public RationaleElementType getElementType()
	{
		return RationaleElementType.CONTINGENCY;
	}
	public void setDescription(String desc)
	{
		description = desc;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	/**
	 * This returns the percentage for an enabled contingency. If the contingency
	 * has been disabled, the amount returned is zero.
	 * @return
	 */
	public float getPercentage()
	{
		if (enabled)
		{
			return percentage; //need to store it, really...
		}
		else
		{
//			System.out.println("Contingency disabled");
			return 0;
		}
	}
	
	public void setPercentage(float percent)
	{
		percentage = percent;
	}
	
	
	/**
	 * Save a contingency to the database
	 * @return the contingency ID
	 */
	public int toDatabase()
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		int ourid = 0;
		
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
				String updateAssump = "UPDATE Contingencies A " +
				"SET A.name = '" + RationaleDB.escape(this.name) +
				"', A.description = '" + RationaleDB.escape(this.description) +
				"', A.amount = " + this.percentage +
				" WHERE " +
				"A.id = " + this.id + " ";
//				System.out.println(updateAssump);
				stmt.execute(updateAssump);
			}
			else 
			{
				
				//now, we have determined that the Contingency is new
				
				String newArgSt = "INSERT INTO Contingencies " +
				"(name, description, amount) " +
				"VALUES ('" +
				RationaleDB.escape(this.name) + "', '" +
				RationaleDB.escape(this.description) + "', " +
				this.percentage + ")";
				
//				***			   System.out.println(newArgSt);
				stmt.execute(newArgSt); 
				
				
				
			}
			//now, we need to get our ID
			String findQuery2 = "SELECT id FROM Contingencies where name='" +
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
				ourid = 0;
			}
			this.id = ourid;
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Contingency.toDatabase()", "Error reading Contingency");
		}
		
		finally { 
			RationaleDB.releaseResources(stmt, rs);
			
		}
		
		return ourid;	
		
	}	
	
	/**
	 * Read in a contingency from the database
	 * @param id - the unique id
	 */
	public void fromDatabase(int id)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		String findQuery = "";		
		this.id = id;
		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			stmt = conn.createStatement();
			
			findQuery = "SELECT *  FROM " +
			"Contingencies where id = " +
			new Integer(id).toString();
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next())
			{
				name = RationaleDB.decode(rs.getString("name"));
				description = RationaleDB.decode(rs.getString("description"));
				percentage = rs.getFloat("amount");
				rs.close();
				this.fromDatabase(name);
			}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Contingency.fromDatabase(int)", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
	}		
	
	/**
	 * Read in a contingency from the database, given its name.
	 * @param name - the contingency name
	 */
	public void fromDatabase(String name)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		String findQuery = "";		
		this.name = name;
		name = RationaleDB.escape(name);
		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			stmt = conn.createStatement();
			
			findQuery = "SELECT *  FROM " +
			"Contingencies where name = '" +
			name + "'";
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next())
			{
				
				id = rs.getInt("id");
				description = RationaleDB.decode(rs.getString("description"));
				percentage = rs.getFloat("amount");
				
				rs.close();
				
				
				
			}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Contingency.fromDatabase(String)", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
	}	
	
	/**
	 * Create a new contingency using the contingency editor.
	 * @param disp - points to the display
	 * @param parent - the parent element
	 * @return true if the user cancels
	 */
	public boolean create(Display disp, RationaleElement parent)
	{
//		System.out.println("create Contingency");
		
		EditContingency ar = new EditContingency(disp, this, true);
		String msg = "Edited Contingency " + this.getName() + " " + ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		return ar.getCanceled(); //can I do this?
	}
	
	/**
	 * Create a new contingency using the editor
	 * @param disp - points back to the display
	 * @return true if the user cancels from the editor
	 */
	public boolean display(Display disp)
	{
		EditContingency ar = new EditContingency(disp, this, false);
		return ar.getCanceled(); //can I do this?
		
	}
	
	/**
	 * This would update the status if we had any but for now, it's empty.
	 */
	public Vector<RationaleStatus> updateStatus()
	{
		return null;
	}
}
