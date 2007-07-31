

/*
 * Claim class
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
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


import edu.wpi.cs.jburge.SEURAT.editors.EditClaim;
import edu.wpi.cs.jburge.SEURAT.inference.ClaimInferences;


/**
 * This define the contents of the Claim Rationale Element. Claims map
 * to elements in the Argument Ontology.
 * @author burgeje
 *
 */
public class Claim extends RationaleElement implements Serializable
{
	// class variables
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -849631420441095385L;
	// instance variables
	/**
	 * How important is the claim?
	 */
	Importance importance;
	/**
	 * What direction does the claim go, with respect to the argument ontology? 
	 * For example, are we claiming that an alternative IS or IS NOT scalable?
	 */
	Direction direction;
	/**
	 * Is the claim enabled? True if it is.
	 */
	boolean enabled;
	/**
	 * What ontology entry is the claim referring to?
	 */
	OntEntry	ontology;
	
	/**
	 * Constructor - just calls superclass
	 *
	 */
	public Claim()
	{
		super();
	} 
	
	public RationaleElementType getElementType()
	{
		return RationaleElementType.CLAIM;
	}
	
	public Importance getImportance()
	{
		return importance;
	}
	public void setImportance(Importance imp)
	{
		importance = imp;
	}
	
	public boolean getEnabled()
	{
		return enabled;
	}
	
	public void setDirection(Direction dir)
	{
		direction = dir;
	}
	
	public Direction getDirection()
	{
		return direction;
	}
	
	public OntEntry getOntology()
	{
		return ontology;
	}
	
	/**
	 * Sets the ontology entry associated with the claim. If there already
	 * is an ontology entry, the reference count is decremented before re-setting
	 * the entry. After setting the entry, the new entry's reference count is incremented.
	 * @param ont - the new ontology entry
	 */
	public void setOntology(OntEntry ont)
	{
		if (ontology != null)
		{
			ontology.decRefs();
		}
		ontology = ont;
		ont.incRefs();
	}
	
	/**
	 * Get the importance value for the claim. If the importance is set to default,
	 * the importance needs to come from the ontology entry.
	 * @return the importance
	 */
	public double getImportanceVal()
	{
		if (!enabled)
			return 0.0;
		
		if (importance == Importance.DEFAULT)
		{
//			***			System.out.println("getting importance from ontology");
			return (ontology.getImportance()).getValue();
		}
		else
		{
//			***			System.out.println("getting importance from ourself");
			return importance.getValue();
		}
	}
	
	
	/**
	 * Saves our claim to the database. 
	 * @return the integer ID from the database
	 */
	public int toDatabase()
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
			
			if (inDatabase())
			{
				
				//now we need up update our ontology entry, and that's it!
				String findQuery3 = "SELECT id FROM OntEntries where name='" +
				RationaleDB.escape(this.ontology.getName()) + "'";
				rs = stmt.executeQuery(findQuery3); 
//				***			System.out.println(findQuery3);
				int ontid;
				if (rs.next())
				{
					ontid = rs.getInt("id");
					rs.close();
				}
				else
				{
					ontid = 0;
				}
				
				//now, update it with the new information
				String updateOnt = "UPDATE Claims " +
				"SET name = '" +
				RationaleDB.escape(this.name) + "', " +
				"description = '" +
				RationaleDB.escape(this.description) + "', " +
				"importance = '" + 
				this.importance.toString() + "', " +
				"direction = '" +
				this.direction.toString() + "', " +
				"enabled = '" +
				"true', " +
				"ontology = " + new Integer(ontid).toString() +
				" WHERE " +
				"id = " + this.id + " " ;
//				System.out.println(updateOnt);
				stmt.execute(updateOnt);
			}
			else 
			{
				
				//now, we have determined that the claim is new
				
				String newArgSt = "INSERT INTO Claims " +
				"(name, description, direction, importance, enabled) " +
				"VALUES ('" +
				RationaleDB.escape(this.name) + "', '" +
				RationaleDB.escape(this.description) + "', '" +
				this.direction.toString() + "', '" +
				this.importance.toString() + "', " +
				"'True')";
				
//				***			   System.out.println(newArgSt);
				stmt.execute(newArgSt); 
				
				
				
			}
			//now, we need to get our ID
			String findQuery2 = "SELECT id FROM claims where name='" +
			RationaleDB.escape(this.name) + "'";
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
			
			//now we need up update our ontology entry, and that's it!
			String findQuery3 = "SELECT id FROM OntEntries where name='" +
			RationaleDB.escape(this.ontology.getName()) + "'";
			rs = stmt.executeQuery(findQuery3); 
//			***			System.out.println(findQuery3);
			int ontid;
			if (rs.next())
			{
				ontid = rs.getInt("id");
				rs.close();
			}
			else
			{
				ontid = 0;
			}
			String updateOnt = "UPDATE Claims C " +
			"SET C.ontology = " + new Integer(ontid).toString() +
			" WHERE " +
			"C.id = " + ourid + " " ;
//			***			  System.out.println(updateOnt);
			stmt.execute(updateOnt);
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Claim.toDatabase", "SQL Error");
		}
		
		finally { 
			RationaleDB.releaseResources(stmt, rs);

		}
		
		return ourid;	
		
	}	
	
	/**
	 * Reads in the claim from the database, given its ID
	 * @param id - the claim unique ID
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
			"claims where id = " +
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
			RationaleDB.reportError(ex, "Claim.fromDatabase(int)", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
	}
	/**
	 * Reads in our claim from the database, given its name
	 * @param name - the claim name
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
			"claims where name = '" +
			RationaleDB.escape(name)+ "'";
//			***			System.out.println(findQuery);
			rs = stmt.executeQuery(findQuery);
			
			int ontologyID;
			
			if (rs.next())
			{
				
				id = rs.getInt("id");
				description = RationaleDB.decode(rs.getString("description"));
				enabled = rs.getBoolean("enabled");
				direction = (Direction) Direction.fromString(rs.getString("direction"));
				importance = (Importance) Importance.fromString(rs.getString("importance"));
				ontologyID = rs.getInt("ontology");
				
				//now, find the ontology entry
				rs.close();
				
				//Now, the arguments against
				String findOntology = "SELECT name FROM OntEntries where " +
				"id = " +
				new Integer(ontologyID).toString(); 
//				***				System.out.println(findOntology);
				rs = stmt.executeQuery(findOntology); 
				
				if (rs.next())
				{
					String ontName = RationaleDB.decode(rs.getString("name"));
					ontology = new OntEntry();
					ontology.fromDatabase(ontName);
					
				}
				rs.close();			
				
			}
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Claim.fromDatabase(String)", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
	}	
	
	/**
	 * Create a new Claim by bringing up the claim editor
	 * @param disp - points back to the display
	 * @param parent - points to the argument the claim is about
	 */
	public boolean create(Display disp, RationaleElement parent)
	{
//		System.out.println("creating a new claim?");
		EditClaim ar = new EditClaim(disp, this, true);
		String msg = "Edited claim " + this.getName() + " " + ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		return ar.getCanceled(); //can I do this?
		
	}
	/**
	 * Displays a claim using the claim editor.
	 * @param disp - points back to the display
	 */
	public boolean display(Display disp)
	{
		EditClaim ar = new EditClaim(disp, this, false);
		return ar.getCanceled(); //can I do this?
		
	}
	
	/**
	 * Inferences over the claim and its associated elements to find out
	 * what the related status should be
	 */
	public Vector<RationaleStatus> updateStatus()
	{
		//need to replace with real content!
		ClaimInferences inf = new ClaimInferences();
		Vector<RationaleStatus> newStat = inf.updateClaim( this);
		return newStat;
		
	}
	
	/**
	 * Given a rationale element, extract our claim from the XML
	 * @param claimN - the XML element describing the claim
	 */
	public void fromXML(Element claimN)
	{
		this.fromXML = true;
		RationaleDB db = RationaleDB.getHandle();
		
		//add idref ***from the XML***
		String idref = claimN.getAttribute("id");
		
		//get our name
		name = claimN.getAttribute("name");
		
		//get importance
		importance = Importance.fromString(claimN.getAttribute("importance"));
		
		//get our status
		direction = Direction.fromString(claimN.getAttribute("direction"));
		
		
		Node descN = claimN.getFirstChild();
		//get the description
		//the text is actually the child of the element, odd...
		Node descT = descN.getFirstChild();
		if (descT instanceof Text) 
		{
			Text text = (Text) descT;
			String data = text.getData();
			setDescription(data);
		}
		
		//and last....
		db.addRef(idref, this);	//important to use the ref from the XML file!
		
		Element child = (Element) descN.getNextSibling();
		String nextName;
		
		nextName = child.getNodeName();
		//here we check the type, then process
		if (nextName.compareTo("DR:ontEntry") == 0)
		{
			ontology = new OntEntry();
			
			//now, recursively call ourselves
			ontology.fromXML((Element) child, null);
		}
		else if (nextName.compareTo("ontref") == 0)
		{
			Node childOnt = child.getFirstChild(); //now, get the text
			//decode the reference
			Text refText = (Text) childOnt;
			String ontRef = refText.getData();
			//get the rationale element from the hash table and add it
			ontology = (OntEntry) db.getRef(ontRef);
		}
	}
	
	/**
	 * Check if our element is already in the database. The check is different
	 * if you are reading it in from XML because you can do a query on the name.
	 * Otherwise you can't because you run the risk of the user having changed the
	 * name.
	 * @return true if in the database already
	 */	
	private boolean inDatabase()
	{
		boolean found = false;
		String findQuery = "";
		
		if (fromXML)
		{
			RationaleDB db = RationaleDB.getHandle();
			Connection conn = db.getConnection();
			
			//find out if this element is already in the database
			Statement stmt = null; 
			ResultSet rs = null; 
			
			try {
				stmt = conn.createStatement(); 
				findQuery = "SELECT id FROM claims where name='" +
				this.name + "'";
				System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery); 
				
				if (rs.next())
				{
					int ourid;
					ourid = rs.getInt("id");
					this.id = ourid;
					found = true;
				}
			}
			catch (SQLException ex) {
				// handle any errors 
				RationaleDB.reportError(ex, "Claim.inDatabase", findQuery); 
			}
			finally { 
				RationaleDB.releaseResources(stmt, rs);
			}
		}
		//If we aren't reading it from the XML, just check the ID
		//checking the name like above won't work because the user may 
		//have modified the name!
		else if (this.getID() >= 0)
		{
			found = true;
		}
		return found;
	}

}


