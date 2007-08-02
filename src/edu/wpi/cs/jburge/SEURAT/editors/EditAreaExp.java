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

import edu.wpi.cs.jburge.SEURAT.rationaleData.AreaExp;
//import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.DesignProductEntry;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Designer;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;

/**
 * @author jburge
 *
 * This is the editor for adding a new area of expertise. This was created when we were
 * re-packaging SEURAT as ORCA and we don't really do anything useful with this yet.
 */
public class EditAreaExp {
	
	private Shell shell;
	
	private Tree componentTree;
	private TreeItem root;
	
	private DesignProductEntry selEntry;
	private Designer ourParent;
	private Combo areaBox;
	
	boolean canceled;
	AreaExp ourArea;
	
	/**
	 * Edit the area of expertise
	 * @param display - points back to the display
	 * @param - editArea - the new (or modified) area of expertise
	 * @param - parent - the designer whose area of expertise we are specifying
	 * @param - newItem - indicates if this is a new area of expertise
	 */
	public EditAreaExp(Display display, AreaExp editArea, Designer parent, boolean newItem) {
		
		GridData gridData;
		
		ourArea = editArea;
		ourParent = parent;
		
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Edit Area of Expertise");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		new Label(shell, SWT.NONE).setText("Design Component:");
		
		
		if (newItem)
		{
			
			new Label(shell, SWT.NONE).setText("");
			new Label(shell, SWT.NONE).setText("");
			componentTree = new Tree(shell, SWT.SINGLE | SWT.V_SCROLL);
			root = new TreeItem(componentTree, SWT.NONE);
			root.setText("Design-Product-Ontology");
			populateTree(root, "Design-Product-Ontology");
			
			gridData = new GridData( GridData.VERTICAL_ALIGN_BEGINNING);
			gridData.horizontalSpan = 2;
			gridData.heightHint = componentTree.getItemHeight() * 4;
			componentTree.setLayoutData(gridData);
			
			
		}
		else
		{
			
			new Label(shell, SWT.NONE).setText(ourArea.getComponent().getName());
		}
		new Label(shell, SWT.NONE).setText("Level:");
		areaBox = new Combo(shell, SWT.NONE);
		int k;
		for (k = 1;k < 11; k++)
		{
			areaBox.add(  new Integer(k).toString());
			if (k == ourArea.getLevel())
			{
				areaBox.select(k-1);
			}
		}
		
		
		areaBox.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		Button findB = new Button(shell, SWT.PUSH);
		findB.setText("Save");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalIndent = 5;
		findB.setLayoutData(gridData);
		findB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				
				TreeItem[] selected = componentTree.getSelection();
				selEntry = new DesignProductEntry();
				selEntry.fromDatabase(selected[0].getText());
				ourArea.setComponent(selEntry);
				int pid = 0;
				if (ourParent != null)
				{
					ourArea.setName(ourParent.getName() + "_" + selEntry.getName());
					pid = ourParent.getID();
				}
				ourArea.setLevel(areaBox.getSelectionIndex());
				ourArea.toDatabase(pid);
				canceled = false;
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
				canceled = true;
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
	
	public boolean getCanceled()
	{
		return canceled;
	}
	
	public DesignProductEntry getSelEntry()
	{
		return selEntry;
	}
	
}
