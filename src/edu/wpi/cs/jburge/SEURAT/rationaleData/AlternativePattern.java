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

package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import SEURAT.events.RationaleElementUpdateEventGenerator;
import SEURAT.events.RationaleUpdateEvent;

import edu.wpi.cs.jburge.SEURAT.editors.EditAlternativePattern;

public class AlternativePattern extends RationaleElement {
	
	Pattern patternInLibrary;
	
	AlternativeStatus status;
	
	double evaluation;
	
	int parent;
	
	RationaleElementType ptype;
	
	boolean isExactMatch;
	
	Vector<Argument> arguments;
	
	Vector<Argument> argumentsAgainst;
	
	Vector<Argument> argumentsFor;
	
	Vector<Argument> relationships;
	
	Vector<Question> questions;
	
	Vector<Decision> subDecisions;
	
	private RationaleElementUpdateEventGenerator<AlternativePattern> m_eventGenerator = 
		new RationaleElementUpdateEventGenerator<AlternativePattern>(this);
	
		
	public AlternativePattern()
	{
		super();
		patternInLibrary = new Pattern();
		status = AlternativeStatus.ATISSUE;
		argumentsAgainst = new Vector<Argument>();
		argumentsFor = new Vector<Argument>();
		arguments = new Vector<Argument>();
		subDecisions = new Vector<Decision>();
		questions = new Vector<Question>();	
		relationships = new Vector<Argument>();
		isExactMatch = true; //default as exact matchi
	}
	
	
	public void fromDatabase(int altPatternID)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		this.id = altPatternID;
		
		Statement stmt = null; 
		ResultSet rs = null; 
		String findQuery = ""; 
		try {
			stmt = conn.createStatement();
			
			findQuery = "SELECT name  FROM " +
			"alternativepatterns where id = " +
			new Integer(altPatternID).toString();

			rs = stmt.executeQuery(findQuery);
			
			if (rs.next())
			{
				name = RationaleDBUtil.decode(rs.getString("name"));
				fromDatabase(name);
			}
			
		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex,"Error in Decision.fromDatabase(1)", findQuery);
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
	}
	
	public void fromDatabase(String name)
	{		
		String realName = name;
		if(realName.indexOf("~") != -1){
			realName = realName.substring(0, realName.indexOf("~"));
		}

		patternInLibrary.fromDatabase(realName);

		RationaleDB db = RationaleDB.getHandle();

		ResultSet rs = null;

		String findQuery = "";

		Statement stmt = null;

		Connection conn = null;

		try {
			findQuery = "SELECT * from alternativepatterns where name = '" + name + "'";			
			conn = db.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(findQuery);
			if (rs.next())
			{	
				this.id = rs.getInt("id");

				this.name = name;				

				db.getStatement_AltPatternFromDB().setString(1, name);
				conn = db.getConnection();
				stmt = conn.createStatement();


				ptype = RationaleElementType.fromString(rs.getString("ptype"));
				parent = rs.getInt("parent");
				status = (AlternativeStatus) AlternativeStatus.fromString(rs.getString("status"));
				evaluation = rs.getFloat("evaluation");
				isExactMatch = Boolean.valueOf(rs.getString("isExactMatch"));

//				need to read in the rest - recursive routines?
				//Now, we need to get the lists of arguments for and against
				//first For
				String findFor = "SELECT id FROM " + RationaleDBUtil.escapeTableName("arguments")
				+" where ptype = 'AlternativePattern' and " +
				"parent = " + 
				new Integer(this.id).toString() + " and " +
				"(type = 'Supports' or " +
				"type = 'Addresses' or " +
				"type = 'Satisfies' or " +
				"type = 'Pre-supposed-by')";
//				***				System.out.println(findFor);
				rs = stmt.executeQuery(findFor); 
				Vector<Integer> aFor = new Vector<Integer>();
				Vector<Integer> aAgainst = new Vector<Integer>();
				Vector<Integer> aRel = new Vector<Integer>();
				while (rs.next())
				{
					aFor.addElement(new Integer(rs.getInt("id")));
				}
				rs.close();

				//Now, the arguments against
				String findAgainst = "SELECT id FROM "+RationaleDBUtil.escapeTableName("arguments")
				+" where ptype = 'AlternativePattern' and " +
				"parent = " + 
				new Integer(this.id).toString() + " and " +
				"(type = 'Denies' or " +
				"type = 'Violates' or " +
				"type = 'Opposed-by')";
//				***				System.out.println(findAgainst);
				rs = stmt.executeQuery(findAgainst); 

				while (rs.next())
				{
					aAgainst.addElement(new Integer(rs.getInt("id")));
				}
				rs.close();	

				//Now, any other useful relationships
				//Now, the arguments against
				String findRel = "SELECT id FROM "+RationaleDBUtil.escapeTableName("arguments")
				+" where ptype = 'AlternativePattern' and " +
				"parent = " + 
				new Integer(this.id).toString() + " and " +
				"(type = 'Opposed' or " +
				"type = 'Pre-supposes')";
//				***				System.out.println(findRel);
				rs = stmt.executeQuery(findRel); 

				while (rs.next())
				{
					aRel.addElement(new Integer(rs.getInt("id")));
				}
				rs.close();	


				// Cleanup before loading arguments
				argumentsFor.removeAllElements();
				//Now that we have the IDs, create the arguments
				Enumeration args = aFor.elements();
				while (args.hasMoreElements())
				{
					Argument arg = new Argument();
					arg.fromDatabase(((Integer) args.nextElement()).intValue());
					if (arg.getParent() == this.id)
						argumentsFor.add(arg);
					else
					{
						System.out.println("argparent = " + arg.getParent() + "not equal" + this.id);
					}
				}

				// Cleanup before loading arguments
				argumentsAgainst.removeAllElements();
				args = aAgainst.elements();
				while (args.hasMoreElements())
				{
					Argument arg = new Argument();
					arg.fromDatabase(((Integer) args.nextElement()).intValue());
					if (arg.getParent() == this.id)
						argumentsAgainst.add(arg);
				}

				// Cleanup before loading arguments
				relationships.removeAllElements();
				args = aRel.elements();
				while (args.hasMoreElements())
				{
					Argument arg = new Argument();
					arg.fromDatabase(((Integer) args.nextElement()).intValue());
					if (arg.getParent() == this.id)
						relationships.add(arg);
				}


				Vector<String> decNames = new Vector<String>();
				String findQuery2 = "SELECT name from DECISIONS where " +
				"ptype = '" + RationaleElementType.ALTERNATIVEPATTERN.toString() +
				"' and parent = " + new Integer(id).toString();
//				***				System.out.println(findQuery2);
				rs = stmt.executeQuery(findQuery2);	
				while (rs.next())				
				{
					decNames.add(RationaleDBUtil.decode(rs.getString("name")));
				}
				Enumeration decs = decNames.elements();

				// Cleanup before loading subdecisions
				subDecisions.removeAllElements();
				while (decs.hasMoreElements())
				{
					Decision subDec = new Decision();
					subDec.fromDatabase((String) decs.nextElement());
					subDecisions.add(subDec);
				}				

				//need to do questions too 
				Vector<String> questNames = new Vector<String>();
				String findQuery3 = "SELECT name from QUESTIONS where " +
				"ptype = '" + RationaleElementType.ALTERNATIVEPATTERN.toString() +
				"' and parent = " + new Integer(id).toString();
//				***			System.out.println(findQuery3);
				rs = stmt.executeQuery(findQuery3);
				while (rs.next())
				{
					questNames.add(RationaleDBUtil.decode(rs.getString("name")));
				}
				Enumeration quests = questNames.elements();
				// Cleanup before loading questions
				questions.removeAllElements();
				while (quests.hasMoreElements())
				{
					Question quest = new Question();
					quest.fromDatabase((String) quests.nextElement());
					questions.add(quest);
				}

			}			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "AlternativePattern.fromDatabase(String)", "Pattern:FromDatabase");
		} finally { 
			RationaleDB.releaseResources(null,rs);
		}		
	}

	public boolean display(Display disp){
		EditAlternativePattern ep = new EditAlternativePattern(disp, this, true);
		return ep.getCanceled();
	}

	public int toDatabase(int parentID, RationaleElementType ptype){

		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		// Update Event To Inform Subscribers Of Changes
		// To Rationale
		RationaleUpdateEvent l_updateEvent;

		int ourid = 0;

		Statement stmt = null; 
		ResultSet rs = null; 

		evaluation = this.evaluate(false); 

		try {
			stmt = conn.createStatement(); 

			if (inDatabase(parentID, ptype))
			{
				String updateParent = "UPDATE alternativepatterns R " +
				"SET R.parent = " + new Integer(parentID).toString() +
				", R.ptype = '" + ptype.toString() +
				"', R.name = '" + RationaleDBUtil.escape(this.name) +
				"', R.status = '" + status.toString() +
				"', R.evaluation = " + new Double(evaluation).toString() +
				"', R.isExactMatch = " + isExactMatch() +
				" WHERE " +
				"R.id = " + this.id + " " ;

				stmt.execute(updateParent);

				l_updateEvent = m_eventGenerator.MakeUpdated();
				
				ourid = this.id;
			}
			else 
			{				
				// new alternativepattern
				String parentSt;
				if (this.parent < 0)
				{
					parentSt = "NULL";
				}
				else
				{
					parentSt = new Integer(parentID).toString();
				}			
				
				String alreadyExist = "Select * from alternativepatterns where name = '" + this.name + "'";
				rs = stmt.executeQuery(alreadyExist);
				String realName = this.name;
				if (rs.next()){
					realName = realName + "~";
				}				
				
				String newAltSt = "INSERT INTO alternativepatterns "+
				"(name, status, ptype, parent, evaluation) " +
				"VALUES ('" +
				RationaleDBUtil.escape(realName) + "', '" +
				this.status.toString() + "', '" +
				ptype.toString() + "', " +
				parentSt + ", " +
				new Double(evaluation).toString() + ")";
				
				stmt.execute(newAltSt); 
				
				l_updateEvent = m_eventGenerator.MakeCreated();
				
				String getLastID = "SELECT LAST_INSERT_ID()";
				rs = stmt.executeQuery(getLastID);
				if(rs.next()){
					//String theID = rs.getString("LAST_INSERT_ID()");
					ourid = rs.getInt("LAST_INSERT_ID()");
				}else{
					ourid = -1;
				}
					
			}
			
			//in either case, we want to update any sub-requirements in case
			//they are new!
			//now, we need to get our ID
//			String findQuery2 = "SELECT id FROM alternativepatterns where name='" +
//			RationaleDBUtil.escape(this.name) + "'";
//			rs = stmt.executeQuery(findQuery2); 
//			
//			if (rs.next())
//			{
//				ourid = rs.getInt("id");
//				rs.close();
//			}
//			else
//			{
//				ourid = -1;
//			}
			this.id = ourid;
			
			Enumeration args = getAllArguments().elements();
			while (args.hasMoreElements()) {
				Argument arg = (Argument) args.nextElement();
				arg.toDatabase(ourid, RationaleElementType.ALTERNATIVEPATTERN);
			}
			
			Enumeration quests = questions.elements();
			while (quests.hasMoreElements()) {
				Question quest = (Question) quests.nextElement();
				quest.toDatabase(ourid, RationaleElementType.ALTERNATIVEPATTERN);
			}
			
			Enumeration decs = subDecisions.elements();
			while (decs.hasMoreElements()) {
				Decision dec = (Decision) decs.nextElement();
				dec.toDatabase(ourid, RationaleElementType.ALTERNATIVEPATTERN);
			}
			
			Enumeration hist = history.elements();
			while (hist.hasMoreElements())
			{
				History his = (History) hist.nextElement();
				his.toDatabase(ourid, RationaleElementType.ALTERNATIVEPATTERN);
			}
			
			m_eventGenerator.Broadcast(l_updateEvent);
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "AlternativePattern.toDatabase", "Bad query");
		} finally { 
			RationaleDB.releaseResources(stmt, rs);
		}
		
		System.out.println("AlternativePattern.toDatabase done!");
		
		return ourid;		
	}
	
	public double evaluate()
	{
		return evaluate(true);
	}
	
	public double evaluate(boolean pSave)
	{
		double result = 0.0;
		Enumeration args = getAllArguments().elements();
		while (args.hasMoreElements()) {
			Argument arg = (Argument) args.nextElement();
//			System.out.println("relevant argument: " + arg.toString());
			result += arg.evaluate();
		}
		
		//should we take into account anyone pre-supposing or opposing us?
		RationaleDB db = RationaleDB.getHandle();
		Vector dependent = db.getDependentAlternatives(this, ArgType.OPPOSES);
		Iterator depI = dependent.iterator();
		while (depI.hasNext())
		{
			Alternative depA = (Alternative) depI.next();
			if (depA.getStatus() == AlternativeStatus.ADOPTED)
			{
				result += -10; //assume amount is MAX
			}
		}
		dependent = db.getDependentAlternatives(this, ArgType.PRESUPPOSES);
		depI = dependent.iterator();
		while (depI.hasNext())
		{
			Alternative depA = (Alternative) depI.next();
			if (depA.getStatus() == AlternativeStatus.ADOPTED)
			{
				result += 10; //assume amount is MAX
			}
		}
		
		//matching ontology entries
		Vector<Requirement> ourNFRRequirements = db.getNFRs();
		Hashtable pattern_values = new Hashtable(); 
		db = RationaleDB.getHandle();
		Enumeration rqus = ourNFRRequirements.elements();
		while (rqus.hasMoreElements()){				
			Requirement q = (Requirement)rqus.nextElement();
			if(this.isExactMatch){
				if (patternInLibrary.getOntEntries().contains(q.getOntology())){
					if(q.getImportance().toString().compareTo(Importance.DEFAULT.toString()) == 0){
						if(patternInLibrary.getPosiOnts().contains(q.getOntology())){
							result = result + 1.0;
						}else{
							result = result - 1.0;
						}
					}else{
						if(patternInLibrary.getPosiOnts().contains(q.getOntology())){
							result = result + q.getImportance().getValue();
						}else{
							result = result - q.getImportance().getValue();
						}
					}				
				}
			}else{
				//contributing match
				if (patternInLibrary.getOntEntries().contains(q.getOntology())){
					if(q.getImportance().toString().compareTo(Importance.DEFAULT.toString()) == 0){
						if(patternInLibrary.getPosiOnts().contains(q.getOntology())){
							result = result + 1.0;
						}else{
							result = result - 1.0;
						}
					}else{
						if(patternInLibrary.getPosiOnts().contains(q.getOntology())){
							result = result + q.getImportance().getValue();
						}else{
							result = result - q.getImportance().getValue();
						}
					}				
				}else{
					OntEntry o = new OntEntry();
					o.fromDatabase(q.getOntology().getName());
					RationaleDB d = RationaleDB.getHandle();
					Vector ontList = d.getOntologyDescendents(o.getName());
					Enumeration ontChildren = ontList.elements();
					while (ontChildren.hasMoreElements()){
						OntEntry ont = (OntEntry)ontChildren.nextElement();
						//System.out.println(ont.getName());
						if(patternInLibrary.getOntEntries().contains(ont)){
							//String test = q.getImportance().toString();
							if(q.getImportance().toString().compareTo(Importance.DEFAULT.toString()) == 0){
								if(patternInLibrary.getPosiOnts().contains(q.getOntology())){
									result = result + 1.0;
								}else{
									result = result - 1.0;
								}
							}else{
								if(patternInLibrary.getPosiOnts().contains(q.getOntology())){
									result = result + q.getImportance().getValue();
								}else{
									result = result - q.getImportance().getValue();
								}
							}	
						}
					}					
				}
				
				// Weak contribution
				for (OntEntry o: patternInLibrary.getOntEntries()){
					int level = db.findRelativeOntLevel(o, q.getOntology());
					if (level > 0){
						int divisor = db.getOntologiesAtLevel(o, level).size();
						if(q.getImportance().toString().compareTo(Importance.DEFAULT.toString()) == 0){
							if(patternInLibrary.getPosiOnts().contains(o)){
								result += ((double) 1) / divisor;
							}
							else{
								result -= ((double) 1) / divisor;
							}
						}
						else{
							if(patternInLibrary.getPosiOnts().contains(q.getOntology())){
								result += q.getImportance().getValue() / divisor;
							}
							else {
								result -= q.getImportance().getValue() / divisor;
							}
						}
					}
				}
			}
		}
		
		setEvaluation(result);
		
		if( pSave )
		{
			///
			// TODO This was added to get the integrated editors
			// updating correctly, however would be better suited
			// in a seperate function which is then refactored
			// outward so other places in SEURAT use it.
			Connection conn = db.getConnection();
			Statement stmt = null; 
			ResultSet rs = null; 
			try
			{
				stmt = conn.createStatement(); 
				
				// TODO Function call here is really hacky, see
				// the function inDatabase()
				if (inDatabase())
				{
					String updateParent = "UPDATE alternatives " +
					"SET evaluation = " + new Double(evaluation).toString() +
					" WHERE " +
					"id = " + this.id + " " ;
					stmt.execute(updateParent);
					
					// Broadcast Notification That Score Has Been Recomputed
					m_eventGenerator.Updated();		
				}
				else
				{
					// If The RationaleElement Wasn't In The Database There 
					// Is No Reason To Attempt To Save The Evaluation Score
					// Or Broadcast A Notification About It
				}
			}
			catch( Exception e )
			{
				System.out.println("Exception while saving evaluation score to database");
			}			
			finally { 
				RationaleDB.releaseResources(stmt, rs);
			}
		}
		
		return result;
	}
	
	private boolean inDatabase()
	{
		return inDatabase(0, null);
	}
	
	private boolean inDatabase(int parentID, RationaleElementType ptype)
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
				findQuery = "SELECT id, parent FROM alternatives where name='" +
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
				RationaleDB.reportError(ex, "Alternative.inDatabase", findQuery); 
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
	
	public boolean delete()
	{
		//need to have a way to inform if delete did not happen
		//can't delete if there are dependencies...
		if ((this.argumentsAgainst.size() > 0) ||
				(this.argumentsFor.size() > 0) ||
				(this.relationships.size() > 0) ||
				(this.questions.size() > 0) ||
				(this.subDecisions.size() > 0))
		{
			MessageDialog.openError(new Shell(),	"Delete Error",	"Can't delete when there are sub-elements.");
			
			return true;
		}

		RationaleDB db = RationaleDB.getHandle();
		
//		//are there any dependencies on this item?
//		if (db.getDependentAlternatives(this).size() > 0)
//		{
//			MessageDialog.openError(new Shell(),	"Delete Error",	"Can't delete when there are depencencies.");
//			return true;
//		}
		
		m_eventGenerator.Destroyed();
		
		db.deleteRationaleElement(this);
		return false;
		
	}
	
	public void savePatternDecisions(){
//		RationaleDB db = RationaleDB.getHandle();
//		Connection conn = db.getConnection();
//		
//		Statement stmt = null; 
//		ResultSet rs = null; 		
//		
//		Vector<PatternDecision> pds = new Vector<PatternDecision>();
//		pds = patternInLibrary.getSubDecisions();
//		Enumeration enu = pds.elements();
//		while(enu.hasMoreElements()){
//			String name = ((PatternDecision)enu.nextElement()).getName();
//			String update = "INSERT INTO decisions (name, description, type, status, phase, subdecreq, ptype, parent, subsys, designer) " +
//					"SELECT name, description, type, status, phase, subdecreq, ptype, parent, subsys, designer" +
//					" FROM patterndecisions WHERE name = '" + name + "'";			
//			try {
//				stmt = conn.createStatement();
//				stmt.executeUpdate(update);
//				String getLastID = "SELECT LAST_INSERT_ID()";
//				rs = stmt.executeQuery(getLastID);
//				Vector<Integer> decisionIDs = new Vector<Integer>();
//				if(rs.next()){
//					//String theID = rs.getString("LAST_INSERT_ID()");
//					decisionIDs.add((rs.getInt("LAST_INSERT_ID()")));
//				}
//				for(int k=0;k < decisionIDs.size();k++){
//					int decisionID = (Integer)decisionIDs.get(k);
//					System.out.println(decisionID);
//
//					update = "UPDATE decisions SET ptype = 'AlternativePattern', parent = " + this.id + " WHERE id=" + decisionID;
//					System.out.println(update);
//					stmt.executeUpdate(update);
//
//					PatternDecision pd = new PatternDecision();
//					pd.fromDatabase(name);
//					String findQuery = "SELECT patternID FROM pattern_decision WHERE decisionID = "
//						+ pd.getID()
//						+ " and parentType = 'decision'";
//					System.out.println(findQuery);
//					rs = stmt.executeQuery(findQuery);
//					Vector<String> names = new Vector<String>();
//					while(rs.next()){
//						Pattern candidate = new Pattern();
//						candidate.fromDatabase(rs.getInt("patternID"));
//						names.add(candidate.getName());
//					}
//					for(int j=0;j<names.size();j++){
//						String insertPattern = "INSERT INTO alternativepatterns (name, status, evaluation, ptype, parent) VALUES ('" + 
//						(String)names.get(j) + "', 'At_Issue', 0, 'Decision', " + decisionID + ")";
//						System.out.println(insertPattern);
//						stmt.execute(insertPattern);
//					}
//				}
//			} catch (SQLException e) {
//				RationaleDB.reportError(e, "AlternativePattern.savePatternDecisions", "Bad update");
//			}			
//			
//		}
	}
	
	public void deletePatternDecisions(){
				
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		
		Statement stmt = null; 
		ResultSet rs = null; 		
		
		Vector<PatternDecision> pds = new Vector<PatternDecision>();
		pds = patternInLibrary.getSubDecisions();
		Enumeration enu = pds.elements();
		while(enu.hasMoreElements()){
			String name = ((PatternDecision)enu.nextElement()).getName();
			String update = "INSERT INTO decisions (name, description, type, status, phase, subdecreq, ptype, parent, subsys, designer) " +
					"SELECT name, description, type, status, phase, subdecreq,ptype, parent, subsys, designer" +
					" FROM patterndecisions WHERE name = '" + name + "'";			
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(update);
				String getLastID = "SELECT LAST_INSERT_ID()";
				rs = stmt.executeQuery(getLastID);
				Vector<Integer> decisionIDs = new Vector<Integer>();
				if(rs.next()){
					//String theID = rs.getString("LAST_INSERT_ID()");
					decisionIDs.add((rs.getInt("LAST_INSERT_ID()")));
				}
				for(int k=0;k < decisionIDs.size();k++){
					int decisionID = (Integer)decisionIDs.get(k);
					System.out.println(decisionID);

					update = "UPDATE decisions SET ptype = 'AlternativePattern', parent = " + this.id + " WHERE id=" + decisionID;
					System.out.println(update);
					stmt.executeUpdate(update);

					PatternDecision pd = new PatternDecision();
					pd.fromDatabase(name);
					String findQuery = "SELECT patternID FROM pattern_decision WHERE decisionID = "
						+ pd.getID()
						+ " and parentType = 'decision'";
					System.out.println(findQuery);
					rs = stmt.executeQuery(findQuery);
					Vector<String> names = new Vector<String>();
					while(rs.next()){
						Pattern candidate = new Pattern();
						candidate.fromDatabase(rs.getInt("patternID"));
						names.add(candidate.getName());
					}
					for(int j=0;j<names.size();j++){
						String insertPattern = "INSERT INTO alternativepatterns (name, status, evaluation, ptype, parent) VALUES ('" + 
						(String)names.get(j) + "', 'At_Issue', 0, 'Decision', " + decisionID + ")";
						System.out.println(insertPattern);
						stmt.execute(insertPattern);
					}
				}
			} catch (SQLException e) {
				RationaleDB.reportError(e, "AlternativePattern.savePatternDecisions", "Bad update");
			}			
			
		}
		
	}
	
	public RationaleElementType getElementType()
	{
		return RationaleElementType.ALTERNATIVEPATTERN;
	}

	public Vector<Argument> getArguments() {
		return arguments;
	}


	public void setArguments(Vector<Argument> arguments) {
		this.arguments = arguments;
	}


	public Vector<Argument> getArgumentsAgainst() {
		return argumentsAgainst;
	}


	public void setArgumentsAgainst(Vector<Argument> argumentsAgainst) {
		this.argumentsAgainst = argumentsAgainst;
	}


	public Vector<Argument> getArgumentsFor() {
		return argumentsFor;
	}


	public void setArgumentsFor(Vector<Argument> argumentsFor) {
		this.argumentsFor = argumentsFor;
	}


	public double getEvaluation() {
		return evaluation;
	}


	public void setEvaluation(double evaluation) {
		this.evaluation = evaluation;
	}


	public int getParent() {
		return parent;
	}


	public void setParent(int parent) {
		this.parent = parent;
	}


	public Pattern getPatternInLibrary() {
		return patternInLibrary;
	}


	public void setPatternInLibrary(Pattern patternInLibrary) {
		this.patternInLibrary = patternInLibrary;
	}


	public RationaleElementType getPtype() {
		return ptype;
	}


	public void setPtype(RationaleElementType ptype) {
		this.ptype = ptype;
	}


	public Vector<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(Vector<Question> questions) {
		this.questions = questions;
	}

	public AlternativeStatus getStatus() {
		return status;
	}

	public void setStatus(AlternativeStatus status) {
		this.status = status;
	}

	public Vector<Decision> getSubDecisions() {
		return subDecisions;
	}

	public void setSubDecisions(Vector<Decision> subDecisions) {
		this.subDecisions = subDecisions;
	}
	
	public boolean isExactMatch() {
		return isExactMatch;
	}

	public void setExactMatch(boolean isExactMatch) {
		this.isExactMatch = isExactMatch;
	}

	public Vector<Argument> getAllArguments() {
		Vector<Argument> args = new Vector<Argument>();
		args.addAll(argumentsFor);
		args.addAll(argumentsAgainst);
		args.addAll(relationships);
		return args;
	}
}