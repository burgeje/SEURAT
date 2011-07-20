package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class ParticipantOperation extends RationaleElement{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1325910349575183220L;
	/**
	 * This stores the participant-type map
	 * (participant id, type)
	 */
	private int partAssoc;
	private int partID;
	private int assocType;

	public int getAssociatedParticipant() {
		return partAssoc;
	}

	public void setAssociatedParticipant(int partAssoc){
		this.partAssoc = partAssoc;
	}

	public void setPartID(int partID){
		this.partID = partID;
	}

	public int getPartID(){
		return partID;
	}

	public ParticipantOperation(){
		super();
		partAssoc = -1;
		partID = -1;
		assocType = -1;
	}

	public void fromDatabase(int opID){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = conn.createStatement();
			String query = "SELECT * FROM OPERATIONS WHERE ID = " + opID;
			rs = stmt.executeQuery(query);
			if (rs.next()){
				id = opID;
				name = RationaleDBUtil.decode(rs.getString("name"));
				partID = rs.getInt("part_id");
			}

			query = "SELECT * FROM OPERATION_PARTICIPANT WHERE oper_id = " + opID;
			rs = stmt.executeQuery(query);
			if (rs.next()){
				partAssoc = rs.getInt("part_id");
				assocType = rs.getInt("type");
			}
			else {
				partAssoc = -1;
				assocType = -1;
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	public void fromDatabase(int partID, String name){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = conn.createStatement();
			String query = "SELECT id FROM OPERATIONS WHERE name = '" + RationaleDBUtil.escape(name) + 
					"' AND part_id = " + partID;
			rs = stmt.executeQuery(query);
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
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			String partID;
			if (this.partID >= 0){
				partID = new Integer(this.partID).toString();
			}
			else{
				partID = "NULL";
			}
			if (inDatabase()){
				//Exists in database
				//Step 1: Delete old operation-participant associations
				String dm = "DELETE FROM OPERATION_PARTICIPANT WHERE oper_id = " + id;
				stmt.execute(dm);
				//Step 2: Update operation
				dm = "UPDATE OPERATIONS SET "
						+ " name = '" + RationaleDBUtil.escape(name)
						+ "' WHERE id = " + id;
				stmt.execute(dm);
			}
			else {
				//Not exists in database
				if (!fromXML){
					id = RationaleDB.findAvailableID("OPERATION_PARTICIPANT");
				}
				String dm = "INSERT INTO OPERATIONS (id, part_id, name) VALUES ("
						+ id + ", " + partID + ", '" + RationaleDBUtil.escape(name) + "')";
				stmt.execute(dm);
			}
			//Add associations
			if (partAssoc >= 0){
				String dm = "INSERT INTO OPERATION_PARTICIPANT (id, oper_id, part_id, type) VALUES ("
						+ RationaleDB.findAvailableID("OPERATION_PARTICIPANT") + ", " + id
						+ ", " + partAssoc + ", " + assocType + ")";
				stmt.execute(dm);
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	/**
	 * Check if this pattern is already in the database. The check is different
	 * if you are reading it in from XML because you can do a query on the name.
	 * Otherwise you can't because you run the risk of the user having changed the
	 * name. 
	 * 
	 * @return true if in the database already
	 */	
	private boolean inDatabase(){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		if (fromXML)
		{
			//find out if this pattern is already in the database
			Statement stmt = null; 
			ResultSet rs = null; 

			try {
				stmt = conn.createStatement();
				String query = "SELECT id FROM OPERATIONS WHERE name = '" + 
						RationaleDBUtil.escape(name) + "' AND part_id = " + partID;
				rs = stmt.executeQuery(query);

				if (rs.next()){
					id = rs.getInt("id");
					return true;
				}

			} catch (SQLException e){
				e.printStackTrace();
			}
		}
		else if (id >= 0) {
			try{
				Statement stmt = conn.createStatement();
				String query = "SELECT id FROM OPERATIONS WHERE id = " + id;
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next())
					return true;
				else return false;
			} catch (SQLException e){
				e.printStackTrace();
			}
		}
		return false;
	}

	public void fromXML(Element opE){
		fromXML = true;
		
		String rid = opE.getAttribute("rid");
		id = new Integer(rid.substring(2));
		
		name = opE.getAttribute("name");
		partID = new Integer(opE.getAttribute("partOf").substring(2));
		partAssoc = new Integer(opE.getAttribute("referencedID").substring(2));
		assocType = new Integer(opE.getAttribute("referencedType"));
		
		//Do not save to the database yet... Do this later in RationaleDBUtil to avoid
		//integrity constraint violation.
	}

	public Element toXML(Document ratDoc){
		Element opE = ratDoc.createElement("DR:participantoperation");
		opE.setAttribute("rid", "po" + id);
		opE.setAttribute("name", name);
		opE.setAttribute("partOf", "pp" + partID);
		opE.setAttribute("referencedID", "pp" + partAssoc);
		opE.setAttribute("referencedType", "" + assocType);
		return opE;
	}
}
