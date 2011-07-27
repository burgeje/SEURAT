package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.sql.*;

/**
 * This class represents a association or a participant in an XMI file.
 * The xmi element is a participant(i.e. class) if part2ID<0
 * Otherwise, it represents an association.
 * @author yechen
 *
 */
public class PatternElement extends RationaleElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2315364263265366204L;
	private int part1ID = -1, part2ID = -1, assocType = -1, altID = -1;
	private String xmiID = null;
	
	//Getters
	public int getPart1ID() {
		return part1ID;
	}



	public int getPart2ID() {
		return part2ID;
	}



	public int getAssocType() {
		return assocType;
	}



	public int getAltID() {
		return altID;
	}
	
	public void setAltID(int newID){
		altID = newID;
	}


	public String getXMIID() {
		return xmiID;
	}

	public PatternElement(int part1ID, int part2ID, int assocType, String xmiID){
		this.part1ID = part1ID;
		this.part2ID = part2ID;
		this.assocType = assocType;
		this.xmiID = xmiID;
	}
	
	public boolean equals(Object other){
		if (other == null) return false;
		if (other.getClass() != PatternElement.class) return false;
		PatternElement target = (PatternElement) other;
		return this.getXMIID().equals(target.getXMIID());
	}
	
	public void fromDatabase(int id){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		try{
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM DIAGRAM_PATTERNELEMENTS WHERE id = " + id;
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()){
				this.id = id;
				this.part1ID = rs.getInt("part_id");
				
				int part2IDTemp = rs.getInt("part2_id");
				if (rs.wasNull()){
					part2ID = -1;
				}
				else part2ID = part2IDTemp;
				
				this.assocType = rs.getInt("assoc_type");
				this.xmiID = RationaleDBUtil.decode(rs.getString("xmi_id"));
				name = xmiID;
				this.altID = rs.getInt("alt_id");
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public void fromDatabase(String xmiID){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		try{
			Statement stmt = conn.createStatement();
			String query = "SELECT id FROM DIAGRAM_PATTERNELEMENTS WHERE xmi_id = '"
					+ RationaleDBUtil.escape(xmiID) + "'";
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()){
				fromDatabase(rs.getInt("id"));
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public void toDatabase(){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		try{
			Statement stmt = conn.createStatement();
			String dm = "";
			String part2IDRep = "NULL";
			if (part2ID > 0) part2IDRep = "" + part2ID;
			if (id >= 0){
				//Exists in database. Update
				dm = "UPDATE DIAGRAM_PATTERNELEMENTS SET "
						+ " part_id = " + part1ID
						+ " part2_id = " + part2IDRep
						+ " assoc_type = " + assocType
						+ " alt_id = " + altID
						+ " xmi_id = '" + RationaleDBUtil.escape(xmiID)
						+ "' WHERE "
						+ " id = " + id;
			}
			else {
				id = RationaleDB.findAvailableID("DIAGRAM_PATTERNELEMENTS");
				dm = "INSERT INTO DIAGRAM_PATTERNELEMENTS " 
						+ "(id, part_id, part2_id, assoc_type, alt_id, xmi_id) "
						+ " VALUES (" + id + ", " + part1ID + ", " + part2IDRep + ", "
						+ assocType + ", " + altID + ", '" + RationaleDBUtil.escape(xmiID)
						+ "')";
			}
			stmt.execute(dm);
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
	public void deleteFromDB(){
		if (id < 0) return;
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		try{
			Statement stmt = conn.createStatement();
			String dm = "DELETE FROM DIAGRAM_PATTERNELEMENTS WHERE id = " + id;
			stmt.execute(dm);
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
	
}
