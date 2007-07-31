

/*
 * Assumption class
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

import edu.wpi.cs.jburge.SEURAT.editors.EditAssumption;
import edu.wpi.cs.jburge.SEURAT.inference.AssumptionInferences;

/**
 * Stores information describing an Assumption in the rationale
 * @author burgeje
 *
 */
public class Assumption extends RationaleElement implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4712825822248440225L;
	// instance variables
	/**
	 * How important is the assumption?
	 */
	Importance importance;
	
	public Assumption()
	{
		super();

	} 
	

	public RationaleElementType getElementType()
	{
		return RationaleElementType.ASSUMPTION;
	}
	/* Why did we need to override things we should inherit?
	public void setDescription(String desc)
	{
		description = desc;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	*/
	
	/**
	 * Get our importance. If the assumption is disabled, return zero.
	 */
	public double getImportanceVal()
	{
		if (enabled)
		{
			return importance.getValue(); //need to store it, really...
		}
		else
		{
//			System.out.println("Assumption disabled");
			return 0.0;
		}
	}
	
	
	/**
	 * Save the assumption to the database
	 * @return the database ID
	 */
	public int toDatabase()
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		int ourid = 0;
		
		Statement stmt = null; 
		ResultSet rs = null; 
		
		try {
			 stmt = conn.createStatement(); 
			String enabledStr;
			if (enabled)
				enabledStr = "True";
			else
				enabledStr = "False";
				
			if (inDatabase())
			{
//***				System.out.println("already there");
//				ourid = rs.getInt("id");
				String updateAssump = "UPDATE assumptions A " +
					"SET A.name = '" + RationaleDB.escape(this.name) +
					"', A.description = '" + RationaleDB.escape(this.description) +
					"', A.importance = '" + this.importance.toString() +
					"', A.enabled = '" + enabledStr +
					"' WHERE " +
					"A.id = " + this.id + " ";
//				System.out.println(updateAssump);
			 	stmt.execute(updateAssump);
			}
		else 
		{
		
			//now, we have determined that the assumption is new
			
			String newArgSt = "INSERT INTO Assumptions " +
			   "(name, description, importance, enabled) " +
			   "VALUES ('" +
			   RationaleDB.escape(this.name) + "', '" +
			   RationaleDB.escape(this.description) + "', '" +
			   "Moderate" + "', " +
			   "'" + enabledStr + "')";

//***			   System.out.println(newArgSt);
			stmt.execute(newArgSt); 
			
		
			
		}
			//now, we need to get our ID
			String findQuery2 = "SELECT id FROM assumptions where name='" +
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
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Assumption.toDatabase", "SQL Error");
	   }
   	   
	   finally { 
		   RationaleDB.releaseResources(stmt, rs);
		   }
		   
		return ourid;	
 
	}	

	/**
	 * Read in the assumption from the database
	 * @param id - the id in the database
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
				 "assumptions where id = " +
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
			RationaleDB.reportError(ex, "Assumption.fromDatabase(int)", findQuery);
	   }
	   finally { 
		   RationaleDB.releaseResources(stmt, rs);
		   }
	
	}		
	
	/**
	 * Read in the assumption from the database, given its name
	 * @param name - the assumption name
	 */
	public void fromDatabase(String name)
	{
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		this.name = name;
		name = RationaleDB.escape(name);
		 String findQuery = ""; 		
		Statement stmt = null; 
		ResultSet rs = null; 
		try {
			 stmt = conn.createStatement();

				 findQuery = "SELECT *  FROM " +
				 "assumptions where name = '" +
				 name + "'";
//***			System.out.println(findQuery);
			 rs = stmt.executeQuery(findQuery);
			 
			 if (rs.next())
			 {
				
				id = rs.getInt("id");
				description = RationaleDB.decode(rs.getString("description"));
				enabled = rs.getBoolean("enabled");
				importance = (Importance) Importance.fromString(rs.getString("importance"));

				rs.close();
			
	
						
		 }

		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Assumption.fromDatabase(String)", findQuery);
	   }
	   finally { 
		   RationaleDB.releaseResources(stmt, rs);
		
		   }
	
	}	
/*	
	public boolean create(RationaleElement parent)
	{
		System.out.println("create decision");
		Frame lf = new Frame();
		AssumptionGUI ar = new AssumptionGUI(lf,  this);
		ar.show();
		return ar.getCanceled();
	}
	*/
	/**
	 * Create a new assumption by displaying the assumption editor
	 * @param disp - points to the display
	 * @param parent - the parent of the assumption (an argument)
	 * @return true if the user cancels out of the editor
	 */
	public boolean create(Display disp, RationaleElement parent)
	{
//		System.out.println("create assumption");

		EditAssumption ar = new EditAssumption(disp, this, true);
		String msg = "Edited assumption " + this.getName() + " " + ar.getCanceled();
		DataLog d = DataLog.getHandle();
		d.writeData(msg);
		return ar.getCanceled(); //can I do this?
	}
/*	public boolean display()
	{
		Frame lf = new Frame();
		AssumptionGUI ar = new AssumptionGUI(lf, this);
		ar.show();
		return ar.getCanceled();
	}
	*/

	/**
	 * Displays an assumption by bringing up the editing display.
	 * @param disp - points to the display
	 * @return true if the user cancels out of the editor
	 */
	public boolean display(Display disp)
	{
		EditAssumption ar = new EditAssumption(disp, this, false);
		return ar.getCanceled(); //can I do this?
		
	}
	
	/**
	 * Inference over the assumption to discover the current status
	 * @return any status changes that should be displayed
	 */
	public Vector<RationaleStatus> updateStatus()
	{
		AssumptionInferences inf = new AssumptionInferences();
		Vector<RationaleStatus> newStat = inf.updateAssumption( this);
		return newStat;

	}
	
	/**
	 * Read in an assumption from XML
	 * @param asN - the XML element
	 */
	public void fromXML(Element asN)
	{
		this.fromXML = true;
		
		RationaleDB db = RationaleDB.getHandle();
		
		//add idref ***from the XML***
		String idref = asN.getAttribute("id");
	
		//get our name
		name = asN.getAttribute("name");

		Node descN = asN.getFirstChild();
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
			
			//find out if this alternative is already in the database
			Statement stmt = null; 
			ResultSet rs = null; 
			
			try {
				stmt = conn.createStatement(); 
				findQuery = "SELECT id FROM assumptions where name='" +
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
				RationaleDB.reportError(ex, "Assumptions.inDatabase", findQuery); 
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
