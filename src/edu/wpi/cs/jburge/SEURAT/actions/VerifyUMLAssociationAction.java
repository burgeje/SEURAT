package edu.wpi.cs.jburge.SEURAT.actions;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Vector;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

import java.sql.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.UMLPackage;

import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternParticipant;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDBUtil;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

/**
 * This class allows the user to verify whether the model still contains the alternative.
 * @author yechen
 *
 */
public class VerifyUMLAssociationAction extends Action{
	public static final int CLASSNOTFOUND = 1;
	public static final int ASSOCIATIONVIOLATED = 2;
	private Alternative alt;
	private Vector<PatternElement> patternElements; //Stores all valid pattern elements in DB to memory.
	private Vector<PatternParticipant> participants; //Stores all pattern participants othe pattern.
	private String packageXMIID = null;
	private String modelFilePath = null;
	private Package package_; //The XMI package containing the pattern.
	private XMIResource resource; //The XMI resource loading the model

	private boolean[] elementsVisited; //Used for association checking. Can't pass by value.
	private Vector<PatternElement> associations; //Only contains existing associations of patternElements
	private boolean hasRun = false, isViolated = false;
	private java.util.Date dateRun = null;
	private String classViolator1 = null, classViolator2 = null; //Used for extra error message displays.
	private int errorno = 0;

	private TreeViewer viewer;

	//Getters
	public boolean hasRun() {
		return hasRun;
	}

	public boolean isViolated() {
		return isViolated;
	}

	public java.util.Date getExecDate(){
		return dateRun;
	}

	public int getErrorNo(){
		return errorno;
	}

	public String getClassViolator1(){
		return classViolator1;
	}

	/**
	 * Used when the user attempts a manual verification.
	 * @param viewer
	 */
	public VerifyUMLAssociationAction(TreeViewer viewer){
		this.viewer = viewer;
		setText("Verify UML Association");
		setToolTipText("Verify whether the association has been violated. i.e.: Whether the critical relations are still intact.");
	}

	/**
	 * Given alternative name, constructor the action.
	 * @param altName
	 */
	public VerifyUMLAssociationAction(String altName){
		viewer = null;
		alt = new Alternative();
		alt.fromDatabase(altName);
		if (alt.getID() <= 0){
			throw new IllegalArgumentException("Invalid Alternative Name");
		}
		setText("Verify UML Association");
		setToolTipText("Verify whether the association has been violated. i.e.: Whether the critical relations are still intact.");
	}

	public void run(){
		hasRun = false;
		errorno = 0;
		if (viewer != null){
			ISelection selection = viewer.getSelection();
			Object obj = ((IStructuredSelection)selection).getFirstElement();

			if (obj instanceof TreeParent){
				TreeParent ourElement = (TreeParent) obj;
				if (ourElement.getType() == RationaleElementType.ALTERNATIVE){
					alt = new Alternative();
					alt.fromDatabase(ourElement.getName());
				}
				else {
					displayErrorMsg("Selected item is not an alternative.");
					return;
				}
			}
			else {
				displayErrorMsg("Selected item is not a tree parent?!");
				return;
			}
		}
		dateRun = Calendar.getInstance().getTime();
		try{
			isViolated = checkViolation();
		} catch (Exception e){
			System.out.println("ERROR while checking violation: " + e.getMessage());
			isViolated = true;
		}
		hasRun = true;
		if (viewer != null){
			MessageBox mbox = new MessageBox(viewer.getControl().getShell(), SWT.ICON_INFORMATION);
			String message = "Check initated at " + DateFormat.getDateTimeInstance().format(dateRun);
			message += " yields the following conclusion: ";
			if (isViolated) {
				if (errorno == 0){
					message += "Cannot read or parse the model. The model may be under write lock, you may have insufficient permissions, or the model may be corrupted.";
				}
				else {
					message += "The alternative has been violated in the diagram. (";
					if (errorno == CLASSNOTFOUND){
						message += "No instance(class) of participant " + classViolator1 + " exists in the diagram.)";
					}
					else if (errorno == ASSOCIATIONVIOLATED){
						message += "One of the associations from " + classViolator1 + " is invalid.)";
					}
				}
			}
			else message += "The alternative is still intact in the diagram.";
			mbox.setMessage(message);
			mbox.setText("UML Verification Complete");
			mbox.open();
		}
	}

	private void displayErrorMsg(String message){
		Shell shell;
		if (viewer != null){
			shell = viewer.getControl().getShell();
		}
		else shell = new Shell();
		MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
		mbox.setMessage(message);
		mbox.setText("Unable to Verify UML");
		mbox.open();
	}

	/**
	 * Checks whether there is a violation in the diagram.
	 * @return False when there is no violation. True if there is a violation.
	 */
	private boolean checkViolation(){
		_ASSERT(alt.getID() > 0);
		loadData();
		if (participants.isEmpty()) return false;
		truncateData();
		if (patternElements.isEmpty()) return true;

		//Check for existence of every participant
		if (!areClassesExistInModel()) return true;

		//Check for associations
		associations = new Vector<PatternElement>();
		for (int i = 0; i < patternElements.size(); i++){
			if (patternElements.get(i).getPart2ID() > 0 ){
				associations.add(patternElements.get(i));
				//Add inverse relationship
				PatternElement inv = new PatternElement(patternElements.get(i).getPart2ID(), 
						patternElements.get(i).getPart1ID(), (0 - patternElements.get(i).getAssocType()), 
						patternElements.get(i).getXMIID());
				inv.setAltID(alt.getID());
				associations.add(inv);
			}
		}
		elementsVisited = new boolean[patternElements.size()];
		for (int i = 0; i < elementsVisited.length; i++){
			elementsVisited[i] = false;
		}
		for (int i = 0; i < patternElements.size(); i++){
			//If the element is a "class" and not an association, and the element has not been visited before.
			if (patternElements.get(i).getPart2ID() <= 0 && !elementsVisited[i]){
				classViolator1 = null;
				if (!isAssociated(i)) {
					PatternParticipant p = new PatternParticipant();
					p.fromDatabase(patternElements.get(i).getPart1ID());
					if (classViolator1 == null)
						classViolator1 = p.getName();
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Base Case: Element has not been visited. Visit the element and test its associations.
	 * Recursion: If the element's associations has not been visited, visit them recursively.
	 * @param elementIndex the index in patternElements representing a CLASS
	 * @return true if the association exist, false otherwise.
	 */
	private boolean isAssociated(int elementIndex){
		_ASSERT(patternElements != null);
		_ASSERT(associations != null);
		_ASSERT(elementsVisited != null);
		_ASSERT(elementIndex >= 0);
		_ASSERT(!elementsVisited[elementIndex]);
		elementsVisited[elementIndex] = true;

		PatternParticipant p = new PatternParticipant();
		p.fromDatabase(patternElements.get(elementIndex).getPart1ID());
		classViolator2 = p.getName();
		_ASSERT(p.getID() >= 0);
		HashMap<Integer, Integer> allParticipants = p.getAllParticipants();
		Integer[] assocIDs = (Integer[]) allParticipants.keySet().toArray(new Integer[0]);
		for (int i = 0; i < assocIDs.length; i++){
			int curType = allParticipants.get(assocIDs[i]);
			if (curType < 0) {
				PatternParticipant targetParticipant = new PatternParticipant();
				targetParticipant.fromDatabase(assocIDs[i]);
				classViolator1 = targetParticipant.getName();
			}
			boolean isExist = false;
			for (int j = 0; j < associations.size(); j++){
				if (assocIDs[i] == associations.get(j).getPart2ID()){
					if (curType == associations.get(j).getAssocType()){
						//Found the association and association type is correct
						int targetIndex = classIndexOf(assocIDs[i]);
						_ASSERT(targetIndex >= 0);
						if (elementsVisited[targetIndex] || isAssociated(targetIndex)){
							isExist = true;
							break;
						}
					}
				}
			}

			if (!isExist){
				//Finished the inner loop w/o finding a good representative.
				errorno = ASSOCIATIONVIOLATED;
				return false;
			}
		}
		return true;
	}

	/**
	 * Given a participant ID, search through patternElements vector.
	 * Find the id of patternElements in which it represents the given participant.
	 * Note that part2ID of that element must be invalid.
	 * @param partID
	 * @return -1 if not found, otherwise, give the correct index.
	 */
	private int classIndexOf(int partID){
		for (int i = 0; i < patternElements.size(); i++){
			PatternElement cur = patternElements.get(i);
			if (cur.getPart1ID() == partID && cur.getPart2ID() <= 0)
				return i;
		}
		return -1;
	}

	/**
	 * Checks for existance of all kinds of classes in the models.
	 * @return
	 */
	private boolean areClassesExistInModel(){
		for (int i = 0; i < participants.size(); i++){
			PatternParticipant cur = participants.get(i);
			boolean isExist = false;
			for (int j = 0; j < patternElements.size(); j++){
				if (patternElements.get(j).getPart1ID() == cur.getID()){
					isExist = true;
					break;
				}
			}
			if (!isExist) {
				classViolator1 = cur.getName();
				errorno = CLASSNOTFOUND;
				return false;
			}
		}
		return true;
	}

	/**
	 * Load the XMI model. Modify patternElements so that all elements deleted from the diagram will be deleted from the vector.
	 */
	private void truncateData(){
		_ASSERT(participants != null);
		_ASSERT(patternElements != null);
		resource = (XMIResource) new ResourceSetImpl().getResource(URI.createFileURI(modelFilePath), true);
		Model model = (Model) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.MODEL);

		EList<Element> modelElements = model.allOwnedElements();
		for (int i = 0; i < patternElements.size(); i++){
			boolean isExist = false;
			String elementID = patternElements.get(i).getXMIID();
			for (int j = 0; j < modelElements.size(); j++){
				String modelID = resource.getID(modelElements.get(j));
				if (modelID != null && modelID.equals(elementID)){
					isExist = true;
					break;
				}
			}
			if (!isExist){
				patternElements.remove(i);
				i--;
			}
		}
	}

	/**
	 * Load data from the database.
	 */
	private void loadData(){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		try{
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM DIAGRAM_ALTERNATIVE WHERE alt_id = " + alt.getID();
			ResultSet rs = stmt.executeQuery(query);
			_ASSERT(rs.next());
			_ASSERT(rs.getInt("pattern_id") == alt.getPatternID());
			packageXMIID = RationaleDBUtil.decode(rs.getString("package_xmi_id"));
			modelFilePath = RationaleDBUtil.decode(rs.getString("file_path"));

			//Now, get all pattern elements.
			patternElements = new Vector<PatternElement>();
			query = "SELECT * FROM DIAGRAM_PATTERNELEMENTS WHERE alt_id = " + alt.getID();
			rs = stmt.executeQuery(query);
			while (rs.next()){
				patternElements.add(new PatternElement(rs.getInt("part_id"), rs.getInt("part2_id"), 
						rs.getInt("assoc_type"), rs.getString("xmi_id")));
				patternElements.lastElement().setAltID(alt.getID());
			}
			participants = db.getParticipantsFromPatternID(alt.getPatternID());
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	private void _ASSERT(boolean verifier){
		if (!verifier) throw new RuntimeException("ASSERTION FAILED AT VerifyUMLAssociationAction");
	}
}
