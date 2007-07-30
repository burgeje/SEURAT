

/*
 * AreaExp class
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


import edu.wpi.cs.jburge.SEURAT.editors.EditAreaExp;

/**
 * Used to identify an area of expertise for a Designer. The expertise
 * can be in a specific design product component and has a level indicating how
 * much of an expert they are. 
 * @author burgeje
 *
 */
public class AreaExp extends RationaleElement implements Serializable 
{
	// class variables

	/**
	 * 
	 */
	private static final long serialVersionUID = 2380976217314263299L;
	// instance variables
	/**
	 * The component the expertise is in
	 */
	DesignProductEntry component;
	/**
	 * The level. This integer attribute is pretty meaningless - we should
	 * figure out what levels we want to be availabe and create a type!
	 */
	int level;


	//constructor called from the XML parsing code
	public AreaExp()
	{
		super();
		component = new DesignProductEntry();
	}
	
	public AreaExp(String nam, AreaExp parnt)
	{
		super();

		component = new DesignProductEntry();
	} 
	
	public RationaleElementType getElementType()
	{
		return RationaleElementType.EXPERTISE;
	}


	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int lev)
	{
		level = lev;
	}
	
	public DesignProductEntry getComponent() {
		return component;
	}

	public void setComponent(DesignProductEntry component) {
		this.component = component;
	}


	/**
	 * Save our area of expertise to the database
	 * @param pid - the ID
	 * @return the ID used in the database
	 */
	public int toDatabase(int pid)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		int ourid = 0;
		
		//find out if this requirement is already in the database
		Statement stmt = null; 
		ResultSet rs = null; 
		
//		System.out.println("Saving to the database");

		try {
			 stmt = conn.createStatement(); 
			if (this.id >= 0)
			{
				
				//now, update it with the new information
				String updateOnt = "UPDATE AreaExp " +
				"SET name = '" + RationaleDB.escape(this.name) + 
				"', des = " +
				 pid + ", " +
				"area  = " +
				 component.getID() + ", " +
				  "level = " + 
				  this.level +
					" WHERE " +
				   "des = " + pid + " AND area = " + 
				   component.getID() + ";";
			  System.out.println(updateOnt);
				stmt.execute(updateOnt);
			}
			else if (pid == 0)
			{
				//now, update it with the new information
				String updateOnt = "UPDATE AreaExp " +
				"SET name = '" + RationaleDB.escape(this.name) + 
				  "level = " + 
				  this.level +
					" WHERE " +
				   "id = " + 
				   this.getID() + ";";
			  System.out.println(updateOnt);
				stmt.execute(updateOnt);
			}
		else 
		{
		
			//now, we have determined that the ontolgy entry is new
			
			String newArgSt = "INSERT INTO AreaExp " +
			   "(name, des, area, level) " +
			   "VALUES ('" +
			   RationaleDB.escape(name) + "', " +
			   pid + ", " +
			   component.getID() + ", " +
			   level + ")"; 

			   System.out.println(newArgSt);
			stmt.execute(newArgSt); 
			
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
		   
		return ourid;	
 
	}	

	/**
	 * Read in an area of expertise from the database
	 * @param id - the database id
	 */
	public void fromDatabase(int id)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		this.id = id;
		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			 stmt = conn.createStatement();
			 String findQuery; 
				 findQuery = "SELECT *  FROM " +
				 "AreaExp where id = " +
				 new Integer(id).toString();
//***			System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery);
			 
			 if (rs.next())
			 {
				name = RationaleDB.decode(rs.getString("name"));
				rs.close();
				this.fromDatabase(name);
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
	
	}		
	public void fromDatabase(String name)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
//***		System.out.println("ont name = " + name);
		
		this.name = name;
		name = RationaleDB.escape(name);
		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			 stmt = conn.createStatement();
			 String findQuery; 
				 findQuery = "SELECT *  FROM " +
				 "AreaExp where name = '" +
				 name + "'";
//***			System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery);
			 
			 if (rs.next())
			 {
				
				id = rs.getInt("id");
				name = RationaleDB.decode(rs.getString("name"));
				level = rs.getInt("level");

				component = new DesignProductEntry();
				component.fromDatabase(rs.getInt("area"));		
			
				rs.close();	
						
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
	
	}	
	
/*	public boolean display()
	{
		Frame lf = new Frame();
		AreaExpGUI ar = new AreaExpGUI(lf, this, null, false);
		ar.show();
		return ar.getCanceled();
	} */
	
	/**
	 * Brings up the editor for the area of expertise
	 * @param disp - points back to the display
	 * @return true if the user cancels out of the editor
	 */
	public boolean display(Display disp)
	{
		EditAreaExp ar = new EditAreaExp(disp, this, null, false);
		String msg = "Edited AreaExp " + this.getName() + " " + ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		return ar.getCanceled(); //can I do this?
		
	}
	
	/**
	 * Create a new area of expertise by bringing up the editor
	 * @param disp - points back to the display
	 * @param parent - the designer the expertise area is for
	 * @return true if the user cancels out of the editor
	 */
	public boolean create(Display disp, RationaleElement parent)
	{
		System.out.println("create AreaExp");
		System.out.println("id = " + parent.getID());

		EditAreaExp ar = new EditAreaExp(disp, this, (Designer) parent, true);
		System.out.println("name in create = " + this.getName());
		((Designer) parent).toDatabase();
		return ar.getCanceled(); //can I do this?
	}
	

	/**
	 * Doesn't do anything yet.
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
