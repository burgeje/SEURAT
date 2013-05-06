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

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.swt.widgets.Display;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.LiteralUnlimitedNatural;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resource.XMI2UMLResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import SEURAT.events.RationaleUpdateEvent;
import SEURAT.events.RationaleElementUpdateEventGenerator;

import edu.wpi.cs.jburge.SEURAT.editors.EditPattern;

/**
 * This class is used to define a instance of Pattern in SEURAT.
 * @author yechen
 *
 */
public class Pattern extends RationaleElement {

	/**
	 * The pattern type. For example, architecture, design, idiom
	 */
	PatternElementType type;

	/**
	 * The id of the problem category this is associating with
	 */
	private int problemcategory;

	String problem;

	String context;

	String solution;

	String implementation;

	String example;

	String url;

	/**
	 * A vector containing all associated positive ontology entries
	 */
	public Vector<OntEntry> posiOnts;

	/**
	 * A vector containing all associated negative ontology entries.
	 */
	public Vector<OntEntry> negaOnts;

	public Vector<OntEntry> ontEntries;

	/**
	 * A vector containing all pattern decisions of the pattern.
	 */
	public Vector<PatternDecision> subDecisions;

	// These are used for XML import/export
	/**
	 * XML variable, no use outside this context
	 */
	private Vector<Integer> posOntID;
	/**
	 * XML variable, no use outside this context
	 */
	private Vector<Integer> negOntID;
	/**
	 * XML variable, no use outside this context
	 */
	private Vector<Integer> subDecID;

	/**
	 * This is used when associating with UML.
	 */
	private Vector<PatternElement> patternElements;

	private RationaleElementUpdateEventGenerator<Pattern> m_eventGenerator = 
			new RationaleElementUpdateEventGenerator<Pattern>(this);

	public Pattern()
	{
		super();
		posiOnts = new Vector<OntEntry>();
		negaOnts = new Vector<OntEntry>();
		ontEntries = new Vector<OntEntry>();
		description = "";
		problem = "";
		context = "";
		solution = "";
		implementation = "";
		example = "";
		url = "";
		subDecisions = new Vector<PatternDecision>();
		posOntID = new Vector<Integer>();
		negOntID = new Vector<Integer>();
		subDecID = new Vector<Integer>();
		patternElements = null;
	} 

	public Iterator<Integer> iteratorPosOntID(){
		return posOntID.iterator();
	}

	public Iterator<Integer> iteratorNegOntID(){
		return negOntID.iterator();
	}

	public Iterator<Integer> iteratorSubDecID(){
		return subDecID.iterator();
	}

	public RationaleElementType getElementType(){
		return RationaleElementType.PATTERN;
	}

	public void setType(PatternElementType newtype)
	{
		type = newtype;
	}

	public PatternElementType getType(){
		return type;
	}

	public void setProblem(String prob){
		problem = prob;
	}

	public String getProblem(){
		return problem;
	}

	public void setContext(String con){
		context = con;
	}

	public String getContext(){
		return context;
	}

	public void setSolution(String solu){
		solution = solu;
	}

	public String getSolution(){
		return solution;
	}

	public void setImplementation(String imple){
		implementation = imple;
	}

	public String getImplementation(){
		return implementation;
	}

	public void setExample(String exa){
		example = exa;
	}

	public String getExample(){
		return example;
	}

	public int getProblemCategory(){
		return problemcategory;
	}

	public void setProblemCategory(int newCategory){
		problemcategory = newCategory;
	}

	//	public void setPosiOnt(OntEntry ont){
	//		if (posiOnt != null)
	//		{
	//			posiOnt.decRefs();
	//		}
	//		posiOnt = ont;
	//		ont.incRefs(); 
	//	}

	public Vector<OntEntry> getOntEntries() {
		return ontEntries;
	}

	public void addOntEntries(OntEntry oe) {
		ontEntries.add(oe);
	}

	public void addPosiOnt(OntEntry oe){
		posiOnts.add(oe);
	}	

	public Vector<OntEntry> getPosiOnts(){
		return posiOnts;
	}

	public void addNegaOnt(OntEntry oe){
		negaOnts.add(oe);
	}	

	public Vector<OntEntry> getNegaOnts(){
		return negaOnts;
	}



	//	public void setNegaOnt(OntEntry ont){
	//		if (negaOnt != null)
	//		{
	//			negaOnt.decRefs();
	//		}
	//		negaOnt = ont;
	//		ont.incRefs(); 
	//	}

	public Vector<PatternDecision> getSubDecisions() {
		return subDecisions;
	}

	public void setSubDecisions(Vector<PatternDecision> subDecisions) {
		this.subDecisions = subDecisions;
	}

	public void addSubDecision(PatternDecision subD){
		this.subDecisions.add(subD);
	}

	public void setNegaOnts(Vector<OntEntry> negaOnts) {
		this.negaOnts = negaOnts;
	}

	public void setOntEntries(Vector<OntEntry> ontEntries) {
		this.ontEntries = ontEntries;
	}

	public void setPosiOnts(Vector<OntEntry> posiOnts) {
		this.posiOnts = posiOnts;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Vector<PatternElement> getPatternElements(){
		return patternElements;
	}

	/**
	 * This is a method that will export this single Pattern to an XML.
	 * It MUST be called by a XML exporter, as it does not have the header of the XML.
	 * @param ratDoc
	 * @return the SAX representation of the object.
	 */
	public Element toXML(Document ratDoc){
		Element patternE;
		RationaleDB db = RationaleDB.getHandle();

		//Now, add pattern to doc (Why do I need this???)
		String entryID = db.getRef(this);
		if (entryID == null){
			entryID = db.addPatternRef(this);
		}

		patternE = ratDoc.createElement("DR:pattern");
		patternE.setAttribute("rid", entryID);
		patternE.setAttribute("name", name);

		//Now, add child elements of this... (other than refs, which are handled in RationaleDBUtil)

		//Type
		Element typeE = ratDoc.createElement("type");
		Text typeText = ratDoc.createTextNode(type.toString());
		typeE.appendChild(typeText);
		patternE.appendChild(typeE);

		//description
		Element descE = ratDoc.createElement("description");
		Text descText = ratDoc.createTextNode(description);
		descE.appendChild(descText);
		patternE.appendChild(descE);

		//problem
		Element probE = ratDoc.createElement("problem");
		Text probText = ratDoc.createTextNode(problem);
		probE.appendChild(probText);
		patternE.appendChild(probE);

		//context
		Element contE = ratDoc.createElement("context");
		Text contText = ratDoc.createTextNode(context);
		contE.appendChild(contText);
		patternE.appendChild(contE);

		//Solution
		Element solE = ratDoc.createElement("solution");
		Text solText = ratDoc.createTextNode(solution);
		solE.appendChild(solText);
		patternE.appendChild(solE);

		//Implementation
		Element implE = ratDoc.createElement("implementation");
		Text implText = ratDoc.createTextNode(implementation);
		implE.appendChild(implText);
		patternE.appendChild(implE);

		//Example
		Element examE = ratDoc.createElement("example");
		Text examText = ratDoc.createTextNode(example);
		examE.appendChild(examText);
		patternE.appendChild(examE);

		Element urlE = ratDoc.createElement("url");
		Text urlText = ratDoc.createTextNode(url);
		urlE.appendChild(urlText);
		patternE.appendChild(urlE);

		//Use the database problem category ID instead.
		Element categoryE = ratDoc.createElement("refCategory");
		Text categoryText = ratDoc.createTextNode("c" + new Integer(problemcategory).toString());
		categoryE.appendChild(categoryText);
		patternE.appendChild(categoryE);

		//Add reference id of positive ontology
		Iterator<OntEntry> posi = posiOnts.iterator();
		while (posi.hasNext()){
			OntEntry cur = posi.next();
			Element curE = ratDoc.createElement("refOntPos");
			Text curText = ratDoc.createTextNode("r" + new Integer(cur.getID()));
			curE.appendChild(curText);
			patternE.appendChild(curE);
		}

		//Add reference id of negative ontology
		Iterator<OntEntry> negi = negaOnts.iterator();
		while (negi.hasNext()){
			OntEntry cur = negi.next();
			Element curE = ratDoc.createElement("refOntNeg");
			Text curText = ratDoc.createTextNode("r" + new Integer(cur.getID()));
			curE.appendChild(curText);
			patternE.appendChild(curE);
		}

		//Add reference id of pattern decisions
		Iterator<PatternDecision> deci = subDecisions.iterator();
		while (deci.hasNext()){
			PatternDecision cur = deci.next();
			Element curE = ratDoc.createElement("refChildDecision");
			Text curText = ratDoc.createTextNode("pd" + new Integer(cur.getID()));
			curE.appendChild(curText);
			patternE.appendChild(curE);
		}

		return patternE;
	}

	public void fromDatabase(int patternID)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		this.id = patternID;

		Statement stmt = null; 
		ResultSet rs = null; 
		String findQuery = ""; 
		try {
			stmt = conn.createStatement();

			findQuery = "SELECT name  FROM " +
					"PATTERNS where id = " +
					new Integer(patternID).toString();

			rs = stmt.executeQuery(findQuery);

			if (rs.next())
			{
				name = RationaleDBUtil.decode(rs.getString("name"));
				rs.close();
				this.fromDatabase(name);
			}

		} catch (SQLException ex) {
			// handle any errors 
			RationaleDB.reportError(ex,"Error in Pattern.fromDatabase(1)", findQuery);
			ex.printStackTrace();
		}
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}

	}

	public void fromDatabase(String name)
	{		
		RationaleDB db = RationaleDB.getHandle();

		ResultSet rs = null;

		String findQuery = "";

		Statement stmt = null;

		Connection conn = null;

		try {
			db.getStatement_PatternFromDB().setString(1, name);
			rs = db.getStatement_PatternFromDB().executeQuery();
			conn = db.getConnection();
			stmt = conn.createStatement();
			if (rs.next())
			{	
				this.id = rs.getInt("id");

				problemcategory = db.getCategoryByPattern(id);

				this.name = name;				

				try {
					this.description = RationaleDBUtil.decode(new String(rs
							.getBlob("description").getBytes(1,
									(int) rs.getBlob("description").length())));
					//this.description = rs.getString("description");
				} catch (NullPointerException e) {
					this.description = "";
				}				
				try {
					this.problem = RationaleDBUtil.decode(new String(rs
							.getBlob("problem").getBytes(1,
									(int) rs.getBlob("problem").length())));
				} catch (NullPointerException e) {
					this.problem = "";
				}				
				try {
					this.context = RationaleDBUtil.decode(new String(rs
							.getBlob("context").getBytes(1,
									(int) rs.getBlob("context").length())));
				} catch (NullPointerException e) {
					this.context = "";
				}				
				try {
					this.solution = RationaleDBUtil.decode(new String(rs
							.getBlob("solution").getBytes(1,
									(int) rs.getBlob("solution").length())));
				} catch (NullPointerException e) {
					this.solution = "";
				}				
				try {
					this.example = RationaleDBUtil.decode(new String(rs
							.getBlob("example").getBytes(1,
									(int) rs.getBlob("example").length())));
				} catch (NullPointerException e) {
					this.example = "";
				}

				try {
					this.implementation = RationaleDBUtil.decode(new String(
							rs.getBlob("implementation")
							.getBytes(
									1,
									(int) rs.getBlob("implementation")
									.length())));
				} catch (NullPointerException e) {
					this.implementation = "";
				}				
				this.type = PatternElementType.fromString(rs.getString("type"));

				try {
					this.url = RationaleDBUtil.decode(rs.getString("url"));
				} catch (NullPointerException e) {
					this.url = "";
				}

				// Now, get the associated ontology entries
				findQuery = "SELECT * FROM PATTERN_ONTENTRIES WHERE patternID = " + this.id;
				rs = stmt.executeQuery(findQuery);
				while (rs.next()){
					if(rs.getString("direction") != null){
						if ((rs.getString("direction")).compareTo("IS") == 0){
							OntEntry ontEn = new OntEntry();
							ontEn.fromDatabase(rs.getInt("ontID"));
							posiOnts.add(ontEn);
							ontEntries.add(ontEn);
						}else{
							OntEntry ontEn = new OntEntry();
							ontEn.fromDatabase(rs.getInt("ontID"));
							negaOnts.add(ontEn);
							ontEntries.add(ontEn);
						}
					}
				}

				findQuery = "SELECT * FROM PATTERNDECISIONS WHERE parent = " + this.id;
				rs = stmt.executeQuery(findQuery);
				while (rs.next()){
					PatternDecision decision = new PatternDecision();
					decision.fromDatabase(rs.getInt("id"));
					addSubDecision(decision);
				}		
			}			
		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Pattern.fromDatabase(String)", "Pattern:FromDatabase");
			ex.printStackTrace();
		}
		finally { 
			RationaleDB.releaseResources(null,rs);
		}		

	}

	public boolean display(Display disp){
		EditPattern ep = new EditPattern(disp, this, true);
		return ep.getCanceled();
	}

	/**
	 * Announce Deletion. Used to close pattern participant editor that has been opened.
	 */
	public void announceDelete(){
		RationaleUpdateEvent l_updateEvent = m_eventGenerator.MakeDestroyed();
		m_eventGenerator.Broadcast(l_updateEvent);
	}

	public void toDatabase(int pid){

		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();

		int ourid = 0;

		// Update Event To Inform Subscribers Of Changes
		// To Rationale
		RationaleUpdateEvent l_updateEvent;

		//find out if this ontology entry is already in the database
		Statement stmt = null; 
		ResultSet rs = null; 

		try {

			if (inDatabase())
			{
				//Take special care when inserting blobs! (re-implemented) (YQ)
				PreparedStatement ps = conn.prepareStatement("UPDATE patterns SET description = ?, problem = ?, context = ?, solution = ?, example = ?, implementation = ?, type = ?, url = ? WHERE name = ?");

				ps.setBytes(1, description.getBytes());
				ps.setBytes(2, problem.getBytes());
				ps.setBytes(3, context.getBytes());
				ps.setBytes(4, solution.getBytes());
				ps.setBytes(5, example.getBytes());
				ps.setBytes(6, implementation.getBytes());
				ps.setString(7,type.toString());
				ps.setString(8, url);
				ps.setString(9, name);
				ps.executeUpdate();
				ps.close();

				stmt = conn.createStatement(); 
				//saving associated ontentries
				//delete associated ones
				String deleteonts = "DELETE FROM pattern_ontentries where patternID = " + this.id;
				stmt.execute(deleteonts);

				//positive ones
				Enumeration pOnts = getPosiOnts().elements();
				while(pOnts.hasMoreElements()){
					String updatePosiOnts = "INSERT INTO pattern_ontentries (patternID, ontID, direction) VALUES (" + this.id + "," + ((OntEntry)pOnts.nextElement()).getID() + "," + "'IS')";
					stmt.execute(updatePosiOnts);
				}
				//				//negative ones
				Enumeration nOnts = getNegaOnts().elements();
				while(nOnts.hasMoreElements()){
					String updateNegaOnts = "INSERT INTO pattern_ontentries (patternID, ontID, direction) VALUES (" + this.id + "," + ((OntEntry)nOnts.nextElement()).getID() + "," + "'NOT')";
					stmt.execute(updateNegaOnts);
				}

				//Now, update pattern_problemcategory
				//Delete all pre-existing entry in the relationship then insert a new row
				//containing the new category.
				stmt = conn.createStatement();
				String deleteExisting = "DELETE from pattern_problemcategory where patternID = " + id + "";
				stmt.execute(deleteExisting);

				l_updateEvent = m_eventGenerator.MakeUpdated();
			}else{
				if (!fromXML)
					id = RationaleDB.findAvailableID("patterns");
				//new pattern
				PreparedStatement ps = conn.prepareStatement("INSERT INTO patterns " +
						"(id, name, type, description, problem, context, solution, implementation, example, url) " +
						"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				ps.setInt(1, id);
				ps.setString(2, name);
				ps.setString(3, type.toString());
				ps.setBytes(4, description.getBytes());
				ps.setBytes(5, problem.getBytes());
				ps.setBytes(6, context.getBytes());
				ps.setBytes(7, solution.getBytes());
				ps.setBytes(8, implementation.getBytes());
				ps.setBytes(9, example.getBytes());
				ps.setString(10, url);
				ps.executeUpdate();
				ps.close();
				System.out.println("insert new pattern done!");

				l_updateEvent = m_eventGenerator.MakeCreated();
			}
			//Finally, it's time to add the pattern_problemcategory relationship
			stmt = conn.createStatement();
			String addNewPCRelation = RationaleDBCreate.pattern_problemCategoryInsert("" + this.id + ", " + this.problemcategory + "");
			stmt.execute(addNewPCRelation);

			m_eventGenerator.Broadcast(l_updateEvent);

		} catch (SQLException ex) {
			RationaleDB.reportError(ex, "Pattern.toDatabase(int)", "SQL Error");
			ex.printStackTrace();
		}		
		finally { 
			RationaleDB.releaseResources(stmt, rs);
		}		
	}

	/**
	 * This method imports the pattern from XML to the database. Note that
	 * the vectors posiOnts, negaOnts, subDecisions, and ontEntries are not set by
	 * the end of this method.
	 * <br>
	 * <b>XML Import utility function is responsible for those vectors.</b>
	 * @param patternE The pattern DOM elment
	 */
	public void fromXML(Element patternE)
	{
		this.fromXML = true;

		RationaleDB db = RationaleDB.getHandle();

		String rid = patternE.getAttribute("rid");
		id = Integer.parseInt(rid.substring(1));

		name = patternE.getAttribute("name");

		Node child = patternE.getFirstChild();
		importHelper(child);

		Node nextNode = child.getNextSibling();
		while (nextNode != null){
			importHelper(nextNode);
			nextNode = nextNode.getNextSibling();
		}

		//Because when we add it, we will violate the referential integrity constraint,
		//we should not immediately add pattern decisions. (Missing data)
		//Instead, we need RationaleDB to have a helper method and we will call it
		//to insert incomplete information about this pattern.

		db.addPatternFromXML(this);
	}

	/**
	 * Because how inconvenient the XML element control is, I have to separate it...
	 * @param child
	 */
	private void importHelper(Node child){
		if (child.getFirstChild() instanceof Text){
			Text text = (Text) child.getFirstChild();
			String data = text.getData();
			if (child.getNodeName().equals("type")){
				type = PatternElementType.fromString(data);
			}
			else if (child.getNodeName().equals("description")){
				setDescription(data);
			}
			else if (child.getNodeName().equals("problem")){
				problem = data;
			}
			else if (child.getNodeName().equals("context")){
				context = data;
			}
			else if (child.getNodeName().equals("solution")){
				solution = data;
			}
			else if (child.getNodeName().equals("implementation")){
				implementation = data;
			}
			else if (child.getNodeName().equals("example")){
				example = data;
			}
			else if (child.getNodeName().equals("url")){
				url = data;
			}
			else if (child.getNodeName().equals("refCategory")){
				int catID = Integer.parseInt(data.substring(1));
				problemcategory = catID;
			}
			else if (child.getNodeName().equals("refOntPos")){
				int ontID = Integer.parseInt(data.substring(1));
				posOntID.add(ontID);
			}
			else if (child.getNodeName().equals("refOntNeg")){
				int ontID = Integer.parseInt(data.substring(1));
				negOntID.add(ontID);
			}
			else if (child.getNodeName().equals("refChildDecision")){
				int decID = Integer.parseInt(data.substring(2));
				subDecID.add(decID);
			}
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
	private boolean inDatabase()
	{
		boolean found = false;
		String findQuery = "";

		if (fromXML)
		{
			RationaleDB db = RationaleDB.getHandle();
			Connection conn = db.getConnection();

			//find out if this pattern is already in the database
			Statement stmt = null; 
			ResultSet rs = null; 

			try {
				stmt = conn.createStatement(); 
				findQuery = "SELECT id FROM patterns where name='" +
						RationaleDBUtil.escape(this.name) + "'";
				rs = stmt.executeQuery(findQuery); 

				if (rs.next())
				{
					int ourid;
					ourid = rs.getInt("id");
					this.id = ourid;
					found = true;
				}
			}catch (SQLException ex) {
				RationaleDB.reportError(ex, "Pattern.inDatabase", findQuery); 
			}finally { 
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

	/**
	 * This is used when reading from an existing XMI file. This allows the user to choose
	 * which package to add the diagram to.
	 * @param path
	 * @return
	 */
	public HashMap<String, Integer> readXMIClassPackages(org.eclipse.emf.common.util.URI path){
		Resource resource = new ResourceSetImpl().getResource(path, true);
		EList<EObject> contents = resource.getContents();
		HashMap<String, Integer> package_index = new HashMap<String, Integer>();
		for (int i = 0; i < contents.size(); i++){
			if (contents.get(i) instanceof Package){
				package_index.put(((Package) contents.get(i)).getName(), i);
			}
		}
		return package_index;
	}

	/**
	 * This is used to add an XMI class diagram export of this pattern into a package of an
	 * existing XMI file.
	 * @param packageIndex The index of the package the user has chosen.
	 * @param path The enhanced URI path where the existing XMI file is located
	 * @param numInstances For each positive integer i, 
	 * numInstances[i] = Number of instances of participant db.getParticipants...().get(i) to be created in the class diagram.
	 * INVARIANT: numInstances.size() == db.getParticipants...().size()
	 * @return True when successful. False when unsuccesful.
	 */
	public String addXMIClassToModel(org.eclipse.emf.common.util.URI path, Vector<Integer> numInstances){
		XMIResource resource = (XMIResource) new ResourceSetImpl().getResource(path, true);
		//Find the model...
		Model model = (Model) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.MODEL);

		//If model is not found, fails the save.
		if (model == null) {
			System.err.println("Cannot Obtain Model Data");
			return null;
		}

		Package package_ = model.createNestedPackage(name);
		addXMIClass(package_, numInstances, resource);

		//Save to disk
		try{
			resource.save(null);
			String packageID = resource.getID(package_);
			return packageID;
		} catch (IOException e){
			e.printStackTrace();
		}
		//Error saving to disk
		return null;
	}

	/**
	 * Create a new XMI Class Diagram.
	 * Generate classes as desired, and maintain all relationships.
	 * Create operations to maintain multiplicity equals to number of classes associated in the diagram.
	 * @param path An enhanced URI path description. This is the path where the xmi file is saved.
	 * @param numInstances For each positive integer i, 
	 * numInstances[i] = Number of instances of participant db.getParticipants...().get(i) to be created in the class diagram.
	 * INVARIANT: numInstances.size() == db.getParticipants...().size()
	 * @return True when successful. False when unsuccessful.
	 */
	public String newXMIClass(org.eclipse.emf.common.util.URI path, Vector<Integer> numInstances){
		XMIResourceFactoryImpl resourceFactoryImpl = new XMIResourceFactoryImpl();
		XMIResource resource = (XMIResource) resourceFactoryImpl.createResource(path);

		Model model = UMLFactory.eINSTANCE.createModel();
		model.setName(name);

		Package package_ = model.createNestedPackage(name);
		addXMIClass(package_, numInstances, resource);
		resource.getContents().add(model);

		//Save to disk
		try{
			resource.save(null);
			String packageID = resource.getID(package_);
			return packageID;
		} catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method is used to add classes to the packages.
	 * @param package_
	 * @param numInstances
	 */
	private void addXMIClass(Package package_, Vector<Integer> numInstances, XMIResource resource){
		patternElements = new Vector<PatternElement>();
		resource.setID(package_, EcoreUtil.generateUUID());

		//Create classes
		RationaleDB db = RationaleDB.getHandle();
		Vector<PatternParticipant> parts = db.getParticipantsFromPatternID(id);
		if (parts.size() != numInstances.size()) 
			throw new RuntimeException("Size of number of instances and participants do not match.");
		Class[][] classes = new org.eclipse.uml2.uml.Class[parts.size()][];
		for (int i = 0; i < parts.size(); i++){
			classes[i] = new org.eclipse.uml2.uml.Class[numInstances.get(i)];
			for (int j = 0; j < classes[i].length; j++){
				//Add class to package.
				classes[i][j] = package_.createOwnedClass(parts.get(i).getName() + (j + 1), false);
				resource.setID(classes[i][j], EcoreUtil.generateUUID());
				patternElements.add(new PatternElement(parts.get(i).getID(), -1, -1, resource.getID(classes[i][j])));
			}
		}

		//Class-Class associations
		for (int i = 0; i < classes.length; i++){
			for (int j = 0; j < classes.length; j++){
				//Check what kind of association is needed...
				//If no associations are needed, break out the inner loop.
				if (parts.get(i).getParticipants().containsKey(parts.get(j).getID())){
					int type = parts.get(i).getParticipants().get(parts.get(j).getID());
					for (int k = 0; k < classes[i].length; k++){
						for (int l = 0; l < classes[j].length; l++){
							if (type == UMLRelation.GENERALIZATION){
								String genID = EcoreUtil.generateUUID();
								resource.setID(classes[i][k].createGeneralization(classes[j][l]), genID);
								patternElements.add(new PatternElement(parts.get(i).getID(), parts.get(j).getID(), UMLRelation.GENERALIZATION, genID));

							} else {
								String assocID = EcoreUtil.generateUUID();
								resource.setID(createXMIAssociation(type, classes[i][k], classes[j][l]), 
										assocID);
								patternElements.add(new PatternElement(parts.get(i).getID(), parts.get(j).getID(), type, assocID));
							}
						}
					}
				}

			}
		}

		//Add operations
		for (int i = 0; i < classes.length; i++){
			for (int j = 0; j < classes[i].length; j++){
				for (int k = 0; k < parts.get(i).getOperations().size(); k++){
					ParticipantOperation op = parts.get(i).getOperations().get(k);
					int numOperationInstances = 1;
					if (op.getAssociatedParticipant() != -1){
						PatternParticipant pp = new PatternParticipant();
						pp.fromDatabase(op.getAssociatedParticipant());
						numOperationInstances = numInstances.get(parts.indexOf(pp));
					}
					for (int l = 0; l < numOperationInstances; l++){
						classes[i][j].createOwnedOperation(op.getName() + (l + 1), null, null);
					}
				}
			}
		}

		//Sort the vector so that the classes will be on the front of the vector.
		Collections.sort(patternElements, new Comparator<PatternElement>(){
			public int compare(PatternElement o1, PatternElement o2) {
				if (o1.getPart2ID() < o2.getPart2ID()){
					return -1;
				}
				if (o1.getPart2ID() == o2.getPart2ID()){
					return 0;
				}
				return 1;
			}

		});

	}


	/**
	 * Create an association between two classes. Note generalization is not created in this method!
	 * @param type UMLRelation compatible integer. (type != UMLRelation.GENERALIZATION)
	 * @param type1 The source class
	 * @param type2 The target class
	 * @return An UML2 Association.
	 */
	private org.eclipse.uml2.uml.Association createXMIAssociation(int type, org.eclipse.uml2.uml.Class type1, org.eclipse.uml2.uml.Class type2){
		boolean end1IsNavigable, end2IsNavigable;
		AggregationKind end1Aggregation, end2Aggregation;
		String end1Name, end2Name;
		int end1LowerBound = 0, end1UpperBound = LiteralUnlimitedNatural.UNLIMITED, end2LowerBound = 0, end2UpperBound = LiteralUnlimitedNatural.UNLIMITED;
		end1Name = "";
		end2Name = "";
		switch (type){
		case UMLRelation.AGGREGATION:
			end1Aggregation = AggregationKind.COMPOSITE_LITERAL;
			end1IsNavigable = true;
			end2IsNavigable = false;
			end2Aggregation = AggregationKind.NONE_LITERAL;
			break;
		case UMLRelation.DELEGATION:
			end1IsNavigable = true;
			end2IsNavigable = false;
			end1Aggregation = AggregationKind.NONE_LITERAL;
			end2Aggregation = AggregationKind.NONE_LITERAL;
			break;
		case UMLRelation.GENERALIZATION:
			throw new RuntimeException("Should have created a generalization instead of an association!");
		default:
			end1IsNavigable = false;
			end2IsNavigable = false;
			end1Aggregation = AggregationKind.NONE_LITERAL;
			end2Aggregation = AggregationKind.NONE_LITERAL;
		}

		org.eclipse.uml2.uml.Association association = type1.createAssociation(end1IsNavigable, end1Aggregation, end1Name, end1LowerBound, end1UpperBound,
				type2, end2IsNavigable, end2Aggregation, end2Name, end2LowerBound, end2UpperBound);
		return association;
	}
	
	/**
	 * Get the pattern evaluation score.
	 * @return
	 */
	public PatternEvalScore getPatternScore(){
		PatternEvalScore ret = new PatternEvalScore(this);
		Vector<Requirement> exact = ret.getExactSati(), contrib = ret.getContribSati(), possible = ret.getPossibleSati();
		
		RationaleDB db = RationaleDB.getHandle();
		Vector<Requirement> nfrs = db.getNFRs();
		
		//Satisfactions
		for (OntEntry pOnt: getPosiOnts()){
			for (Requirement req: nfrs){
				OntEntry rOnt = req.getOntology();
				//Exact
				if (rOnt.getName().equals(pOnt.getName())){
					exact.add(req);
					continue;
				}
				
				//Contribution
				Vector<OntEntry> ontList = db.getOntologyDescendents(rOnt.getName());
				for (OntEntry dOnt: ontList){
					if (dOnt.getName().equals(pOnt.getName())){
						contrib.add(req);
						continue;
					}
				}
				
				//Possible
				ontList = db.getOntologyDescendents(pOnt.getName());
				for (OntEntry dOnt: ontList){
					if (dOnt.getName().equals(rOnt.getName())){
						possible.add(req);
						continue;
					}
				}
			}
		}
		
		exact = ret.getExactViol();
		contrib = ret.getContribViol();
		possible = ret.getPossibleViol();
		
		//Violations
		for (OntEntry pOnt: getNegaOnts()){
			for (Requirement req: nfrs){
				OntEntry rOnt = req.getOntology();
				//Exact
				if (rOnt.getName().equals(pOnt.getName())){
					exact.add(req);
					continue;
				}
				
				//Contribution
				Vector<OntEntry> ontList = db.getOntologyDescendents(rOnt.getName());
				for (OntEntry dOnt: ontList){
					if (dOnt.getName().equals(pOnt.getName())){
						contrib.add(req);
						continue;
					}
				}
				
				//Possible
				ontList = db.getOntologyDescendents(pOnt.getName());
				for (OntEntry dOnt: ontList){
					if (dOnt.getName().equals(rOnt.getName())){
						possible.add(req);
						continue;
					}
				}
			}
		}
		
		return ret;
	}
}