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

import edu.wpi.cs.jburge.SEURAT.rationaleData.Constraint;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;

/**
 * SelectConstraint is used to let the user select from a list of 
 * design constraints. This was used in the SEURAT ORCA enhancements and
 * is not typically used when using SEURAT for software development and maintenance.
 * @author jburge
 */
public class SelectConstraint {
	
	/**
	 * Points back to the display
	 */
	private Display ourDisplay;
	
	/**
	 * Points back to the shell
	 */
	private Shell shell;
	
	/**
	 * This is the tree structure that contains the higherarchy of 
	 * design constraints
	 */
	private Tree componentTree;
	
	/**
	 * The root of the constraint tree
	 */
	private TreeItem root;
	
	/**
	 * The selected design constraint
	 */
	private Constraint sel;
	
	/**
	 * Present the user with a tree of constraints that they can
	 * select from.
	 * @param display - points back to the display
	 * @param allowAdd - true if they are allowed to add new constraints to the tree
	 */
	public SelectConstraint(Display display, boolean allowAdd) {
		
		ourDisplay = display;
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Select Design Constraint");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		new Label(shell, SWT.NONE).setText("Design Constraint:");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		componentTree = new Tree(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		root = new TreeItem(componentTree, SWT.NONE);
		root.setText("Design-Constraints");
		populateTree(root, "Design-Constraints");
		
		GridData gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 3;
		gridData.heightHint = componentTree.getItemHeight() * 15;
		componentTree.setLayoutData(gridData);
		
		if (allowAdd)
		{
			Button addB = new Button(shell, SWT.PUSH);
			addB.setText("New");
			gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//			gridData.horizontalIndent = 5;
			addB.setLayoutData(gridData);
			addB.addSelectionListener(new SelectionAdapter() {
				
				public void widgetSelected(SelectionEvent event) {
					
					TreeItem[] selected = componentTree.getSelection();
					sel = new Constraint();
					if (selected.length > 0)
					{
						Constraint parent = new Constraint();
						parent.fromDatabase(selected[0].getText());
						Constraint newOnt = new Constraint();
						EditConstraint ar = new EditConstraint(ourDisplay, newOnt, parent, true);
						if (!ar.canceled)
						{
							newOnt = (Constraint) ar.getItem();
							TreeItem newItem = new TreeItem(selected[0], SWT.NONE);
							newItem.setText(newOnt.getName());
							componentTree.redraw();
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
//		gridData.horizontalIndent = 5;
		findB.setLayoutData(gridData);
		findB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				
				TreeItem[] selected = componentTree.getSelection();
				sel = new Constraint();
				sel.fromDatabase(selected[0].getText());
				shell.close();
				shell.dispose();
				
			}
		});
		
		Button cancelB = new Button(shell, SWT.PUSH);
		cancelB.setText("Cancel");
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//		gridData2.horizontalIndent = 5;
		cancelB.setLayoutData(gridData2);
		cancelB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				sel = null;
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
	 * Populates a constraint tree from the rationale database. This is a
	 * recursive method that calls itself to add new children to the current
	 * root node.
	 * @param node - the root node for the tree
	 * @param parentName - the name of the parent at the root node.
	 */
	private void populateTree(TreeItem node, String parentName )
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector ontList = d.getConstraintElements(parentName);
		Enumeration onts = ontList.elements();
		while (onts.hasMoreElements())
		{
			TreeItem next = null;
			Constraint child = (Constraint) onts.nextElement();
			next = new TreeItem(node, SWT.NONE);
			next.setText(child.getName());
			populateTree(next, child.getName());
		}
		
	}
	
	/**
	 * Returns the constraint that has been selected
	 * @return the selected constraint
	 */
	public Constraint getSelConstraint()
	{
		return sel;
	}
	
}
