package edu.wpi.cs.jburge.SEURAT.editors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException; 

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;

/**
 * It is a requirement that each element of a given type have a unique name. The name is used as
 * a primary key for the database tables so if duplicate names are entered very bad things happen. This
 * is a utility class that checks to see if an item with a given name is already in the database.
 * <p>
 * This probably should have been implemented as a static class that did the database check to
 * avoid the overhead of creating all these new objects each time a comparision needs to be done.
 * Then again, the object creation overhead is probably negligible compared to the DB query.
 * @author yue
 *
 */
public class ConsistencyChecker {
	private int id;
	
	/**
	 * Name being checked
	 */
	private String Name; // name of the type
	/**
	 * The database table being examined
	 */
	private String Type; // includes decision, alternative,
						// requirements, and arguments
	
	ConsistencyChecker()
	{
		Name = "";
		Type = "";
	}
	
	/**
	 * Constructor
	 * @param id - the ID in the database
	 * @param n - the name of the item
	 * @param t - the type of the item - this needs to match the database table name. Not a good design!
	 */
	ConsistencyChecker(int id, String n, String t)
	{
		this.id = id;
		Name = n;
		Type = t;
	}
	
	/**
	 * This method does the actual check using the data sent to it in the constructor. If the class
	 * was a static you could just pass that information in here and only have the one call.
	 * @return
	 */
	public boolean check()
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null; 
		ResultSet rs = null;
		String query = "";
		boolean flag = false;
				
		try{
			stmt = conn.createStatement();
			query = "SELECT id FROM " + Type + " WHERE name = '"
					+ Name + "'";
			rs = stmt.executeQuery(query);
			if(rs.next() && Integer.parseInt(rs.getString(1)) != id)
			{
				MessageDialog.openError(new Shell(), "Inconsistency", "Name already exists...");
				flag = false;
			}
			else
			{
				flag = true;
			}
		}catch(SQLException ex)
		{
			RationaleDB.reportError(ex,"Error in " + Name + ".toDatabase", "");
		}
		
		return flag;
	}
}
