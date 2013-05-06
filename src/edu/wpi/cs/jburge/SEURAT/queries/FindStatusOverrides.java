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
package edu.wpi.cs.jburge.SEURAT.queries;

import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
//import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleStatus;

/**
 * Displays the list of overriden status messages. The user has the option of selecting a message
 * removing the override.
 * @author jburge
 *
 * 
 */
public class FindStatusOverrides {
	
	/**
	 * The shell
	 */
	private Shell shell;
	/**
	 * The list of status messages that have been overriden
	 */
	private List entityList;
	
	/**
	 * The index of the selected item
	 */
	int selectionIndex;
	
	Vector<RationaleStatus> updatedStatus;
	
	/**
	 * Constructor.
	 * 
	 * @param display   The parent display to this one
	 */
	public FindStatusOverrides(Display display) {
		
		selectionIndex = 0;
		
		updatedStatus = new Vector<RationaleStatus>();
		
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Status Overrides");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		entityList = new List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, true);
		gridData.horizontalSpan = 5;
		
		RationaleDB db = RationaleDB.getHandle();
		Vector overridenStatus = db.getOverrides();
		Enumeration listE = overridenStatus.elements();
		
		while (listE.hasMoreElements())
		{
			entityList.add( ((RationaleStatus) listE.nextElement()).toString());
		}
		
		// we need to set the width and height for our text box. We want approx. six rows of
		// information and we don't want the box to be so wide that it goes off the screen.
		int listHeight = entityList.getItemHeight() * 6;
		int listWidth = entityList.getFont().getFontData().length * 60;
		Rectangle trim = entityList.computeTrim(0, 0, listWidth, listHeight);
		gridData.heightHint = trim.height;
		gridData.widthHint = trim.width;
		entityList.setLayoutData(gridData);
		
		new Label(shell, SWT.NONE).setText(" ");	
		new Label(shell, SWT.NONE).setText(" ");	
		new Label(shell, SWT.NONE).setText(" ");	
		Button editB = new Button(shell, SWT.PUSH);
		
		editB.setText("Remove");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		editB.setLayoutData(gridData);
		editB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				
				String name = entityList.getItem(entityList.getSelectionIndex());
				RationaleDB db = RationaleDB.getHandle();
				Vector<RationaleStatus> overridenStatus = db.getOverrides();
				Enumeration listE = overridenStatus.elements();
				boolean found = false;
				
				RationaleStatus ourStatus = null;
				while (!found && (listE.hasMoreElements()))
				{	
					ourStatus = (RationaleStatus) listE.nextElement();
					if (ourStatus.toString().compareTo(name) == 0)
					{
						found = true;
					}
				}
				
				ourStatus.setOverride(false);
				updatedStatus.add(ourStatus);
				entityList.remove(name);
				ourStatus.toDatabase(ourStatus.getParent());
			}
		});
		
		Button cancelB = new Button(shell, SWT.PUSH);
		cancelB.setText("Exit");
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		//	cancelB.setSize(searchB.getSize());
//		gridData2.horizontalIndent = 5;
		cancelB.setLayoutData(gridData2);
		cancelB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
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
	 * Used to get the updated status after the user has overridden elements. 
	 * @return status  a vector of the updated RationaleStatus elements.
	 */
	
	public Vector<RationaleStatus> getUpdatedStatus()
	{
		return updatedStatus;
	}
	
}
