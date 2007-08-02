/*
 * DesignProductEntry class
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


import edu.wpi.cs.jburge.SEURAT.editors.EditDesignProductEntry;

public class DesignProductEntry extends RationaleElement implements Serializable 
{
	// class variables
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1715818196095398724L;
	// instance variables
	
	/**
	 * The number of references to this design component
	 */
	int nRefs;
	/**
	 * The level in the hierarchy this component is. This is a bit
	 * flakey right now since the level will be set to the level of it's most
	 * recent parent plus one. 
	 */
	int level;
	/**
	 * The parents of this element. Since it is a vector, it must be
	 * possible to have an element appear in multiple places. Not sure this is 
	 * correct or if it is an artifact from the Argument Ontology.
	 */
	Vector<DesignProductEntry> parents;
	/**
	 * The sub-elements in the component tree
	 */
	Vector<DesignProductEntry> children;
	
	/**
	 * The constructor called from the XML parsing code
	 */
	public DesignProductEntry()
	{
		super();
		nRefs = 0;
		children = new Vector<DesignProductEntry>();
		parents = new Vector<DesignProductEntry>();		
	}
	
	/**
	 * The constructor
	 * @param nam - the component name
	 * @param parnt - the component's parent
	 */
	public DesignProductEntry(String nam, DesignProductEntry parnt)
	{
		super();
		nRefs = 0;
		children = new Vector<DesignProductEntry>();
		parents = new Vector<DesignProductEntry>();
		parents.addElement(parnt);
		name = nam;
//		System.out.println(description);
		//currently the level is somewhat arbitrarily set based on the
		//initial parent. 
		if (parnt != null)
			level = parnt.getLevel() + 1;
		else
			level = 0;
		
	} 
	
	public RationaleElementType getElementType()
	{
		return RationaleElementType.DESIGNPRODUCTENTRY;
	}
	
	/**
	 * Adds a child element. The element is new and needs to be created. 
	 * Adding it also incrementing the parent's number of references. The child's
	 * level is set to that of the parent plus one.
	 * @param nam - the name of the child.
	 * @return
	 */
	public DesignProductEntry addChild(String nam)
	{
		DesignProductEntry newEntry = new DesignProductEntry(nam, this);
		newEntry.setLevel(level + 1);
		children.addElement(newEntry);
		this.incRefs();
		return newEntry;
	}
	
	/**
	 * Adds a child entry, given a child element. It doesn't need to be
	 * created but this element (the parent) needs its reference count
	 * incremented. The child's level is set to the parents plus one.
	 * @param child
	 */
	public void addChild(DesignProductEntry child)
	{
		children.addElement(child);
		this.incRefs();
		child.addParent(this);
	}
	
	/**
	 * Remove the child from this component and decrement the number of references
	 * to the current component.
	 * @param child
	 */
	public void removeChild(DesignProductEntry child)
	{
		children.removeElement(child);
		this.decRefs();
	}
	
	/**
	 * Adds a parent entry. The hierarchy basically acts like a form of
	 * doubly linked list where parents and children point towards each other
	 * so you can navigate in either direction. 
	 * @param parnt
	 */
	public void addParent(DesignProductEntry parnt)
	{
		parents.addElement(parnt);
	}
	
	
	public int getLevel()
	{
		return level;
	}
	
	public void setLevel(int lev)
	{
		level = lev;
	}
	
	public Vector getChildren()
	{
		return children;
	}
	
	public Vector getParents()
	{
		return parents;
	}
	
	public void incRefs()
	{
		nRefs += 1;
	}
	
	public void decRefs()
	{
		nRefs -=1;
	}
	
	public int getRefs()
	{
		return nRefs;
	}
	
	
	/**
	 * Save our component to the database
	 * @param pid - the ID of our parent
	 * @return the unique ID used in the database
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
			/*
			 String findQuery = "SELECT id  FROM OntEntries where name='" +
			 this.name + "'";
			 //***			 System.out.println(findQuery);
			  rs = stmt.executeQuery(findQuery); 
			  
			  if (rs.next())
			  {
			  System.out.println("already there");
			  ourid = rs.getInt("id");
			  */
			if (this.id >= 0)
			{
				
				//now, update it with the new information
				String updateOnt = "UPDATE DesignComponents " +
				"SET name = '" +
				RationaleDB.escape(this.name) + "', " +
				"description = '" +
				RationaleDB.escape(this.description) + "'" +
				" WHERE " +
				"id = " + this.id + " " ;
//				System.out.println(updateOnt);
				stmt.execute(updateOnt);
			}
			else 
			{
				
				//now, we have determined that the ontolgy entry is new
				
				String newArgSt = "INSERT INTO DesignComponents " +
				"(name, description) " +
				"VALUES ('" +
				RationaleDB.escape(this.name) + "', '" +
				RationaleDB.escape(this.description) +  "')"; 
				
//				***			   System.out.println(newArgSt);
				stmt.execute(newArgSt); 
				
				
				
			}
			//now, we need to get our ID
			String findQuery2 = "SELECT id FROM DesignComponents where name='" +
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
			
			//if the parent ID is not zero, then update the parent-child relationship
			if (pid > 0)
			{
				String findQuery3 = "SELECT * from DesignComponentRelationships WHERE " +
				"parent = " + new Integer(pid).toString() +
				" and child = " + new Integer(ourid).toString();
//				***				   System.out.println(findQuery3);
				rs = stmt.executeQuery(findQuery3);
				if (rs.next())
				{
					rs.close();
				}
				else
				{
					String insertRel = "INSERT INTO DesignComponentRelationships (parent, child) " +
					"VALUES (" +
					new Integer(pid).toString() + ", " +
					new Integer(ourid).toString() + ")";
					System.out.println(insertRel);
					stmt.execute(insertRel);
				}
			} //checking parent
			
			//now, decode our children
			Enumeration kids = children.elements();
			while (kids.hasMoreElements())
			{
				DesignProductEntry kid = (DesignProductEntry) kids.nextElement();
				kid.toDatabase(ourid);
			}
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "DesignProductEntry.toDatabase", "SQL Error");
		}
		
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
		return ourid;	
		
	}	
	
	/**
	 * Read our item from the database. 
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
			"DesignComponents where id = " +
			new Integer(id).toString();
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next())
			{
				name = RationaleDB.decode(rs.getString("name"));
				rs.close();
				this.fromDatabase(name);
			}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "DesignProductEntry(int)", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
	}		
	/**
	 * Gets our component from the database, given its name
	 * @param name - our name
	 */
	public void fromDatabase(String name)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		String findQuery = ""; 
		//if this is coming from a textual claim descriptor we will want to 
		//strip out the "IS" and "NOT" from the name.
		
		if (name.startsWith("IS "))
		{
			name = name.substring(3); //strip off "IS "
		}
		else if (name.startsWith("NOT "))
		{
			name = name.substring(4);
		}
		
//		***		System.out.println("ont name = " + name);
		
		this.name = name;
		name = RationaleDB.escape(name);
		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			stmt = conn.createStatement();
			findQuery = "SELECT *  FROM " +
			"DesignComponents where name = '" +
			name + "'";
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next())
			{
				
				id = rs.getInt("id");
				description = rs.getString("description");
				rs.close();	
				
			}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "DesignProductEntry.fromDatabase(String)", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
	}	
	
	
	/**
	 * Displays our component by bringing up the DesignProduct Entry GUI
	 * @param disp - points to our display
	 * @return true if the user cancels
	 */
	public boolean display(Display disp)
	{
		EditDesignProductEntry ar = new EditDesignProductEntry(disp, this, null, false);
		String msg = "Edited DesignProductEntry " + this.getName() + " " + ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		return ar.getCanceled(); //can I do this?
		
	}
	
	/**
	 * Create a new component using the DesignProductEntry GUI
	 * @param disp - points to the display
	 * @param parent - our parent element
	 * @return true if the user cancels
	 */
	public boolean create(Display disp, RationaleElement parent)
	{
//		System.out.println("create decision");
		this.addParent((DesignProductEntry) parent);
		System.out.println("product parent = " + parent.getID());
		EditDesignProductEntry ar = new EditDesignProductEntry(disp, this, (DesignProductEntry) parent, true);
		return ar.getCanceled(); //can I do this?
	}
	
	/**
	 * If we affected status, here's where the effects would be determined.
	 */
	public Vector<RationaleStatus> updateStatus()
	{
		//need to replace with real content!
		//currently no inferences
		Vector<RationaleStatus> newStat = null;
		
		return newStat;
		
	}
	
}
