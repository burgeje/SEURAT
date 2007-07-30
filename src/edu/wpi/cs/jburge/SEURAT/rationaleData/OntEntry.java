

/*
 * OntEntry class
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import edu.wpi.cs.jburge.SEURAT.editors.EditOntEntry;
import edu.wpi.cs.jburge.SEURAT.inference.OntologyInferences;

/**
 * This defines the contents of an element in the Argument Ontology. 
 * Ontology entries are typically types of non-functional requirements (like
 * scalability and security).
 * @author burgeje
 *
 */
public class OntEntry extends RationaleElement implements Serializable 
{
	// class variables

	/**
	 * 
	 */
	private static final long serialVersionUID = 8242954644657215366L;
	// instance variables
	/**
	 * How many other elements refer to this entry
	 */
	int nRefs;
	/**
	 * How important is this entry (NFR)
	 */
	Importance importance;
	/**
	 * What level in the hierarchy is this element? An item can be in multiple
	 * places so the level ends up being the level of the last place it
	 * was added... probably not what we want!
	 */
	int level;
	/**
	 * Our parent items. The tree works like a doubly-linked list where children
	 * and parents point to each other
	 */
	Vector<OntEntry> parents;
	/**
	 * Our child entries (those lower than us in the hierarchy)
	 */
	Vector<OntEntry> children;

	/**
	 * constructor called from the XML parsing code
	 */
	public OntEntry()
	{
		super();
		nRefs = 0;
		children = new Vector<OntEntry>();
		parents = new Vector<OntEntry>();		
	}

	/**
	 * The standard constructor
	 * @param nam - the ontology entry name
	 * @param parnt - our parent
	 */
	public OntEntry(String nam, OntEntry parnt)
	{
		super();
		nRefs = 0;
		children = new Vector<OntEntry>();
		parents = new Vector<OntEntry>();
		parents.addElement(parnt);
		name = nam;
//		System.out.println(description);
		//currently the level is somewhat arbitrarily set based on the
		//initial parent. 
		if (parnt != null)
			level = parnt.getLevel() + 1;
		else
			level = 0;
		importance = Importance.MODERATE;
	} 
	
	public RationaleElementType getElementType()
	{
		return RationaleElementType.ONTENTRY;
	}

	/**
	 * Create a new ontology entry and add it to this one as a child
	 * @param nam - the new entry's name
	 * @return our new child
	 */
	public OntEntry addChild(String nam)
	{
		OntEntry newEntry = new OntEntry(nam, this);
		newEntry.setLevel(level + 1);
		children.addElement(newEntry);
		this.incRefs();
		return newEntry;
	}

	/**
	 * Add an existing ontology entry as a child
	 * @param child
	 */
	public void addChild(OntEntry child)
	{
		children.addElement(child);
		this.incRefs();
		child.addParent(this);
	}
	
	/**
	 * Remove a child from this entry. The number of references count 
	 * is decremented.
	 * @param child
	 */
	public void removeChild(OntEntry child)
	{
		children.removeElement(child);
		this.decRefs();
	}
	
	public void addParent(OntEntry parnt)
	{
		parents.addElement(parnt);
	}

	public Importance getImportance()
	{
		return importance;
	}
	
	public void setImportance(Importance imp)
	{
		importance = imp;
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
	 * save the ontology entry to the database.
	 * @param pid - the parent ID
	 * @return the ID of our entry
	 */
	public int toDatabase(int pid)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		int ourid = 0;
		
		//find out if this ontology entry is already in the database
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
				String updateOnt = "UPDATE OntEntries " +
				"SET name = '" +
				 RationaleDB.escape(this.name) + "', " +
				"description = '" +
				 RationaleDB.escape(this.description) + "', " +
				 "importance = '" + 
				 this.importance.toString() + "'" +
					" WHERE " +
				   "id = " + this.id + " " ;
//			  System.out.println(updateOnt);
				stmt.execute(updateOnt);
			}
		else 
		{
		
			//now, we have determined that the ontolgy entry is new
			
			String newArgSt = "INSERT INTO OntEntries " +
			   "(name, description, importance) " +
			   "VALUES ('" +
			   RationaleDB.escape(this.name) + "', '" +
			   RationaleDB.escape(this.description) + "', '" +
			   this.importance.toString() + "')"; 

			   System.out.println(newArgSt);
			stmt.execute(newArgSt); 
			
		
			
		}
			//now, we need to get our ID
			String findQuery2 = "SELECT id FROM OntEntries where name='" +
			   this.name + "'";
			rs = stmt.executeQuery(findQuery2); 
//***			System.out.println(findQuery2);

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
				String findQuery3 = "SELECT * from OntRelationships WHERE " +
				   "parent = " + new Integer(pid).toString() +
				   " and child = " + new Integer(ourid).toString();
//***				   System.out.println(findQuery3);
				   rs = stmt.executeQuery(findQuery3);
				if (rs.next())
				{
					rs.close();
				}
				else
				{
					String insertRel = "INSERT INTO OntRelationships (parent, child) " +
					   "VALUES (" +
					   new Integer(pid).toString() + ", " +
					   new Integer(ourid).toString() + ")";
//***					System.out.println(insertRel);
					stmt.execute(insertRel);
				}
			} //checking parent
			
			//now, decode our children
			Enumeration kids = children.elements();
			while (kids.hasMoreElements())
			{
				OntEntry kid = (OntEntry) kids.nextElement();
				kid.toDatabase(ourid);
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
	 * Save one read from the XML into the database. This is redundant.
	 * @param pid - the parent ID
	 * @return our ID
	 */
	public int toDatabaseXML(int pid)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		int ourid = 0;
		
		//find out if this ontology entry is already in the database
		Statement stmt = null; 
		ResultSet rs = null; 
		
//		System.out.println("Saving to the database");

		try {
			 stmt = conn.createStatement(); 
	
			 String findQuery = "SELECT id  FROM OntEntries where name='" +
				this.name + "'";
//***			 System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery); 

			if (rs.next())
			{
				//System.out.println("already there");
				ourid = rs.getInt("id");
				this.id = ourid;

				
				//now, update it with the new information
				String updateOnt = "UPDATE OntEntries " +
				"SET name = '" +
				 RationaleDB.escape(this.name) + "', " +
				"description = '" +
				 RationaleDB.escape(this.description) + "', " +
				 "importance = '" + 
				 this.importance.toString() + "'" +
					" WHERE " +
				   "id = " + this.id + " " ;
//			  System.out.println(updateOnt);
				stmt.execute(updateOnt);
			}
			else 
			{

				//now, we have determined that the ontolgy entry is new

				String newArgSt = "INSERT INTO OntEntries " +
				"(name, description, importance) " +
				"VALUES ('" +
				RationaleDB.escape(this.name) + "', '" +
				RationaleDB.escape(this.description) + "', '" +
				this.importance.toString() + "')"; 

				System.out.println(newArgSt);
				stmt.execute(newArgSt); 



			}
			//now, we need to get our ID
			String findQuery2 = "SELECT id FROM OntEntries where name='" +
			   this.name + "'";
			rs = stmt.executeQuery(findQuery2); 
//***			System.out.println(findQuery2);

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
				String findQuery3 = "SELECT * from OntRelationships WHERE " +
				   "parent = " + new Integer(pid).toString() +
				   " and child = " + new Integer(ourid).toString();
//***				   System.out.println(findQuery3);
				   rs = stmt.executeQuery(findQuery3);
				if (rs.next())
				{
					rs.close();
				}
				else
				{
					String insertRel = "INSERT INTO OntRelationships (parent, child) " +
					   "VALUES (" +
					   new Integer(pid).toString() + ", " +
					   new Integer(ourid).toString() + ")";
//***					System.out.println(insertRel);
					stmt.execute(insertRel);
				}
			} //checking parent
			
			//now, decode our children
			Enumeration kids = children.elements();
			while (kids.hasMoreElements())
			{
				OntEntry kid = (OntEntry) kids.nextElement();
				kid.toDatabase(ourid);
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
	 * Read in the ontology entry from the database, given its ID
	 * @param id - the id
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
				 "OntEntries where id = " +
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
	
	/**
	 * Read in the entry from the database, given its name
	 * @param name - the name
	 */
	public void fromDatabase(String name)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

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
		
//***		System.out.println("ont name = " + name);
		
		this.name = name;
		name = RationaleDB.escape(name);
		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			 stmt = conn.createStatement();
			 String findQuery; 
				 findQuery = "SELECT *  FROM " +
				 "OntEntries where name = '" +
				 name + "'";
//***			System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery);
			 
			 if (rs.next())
			 {
				
				id = rs.getInt("id");
				description = rs.getString("description");
				importance = (Importance) Importance.fromString(rs.getString("importance"));

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
		OntEntryGUI ar = new OntEntryGUI(lf, this, null, false);
		ar.show();
		return ar.getCanceled();
	} */
	
	public boolean display(Display disp)
	{
		EditOntEntry ar = new EditOntEntry(disp, this, null, false);
		String msg = "Edited ontEntry " + this.getName() + " " + ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		return ar.getCanceled(); //can I do this?
		
	}
	
	public boolean create(Display disp, RationaleElement parent)
	{
		System.out.println("create ontentry");
		System.out.println("id = " + parent.getID());
		this.addParent((OntEntry) parent);

		EditOntEntry ar = new EditOntEntry(disp, this, (OntEntry) parent, true);
		System.out.println("name in create = " + this.getName());
		return ar.getCanceled(); //can I do this?
	}
	
	public Vector<RationaleStatus> updateStatus()
	{
		//need to replace with real content!
		OntologyInferences inf = new OntologyInferences();
		Vector<RationaleStatus> newStat = inf.updateOntEntry( this);
		
		return newStat;

	}
	
	public void fromXML(Element ontE, OntEntry parent)
	{
		RationaleDB db = RationaleDB.getHandle();
		
		//add idref ***from the XML***
		String idref = ontE.getAttribute("id");

		
		//get our name
		name = ontE.getAttribute("name");

		//set our parent
		if (parent != null)
			addParent(parent);
		
		//get importance
		importance = Importance.fromString(ontE.getAttribute("importance"));
		
		//get description
		Node  childE = ontE.getFirstChild();
	
		//the text is actually the child of the element, odd...
		Node descN = childE.getFirstChild();
		if (descN instanceof Text) 
		{
		  Text text = (Text) descN;
		  String data = text.getData();
		  setDescription(data);
		}
/*		else
		{
			System.out.println("Couldn't find description node");
		} */
		
		//now get all our children
		childE = (Element) childE.getNextSibling();
		
		while (childE != null)
		{
			//don't forget to increase our own reference count!
			incRefs();
			
			//now, this will be a sub-entry, so we need to get its child
			Node childOnt = childE.getFirstChild();
			String childName = childOnt.getNodeName();
			//now, check to see if this is a reference, not a new definition
			if (childName.compareTo("ontref") == 0)
			{
				childOnt = childOnt.getFirstChild(); //now, get the text
				System.out.println(childName);
				//decode the reference
				Text refText = (Text) childOnt;
				String ontRef = refText.getData();
				//get the rationale element from the hash table and add it
				OntEntry refEntry = (OntEntry) db.getRef(ontRef);
				if (refEntry == null)
					System.out.println("error - refEntry null");
				else
				{
					System.out.println("Saving rationale entry");
					System.out.println(refEntry.toString());
					addChild(refEntry); 
				}
			}
			else
			{
				OntEntry child = new OntEntry();

				addChild(child); //add our new kid
			
				//now, recursively call ourselves
				child.fromXML((Element) childOnt, this);

			}
			
			//finally, go to the next child
			childE = (Element) childE.getNextSibling();
	
		}
		
//		System.out.println(idref);
		db.addRef(idref, this);	//important to use the ref from the XML file!	
		
		
	}
	
}
