package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import SEURAT.events.RationaleElementUpdateEventGenerator;
import SEURAT.events.RationaleUpdateEvent;

public class Tactic extends RationaleElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3999306799384937592L;
	
	private OntEntry category;
	
	private int time_behavior;
	
	private Vector<TacticPattern> patterns;
	
	private Vector<OntEntry> badEffects;
	
	private boolean fromXML = false;
	
	
	public static final String[] behaviorCategories = {"undefined", "Add new timing, explicit", "Add new timing, implicit", "Change existing timing, explicit", "Change existing timing, implicit"};
	
	private RationaleElementUpdateEventGenerator<Tactic> m_eventGenerator = 
		new RationaleElementUpdateEventGenerator<Tactic>(this);
	
	public Tactic(){
		super();
		category = null;
		time_behavior = 0;
		patterns = new Vector<TacticPattern>();
		badEffects = new Vector<OntEntry>();
	}
	
	public RationaleElementType getElementType(){
		return RationaleElementType.TACTIC;
	}
	
	
	public int getTime_behavior() {
		return time_behavior;
	}

	public void setTime_behavior(int time_behavior) {
		this.time_behavior = time_behavior;
	}

	public OntEntry getCategory(){
		return category;
	}
	
	public boolean setCategory(OntEntry entry){
		if (entry == null) return false;
		if (entry.getChildren().isEmpty()){
			category = entry;
			return true;
		}
		return false;
	}
	
	public Vector<OntEntry> getBadEffects(){
		return badEffects;
	}
	
	public void addBadEffect(OntEntry entry){
		badEffects.add(entry);
	}
	
	public Vector<TacticPattern> getPatterns(){
		return patterns;
	}
	
	public void addPattern(TacticPattern pattern){
		patterns.add(pattern);
	}
	
	public boolean deleteBadEffect(String entryName){
		if (entryName == null) return false;
		for (int i = 0; i < badEffects.size(); i++){
			OntEntry cur = badEffects.get(i);
			if(cur.getName().equals(entryName)){
				badEffects.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public void fromDatabase(String name){
		this.name = name;
		name = RationaleDBUtil.escape(name);
		RationaleDB db = RationaleDB.getHandle();
		
		ResultSet rs = null;
		
		String findQuery = "";
		
		Statement stmt = null;
		
		Connection conn = db.getConnection();
		
		try {
			stmt = conn.createStatement();
			
			findQuery = "SELECT * FROM tactics WHERE name = '" + name + "'";
			
			rs = stmt.executeQuery(findQuery);
			
			int categoryID = -1;
			
			if (rs.next()){
				id = rs.getInt("id");
				description = RationaleDBUtil.decode(rs.getString("description"));
				categoryID = rs.getInt("quality");
				time_behavior = rs.getInt("time_beh");
			}
			rs.close();
			
			if (categoryID != -1){
				category = new OntEntry();
				category.fromDatabase(categoryID);
			}
			
			//Get Tactic_Patterns
			findQuery = "SELECT * FROM TACTIC_PATTERN WHERE tactic_id = " + id;
			rs = stmt.executeQuery(findQuery);
			while (rs.next()){
				TacticPattern tp = new TacticPattern();
				tp.fromDatabase(rs.getInt("id"));
				patterns.add(tp);
			}
			
			//Get negative quality attributes
			findQuery = "SELECT * FROM TACTIC_NEGONTENTRIES	WHERE tactic_id = " + id;
			rs = stmt.executeQuery(findQuery);
			
			while (rs.next()){
				OntEntry negQuality = new OntEntry();
				negQuality.fromDatabase(rs.getInt("ont_id"));
				badEffects.add(negQuality);
			}
			
		} catch (SQLException e){
			RationaleDB.reportError(e, "Tactic.fromDatabase(String)", "Tactic:FromDatabase");
			e.printStackTrace();
		} finally{
			RationaleDB.releaseResources(stmt,rs);
		}
	}
	
	public void fromDatabase (int id){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		this.id = id;
		
		Statement stmt = null; 
		ResultSet rs = null; 
		String findQuery = ""; 
		try {
			stmt = conn.createStatement();
			findQuery = "SELECT name from TACTICS where id = " + id;
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next()){
				name = RationaleDBUtil.decode(rs.getString("name"));
				rs.close();
				fromDatabase(name);
			}
		} catch (SQLException e){
			e.printStackTrace();
		} finally{
			RationaleDB.releaseResources(stmt, rs);
		}
	}
	
	private boolean inDatabase(){
		if (fromXML){
			RationaleDB db = RationaleDB.getHandle();

			ResultSet rs = null;

			String findQuery = "";

			Statement stmt = null;

			Connection conn = db.getConnection();
			
			try {
				stmt = conn.createStatement();
				findQuery = "SELECT * FROM TACTICS WHERE name = '" + RationaleDBUtil.escape(name) + "'";
				rs = stmt.executeQuery(findQuery);
				
				if (rs.next()){
					return true;
				}
				return false;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				RationaleDB.releaseResources(stmt, rs);
			}
			
		}
		else{
			if (id != -1) return true;
			
		}
		return false;
	}
	
	public void toDatabase(){
		RationaleUpdateEvent l_updateEvent;
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		
		try {
			stmt = conn.createStatement();
			String dm = "";
			
			if (inDatabase()){
				dm = "UPDATE TACTICS t " +
				"SET t.name = '" + RationaleDBUtil.escape(name) + "', "
				+ "t.quality = " + category.getID() + ", "
				+ "t.description = '" + RationaleDBUtil.escape(description) + "', "
				+ "t.time_beh = " + time_behavior
				+ " WHERE t.id = " + id;
				stmt.execute(dm);
				
				//This does not delete the tactic-pattern!
				
				dm = "DELETE FROM TACTIC_NEGONTENTRIES WHERE tactic_id = " + id;
				stmt.execute(dm);
				
				l_updateEvent = m_eventGenerator.MakeUpdated();
			}
			else {
				id = RationaleDB.findAvailableID("TACTICS");
				dm = "INSERT INTO TACTICS" + 
				" (id, name, quality, description, time_beh)"
				+ " VALUES ("
				+ id + ", '" + RationaleDBUtil.escape(name) + "', " + category.getID()
				+ ", '" + RationaleDBUtil.escape(description) + "', " + time_behavior +
				")";
				stmt.execute(dm);
				
				l_updateEvent = m_eventGenerator.MakeCreated();
			}
			
			//Add all ont_entries back...
			Iterator<OntEntry> effectsI = badEffects.iterator();
			while (effectsI.hasNext()){
				int effectID = RationaleDB.findAvailableID("TACTIC_NEGONTENTRIES");
				OntEntry cur = effectsI.next();
				dm = "INSERT INTO TACTIC_NEGONTENTRIES " +
				" (id, tactic_id, ont_id) VALUES (" + effectID + ", " + id + ", " + 
				cur.getID() + ")";
				stmt.execute(dm);
			}
			
			m_eventGenerator.Broadcast(l_updateEvent);
			stmt.close();
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	}
	
	

}
