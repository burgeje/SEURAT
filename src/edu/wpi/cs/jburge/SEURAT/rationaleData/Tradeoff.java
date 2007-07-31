/*
 * Tradeoff class - this is used to represent both tradeoffs and co-occurence relationships
 */

package edu.wpi.cs.jburge.SEURAT.rationaleData;

import instrumentation.DataLog;

import java.io.*;
import java.sql.*;
import java.util.Vector;

import org.eclipse.swt.widgets.Display;

import edu.wpi.cs.jburge.SEURAT.editors.EditTradeoff;
import edu.wpi.cs.jburge.SEURAT.inference.TradeoffInferences;

/**
 * Defines the structure of both tradeoff and co-occurence relationships.
 * @author burgeje
 *
 */
public class Tradeoff extends RationaleElement implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3908120519600098105L;
	/**
	 * The first ontology entry in the tradeoff
	 */
	OntEntry ont1;
	/**
	 * The second ontology entry in the tradeoff
	 */
	OntEntry ont2;
	/**
	 * True if a tradeoff, false if a co-occurence
	 */
	boolean tradeoff;
	/**
	 * Is it symetric? In a semetric tradeoff, it would be an error if ont 1
	 * was present without ont2 and vice versa. In a non-symmetric tradeoff, if ont2 was
	 * present without ont1 it would be ok. For example, flexibility vs. time to code -
	 * a more flexible solution might take longer to implement but a solution that takes
	 * longer to implement isn't necessarily more flexible. 
	 */
	boolean symmetric;

	public Tradeoff()
	{
		super();
	} 
	
	/**
	 * Our constructor
	 * @param trade - true if it's a tradeoff
	 */
	public Tradeoff(boolean trade)
	{
		super();
		tradeoff = trade;
	}
	
	public RationaleElementType getElementType()
	{
		if (tradeoff)
		{
			return RationaleElementType.TRADEOFF;
		}
		else
		{
			return RationaleElementType.COOCCURRENCE;
		}
	}
	public OntEntry getOnt1()
	{
		return ont1;
	}
	public OntEntry getOnt2()
	{
		return ont2;
	}
	
	/**
	 * Set our first ontology entry. Increment its reference count.
	 * @param ont - our ontology entry
	 */
	public void setOnt1(OntEntry ont)
	{
		if (ont1 != null)
		{
			ont1.decRefs();
		}
		ont1 = ont;
		ont.incRefs();
	}
	
	/**
	 * Set our second ontology entry. Increment its reference count
	 * @param ont - our ontology entry
	 */
	public void setOnt2(OntEntry ont)
	{
		if (ont2 != null)
		{
			ont2.decRefs();
		}
		ont2 = ont;
		ont.incRefs();
	}
	
	
	public void setTradeoff(boolean trade)
	{
		tradeoff = trade;
	}
	
	public boolean getTradeoff()
	{
		return tradeoff;
	}

	/**
	 * Check to see if two tradeoffs/co-occurences are equivalent to each other.
	 * Is there any risk of this getting called to compare a tradeoff
	 * to a co-occurence? If yes, we have a bug here.
	 * @param trade - the other tradeoff/co-occurence we are checking
	 * @return true if equivalent
	 */
	public boolean equivalent(Tradeoff trade)
	{
		boolean eq = false;
		if ((trade.getOnt1().toString().equals(ont1.toString())) &&
			(trade.getOnt2().toString().equals(ont2.toString())))
			eq = true;
		else if ((trade.getOnt1().toString().equals(ont2.toString())) &&
			(trade.getOnt2().toString().equals(ont1.toString())))
			eq = true;
		return eq;
	}
	
	public boolean getSymmetric()
	{
		return symmetric;	
	}
	
	public void setSymmetric(boolean sym)
	{
		symmetric = sym;
	}

	/**
	 * Write our tradeoff to the database
	 * @return
	 */
	public int toDatabase()
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		int ourid = 0;
		

		
		//find out if this tradeoff is already in the database
		Statement stmt = null; 
		ResultSet rs = null; 
		
//		System.out.println("Saving to the database");

		try {
			 stmt = conn.createStatement(); 
			 		
		 //now we need to find our ontology entries, and that's it!
		 String findQuery3 = "SELECT id FROM OntEntries where name='" +
			RationaleDB.escape(this.ont1.getName()) + "'";
		  rs = stmt.executeQuery(findQuery3); 
//		  ***			System.out.println(findQuery3);
		  int ontid1;
		 if (rs.next())
		 {
			 ontid1 = rs.getInt("id");
//			 rs.close();
		 }
		 else
		 {
		  ontid1 = 0;
		 }
				
		 //now we need to find our ontology entries
		 String findQuery4 = "SELECT id FROM OntEntries where name='" +
			RationaleDB.escape(this.ont2.getName()) + "'";
		  rs = stmt.executeQuery(findQuery4); 
//***			System.out.println(findQuery4);
		  int ontid2;
		 if (rs.next())
		 {
			 ontid2 = rs.getInt("id");
//			 rs.close();
		 }
		 else
		 {
		  ontid2 = 0;
		 }
				
		 String trade;
		 if (tradeoff)
		 {
			 trade = "Tradeoff";
		 }
		 else
		 {
			 trade = "Co-Occurrence";
		 }
		 String sym;
		 sym = new Boolean(symmetric).toString();

/*
			 String findQuery = "SELECT id  FROM tradeoffs where name='" +
				this.name + "'";
			 System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery); 

			if (rs.next())
			{
				System.out.println("already there");
				ourid = rs.getInt("id");
//				rs.close();
 */
 
		if (this.id > 0)
		{				
				//now, update it with the new information
				String updateOnt = "UPDATE Tradeoffs " +
				"SET name = '" +
				 RationaleDB.escape(this.name) + "', " +
				"description = '" +
				 RationaleDB.escape(this.description) + "', " +
				 "type = '" + 
				 trade + "', " +
				 "symmetric = '" +
				 sym + "', " +
				   "ontology1 = " + new Integer(ontid1).toString() + ", " +
				   "ontology2 = " + new Integer(ontid2).toString() +
					" WHERE " +
				   "id = " + this.id + " " ;
//			  System.out.println(updateOnt);
				stmt.execute(updateOnt);
	//			System.out.println("sucessfully added?");
			}
			else 
			{
		
			//now, we have determined that the tradeoff is new
			
			String newArgSt = "INSERT INTO Tradeoffs " +
			   "(name, description, type, symmetric, ontology1, ontology2) " +
			   "VALUES ('" +
			   RationaleDB.escape(this.name) + "', '" +
			   RationaleDB.escape(this.description) + "', '" +
			   trade + "', '" +
			   sym + "', " +
			   new Integer(ontid1).toString() + ", " +
			   new Integer(ontid2).toString() + ")";

//			   System.out.println(newArgSt);
			stmt.execute(newArgSt); 
//			System.out.println("inserted stuff");
			
//			now, we need to get our ID
			String findQuery2 = "SELECT id FROM tradeoffs where name='" +
			   RationaleDB.escape(this.name) + "'";
			rs = stmt.executeQuery(findQuery2); 

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
			
		}

		
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Tradeoff.toDatabase", "SQL Error");
	   }
   	   
	   finally { 
		   RationaleDB.releaseResources(stmt, rs);
		   }
		   
		return ourid;	
 
	}	
	
	/**
	 * Given a name, get the tradeoff from the database.
	 */
	public void fromDatabase(String name)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		this.name = name;
		name = RationaleDB.escape(name);
		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			 stmt = conn.createStatement();
			 String findQuery; 
				 findQuery = "SELECT *  FROM " +
				 "tradeoffs where name = '" +
				 name + "'";
//***			System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery);
			 
			 int ont1ID;
			 int ont2ID;
			 
			 if (rs.next())
			 {
				
				id = rs.getInt("id");
				description = RationaleDB.decode(rs.getString("description"));
				symmetric = rs.getBoolean("symmetric");
				String trade = rs.getString("type");
				if (trade.compareTo("Tradeoff") == 0)
				{
					tradeoff = true;
				}
				else
				{
					tradeoff = false;
				}
				
				ont1ID = rs.getInt("ontology1");
				ont2ID = rs.getInt("ontology2");

				//now, find the ontology entry
				rs.close();
			
				//Now, the arguments against
				String findOntology = "SELECT name FROM OntEntries where " +
				"id = " +
				new Integer(ont1ID).toString(); 
//***				System.out.println(findOntology);
				rs = stmt.executeQuery(findOntology); 

				if (rs.next())
				{
					String ontName = RationaleDB.decode(rs.getString("name"));
					this.ont1 = new OntEntry();
					this.ont1.fromDatabase(ontName);
					
				}
				rs.close();	
				
				//Now, the arguments against
				findOntology = "SELECT name FROM OntEntries where " +
				"id = " +
				new Integer(ont2ID).toString(); 
//***				System.out.println(findOntology);
				rs = stmt.executeQuery(findOntology); 

				if (rs.next())
				{
					String ontName = RationaleDB.decode(rs.getString("name"));
					this.ont2 = new OntEntry();
					this.ont2.fromDatabase(ontName);
					
				}
				rs.close();			
						
		 }

		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Tradeoff.fromDatabase", "SQL error");
	   }
	   finally { 
		   RationaleDB.releaseResources(stmt, rs);
		   }
	
	}	
		

	/**
	 * Display our tradeoff using the tradeoff editor
	 * @param disp - points back to the display
	 * @return true if the user cancels the editor
	 */
	public boolean display(Display disp)
	{
		EditTradeoff ar = new EditTradeoff(disp, this, false);
		String msg = "Edited tradeoff " + this.getName() + " " + ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		return ar.getCanceled(); //can I do this?
		
	}
	
	/**
	 * Create a new tradeoff using the tradeoff editor
	 * @param disp - points back to the display
	 * @param parent - the parent rationale element. We don't
	 * actually have one - this is there so we fit the interface
	 * @return true if the user cancels out
	 */
	public boolean create(Display disp, RationaleElement parent)
	{
		EditTradeoff ar = new EditTradeoff(disp, this, true);
		return ar.getCanceled(); //can I do this?
		
	}
	

	/**
	 * Delete our tradeoff. 
	 * 
	 */
	public boolean delete()
	{
		//no downward dependencies for tradeoffs
		RationaleDB db = RationaleDB.getHandle();
		db.deleteRationaleElement(this);
		return false;
		
	}
	/**
	 * Update the status of the tradeoff (check for violations or lack thereof)
	 * @return the new status
	 */
	public Vector<RationaleStatus> updateStatus()
	{
		//don't update status of the parent element!
		if ((this.name.compareTo("Tradeoffs") == 0) ||
			(this.name.compareTo("Co-occurrences") == 0))
		{
			return null;
		}
		//need to replace with real content!
		Vector<RationaleStatus> newStat = new Vector<RationaleStatus>();
		TradeoffInferences inf = new TradeoffInferences();
		newStat = inf.updateTradeoffStatus(this);
		return newStat;

	}
	
	/**
	 * Make any needed updates when the tradeoff is deleted. Basically, we
	 * just inference over it as normal since the tradeoff itself doesn't have
	 * status.
	 */
	public Vector<RationaleStatus> updateOnDelete()
	{
		return this.updateStatus();
	}
	
}
