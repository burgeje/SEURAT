package edu.wpi.cs.jburge.SEURAT.actions;

import java.util.Vector;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import java.sql.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.PatternParticipant;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDBUtil;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

/**
 * This class allows the users to associate an alternative pattern with a UML model.
 * @author yechen
 *
 */
public class AssociateUMLAction extends Action{

	private boolean associate;
	private Viewer viewer;
	private boolean modelExists;
	private ISelection selection;
	private String filePath;
	private Pattern pat;
	private String packageXMIID;
	private int altID;

	public AssociateUMLAction(boolean associate, boolean isExists, Viewer viewer){
		this.associate = associate;
		modelExists = isExists;
		this.viewer = viewer;
		altID = -1;
		packageXMIID = null;
	}

	public void run(){
		selection = viewer.getSelection();
		pat = new Pattern();
		if (modelExists){
			exportExisting();
		} else {
			exportNew();
		}

		if (associate){
			associateUML();
		}
	}

	/**
	 * Checks whether all data are ok. If so, call the write to database.
	 */
	private void associateUML(){
		if (filePath == null) return;
		Object obj = ((IStructuredSelection)selection).getFirstElement();

		if (obj instanceof TreeParent){
			TreeParent ourElement = (TreeParent) obj;
			if (ourElement.getType() != RationaleElementType.ALTERNATIVE){
				displayErrorMsg("The selected item is not an alternative.");
				return;
			}
			if (pat.getID() < 0){
				displayErrorMsg("Unable to retrieve the related pattern.");
				return;
			}
			if (altID < 0){
				displayErrorMsg("The selected item is not in Alternative table.");
				return;
			}

			if (existInDB()){
				displayErrorMsg("Disassociate the alternative before associating it with another UML");
				return;
			}

			writeToDB();
			//Broadcast to the editors.
			Alternative alt = new Alternative();
			alt.fromDatabase(altID);
			alt.broadcastUpdate();
		}
	}

	/**
	 * Associate the UML with the SEURAT database.
	 */
	private void writeToDB(){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		try{
			Statement stmt = conn.createStatement();
			String dm = "DELETE FROM DIAGRAM_PATTERNELEMENTS WHERE alt_id = " + altID;
			stmt.execute(dm); //Flush the old associations if the deletion crashed last time.

			dm = "INSERT INTO DIAGRAM_ALTERNATIVE "
					+ "(id, alt_id, pattern_id, package_xmi_id, file_path)"
					+ " VALUES(" + RationaleDB.findAvailableID("DIAGRAM_PATTERNELEMENTS")
					+ ", " + altID + ", " + pat.getID() + ", '" + RationaleDBUtil.escape(packageXMIID) 
					+ "', '" + RationaleDBUtil.escape(filePath) + "')";
			stmt.execute(dm);


			//Get patternElements from Pattern object, then assign altID!
			Vector<PatternElement> pe = pat.getPatternElements();
			for (int i = 0; i < pe.size(); i++){
				PatternElement cur = pe.get(i);
				cur.setAltID(altID);
				cur.toDatabase();
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
	}

	/**
	 * Check whether an association of the same alternative exists in the db already.
	 * @return
	 */
	private boolean existInDB(){
		RationaleDB db = RationaleDB.getHandle();
		Connection conn = db.getConnection();
		try{
			Statement stmt = conn.createStatement();
			String query = "SELECT * FROM DIAGRAM_ALTERNATIVE WHERE alt_id = " + altID;
			ResultSet rs = stmt.executeQuery(query);
			if (rs.next()) return true;
			return false;
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}

	private void displayErrorMsg(String message){
		MessageBox mbox = new MessageBox(new Shell(), SWT.ICON_ERROR);
		mbox.setMessage(message);
		mbox.setText("Unable to Associate UML");
		mbox.open();
	}

	/**
	 * Export UML of a pattern to an existing UML model
	 */
	private void exportExisting(){
		Shell shell = new Shell();
		FileDialog path = new FileDialog(shell, SWT.OPEN);
		String[] ext = {"*.xmi", "*.uml"};
		String[] name = {"XMI (*.xmi)", "UML XMI (*.uml)"};
		path.setFilterExtensions(ext);
		path.setFilterNames(name);
		path.setFileName(RationaleDB.getOntName());
		path.setOverwrite(false); //Do not prompt for overwrite.
		shell.pack();

		filePath = path.open();
		if (filePath == null) return; //Return if user cancelled.
		Object obj = ((IStructuredSelection)selection).getFirstElement();

		if (obj instanceof TreeParent){
			TreeParent ourElement = (TreeParent) obj;
			if (ourElement.getType() == RationaleElementType.PATTERN){
				pat.fromDatabase(ourElement.getName());
			} else if (ourElement.getType() == RationaleElementType.ALTERNATIVE){
				Alternative alt = new Alternative();
				alt.fromDatabase(ourElement.getName());
				pat.fromDatabase(alt.getPatternID());
				altID = alt.getID();
			} else {
				return;
			}

			if (pat.getID() < 0) return;

			URI uri = URI.createFileURI(filePath);

			RationaleDB db = RationaleDB.getHandle();
			Vector<PatternParticipant> parts = 
					db.getParticipantsFromPatternName(ourElement.getName());
			Vector<Integer> numInstances = new Vector<Integer>();
			for (int i = 0; i < parts.size(); i++){
				numInstances.add(new NumberInputDialog(shell, "Participant Instances", 
						"# of Instances of " + parts.get(i).getName() + ": ").open());
			}
			try{
				packageXMIID = pat.addXMIClassToModel(uri, numInstances);
			} catch (Exception e){
				displayErrorMsg("Unable to write to the target file.");
			}
		}
	}

	/**
	 * Export UML of a pattern to a new UML model.
	 */
	private void exportNew(){
		Shell shell = new Shell();
		FileDialog path = new FileDialog(shell, SWT.SAVE);
		String[] ext = {"*.xmi", "*.uml"};
		String[] name = {"XMI (*.xmi)", "UML XMI (*.uml)"};
		path.setFilterExtensions(ext);
		path.setFilterNames(name);
		path.setFileName(RationaleDB.getOntName());
		shell.pack();

		filePath = path.open();
		if (filePath == null) return; //Return if user cancelled.

		Object obj = ((IStructuredSelection)selection).getFirstElement();

		if (obj instanceof TreeParent){
			TreeParent ourElement = (TreeParent) obj;
			if (ourElement.getType() == RationaleElementType.PATTERN){
				pat.fromDatabase(ourElement.getName());
			} else if (ourElement.getType() == RationaleElementType.ALTERNATIVE){
				Alternative alt = new Alternative();
				alt.fromDatabase(ourElement.getName());
				pat.fromDatabase(alt.getPatternID());
				altID = alt.getID();
			} else {
				return;
			}

			if (pat.getID() < 0) return;

			URI uri = URI.createFileURI(filePath);

			RationaleDB db = RationaleDB.getHandle();
			Vector<PatternParticipant> parts = 
					db.getParticipantsFromPatternName(ourElement.getName());
			Vector<Integer> numInstances = new Vector<Integer>();
			for (int i = 0; i < parts.size(); i++){
				numInstances.add(new NumberInputDialog(shell, "Participant Instances", 
						"# of Instances of " + parts.get(i).getName() + ": ").open());
			}
			try{
				packageXMIID = pat.newXMIClass(uri, numInstances);
			} catch (Exception e){
				displayErrorMsg("Unable to write to the target file.");
			}
		}
	}


	/**
	 * Allows the users to enter number of instances of a particular participant.
	 * @author yechen
	 *
	 */
	private class NumberInputDialog extends Dialog {
		Integer value;
		String title, prompt;

		/**
		 * @param parent
		 */
		public NumberInputDialog(Shell parent, String title, String prompt) {
			super(parent);
			this.title = title;
			this.prompt = prompt;
		}

		/**
		 * Makes the dialog visible.
		 * 
		 * @return
		 */
		public Integer open() {
			Shell parent = getParent();
			final Shell shell =
					new Shell(parent, SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
			shell.setText(title);

			shell.setLayout(new org.eclipse.swt.layout.GridLayout(2, true));

			Label label = new Label(shell, SWT.NULL);
			label.setText(prompt);

			final Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);

			final Button buttonOK = new Button(shell, SWT.PUSH);
			buttonOK.setText("Ok");
			buttonOK.setLayoutData(new org.eclipse.swt.layout.GridData(org.eclipse.swt.layout.GridData.HORIZONTAL_ALIGN_END));

			text.addListener(SWT.Modify, new Listener() {
				public void handleEvent(Event event) {
					try {
						value = new Integer(text.getText());
						if (value >= 0)
							buttonOK.setEnabled(true);
						else buttonOK.setEnabled(false);
					} catch (Exception e) {
						buttonOK.setEnabled(false);
					}
				}
			});

			buttonOK.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					shell.dispose();
				}
			});

			shell.addListener(SWT.Traverse, new Listener() {
				public void handleEvent(Event event) {
					if(event.detail == SWT.TRAVERSE_ESCAPE)
						event.doit = false;
				}
			});

			text.setText("");
			shell.pack();
			shell.open();

			Display display = parent.getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch())
					display.sleep();
			}

			return value;
		}
	}
}
