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
 * Displays the editor for a contingency
 * @author burgeje
 *
 */
public class EditContingency extends NewRationaleElementGUI implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 885346007915063921L;
	private Display ourDisplay;
	private Shell shell;
	
	/**
	 * The contingency being edited
	 */	
	private Contingency ourContingency;
	/**
	 * The name of the contingency
	 */  
	private Text nameField;
	/**
	 * The description of the contingency
	 */
	private Text descArea;
	/**
	 * The contingency amount
	 */  
	private Text amountArea;
	/**
	 * Button to add the contingency
	 */
	private Button addButton;
	/**
	 * Button to cancel edits
	 */
	private Button cancelButton;
	
	
	/**
	 * Constructor to create or edit a contingency
	 * @param display - points back to the display
	 * @param editContin - the contingency being edited
	 * @param newItem - true if the item is new
	 */  
	public EditContingency(Display display, Contingency editContin, boolean newItem)
	{
		super();
		ourDisplay = display;
		shell = new Shell(ourDisplay, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Contingency Information");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		gridLayout.marginHeight = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		ourContingency = editContin;
		
		
		new Label(shell, SWT.NONE).setText("Name:");
		
		nameField =  new Text(shell, SWT.SINGLE | SWT.BORDER);
		nameField.setText(ourContingency.getName());
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.horizontalSpan = 2;
		nameField.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Description:");
		
		descArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		descArea.setText(ourContingency.getDescription());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		gridData.heightHint = descArea.getLineHeight() * 3;
		descArea.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText("Percentage:");
		
		amountArea = new Text(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		amountArea.setText(new Float(ourContingency.getPercentage()).toString());
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;
		
		amountArea.setLayoutData(gridData);
		
		
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
						ConsistencyChecker checker = new ConsistencyChecker(ourContingency.getID(), nameField.getText(), "Contingencies");
						
						if(ourContingency.getName() == nameField.getText() || checker.check())
						{
							ourContingency.setName(nameField.getText());
							ourContingency.setDescription(descArea.getText());
							String percentString = amountArea.getText();
							try
							{
								Float testFlt = Float.valueOf(percentString);
								ourContingency.setPercentage(testFlt.floatValue());
								
								
								//comment before this made no sense...
								ourContingency.toDatabase();
								
								shell.close();
								shell.dispose();	
								
							} catch (NumberFormatException ex)
							{
								MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
								mbox.setMessage("Need to provide a valid percentage");
								mbox.open();
							}
						}
					}
					else
					{
						MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
						mbox.setMessage("Need to provide the Contingency name");
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
					
					ConsistencyChecker checker = new ConsistencyChecker(ourContingency.getID(), nameField.getText(), "Contingencies");
					
					if(ourContingency.getName() == nameField.getText() || checker.check())
					{
						ourContingency.setName(nameField.getText());
						ourContingency.setDescription(descArea.getText());
						String percentString = amountArea.getText();
						try
						{
							Float testFlt = Float.valueOf(percentString);
							ourContingency.setPercentage(testFlt.floatValue());
							
							
							//since this is a save, not an add, the type and parent are ignored
							ourContingency.toDatabase();
							
							shell.close();
							shell.dispose();	
							
						} catch (NumberFormatException ex)
						{
							MessageBox mbox = new MessageBox(shell, SWT.ICON_ERROR);
							mbox.setMessage("Need to provide a valid percentage");
							mbox.open();
						}
					}
					
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
	 * Get our contingency
	 */   
	public RationaleElement getItem()
	{
		return ourContingency;
	}
	
	
}






