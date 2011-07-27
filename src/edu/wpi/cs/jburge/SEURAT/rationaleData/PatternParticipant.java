package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


public class PatternParticipant extends RationaleElement{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6594823055438128798L;

	/**
	 * This stores the participant-type map
	 * (participant id, type)
	 */
	private HashMap<Integer, Integer> participants;

	/**
	 * This stores the reference to the operations of the participant
	 */
	private Vector<ParticipantOperation> operations;

	private int patternID;

	private int min, max;

	/**
	 * Associate a pattern participant with this one.
	 * @param participantID target participant
	 * @param type A UMLRelation
	 * @return true if successful. false otherwise.
	 */
	public boolean associateParticipant(int participantID, int type){
		if (participants.containsKey(participantID)) return false;
		//Check cycle?
		if (type < 0 || type >= UMLRelation.NUMRELATIONS) return false;
		participants.put(participantID, type);
		return true;
	}

	/**
	 * Remove a participant ID from the hash map.
	 * @param participantID The key to remove.
	 * @return True if successful. False otherwise
	 */
	public boolean removeParticipant(int participantID){
		if (participants.containsKey(participantID)){
			participants.remove(new Integer(participantID));
		}
		return false;
	}


	public boolean addOperation(ParticipantOperation op){
		if (operations.contains(op)) return false;
		operations.add(op);
		return true;
	}


	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	public int getPatternID() {
		return patternID;
	}

	public void setPatternID(int patternID) {
		this.patternID = patternID;
	}

	public HashMap<Integer, Integer> getParticipants() {
		return participants;
	}

	public Vector<ParticipantOperation> getOperations() {
		return operations;
	}

	public PatternParticipant(){
		super();
		min = -1;
		max = -1;
		participants = new HashMap<Integer, Integer>();
		operations = new Vector<ParticipantOperation>();
		patternID = -1;
	}

	public void fromDatabase(int partID){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = conn.createStatement();
			String query = "SELECT * FROM PATTERNPARTICIPANTS WHERE id = " + partID;
			rs = stmt.executeQuery(query);
			if (rs.next()){
				id = partID;
				patternID = rs.getInt("pattern_id");
				name = RationaleDBUtil.decode(rs.getString("name"));
				min = rs.getInt("minParticipants");
				max = rs.getInt("maxParticipants");

				//Participant-Participant relationships
				participants = new HashMap<Integer, Integer>();
				query = "SELECT * FROM PART_PART WHERE part_id = " + id;
				rs = stmt.executeQuery(query);
				while (rs.next()){
					participants.put(rs.getInt("ref_id"), rs.getInt("type"));
				}

				//Operations
				operations = new Vector<ParticipantOperation>();
				Vector<Integer> operationIDs = findOperationsID();
				for (int i = 0; i < operationIDs.size(); i++){
					ParticipantOperation op = new ParticipantOperation();
					op.fromDatabase(operationIDs.get(i));
					operations.add(op);
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	private Vector<Integer> findOperationsID(){
		Vector<Integer> toReturn = new Vector<Integer>();
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try{		
			stmt = conn.createStatement();
			String query = "SELECT * FROM OPERATIONS WHERE part_id = " + id;
			rs = stmt.executeQuery(query);
			while (rs.next()){
				toReturn.add(rs.getInt("id"));
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return toReturn;
	}

	public void fromDatabase(int patternID, String name){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try{
			stmt = conn.createStatement();
			String query = "SELECT id from PATTERNPARTICIPANTS"
					+ " WHERE name = '" + RationaleDBUtil.escape(name) + 
					"' AND pattern_id = " + patternID;
			rs = stmt.executeQuery(query);
			if (rs.next()){
				fromDatabase(rs.getInt("id"));
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	/**
	 * This only stores the participant and not the relationships.
	 * 
	 * Used as "first passthrough" when importing an XML, since participants and operations
	 * cannot be added until later.
	 */
	public void storeParticipant(){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			if (inDatabase()){
				//Exists in database.
				//Step 1: Delete original operations and associations
				Vector<Integer> operationsToDelete = findOperationsID();
				for (int i = 0 ; i < operationsToDelete.size(); i++){
					String dm = "DELETE FROM OPERATION_PARTICIPANT WHERE oper_id = " + 
							operationsToDelete.get(i);
					stmt.execute(dm);
				}
				String dm = "DELETE FROM OPERATIONS WHERE part_id = " + id;
				stmt.execute(dm);
				dm = "DELETE FROM PART_PART WHERE part_id = " + id;
				stmt.execute(dm);

				//Step 2: Update participant table
				dm = "UPDATE PATTERNPARTICIPANTS SET" +
						" minParticipants = " + min +
						", maxParticipants = " + max +
						", name = '" + RationaleDBUtil.escape(name) + "'" + 
						" WHERE id = " + id;
				stmt.execute(dm);
			}
			else {
				//Not exists in database
				if (!fromXML){
					id = RationaleDB.findAvailableID("PATTERNPARTICIPANTS");
				}
				String dm = "INSERT INTO PATTERNPARTICIPANTS (id, pattern_id, name, minParticipants, maxParticipants) values ("
						+ id + ", " + patternID + ", '"
						+ RationaleDBUtil.escape(name) + "', " + min + ", " + max + ")";
				stmt.execute(dm);
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
			storeParticipant();
			//Add associations and operations
			Integer[] partIDs = (Integer[]) participants.keySet().toArray(new Integer[0]);
			for (int i = 0; i < partIDs.length; i++){
				String dm = "INSERT INTO PART_PART (id, part_id, ref_id, type) values ("
						+ RationaleDB.findAvailableID("PART_PART") + ", " + id + ", "
						+ partIDs[i] + ", " + participants.get(partIDs[i]) + ")";
				stmt.execute(dm);
			}

			Iterator<ParticipantOperation> operationsI = operations.iterator();
			while (operationsI.hasNext()){
				operationsI.next().toDatabase();
			}

		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	/**
	 * Delete this pattern participant from database.
	 * Deletes associations to or from the participant.
	 */
	public void deleteFromDB(){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			deleteRelFromDB();
			String dm = "DELETE FROM PATTERNPARTICIPANTS WHERE id = " + id;
			stmt.execute(dm);
			id = -1;
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	/**
	 * Delete all relations to the participant from DB. Used before deleting the participant.
	 * Also used before saving data in the editor.
	 */
	public void deleteRelFromDB(){

		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null;
		try{
			stmt = conn.createStatement();
			String dm = "DELETE FROM OPERATION_PARTICIPANT WHERE " +
					"part_id = " + id;
			stmt.execute(dm);
			String query = "SELECT id FROM OPERATIONS WHERE part_id = " + id;
			Statement queryStatement = conn.createStatement();
			ResultSet rs = queryStatement.executeQuery(query);
			while (rs.next()){
				dm = "DELETE FROM OPERATION_PARTICIPANT WHERE oper_id = " + rs.getInt("id");
				stmt.execute(dm);
			}
			dm = "DELETE FROM OPERATIONS WHERE part_id = " + id;
			stmt.execute(dm);
			dm = "DELETE FROM PART_PART WHERE part_id = " + id +
					" OR ref_id = " + id;
			stmt.execute(dm);
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
		if (fromXML)
		{
			RationaleDB db = RationaleDB.getHandle();
			Connection conn = db.getConnection();

			//find out if this pattern is already in the database
			Statement stmt = null; 
			ResultSet rs = null; 

			try {
				stmt = conn.createStatement();
				String query = "SELECT id FROM PATTERNPARTICIPANTS WHERE name = '" + 
						RationaleDBUtil.escape(name) + "' AND pattern_id = " + patternID;
				rs = stmt.executeQuery(query);

				if (rs.next()){
					id = rs.getInt("id");
					return true;
				}

			} catch (SQLException e){
				e.printStackTrace();
			}
		}
		else if (id >= 0) return true;
		return false;
	}

	/**
	 * This method imports from XML.
	 * 
	 * Note that operation imports are not part of this method, and has to be done later.
	 * @param participantE
	 */
	public void fromXML(Element participantE){
		fromXML = true;

		String rid = participantE.getAttribute("rid");
		id = Integer.parseInt(rid.substring(2));
		patternID = Integer.parseInt(participantE.getAttribute("pid").substring(1));
		name = participantE.getAttribute("name");
		min = Integer.parseInt(participantE.getAttribute("min"));
		max = Integer.parseInt(participantE.getAttribute("max"));

		Node child = participantE.getFirstChild();
		if (child != null){
			importHelper(child);
			Node nextNode = child.getNextSibling();
			while (nextNode != null){
				importHelper(nextNode);
				nextNode = nextNode.getNextSibling();
			}
		}

		storeParticipant();
	}

	/**
	 * Because how inconvenient the XML element control is, I have to separate it...
	 * @param child
	 */
	private void importHelper(Node child){
		if (child instanceof Element){
			Element childE = (Element) child;
			if (childE.getTagName().equals("assocParticipant")){
				int assocID = new Integer(childE.getAttribute("rid").substring(2));
				int type = new Integer(childE.getAttribute("type"));
				associateParticipant(assocID, type);
			}
		}
	}

	public Element toXML(Document ratDoc){
		Element participantE;

		participantE = ratDoc.createElement("DR:patternparticipant");
		participantE.setAttribute("rid", "pp" + id);
		participantE.setAttribute("pid", "p" + patternID);
		participantE.setAttribute("name", name);
		participantE.setAttribute("min", "" + min);
		participantE.setAttribute("max", "" + max);

		Integer[] assocIDs = (Integer[]) participants.keySet().toArray(new Integer[0]);
		for (int i = 0; i < assocIDs.length; i++){
			Element cur = ratDoc.createElement("assocParticipant");
			cur.setAttribute("rid", "pp" + assocIDs[i]);
			cur.setAttribute("type", "" + participants.get(assocIDs[i]));
			participantE.appendChild(cur);
		}

		return participantE;
		//Add operations in ParticipantOperation later.
	}

	public HashMap<String, Integer> getIndirectAssocParticipants(){
		HashMap<String, Integer> toReturn = new HashMap<String, Integer>();
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		try{
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM PART_PART WHERE " +
					"ref_id = " + id;
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()){
				int assocID = rs.getInt("part_id");
				int type = rs.getInt("type");
				String nameQuery = "SELECT name FROM PATTERNPARTICIPANTS WHERE id = " + assocID;
				Statement nameStatement = conn.createStatement();
				ResultSet nameSet = nameStatement.executeQuery(nameQuery);
				if (nameSet.next()){
					toReturn.put(RationaleDBUtil.decode(nameSet.getString("name")), type);
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * Since pattern participant names can overlap each other (such as Client), it is unwise to make name of the participants as a candidate key.
	 * Therefore, we throw an exception every time an adapter tries to access this method.
	 * @deprecated {PatternParticipant.NAME} is NOT a Candidate Key
	 */
	public void fromDatabase(String name){
		throw new IllegalArgumentException("Name of pattern participant is not a candidate key!");
	}

}
