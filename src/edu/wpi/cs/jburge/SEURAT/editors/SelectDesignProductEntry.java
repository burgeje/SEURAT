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

import edu.wpi.cs.jburge.SEURAT.rationaleData.DesignProductEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;

/**
 * Used to select a design product entry fro a tree of design products.
 * @author jburge
 */
public class SelectDesignProductEntry {
	
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
	 * product components
	 */
	private Tree componentTree;
	
	/**
	 * The root of the product component tree
	 */
	private TreeItem root;
	
	/**
	 * The selected product component element
	 */
	private DesignProductEntry selEntry;
	
	/**
	 * Present the user with a tree of product components that they can
	 * select from.
	 * @param display - points back to the display
	 * @param allowAdd - true if they are allowed to add new components to the tree
	 */
	public SelectDesignProductEntry(Display display, boolean allowAdd) {
		
		ourDisplay = display;
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Select Design Product Entity");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		new Label(shell, SWT.NONE).setText("Design Component:");
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		componentTree = new Tree(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		root = new TreeItem(componentTree, SWT.NONE);
		root.setText("Design-Product-Ontology");
		populateTree(root, "Design-Product-Ontology");
		
		GridData gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 3;
		gridData.heightHint = componentTree.getItemHeight() * 15;
		componentTree.setLayoutData(gridData);
		
		if (allowAdd)
		{
			Button addB = new Button(shell, SWT.PUSH);
			addB.setText("New");
			gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
			gridData.horizontalIndent = 5;
			addB.setLayoutData(gridData);
			addB.addSelectionListener(new SelectionAdapter() {
				
				public void widgetSelected(SelectionEvent event) {
					
					TreeItem[] selected = componentTree.getSelection();
					selEntry = new DesignProductEntry();
					if (selected.length > 0)
					{
						DesignProductEntry parent = new DesignProductEntry();
						parent.fromDatabase(selected[0].getText());
						DesignProductEntry newOnt = new DesignProductEntry();
						EditDesignProductEntry ar = new EditDesignProductEntry(ourDisplay, newOnt, parent, true);
						if (!ar.canceled)
						{
							newOnt = (DesignProductEntry) ar.getItem();
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
		gridData.horizontalIndent = 5;
		findB.setLayoutData(gridData);
		findB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				
				TreeItem[] selected = componentTree.getSelection();
				selEntry = new DesignProductEntry();
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
	 * Populates a component tree from the rationale database. This is a
	 * recursive method that calls itself to add new children to the current
	 * root node.
	 * @param node - the root node for the tree
	 * @param parentName - the name of the parent at the root node.
	 */
	
	private void populateTree(TreeItem node, String parentName )
	{
		RationaleDB d = RationaleDB.getHandle();
		Vector ontList = d.getDesignProductElements(parentName);
		Enumeration onts = ontList.elements();
		while (onts.hasMoreElements())
		{
			TreeItem next = null;
			DesignProductEntry child = (DesignProductEntry) onts.nextElement();
			next = new TreeItem(node, SWT.NONE);
			next.setText(child.getName());
			populateTree(next, child.getName());
		}
		
	}
	
	/**
	 * Returns the selected product component
	 * @return the selected product component
	 */
	public DesignProductEntry getSelEntry()
	{
		return selEntry;
	}
	
}
