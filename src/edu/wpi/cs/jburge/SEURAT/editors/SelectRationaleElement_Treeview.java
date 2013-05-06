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

package edu.wpi.cs.jburge.SEURAT.editors;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import edu.wpi.cs.jburge.SEURAT.rationaleData.OntEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;

public class SelectRationaleElement_Treeview {
	
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
	private RationaleElement selElement;
	
	/**
	 * The type of selected item
	 */
	private RationaleElementType eleType;
	
	
	/**
	 * Used to display the argument ontology tree so an element can be selected
	 * @param display - points back to the display
	 * @param type - the type of the element being selected to move around the tree
	 */
	public SelectRationaleElement_Treeview(Display display,RationaleElementType type) {
		
		this.eleType=type;
		ourDisplay = display;
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Select Rationale Element");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
	
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		ontologyTree = new Tree(shell, SWT.SINGLE | SWT.V_SCROLL| SWT.H_SCROLL);
		
		root = new TreeItem(ontologyTree, SWT.NONE);
		
		if (type==RationaleElementType.ALTERNATIVE)
		{
			root.setText("Decision");
			populateTree(root, "NotNull", RationaleElementType.DECISION,true);
		}
		if (type==RationaleElementType.REQUIREMENT)
		{
			root.setText(type.toString());
			populateTree(root, "NotNull", type,true);
		}
		
		
		GridData gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.heightHint = ontologyTree.getItemHeight() * 15;
		ontologyTree.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("");		
		
		Button findB = new Button(shell, SWT.PUSH);
		findB.setText("Select");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalIndent = 5;
		findB.setLayoutData(gridData);
		if (eleType==RationaleElementType.ALTERNATIVE)
			eleType=RationaleElementType.DECISION;
		findB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				
				TreeItem[] selected = ontologyTree.getSelection();
				selElement = new RationaleElement();
				selElement  = RationaleDB.getRationaleElement(selected[0].getText(), eleType);	
				
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
				selElement = null;
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
	 * This method is for moving decision and argument to different type of element on the tree
	 * @param display - points back to the display
	 * @param type - the type of the element being selected to move around the tree
	 * @param ptype - the type parent element
	 */
	public SelectRationaleElement_Treeview(Display display,RationaleElementType ele, RationaleElementType ptype) {
		
		this.eleType=ptype;
		ourDisplay = display;
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Select Rationale Element");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
	
		new Label(shell, SWT.NONE).setText("");
		new Label(shell, SWT.NONE).setText("");
		ontologyTree = new Tree(shell, SWT.SINGLE | SWT.V_SCROLL| SWT.H_SCROLL);
		
		root = new TreeItem(ontologyTree, SWT.NONE);
		
		if (ptype==RationaleElementType.ALTERNATIVE)
		{
			root.setText("Alternative");
			populateTree(root, "NotNull", ptype,true);
		}
		if (ptype==RationaleElementType.DECISION)
		{
			root.setText("Decision");
			populateTree(root, "NotNull", ptype,true);
		}
		if (ptype==RationaleElementType.REQUIREMENT)
		{
			root.setText("Requirement");
			populateTree(root, "NotNull", ptype,true);
		}
		
		
		GridData gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING | GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 3;
		gridData.heightHint = ontologyTree.getItemHeight() * 15;
		ontologyTree.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("");		
		
		Button findB = new Button(shell, SWT.PUSH);
		findB.setText("Select");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalIndent = 5;
		findB.setLayoutData(gridData);
		findB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				
				TreeItem[] selected = ontologyTree.getSelection();
				selElement = new RationaleElement();
				selElement  = RationaleDB.getRationaleElement(selected[0].getText(), eleType);	
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
				selElement = null;
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
	 * Populates the rationale elements from the rationale database. This is a
	 * recursive method that calls itself to add new children to the current
	 * root node.
	 * @param node - the root node for the tree
	 * @param parentName - the name of the parent at the root node.
	 * @param type specify which type of element we should display as treeview.
	 * @param firstTime flag for whether this is the first time to call this method.
	 */
	void populateTree(TreeItem node, String parentName, RationaleElementType type, boolean firstTime )
	{
		if (parentName==null)
			return;
		RationaleDB d = RationaleDB.getHandle();
		Vector eleList = d.getElements_TreeView(parentName,type,firstTime);
		firstTime=false;
		Enumeration onts = eleList.elements();
		while (onts.hasMoreElements())
		{
			String child = (String)onts.nextElement();
			TreeItem next = new TreeItem(node, SWT.NONE);
			next.setText(child);
			populateTree(next, child, type, firstTime);
		}
		
	}
	
	/**
	 * Get the selected item from the tree
	 * @return the selected ontology entry
	 */
	public RationaleElement getSelEle()
	{
		return selElement;
	}
	
}
