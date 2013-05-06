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


import java.io.Serializable;
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;


import edu.wpi.cs.jburge.SEURAT.rationaleData.*;

/**
 * Edits an element in the design product ontology (a collection of standard parts for
 * the thing that is being designed). 
 * @author burgeje
 *
 */
public class EditDesignProductEntry extends NewRationaleElementGUI implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2918209021033271522L;
	private Shell shell;
	/**
	 * The design product entry being edited
	 */	
	private DesignProductEntry ourDesignProductEntry;
	/**
	 * The "parent" item in the hieararchy
	 */
	private DesignProductEntry ourParent;
	/**
	 * The name of the produce element
	 */
	private Text nameField;
	/**
	 * A description of the element
	 */
	private Text descArea;
//	private boolean newItem;
	/**
	 * A button for adding the element
	 */
	private Button addButton;
	/**
	 * A button for canceling edits
	 */
	private Button cancelButton;
	
	/**
	 * Constructor to create the editor for the design product ontology elements.
	 * @param display - points to the display
	 * @param editDesignProductEntry - the element being modified
	 * @param ontParent - its parent
	 * @param newItem - true if this is a new element
	 */
	public EditDesignProductEntry(Display display, DesignProductEntry editDesignProductEntry, DesignProductEntry ontParent, boolean newItem)
	{
		super();
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("DesignProductEntry Information");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		ourDesignProductEntry = editDesignProductEntry;
		ourParent = ontParent;
		
		if (newItem)
		{
			ourDesignProductEntry.setName("");
		}
		
		
		new Label(shell, SWT.NONE).setText("Name:");
		
		nameField =  new Text(shell, SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL);
		nameField.setText(ourDesignProductEntry.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		DisplayUtilities.setTextDimensions(nameField, gridData, 75);
		gridData.horizontalSpan = 2;
		
		nameField.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Description:");
		
		descArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourDesignProductEntry.getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		DisplayUtilities.setTextDimensions(descArea, gridData, 75, 5);
		gridData.horizontalSpan = 2;
		gridData.heightHint = descArea.getLineHeight() * 3;
		descArea.setLayoutData(gridData);
		
		
		
		addButton = new Button(shell, SWT.PUSH); 
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		addButton.setLayoutData(gridData);
		if (newItem)
		{
			addButton.setText("Add");
			addButton.addSelectionListener(new SelectionAdapter() {
				
				public void widgetSelected(SelectionEvent event) 
				{
					
					canceled = false;
					if (!nameField.getText().trim().equals(""))
					{
						ourParent.addChild(ourDesignProductEntry);
						ourDesignProductEntry.setName(nameField.getText());
						ourDesignProductEntry.setDescription(descArea.getText());
						
						//comment before this made no sense...
						ourDesignProductEntry.setID(ourDesignProductEntry.toDatabase(ourParent.getID()));
						
						
						shell.close();
						shell.dispose();		   	
					}
					else
					{
						MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
						mbox.setMessage("Need to provide the DesignProductEntry name");
						mbox.open();
					}
				}
			});
			
		}
		else
		{
			addButton.setText("Save");
			addButton.addSelectionListener(new SelectionAdapter() {
				
				public void widgetSelected(SelectionEvent event) 
				{
					canceled = false;
					
					ConsistencyChecker checker = new ConsistencyChecker(ourDesignProductEntry.getID(), nameField.getText(), "DesignComponents");
					
					if(ourDesignProductEntry.getName() == nameField.getText() || checker.check())
					{
						ourDesignProductEntry.setName(nameField.getText());
						ourDesignProductEntry.setDescription(descArea.getText());
						//since this is a save, not an add, the type and parent are ignored
						ourDesignProductEntry.setID(ourDesignProductEntry.toDatabase(0));
						
						//			RationaleDB db = RationaleDB.getHandle();
						//			db.addDesignProductEntry(ourDesignProductEntry);
					}
					shell.close();
					shell.dispose();	
					
				}
			});
		}
		
		
		
		cancelButton = new Button(shell, SWT.PUSH); 
		cancelButton.setText("Cancel");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		cancelButton.setLayoutData(gridData);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) 
			{
				canceled = true;
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
	
	
	/**
	 * Get our element
	 */  
	public RationaleElement getItem()
	{
		return ourDesignProductEntry;
	}
	
	
}







