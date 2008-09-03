/*
 * Created on May 15, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.editors;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.OntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;

/**
 * Select an ontology entry from the argument ontology tree.
 * @author jburge
 */
public class SelectOntEntry {
	
	/**
	 * Points back to the display
	 */
	private Display ourDisplay;
	/**
	 * Points to the shell
	 */
	private Shell shell;
	
	/**
	 * The tree of ontology entries that form the argument ontology
	 */
	private Tree ontologyTree;
	/**
	 * The root element of the tree
	 */
	private TreeItem root;
	
	/**
	 * The selected item from the tree
	 */
	private OntEntry selEntry;
	
	
	/**
	 * Used to display the argument ontology tree so an element can be selected
	 * @param display - points back to the display
	 * @param allowAdd - true if new entries can be added (usually not allowed)
	 */
	public SelectOntEntry(Display display, boolean allowAdd) {
		
		ourDisplay = display;
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Select Ontology Entity");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		new Label(shell, SWT.NONE).setText("Argument Ontology:");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		ontologyTree = new Tree(shell, SWT.SINGLE | SWT.V_SCROLL| SWT.H_SCROLL);
		
		root = new TreeItem(ontologyTree, SWT.NONE);
		root.setText("Argument-Ontology");
		populateTree(root, "Argument-Ontology");
		
		GridData gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.heightHint = ontologyTree.getItemHeight() * 15;
		ontologyTree.setLayoutData(gridData);
		
		if (allowAdd)
		{
			Button addB = new Button(shell, SWT.PUSH);
			addB.setText("New");
			gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			gridData.horizontalIndent = 5;
			addB.setLayoutData(gridData);
			addB.addSelectionListener(new SelectionAdapter() {
				
				public void widgetSelected(SelectionEvent event) {
					
					TreeItem[] selected = ontologyTree.getSelection();
					selEntry = new OntEntry();
					if (selected.length > 0)
					{
						OntEntry parent = new OntEntry();
						parent.fromDatabase(selected[0].getText());
						OntEntry newOnt = new OntEntry();
						EditOntEntry ar = new EditOntEntry(ourDisplay, newOnt, parent, true);
						if (!ar.canceled)
						{
							newOnt = (OntEntry) ar.getItem();
							TreeItem newItem = new TreeItem(selected[0], SWT.NONE);
							newItem.setText(newOnt.getName());
							ontologyTree.redraw();
						}
					}
					
				}
			});
		}
		else
		{
			new Label(shell, SWT.NONE).setText("");
		}
		
		
		Button findB = new Button(shell, SWT.PUSH);
		findB.setText("Select");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalIndent = 5;
		findB.setLayoutData(gridData);
		findB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				
				TreeItem[] selected = ontologyTree.getSelection();
				selEntry = new OntEntry();
				selEntry.fromDatabase(selected[0].getText());
				shell.close();
				shell.dispose();
				
			}
		});
		
		Button cancelB = new Button(shell, SWT.PUSH);
		cancelB.setText("Cancel");
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData2.horizontalIndent = 5;
		cancelB.setLayoutData(gridData2);
		cancelB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				selEntry = null;
				shell.close();
				shell.dispose();
				
			}
		});
		
		//We want the buttons to be of equal size...
		findB.setSize(cancelB.getSize());
		
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
	}
	
	/**
	 * Populates the ontology tree from the rationale database. This is a
	 * recursive method that calls itself to add new children to the current
	 * root node.
	 * @param node - the root node for the tree
	 * @param parentName - the name of the parent at the root node.
	 */
	void populateTree(TreeItem node, String parentName )
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector ontList = d.getOntologyElements(parentName);
		Enumeration onts = ontList.elements();
		while (onts.hasMoreElements())
		{
			OntEntry child = (OntEntry) onts.nextElement();
			TreeItem next = new TreeItem(node, SWT.NONE);
			next.setText(child.getName());
			populateTree(next, child.getName());
		}
		
	}
	
	/**
	 * Get the selected item from the tree
	 * @return the selected ontology entry
	 */
	public OntEntry getSelOntEntry()
	{
		return selEntry;
	}
	
}
