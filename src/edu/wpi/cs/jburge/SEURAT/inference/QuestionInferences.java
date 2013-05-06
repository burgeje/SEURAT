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
/*
 * Created on Jan 15, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.inference;

import java.sql.*;
import java.util.Vector;

import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Performs any necessary inferences when a question is changed.
 * @author jburge
 */
public class QuestionInferences {
	
	/**
	 * Empty constructor
	 */
	public QuestionInferences() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Update the rationale status based on the change.
	 * @param quest - the question to update
	 * @return a vector of any status changes that need to be displayed
	 */
	public Vector<RationaleStatus> updateQuestion(Question quest) {
		Vector<RationaleStatus> newStatus = new Vector<RationaleStatus>();
		quest.toDatabase(quest.getParent(), quest.getPtype());
		
		DecisionInferences decinf = new DecisionInferences();
		AlternativeInferences altinf = new AlternativeInferences();
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
		String findQuery = "";
		try {
			stmt = conn.createStatement();
			
			
			if (quest.getPtype() == RationaleElementType.DECISION)
			{
				findQuery = "SELECT name  FROM " +
				"decisions where " +
				"id = " + quest.getParent();
//				***				 System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery);
				while (rs.next())
				{
					Decision dec = new Decision();
					dec.fromDatabase(RationaleDBUtil.decode(rs.getString("name")));
					Vector<RationaleStatus> results = decinf.updateDecisionStatus(dec);
					if (results != null)
					{
						newStatus.addAll(results);
					}
				}
			}
			else if (quest.getPtype() == RationaleElementType.ALTERNATIVE)
			{
				findQuery = "SELECT name  FROM " +
				"alternatives where " +
				"id = " + quest.getParent();
//				***				System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery);
				while (rs.next())
				{
					Alternative alt = new Alternative();
					alt.fromDatabase(RationaleDBUtil.decode(rs.getString("name")));
					Vector<RationaleStatus> results = altinf.updateAlternative(alt);
					if (results != null)
					{
						newStatus.addAll(results);
					}
				}			 	
			}
			
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "QuestionInferences.updateQuestion",
					findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
			
		}
		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(quest.getID(), quest.getName(), RationaleElementType.QUESTION);
		
		return newStatus;
	}
	
	public Vector<RationaleStatus> updateOnDelete(Question quest) {
		Vector<RationaleStatus> newStatus = new Vector<RationaleStatus>();
		
		DecisionInferences decinf = new DecisionInferences();
		AlternativeInferences altinf = new AlternativeInferences();
		
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 
		String findQuery = null;
		//	boolean error = false;
		try {
			stmt = conn.createStatement();
			
			
			if (quest.getPtype() == RationaleElementType.DECISION)
			{
				findQuery = "SELECT name  FROM " +
				"decisions where " +
				"id = " + quest.getParent();
//				***				 System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery);
				while (rs.next())
				{
					Decision dec = new Decision();
					dec.fromDatabase(RationaleDBUtil.decode(rs.getString("name")));
					Vector<RationaleStatus> results = decinf.updateDecisionStatus(dec);
					if (results != null)
					{
						newStatus.addAll(results);
					}
				}
			}
			else if (quest.getPtype() == RationaleElementType.ALTERNATIVE)
			{
				findQuery = "SELECT name  FROM " +
				"alternatives where " +
				"id = " + quest.getParent();
//				***				System.out.println(findQuery);
				rs = stmt.executeQuery(findQuery);
				while (rs.next())
				{
					Alternative alt = new Alternative();
					alt.fromDatabase(RationaleDBUtil.decode(rs.getString("name")));
					Vector<RationaleStatus> results = altinf.updateAlternative(alt);
					if (results != null)
					{
						newStatus.addAll(results);
					}
				}			 	
			}
			
			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "QuestionInferences.updateOnDelete",
					findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
		UpdateManager manager = UpdateManager.getHandle();
		manager.addUpdate(quest.getID(), quest.getName(), RationaleElementType.QUESTION);
		
		return newStatus;
	}
	
	
}
