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
import org.eclipse.swt.graphics.Rectangle;

import edu.wpi.cs.jburge.SEURAT.rationaleData.AltConstRel;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Alternative;
import edu.wpi.cs.jburge.SEURAT.rationaleData.Constraint;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;

/**
 * @author jburge
 *
 * This is the editor for Alternative-Constraint relationships
 */
public class EditAltConstRel {
	
	/**
	 * The GUI shell
	 */
	private Shell shell;

	/**
	 * The tree of constraints
	 */
	private Tree componentTree;
	
	/**
	 * The root of the tree
	 */
	private TreeItem root;
	
	/**
	 * The constraint selected
	 */
	private Constraint selEntry;
	/**
	 * The alternative it is being related to
	 */
	private Alternative ourParent;
	
	/**
	 * The amount associated with the constraint
	 */
	  private Text amtField;
	  
	  /**
	   * The units associated with the amount (km, kg, etc.)
	   */
	  private Text unitsField;
	  
	  /**
	   * Indicates if this is a new item
	   */
	  private boolean aNewItem;
	  
	  /**
	   * True if cancelled
	   */
	  boolean canceled;
	  
	  /**
	   * The relationship
	   */
	  AltConstRel ourArea;

	/**
	 * Constructor for editing the alternative-constraint relationships
	 * @param display - points back to the parent display
	 * @param editArea - this is our relationship
	 * @param parent - this is the parent alternative we accessed the editor from. 
	 * This is null if we are editing an existing relationship (info is not available)
	 * @param newItem - indicates if we are creating a new relationship
	 * 
	 */
	public EditAltConstRel(Display display, AltConstRel editArea, Alternative parent, boolean newItem) {
		
		GridData gridData;
		
		ourArea = editArea;
		ourParent = parent;
		
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Edit Constraint Relationship");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		new Label(shell, SWT.NONE).setText("Design Constraint:");

		
		if (newItem)
		{
			new Label(shell, SWT.NONE).setText("");
			componentTree = new Tree(shell, SWT.SINGLE | SWT.V_SCROLL);
			root = new TreeItem(componentTree, SWT.NONE);
			root.setText("Design-Constraints");
			populateTree(root, "Design-Constraints");
			gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
			int treeHeight = componentTree.getItemHeight() * 4;
			int treeWidth = root.getFont().getFontData().length * 200;
			Rectangle trim = componentTree.computeTrim(0, 0, treeWidth, treeHeight);
			gridData.widthHint = trim.width;
			gridData.heightHint = trim.height;
			gridData.horizontalSpan = 2;
			componentTree.setLayoutData(gridData);
			

		}
		else
		{
		
			new Label(shell, SWT.NONE).setText(ourArea.getConstr().getName());
			
		}
		//need the amount and units!
		new Label(shell, SWT.NONE).setText("Amount:");
		
		amtField =  new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		amtField.setText(new Float(ourArea.getAmount()).toString());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(amtField, gridData, 25);
		gridData.horizontalSpan = 1;
		amtField.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Units:");
		
		unitsField =  new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		unitsField.setText(ourArea.getUnits());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 1;
		DisplayUtilities.setTextDimensions(unitsField, gridData, 25);
		unitsField.setLayoutData(gridData);
		
		Button findB = new Button(shell, SWT.PUSH);
		findB.setText("Save");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//		gridData.horizontalIndent = 5;
		findB.setLayoutData(gridData);
		gridData.horizontalSpan = 1;

		findB.addSelectionListener(new SelectionAdapter() {

		   public void widgetSelected(SelectionEvent event) {
		   	
			   int pid = 0;
			if (aNewItem)
			{
		   	TreeItem[] selected = componentTree.getSelection();
		   	selEntry = new Constraint();
			selEntry.fromDatabase(selected[0].getText());
			ourArea.setConstr(selEntry);

			if (ourParent != null)
			{
				ourArea.setName(ourParent.getName() + "_" + selEntry.getName());
				pid = ourParent.getID();
			}
			} //not a new item
			else
			{
				pid = ourArea.getID(); //in this case, the ID is equal to the parent?
			}
			ourArea.setUnits(unitsField.getText());
			
			String amountString = amtField.getText();
			try
			{
			Float testFlt = Float.valueOf(amountString);
			ourArea.setAmount(testFlt.floatValue());
			
			ourArea.toDatabase(pid);
			canceled = false;
			shell.close();
			shell.dispose();
			
			   } catch (NumberFormatException ex)
				{
					   MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
					   mbox.setMessage("Need to provide a valid percentage");
					   mbox.open();
				}

		   }
		});
		
		Button cancelB = new Button(shell, SWT.PUSH);
		cancelB.setText("Cancel");
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//		gridData2.horizontalIndent = 5;
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

	/**
	 * populates our constraint tree that we select from when creating a new relationship. 
	 * This is a recursive routine.
	 * @param node - the parent tree node
	 * @param parentName - the name of the parent
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
	 * checks if the editor was canceled from and didn't modify anything
	 * @return true if cancelled
	 */
	public boolean getCanceled()
	{
		return canceled;
	}
	
	/**
	 * used to get our selected constraint 
	 * @return the selected constraint
	 */
	public Constraint getSelEntry()
	{
	  return selEntry;
	}

}
