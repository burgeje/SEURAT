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
import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

import edu.wpi.cs.jburge.SEURAT.rationaleData.ReqStatus;

/**
 * Used to find out what type of requirement status the user is interested
 * in when they use the "Find Requirements" query option.
 * @author jburge
 */
public class FindRequirements {
	
	/**
	 *  our shell
	 */
	private Shell shell;
	
	/**
	 * A combo box to specify the argument type
	 */
	private Combo argumentType;
	/**
	 * Find out what type of requirement they want to look for
	 */
	public FindRequirements(Display display) {
		
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Find Requirements");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		new Label(shell, SWT.NONE).setText("Requirement Status:");
		argumentType = new Combo(shell, SWT.NONE);
		Enumeration reqE = ReqStatus.elements();
		while (reqE.hasMoreElements())
		{
			argumentType.add(((ReqStatus) reqE.nextElement()).toString());
		}
		argumentType.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		
		Button findB = new Button(shell, SWT.PUSH);
		findB.setText(" Find ");
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//		gridData.horizontalIndent = 5;
		findB.setLayoutData(gridData);
		findB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
//				Vector ourArguments;		
				int selI = argumentType.getSelectionIndex();
				ReqStatus ourEle = ReqStatus.fromString(argumentType.getItem(selI));
				@SuppressWarnings("unused") RequirementStatusDisplay cd = new RequirementStatusDisplay(shell, ourEle);
				
				/* -- we might want to do more than one query?
				 shell.close();
				 shell.dispose();
				 */
				
			}
		});
		
		Button cancelB = new Button(shell, SWT.PUSH);
		cancelB.setText("Cancel");
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//		gridData2.horizontalIndent = 5;
		cancelB.setLayoutData(gridData2);
		cancelB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
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
	
}
