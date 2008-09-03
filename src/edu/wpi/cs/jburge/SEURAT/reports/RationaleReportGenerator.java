package edu.wpi.cs.jburge.SEURAT.reports;

import java.util.Iterator;
import java.util.Vector;
import java.io.*;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;



import SEURAT.xmlIO.RationaleEntry;

import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.TreeObject;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

/**
 * A Report Generator Object Will Create A Report For
 * The Rationale. Once the constructor completes, the method
 * {@link RationaleReportGenerator#getReport()} can be used
 * to retrieve the generated report
 * 
 * Supported report formats currently include:
 * <UL>
 * <LI> {@link RationaleReportGenerator#PLAIN} - Plain Text
 * </UL>
 */
public class RationaleReportGenerator {
	/**
	 * A Constant Representing The Plain Text Report Format
	 */
	public static final int PLAIN = 0;
	/**
	 * Storage For The Report That Is Generated.
	 */
	String output = "";
	/**
	 * Storage For The Formatting Character Of The Plain Text Report Format
	 */
	String adder = "|  ";
	
	/**
	 * Controls whether or not Argument-Ontology entries are included in the report
	 */
	boolean argOnt = false;
	
	/**
	 * Recurse through the rationale tree and generate a text based
	 * representation of the elements in the tree.
	 * 
	 * @param children An Array Of Children To Generate Report Information For
	 */
	private void plainRecurse(TreeObject[] children){
		for(int x = 0; x < children.length; x++){
			// Determine prefix (rationale element type abbreviation)
			String type = children[x].getType().toString();
			String pfx = "";
			String sfx = "";
			if (type.equals("Assumption")) pfx = "(Asm) ";
			else if (type.equals("Constraint")) pfx = "(Cns) ";
			else if (type.equals("Contingency")) pfx = "(Cnt) ";
			else if (type.equals("Alternative Constraint")) pfx = "(Altcns) ";
			else pfx = "("+type.substring(0, 3)+") ";
			// Determine suffix (only for args and alts, displays the type or status)
			if (type.equals("Argument")) {
				Argument nArg = new Argument();
				nArg.fromDatabase(children[x].getName());
				sfx = " ("+nArg.getType().toString()+")";
			} else if (type.equals("Alternative")) {
				Alternative nAlt = new Alternative();
				nAlt.fromDatabase(children[x].getName());
				sfx = " ("+nAlt.getStatus().toString()+")";
			}
			// Output
			if(!children[x].getName().toString().equals("Argument-Ontology") || argOnt ){
				output += adder+pfx+children[x]+sfx+"\r\n";
				if(((TreeParent)children[x]).hasChildren()){
					adder += "|  ";
					plainRecurse (((TreeParent)children[x]).getChildren());
					adder = adder.substring(3);
				}
			}
		}
	}

	/**
	 * Construct A Report Generator Using A Set Of Selected Nodes
	 * in the rationale tree. Once the report generator is constructed
	 * the report can be retrieved with {@link RationaleReportGenerator#getReport()}
	 * 
	 * @param shell The Shell Object Which Owns The Rationale Tree
	 * @param sel The Selected Nodes In The Rationale Tree Should Be Of Type IStructuredSelection
	 * @param format The Report Format To Be Generated (use {@link RationaleReportGenerator#PLAIN})
	 */
	public RationaleReportGenerator(Shell shell, Object sel, int format, boolean argOntn){
		/* Broken xml out
		RationaleDB db = RationaleDB.getHandle();
		FileOutputStream fios = null;
		try{
			fios = new FileOutputStream(fileName);
			db.saveData(fios);
			fios.close();
		}catch(IOException e){
			//alert the user
		}
		*/
		argOnt = argOntn;
		IStructuredSelection isel = (IStructuredSelection) sel;
		//Iterator selI = isel.iterator();
		if(format == PLAIN){
			TreeParent tp = (TreeParent)isel.getFirstElement();
			output += ""+tp+"\r\n";
			TreeObject[] children = tp.getChildren();
			plainRecurse(children);
		}
		/* old way; remove me
		try{
			System.out.println(fileName);
			FileWriter fios = new FileWriter(fileName);
			fios.write(output);
			fios.close();
		}catch(IOException ioe){
			//alert user
			System.out.println("FAIL");
		}
		*/
	}
	
	/**
	 * Accessor Method To Retrieve The Current State Of The Report Generation
	 * 
	 * @return a string containing the report
	 */
	public String getReport(){
		return output;
	}
}
