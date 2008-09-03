package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.FileWriter;
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
import org.xml.sax.SAXException;

import edu.wpi.cs.jburge.SEURAT.SEURATPlugin;
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
		
		if( checkDBType() == DBTypes.MYSQL ) {
			Matcher matcher = Pattern.compile("([\'\"])").matcher(txt);
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
	 * This method handles the importing of an argument ontology from XML.
	 * The file to import from is hardcoded in the RationaleDB class but
	 * could be moved to the preference page in the future, which is why this
	 * method takes it as a parameter.
	 * 
	 * @param ontFile - the filename to import the XML ontology from
	 * @return boolean indicating success or failure
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
}
