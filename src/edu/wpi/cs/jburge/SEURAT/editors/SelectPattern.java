package edu.wpi.cs.jburge.SEURAT.editors;

import java.util.Vector;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.Pattern;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
import edu.wpi.cs.jburge.SEURAT.views.TreeParent;

/**
 * Select a pattern from pattern library. Invoked when a user clicks "select" in tactic
 * library.
 * @author yechen
 *
 */
public class SelectPattern {
	/**
	 * Points back to the display
	 */
	private Display ourDisplay;
	
	/**
	 * Points to the shell
	 */
	private Shell shell;
	
	/**
	 * Tree that contains patterns of different categories.
	 */
	private Tree tree;
	private TreeItem root;
	
	private Pattern selectedPattern;
	
	public SelectPattern(Display display){
		ourDisplay = display;
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Select Pattern");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		new Label(shell, SWT.NONE).setText("Pattern Ontology:");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		
		tree = new Tree(shell, SWT.SINGLE | SWT.V_SCROLL| SWT.H_SCROLL);
		populateTree();
		
		GridData gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.heightHint = tree.getItemHeight() * 15;
		tree.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("");
		Button findB = new Button(shell, SWT.PUSH);
		findB.setText("Select");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalIndent = 5;
		findB.setLayoutData(gridData);
		findB.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				TreeItem selected = tree.getSelection()[0];
				if (selected == root || selected.getParentItem().equals(root)){
					MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
					mbox.setMessage("You must select one of the pattern and not its category.");
					mbox.setText("Cannot Select Tactic Category");
					mbox.open();
					return;
				}
				//So, selection is valid...
				selectedPattern = new Pattern();
				selectedPattern.fromDatabase(selected.getText());
				shell.close();
				shell.dispose();
			}
		});
		
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
	}
	
	public void populateTree(){
		root = new TreeItem(tree, SWT.NONE);
		root.setText("Pattern Ontology");
		RationaleDB db = RationaleDB.getHandle();

		TreeItem archTop = new TreeItem(root, SWT.NONE);
		archTop.setText("Architecture Patterns");
		Vector<TreeParent> archPatterns = 
			db.getArchitecturePatterns();
		for (int i = 0; i < archPatterns.size(); i++){
			TreeItem cur = new TreeItem(archTop, SWT.NONE);
			cur.setText(archPatterns.get(i).getName());
		}
		
		TreeItem designTop = new TreeItem(root, SWT.NONE);
		designTop.setText("Design Patterns");
		Vector<TreeParent> designPatterns = 
			db.getDesignPatterns();
		for (int i = 0; i < designPatterns.size(); i++){
			TreeItem cur = new TreeItem(designTop, SWT.NONE);
			cur.setText(designPatterns.get(i).getName());
		}
		
		TreeItem idiomTop = new TreeItem(root, SWT.NONE);
		idiomTop.setText("Idioms");
		Vector<TreeParent> idioms = db.getDesignPatterns();
		for (int i = 0; i < idioms.size(); i++){
			TreeItem cur = new TreeItem(idiomTop, SWT.NONE);
			cur.setText(idioms.get(i).getName());
		}
	}
	
	public Pattern getSelPattern(){
		return selectedPattern;
	}
}
