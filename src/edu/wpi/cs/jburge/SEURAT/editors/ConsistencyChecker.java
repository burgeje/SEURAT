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

package edu.wpi.cs.jburge.SEURAT.editors;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException; 

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDBUtil;

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
	
	public ConsistencyChecker()
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
	public ConsistencyChecker(int id, String n, String t)
	{
		this.id = id;
		Name = n;
		Type = t;
	}
	
	/**
	 * This method does the actual check using the data sent to it in the constructor. If the class
	 * was a static you could just pass that information in here and only have the one call.
	 * @return true if the name is not a duplicate
	 */
	public boolean check(boolean showDialog)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null; 
		ResultSet rs = null;
		String query = "";
		boolean flag = false;
		
		try{
			stmt = conn.createStatement();
			query = "SELECT id FROM " + RationaleDBUtil.escapeTableName(Type) + " WHERE name = '"
			+ RationaleDBUtil.escape(Name) + "'";
			rs = stmt.executeQuery(query);
			if(rs.next() && Integer.parseInt(rs.getString(1)) != id)
			{
				if (showDialog) MessageDialog.openError(new Shell(), "Inconsistency", "Name already exists...");
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
	
	/**
	 * Version of the check method that doesn't take a boolean value.  Added for
	 * compatibility.  Simply calls the other method with showDialog set to true.
	 */
	public boolean check() {
		return check(true);
	}
}
