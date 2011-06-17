package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import SEURAT.events.RationaleElementUpdateEventGenerator;
import SEURAT.events.RationaleUpdateEvent;

public class TacticPattern extends RationaleElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6126464348990010212L;
	private int tacticID, patternID, struct_change, beh_change, num_changes, overall;
	private boolean fromXML = false;

	public static final int INVALID = -1;

	public static final String[] CHANGECATEGORIES = {"Implemented In", "Replicates", "Add in pattern", "Add out of pattern", "Modify"};
	
	public static final int[] AUTOSCORECHANGEBASE = {0, 0, 1, 5, 2};
	public static final int[] AUTOSCORECHANGEDELTA = {1, 1, 1, 1, 2};
	public static final int[] AUTOSCOREMAXNUMCHANGES = {1, -1, -1, -1, -1};

	private RationaleElementUpdateEventGenerator<TacticPattern> m_eventGenerator = 
		new RationaleElementUpdateEventGenerator<TacticPattern>(this);

	public TacticPattern(){
		super();
		tacticID=INVALID;
		patternID = INVALID;
		struct_change = INVALID;
		beh_change = INVALID;
		num_changes = INVALID;
		overall = INVALID;
	}

	public RationaleElementType getElementType(){
		return RationaleElementType.TACTICPATTERN;
	}
	
	

	public int getPatternID() {
		return patternID;
	}

	public void setPatternID(int patternID) {
		this.patternID = patternID;
		name = combineNames(getPatternName(), getTacticName());
	}

	public int getTacticID() {
		return tacticID;
	}

	public void setTacticID(int tacticID) {
		this.tacticID = tacticID;
		name = combineNames(getPatternName(), getTacticName());
	}

	public int getStruct_change() {
		return struct_change;
	}

	public void setStruct_change(int struct_change) {
		this.struct_change = struct_change;
	}

	public int getBeh_change() {
		return beh_change;
	}

	public void setBeh_change(int beh_change) {
		this.beh_change = beh_change;
	}

	public int getOverallScore() {
		return overall;
	}

	public void setOverallScore(int changes) {
		this.overall = changes;
	}
	
	public int getNumChanges(){
		return num_changes;
	}
	
	public void setNumChanges(int changes){
		num_changes = changes;
	}
	
	public String getTacticName(){
		RationaleDB db = RationaleDB.getHandle();

		ResultSet rs = null;

		String findQuery = "";

		Statement stmt = null;

		Connection conn = db.getConnection();
		
		try {
			stmt = conn.createStatement();
			findQuery = "SELECT name FROM TACTICS WHERE id = " + tacticID;
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next()){
				return RationaleDBUtil.decode(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Tactic Not Found";
	}
	
	public String getPatternName(){
		RationaleDB db = RationaleDB.getHandle();

		ResultSet rs = null;

		String findQuery = "";

		Statement stmt = null;

		Connection conn = db.getConnection();
		
		try {
			stmt = conn.createStatement();
			findQuery = "SELECT name FROM PATTERNS WHERE id = " + patternID;
			rs = stmt.executeQuery(findQuery);
			
			if (rs.next()){
				return RationaleDBUtil.decode(rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "Pattern Not Found";
	}
	
	/**
	 * Given name of the pattern and tactic, combine the names together into one name.
	 * Used to store the element in memory to satisfy the unique name requirement.
	 * @param patternName
	 * @param tacticName
	 * @return
	 */
	public static String combineNames(String patternName, String tacticName){
		return patternName + "," + tacticName;
	}
	
	/**
	 * Given the name of a tactic-pattern, separate them into patternName and tacticName
	 * @param name
	 * @return an array of strings where String[0] is the patternName, string[1] is tacticName
	 */
	public static String[] sepNames(String name){
		String[] toReturn = new String[2];
		int sepIndex = name.indexOf(",");
		toReturn[0] = name.substring(0, sepIndex);
		toReturn[1] = name.substring(sepIndex + 1);
		return toReturn;
	}

	/**
	 * "Name" is in two parts: [pattern],[tactic]
	 */
	public void fromDatabase(String name){
		this.name = name;
		String[] names = sepNames(name);
		String patternName = RationaleDBUtil.escape(names[0]);
		String tacticName = RationaleDBUtil.escape(names[1]);
		name = RationaleDBUtil.escape(name);
		RationaleDB db = RationaleDB.getHandle();

		ResultSet rs = null;

		String findQuery = "";

		Statement stmt = null;

		Connection conn = db.getConnection();

		try {
			stmt = conn.createStatement();
			findQuery = "SELECT * FROM PATTERNS p WHERE p.name = '" + patternName +
			"' AND EXISTS (SELECT * FROM TACTICS t WHERE t.name = '" + tacticName + "')";
			rs = stmt.executeQuery(findQuery);

			if (rs.next()){
				patternID = rs.getInt("id");
				findQuery = "SELECT * FROM TACTIC_PATTERN WHERE pattern_id = " + patternID;
				rs = stmt.executeQuery(findQuery);

				if (rs.next()){
					id = rs.getInt("id");
					tacticID = rs.getInt("tactic_id");
					struct_change = rs.getInt("struct_change");
					beh_change = rs.getInt("beh_change");
					num_changes = rs.getInt("num_changes");
					overall = rs.getInt("changes");
					description = RationaleDBUtil.decode(rs.getString("description"));
				}
				rs.close();
			}
		}
		catch (SQLException e){
			RationaleDB.reportError(e, "Pattern.fromDatabase(String)", "Pattern:FromDatabase");
			e.printStackTrace();
		}
		finally{
			RationaleDB.releaseResources(stmt, rs);
		}
	}

	public void fromDatabase(int id){
		this.id = id;
		RationaleDB db = RationaleDB.getHandle();

		ResultSet rs = null;

		String findQuery = "";

		Statement stmt = null;

		Connection conn = db.getConnection();

		try{
			stmt = conn.createStatement();
			findQuery = "SELECT * FROM TACTIC_PATTERN where id = " + id;
			rs = stmt.executeQuery(findQuery);
			if (rs.next()){
				tacticID = rs.getInt("tactic_id");
				patternID = rs.getInt("pattern_id");
				description = RationaleDBUtil.decode(rs.getString("description"));
				struct_change = rs.getInt("struct_change");
				num_changes = rs.getInt("num_changes");
				beh_change = rs.getInt("beh_change");
				overall = rs.getInt("changes");
				
				findQuery = "SELECT name from PATTERNS where id = " + patternID;
				rs = stmt.executeQuery(findQuery);
				if(rs.next()){
					name = combineNames(getPatternName(), getTacticName());
				}
				else name = "TACTICPATTERN_NOT_EXIST ERROR";
			}
		} catch (SQLException e){
			RationaleDB.reportError(e, "TacticPattern.fromDatabase(String)", "TacticPattern:FromDatabase");
			e.printStackTrace();
		}
		finally{
			RationaleDB.releaseResources(stmt, rs);
		}
	}

	/**
	 * Store the data to the database. [Two cases: new, edit]
	 * @return the id in the database.
	 */
	public void toDatabase(){
		RationaleUpdateEvent l_updateEvent;
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String dm = "";

			if (inDatabase()){
				dm = "UPDATE TACTIC_PATTERN tp " +
				"SET tp.tactic_id = " + tacticID +
				", tp.pattern_id = " + patternID +
				", tp.struct_change = " + struct_change +
				", tp.num_changes = " + num_changes +
				", tp.beh_change = " + beh_change +
				", tp.changes = " + overall +
				", tp.description = '" + RationaleDBUtil.escape(description) +
				"' WHERE tp.id = " + id;
				stmt.execute(dm);

				l_updateEvent = m_eventGenerator.MakeUpdated();
			}
			else{
				id = RationaleDB.findAvailableID("TACTIC_PATTERN");
				dm = "INSERT INTO TACTIC_PATTERN " +
				"(id, tactic_id, pattern_id, struct_change, num_changes, beh_change, changes, description) " +
				"values (" +
				id + ", " + tacticID + ", " + patternID + ", " + struct_change + "," + num_changes +
				", " + beh_change + ", " + overall + ", '" + RationaleDBUtil.escape(description) + "')";
				stmt.execute(dm);
				
				l_updateEvent = m_eventGenerator.MakeCreated();
			}

			m_eventGenerator.Broadcast(l_updateEvent);
			stmt.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	public boolean inDatabase(){
		if (fromXML){
			RationaleDB db = RationaleDB.getHandle();

			ResultSet rs = null;

			String findQuery = "";

			Statement stmt = null;

			Connection conn = db.getConnection();

			try {
				stmt = conn.createStatement();
				findQuery = "SELECT * FROM TACTIC_PATTERN WHERE pattern_id = " + patternID +
				"AND tactic_id = " + tacticID;
				rs = stmt.executeQuery(findQuery);

				if (rs.next()) return true;
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				RationaleDB.releaseResources(stmt, rs);
			}
			return false;
		}
		//Not from xml...
		else if (id != -1) return true;
		return false;
	}
	
	/**
	 * Invoked when user clicks "delete tactic pattern" from tactic library.
	 * @return
	 */
	public boolean deleteFromDB(){
		RationaleUpdateEvent l_updateEvent;
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			String dm = "DELETE FROM TACTIC_PATTERN WHERE " + 
			"tactic_id = " + tacticID + " AND pattern_id = " + patternID;
			stmt.execute(dm);
			l_updateEvent = m_eventGenerator.MakeDestroyed();
			m_eventGenerator.Broadcast(l_updateEvent);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
