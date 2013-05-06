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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.jface.preference.IPreferenceStore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
import edu.wpi.cs.jburge.SEURAT.views.PatternLibrary;
import SEURAT.preferences.PreferenceConstants;

/**
 * This class contains some very important utility methods for ensuring that
 * SQL table names and statements are properly formatted depending on the database
 * type currently in use, and thus ensuring the proper behavior of SEURAT's
 * database queries and statements.  It also holds the utility methods for
 * exporting and importing the argument ontology to and from XML.
 */
public class RationaleDBUtil 
{
	/**
	 * Enumeration consisting of the different database types that SEURAT uses.
	 */
	public enum DBTypes
	{
		MYSQL,
		DERBY,

		UNKNOWN
	}

	/**
	 * The preference store for the SEURAT plugin.
	 */
	private static IPreferenceStore store = SEURATPlugin.getDefault().getPreferenceStore();

	/**
	 * Examines the preference store and determines the DB type that is currently in use.
	 * @return DBTypes.x, where x is the DB type currently in use
	 */
	public static DBTypes checkDBType()
	{
		String lType = store.getString(PreferenceConstants.P_DATABASETYPE);
		if( lType.equals(PreferenceConstants.DatabaseType.DERBY) )
			return DBTypes.DERBY;
		else
			if( lType.equals(PreferenceConstants.DatabaseType.MYSQL))
				return DBTypes.MYSQL;

		return DBTypes.UNKNOWN;			
	}

	/**
	 * Properly escapes a table name based on the database type that is in use.
	 * @param pName the table name to be escaped
	 * @return the escaped table name
	 */
	public static String escapeTableName(String pName)
	{
		DBTypes lType = checkDBType();

		pName=pName.toUpperCase();

		if( lType == DBTypes.MYSQL )
			return "`" + pName + "`";

		if( lType == DBTypes.DERBY )
			return "\"" + pName + "\"";

		// Use quote characters (") as default.
		return "\"" + pName + "\"";		
	}

	/**
	 * The user can type all sorts of stuff into a text box that
	 * will make the database unhappy. Put escape characters in
	 * where needed
	 * @param txt - the text to be encoded prior to saving
	 * @return the text with escape characters added
	 */
	public static String escape(String txt) {
		if (txt == null) {
			return new String("");
		}
		if (txt.length() > 255)
		{
			txt = txt.substring(0, 254);
		}

		if( checkDBType() == DBTypes.MYSQL ) {
			Matcher matcher = Pattern.compile("([\'\"\'])").matcher(txt);
			String out = matcher.replaceAll("\\\\$1");
			//   	System.out.println(out);
			return out;
		}
		else {// TREAT DERBY AS DEFAULT
			// if (checkDBType() == DBTypes.DERBY )
			Matcher matcher = Pattern.compile("([\'])").matcher(txt);
			String out = matcher.replaceAll("'$1");
			return out;
		}
	}

	/**
	 * Decode text read in from the database to remove any escape characters
	 * we added earlier
	 * @param txt - the text read in
	 * @return the text after decoding
	 */
	public static String decode(String txt) {
		if (txt == null) {
			return new String("");
		}
		if( checkDBType() == DBTypes.MYSQL ) {
			Matcher matcher = Pattern.compile("\\\\([\'\"])").matcher(txt);
			String out = matcher.replaceAll("'$1");
			//    	System.out.println(out);
			return out;
		}
		else { // TREAT DERBY AS DEFAULT
			// if( checkDBType() == DBTypes.DERBY )
			//Matcher matcher = Pattern.compile("('')").matcher(txt);
			//	String out = matcher.replaceAll("'");
			return txt;
		}
	}

	/**
	 * Returns the correct string represention of a boolean value based on 
	 * current database type.
	 * @param val true or false
	 * @return the string representation of val for the current database type
	 */
	public static String correctBooleanStr(boolean val) {
		if( checkDBType() == DBTypes.MYSQL ) {
			if (val) return "True";
			else return "False";
		} else { // TREAT DERBY AS DEFAULT
			if (val) return "1";
			else return "0";
		}
	}
	
	/**
	 * Moved it from exportToXML method for readability issues.
	 * @param ratDoc
	 * @return
	 * @throws ParserConfigurationException
	 */
	private static Element exportPatternLibrary(Document ratDoc) throws ParserConfigurationException{
		RationaleDB db = RationaleDB.getHandle();
		
		//I should now get the pattern xml written...
		Element patternLib = ratDoc.createElement("DR:patternLibrary");

		//First, I should export the problem category database to XML in sequence...
		Iterator<String[]> problemcategories = db.getProblemCategoryData().iterator();
		while (problemcategories.hasNext()){
			String[] content = problemcategories.next();
			Element curE = ratDoc.createElement("DR:patternCategory");
			curE.setAttribute("rid", "c" + content[0]);
			curE.setAttribute("name", content[1]);
			curE.setAttribute("type", content[2]);
			patternLib.appendChild(curE);
		}
		//Need to keep track of pattern decisions from each patterns.
		Vector<PatternDecision> patternDecisions = new Vector<PatternDecision>();
		Vector<Integer> subDecIDs = new Vector<Integer>();
		//Next, I should export the patterns
		Iterator<edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern> patterns = db.getPatternData().iterator();
		while (patterns.hasNext()){
			edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern cur = patterns.next();
			Element curE = cur.toXML(ratDoc);
			patternLib.appendChild(curE);
			//patternDecisions.addAll(cur.getSubDecisions());
			
			//There might be duplicated entries, so only add those to XML if there's no duplication.
			Iterator<PatternDecision> subDec = cur.getSubDecisions().iterator();
			while (subDec.hasNext()){
				PatternDecision dec = subDec.next();
				if (!subDecIDs.contains((dec.getID()))){
					patternDecisions.add(dec);
					subDecIDs.add(dec.getID());
				}
			}
		}

		//Then, I need to export the pattern decisions
		Iterator<PatternDecision> pdi = patternDecisions.iterator();
		while (pdi.hasNext()){
			PatternDecision cur = pdi.next();
			Element curE = cur.toXML(ratDoc);
			patternLib.appendChild(curE);
		}
		
		//Finally, export pattern participants and operations...
		Iterator<PatternParticipant> pi = db.getParticipantsFromPatternID(-1).iterator();
		while (pi.hasNext()){
			PatternParticipant cur = pi.next();
			Element curE = cur.toXML(ratDoc);
			patternLib.appendChild(curE);
		}
		
		Iterator<ParticipantOperation> oi = db.getAllParticipantOperations().iterator();
		while (oi.hasNext()){
			ParticipantOperation cur = oi.next();
			Element curE = cur.toXML(ratDoc);
			patternLib.appendChild(curE);
		}
		
		return patternLib;
	}
	
	private static Element exportTacticLibrary(Document ratDoc) throws ParserConfigurationException{
		RationaleDB db = RationaleDB.getHandle();
		
		Element tacticLib = ratDoc.createElement("DR:tacticLibrary");
		
		Iterator<Tactic> tactics = db.getTacticData().iterator();
		while (tactics.hasNext()){
			Tactic cur = tactics.next();
			Element curE = cur.toXML(ratDoc);
			tacticLib.appendChild(curE);
			
			Iterator<TacticPattern> tpi = cur.getPatterns().iterator();
			while (tpi.hasNext()){
				TacticPattern tp = tpi.next();
				Element tpE = tp.toXML(ratDoc);
				tacticLib.appendChild(tpE);
			}
		} 
		
		return tacticLib;
	}

	/**
	 * This method handles the exporting of a database to XML. Currently
	 * the method only exports the pattern library and the ontology.
	 * @param ontFile
	 * @return
	 */
	public static boolean exportToXML(String xmlFile){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document ratDoc = null;

		try{
			DocumentBuilder builder = factory.newDocumentBuilder();

			File xmlf = new File(xmlFile);

			//Right now, let's not worry about the case when the XML exists.
			//If exists, then export fails for now...
			if (xmlf.exists()){
				return false;
			}
			ratDoc = builder.newDocument();
			// set up the document
			Element ratTop = ratDoc.createElement("DR:rationale");
			ratTop.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			ratTop.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
			ratTop.setAttribute("xmlns:DR", "http://www.cs.wpi.edu/~jburge/DRXML/Rationale");
			ratTop.setAttribute("xmlns", "http://www.cs.wpi.edu/~jburge/DRXML/Rationale");
			ratTop.setAttribute("xsi:schemaLocation","http://www.cs.wpi.edu/~jburge/DRXML/Rationale http://www.cs.wpi.edu/~jburge/DRXML/Rationale.xsd");
			Element ratNext = ratDoc.createElement("DR:argOntology");
			//process argument ontology
			OntEntry topE = new OntEntry();
			topE.fromDatabase(1); // get root of ontology from database
			System.out.println(topE.name + " " + topE.description);
			Element newTopOnt = topE.toXML(ratDoc); // construct the new ontology xml
			ratNext.appendChild(newTopOnt);
			ratTop.appendChild(ratNext);
			ratDoc.appendChild(ratTop);
			System.out.println("child appended");

			Element patternLib = exportPatternLibrary(ratDoc);
			ratTop.appendChild(patternLib);
			
			Element tacticLib = exportTacticLibrary(ratDoc);
			ratTop.appendChild(tacticLib);

		} catch (ParserConfigurationException e){
			e.printStackTrace();
			return false;
		}

		try {
			// set up a transformer
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.INDENT, "no");
			//create a string from the xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(ratDoc);
			trans.transform(source, result);
			String xmlString = sw.toString();
			// write the file
			FileWriter fw = new FileWriter(xmlFile);
			fw.write(xmlString);
			fw.close();
			return true;
		} catch (TransformerConfigurationException e) {
			System.err.println( e.toString());
		} catch (TransformerException tfe) {
			System.err.println( tfe.toString());
		} catch(IOException e){
			System.err.println(e.toString());
		}
		return false;
	}

	/**
	 * This method handles the exporting of a database's argument ontology to XML.
	 * The file to export to is hardcoded in the RationaleDB class but
	 * could be moved to the preference page in the future, which is why this
	 * method takes it as a parameter.
	 * 
	 * @param ontFile - the filename to import the XML ontology from
	 * @return boolean indicating whether or not the export was successful
	 */
	public static boolean exportArgumentOntology(String ontFile) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document ratDoc;

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			File ontf = new File(ontFile);
			if (ontf.exists()) {
				ratDoc = builder.parse(ontf);
				Element ratTop = ratDoc.getDocumentElement();
				//this should be our parent Rationale element

				Node nextNode = ratTop.getFirstChild();
				Element ratNext = null;

				// Loop to handle class cast exceptions (sometimes the first
				// child will be text or something other than an element)
				while (nextNode != null) {
					try {
						ratNext = (Element) nextNode;
						break; // got the element
					} catch (ClassCastException cce) {
						//System.out.println("cce");
					}
					nextNode = nextNode.getNextSibling();
				}

				if (ratNext == null) {
					System.out.println("argument ontology not found in " + ontFile);
				} else {
					String nextName;
					nextName = ratNext.getNodeName();

					//here we check the type, then process
					if (nextName.compareTo("DR:argOntology") == 0)
					{
						System.out.println("found the ontology");
						//need to get the root ontology entry
						Element topOnt = (Element) ratNext.getFirstChild();
						//process argument ontology
						OntEntry topE = new OntEntry();
						topE.fromDatabase(1); // get root of ontology from database
						System.out.println(topE.name + " " + topE.description);
						Element newTopOnt = topE.toXML(ratDoc); // construct the new ontology xml

						// replace old topOnt with new one
						ratNext.removeChild(topOnt);
						ratNext.appendChild(newTopOnt);
						System.out.println("child replaced");
					}
					else {
						System.out.println("something other than argument ontology specified in " + ontFile);
					}
				}
			} else {
				ratDoc = builder.newDocument();
				// set up the document
				Element ratTop = ratDoc.createElement("DR:rationale");
				ratTop.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
				ratTop.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
				ratTop.setAttribute("xmlns:DR", "http://www.cs.wpi.edu/~jburge/DRXML/Rationale");
				ratTop.setAttribute("xmlns", "http://www.cs.wpi.edu/~jburge/DRXML/Rationale");
				ratTop.setAttribute("xsi:schemaLocation","http://www.cs.wpi.edu/~jburge/DRXML/Rationale http://www.cs.wpi.edu/~jburge/DRXML/Rationale.xsd");
				Element ratNext = ratDoc.createElement("DR:argOntology");
				//process argument ontology
				OntEntry topE = new OntEntry();
				topE.fromDatabase(1); // get root of ontology from database
				System.out.println(topE.name + " " + topE.description);
				Element newTopOnt = topE.toXML(ratDoc); // construct the new ontology xml
				ratNext.appendChild(newTopOnt);
				ratTop.appendChild(ratNext);
				ratDoc.appendChild(ratTop);
				System.out.println("child appended");
			}
			// now we do the actual writing
			try {
				// set up a transformer
				TransformerFactory transfac = TransformerFactory.newInstance();
				Transformer trans = transfac.newTransformer();
				trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
				trans.setOutputProperty(OutputKeys.INDENT, "no");
				//create a string from the xml tree
				StringWriter sw = new StringWriter();
				StreamResult result = new StreamResult(sw);
				DOMSource source = new DOMSource(ratDoc);
				trans.transform(source, result);
				String xmlString = sw.toString();
				// write the file
				FileWriter fw = new FileWriter(ontf);
				fw.write(xmlString);
				fw.close();
				return true;
			} catch (TransformerConfigurationException e) {
				System.err.println( e.toString());
			} catch (TransformerException tfe) {
				System.err.println( tfe.toString());
			}
		} catch (SAXException sce) {
			System.err.println( sce.toString());
		} catch (IOException ioe) {
			System.err.println (ioe.toString());
		} catch (ParserConfigurationException pce) {
			System.err.println (pce.toString());
		}
		return false;
	}

	/**
	 * This is the method to import XML. Currently, we're only importing the ontology
	 * and the patternLibrary. But this can be easily changed here...
	 * @param xmlFile
	 * @return true if import is successful, false otherwise.
	 */
	public static boolean importFromXML(String xmlFile){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document ratDoc;
		System.out.println("Import from XML has started...");
		try{
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource source = new InputSource(new InputStreamReader(new FileInputStream(new File(xmlFile))));
			source.setEncoding("UTF-8");
			ratDoc = builder.parse(source);
			Element ratTop = ratDoc.getDocumentElement();

			//The child of document element tells me which xml I'm importing from...
			NodeList ratList = ratTop.getChildNodes();
			for (int i = 0; i < ratList.getLength(); i++){
				Node rat = ratList.item(i);
				String ratname = rat.getNodeName();
				System.out.println("Identifier: " + ratname);
				if (ratname.equals("DR:argOntology")){
					System.out.println("Found Argument Ontology. Importing...");
					xmlImportOntology(rat);
				}
				else if (ratname.equals("DR:patternLibrary")){
					System.out.println("Found Pattern Library. Importing...");
					xmlImportPatternLibrary(rat);
				}
				else if (ratname.equals("DR:tacticLibrary")){
					System.out.println("Found Tactic Library. Importing...");
					xmlImportTacticLibrary(rat);
				}
			}
			
			return true;
		} catch (SAXException e){
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		} catch (ParserConfigurationException e){
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * Given the top node DR:tacticLibrary, import its children
	 * @param tacticLib
	 * @return
	 */
	public static boolean xmlImportTacticLibrary(Node tacticLib){
		if (!tacticLib.getNodeName().equals("DR:tacticLibrary")){
			System.err.println("tacticLibrary node is illegal");
			return false;
		}
		
		NodeList libraryNodes = tacticLib.getChildNodes();
		
		for (int i = 0; i < libraryNodes.getLength(); i++){
			Node libItem = libraryNodes.item(i);
			String libName = libItem.getNodeName();
			if (libName.equals("DR:tactic")){
				Tactic tactic = new Tactic();
				tactic.fromXML((Element) libItem);
			}
			else if (libName.equals("DR:tacticpattern")){
				TacticPattern tp = new TacticPattern();
				tp.fromXML((Element) libItem);
			}
		}
		
		System.out.println("Import of tactic library was successful");
		return true;
	}
	
	/**
	 * Given the top node DR:patternLibrary, import from its children.
	 * @param patternLib
	 * @return true if import is successful. false otherwise.
	 */
	public static boolean xmlImportPatternLibrary(Node patternLib){
		if (!patternLib.getNodeName().equals("DR:patternLibrary")){
			System.err.println("patternLibrary node is illegal");
			return false;
		}
		RationaleDB db = RationaleDB.getHandle();
		NodeList libraryNodes = patternLib.getChildNodes();
		Vector<edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern> patterns = 
			new Vector<edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern>();
		Vector<PatternDecision> patternDecisions = new Vector<PatternDecision>();
		Vector<PatternParticipant> pp = new Vector<PatternParticipant>();
		Vector<ParticipantOperation> po = new Vector<ParticipantOperation>();
		
		for (int i = 0; i < libraryNodes.getLength(); i++){
			Node libItem = libraryNodes.item(i);
			String libName = libItem.getNodeName();
			
			if (libName.equals("DR:patternCategory")){
				Element element = (Element) libItem;
				String rid = element.getAttribute("rid");
				String name = element.getAttribute("name");
				String type = element.getAttribute("type");
				try{
					int id = Integer.parseInt(rid.substring(1));
					db.addProblemCategory(id, name, type);
				} catch (RuntimeException e){
					System.err.println("Invalid rid for pattern category... Skipping...");
				}
			}
			else if (libName.equals("DR:pattern")){
				edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern pattern = 
					new edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern();
				pattern.fromXML((Element) libItem);
				patterns.add(pattern);
				System.out.println("Added pattern " + pattern.getName());
			}
			else if (libName.equals("DR:patternDecision")){
				PatternDecision pd = new PatternDecision();
				pd.fromXML((Element) libItem);
				patternDecisions.add(pd);
			}
			else if (libName.equals("DR:patternparticipant")){
				PatternParticipant p = new PatternParticipant();
				p.fromXML((Element) libItem);
				pp.add(p);
			}
			else if (libName.equals("DR:participantoperation")){
				ParticipantOperation o = new ParticipantOperation();
				o.fromXML((Element) libItem);
				po.add(o);
			}

		}
		System.out.println("Associating pattern to child decisions...");
		//Associate pattern and child decisions.
		for (int i = 0; i < patterns.size(); i++){
			edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern pattern = patterns.get(i);
			Iterator<Integer> iteratorDecID = pattern.iteratorSubDecID();
			while (iteratorDecID.hasNext()){
				int pdID = iteratorDecID.next();
				db.assocPatternAndDecisionFromXML(pattern.getID(), pdID);
			}
		}
		
		//Storing participants and participants' operations to database...
		for (int i = 0; i < pp.size(); i++){
			pp.get(i).toDatabase();
		}
		for (int i = 0; i < po.size(); i++){
			po.get(i).toDatabase();
		}
		
		System.out.println("Import of pattern library was successful.");
		
		//At here, the database satisfies invariant and has been imported. But the elements are not correct.
		//Easiest way to update the tree is to rebuild tree from database.
		
		
		return true;
	}

	/**
	 * Given the top node DR:argOntology, import from the children
	 * @param argOntology
	 * @return true if import is successful. false otherwise.
	 */
	public static boolean xmlImportOntology(Node argOntology){
		if (argOntology.getNodeName().equals("DR:argOntology")){
			//need to get the root ontology entry
			Element topOnt = (Element) argOntology.getFirstChild();
			//process argument ontology
			OntEntry topE = new OntEntry();
			topE.fromXML(topOnt, null); //null parent
			// This will get us the entire ontology and relationships,
			// so we should be able to simply send to database now
			// Root of argument ontology is always id 0
			topE.toDatabase(0);
			return true;
		}
		System.err.println("ArgOntology node is illegal.");
		return false;
	}

	
	/**
	 * This method handles the importing of an argument ontology from XML.
	 * The file to import from is hardcoded in the RationaleDB class but
	 * could be moved to the preference page in the future, which is why this
	 * method takes it as a parameter.
	 * 
	 * @param ontFile - the filename to import the XML ontology from
	 * @return boolean indicating success or failure
	 * @deprecated This method is no longer in use. We should use importFromXML(String) method.
	 */
	public static boolean importArgumentOntology(String ontFile) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document ratDoc;

		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			ratDoc = builder.parse(new File(ontFile));
			Element ratTop = ratDoc.getDocumentElement();
			//this should be our parent Rationale element

			Node nextNode = ratTop.getFirstChild();
			Element ratNext = null;

			// Loop to handle class cast exceptions (sometimes the first
			// child will be text or something other than an element)
			while (nextNode != null) {
				try {
					ratNext = (Element) nextNode;
					break; // got the element
				} catch (ClassCastException cce) {
					//System.out.println("cce");
				}
				nextNode = nextNode.getNextSibling();
			}

			if (ratNext == null) {
				System.out.println("argument ontology not found in " + ontFile);
			} else {
				String nextName;
				nextName = ratNext.getNodeName();

				//here we check the type, then process
				if (nextName.compareTo("DR:argOntology") == 0)
				{
					//					System.out.println("found the ontology");
					//need to get the root ontology entry
					Element topOnt = (Element) ratNext.getFirstChild();
					//process argument ontology
					OntEntry topE = new OntEntry();
					topE.fromXML(topOnt, null); //null parent
					// This will get us the entire ontology and relationships,
					// so we should be able to simply send to database now
					// Root of argument ontology is always id 0
					topE.toDatabase(0);
					return true;
				}
				else {
					System.out.println("something other than argument ontology specified in " + ontFile);
				}
			}
		} catch (SAXException sce) {
			System.err.println( sce.toString());
		} catch (IOException ioe) {
			System.err.println (ioe.toString());
		} catch (ParserConfigurationException pce) {
			System.err.println (pce.toString());
		}
		return false;
	}


	/**
	 * Checks to see if an entry where name = given "name" is in the table "type".
	 * @param name
	 * @param type
	 * @return true if there is such entry, false otherwise.
	 */
	public static boolean isExist(String name, String type)
	{
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		Statement stmt = null; 
		ResultSet rs = null;
		String query = "";
		boolean flag = true;

		try{
			stmt = conn.createStatement();
			query = "SELECT name FROM " + RationaleDBUtil.escapeTableName(type) + " WHERE name = '"
			+ RationaleDBUtil.escape(name) + "'";
			rs = stmt.executeQuery(query);
			if(rs.next())
			{				
				flag = true;
			}
			else
			{
				flag = false;
			}
		}catch(SQLException ex){
			RationaleDB.reportError(ex,"Error in GeneratingCandidatePatternsDisplay", "");
		}finally{
			RationaleDB.releaseResources(stmt, rs);			
		}

		return flag;
	}
}
