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
import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.graphics.Rectangle;

//import edu.wpi.cs.jburge.SEURAT.rationaleData.Argument;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleDB;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElement;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementFactory;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;
//import edu.wpi.cs.jburge.SEURAT.views.RationaleUpdateEvent;
//import edu.wpi.cs.jburge.SEURAT.views.UpdateType;

/**
 * Presents the user with a list of rationale elements of a specific type to
 * select from. The user also has the option of searching for an element in the list
 * and can add new items.
 * @author jburge
 */
public class SelectType {
	
	/**
	 * Points back to the display
	 */
	private Display ourDisplay;
	/**
	 * Points to the shell
	 */
	private Shell shell;
	/**
	 * The type of rationale element that is being selected
	 */
	private RationaleElementType type;
	/**
	 * The text box where the user can enter a search string
	 */
	private Text searchText;
	/**
	 * The list of entities that the user can select from
	 */
	private List entityList;
	
	/**
	 * The string typed into the text box (searchText)
	 */
	String selectionString;
	/**
	 * The index to the selected item
	 */
	int selectionIndex;
	
	/**
	 * The selected item
	 */
	private RationaleElement ourItem;
	
	/**
	 * The selected item
	 */
	private String eleType;
	
	/**
	 * Displays a list of rationale elements that the user can select from
	 * @param display - points to the display
	 * @param eType - the rationale element type
	 */
	public SelectType(Display display) {
		
		//set up our display
		ourDisplay = display;
		
		ourItem = null;
		selectionIndex = 0;
		
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		
		new Label(shell, SWT.NONE).setText("Select a type:");	
		shell.setText("Change element type");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);

		entityList = new List(shell, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 5;
		
		Vector<String> listV = new Vector<String>();
		listV.add("Requirement");
		listV.add("Decision");
		listV.add("Alternative");
		listV.add("Argument");
		
		// for future use
//		listV.add("Assumption");
//		listV.add("Question");
		Enumeration<String> listE = listV.elements();
		while (listE.hasMoreElements())
		{
			entityList.add( (String) listE.nextElement());
		}

		DisplayUtilities.setListDimensions(entityList, gridData, 12, 80);
		entityList.setLayoutData(gridData);
	
	
		Button editB = new Button(shell, SWT.PUSH);
		editB.setText("Select");
//		editB.setSize(buttonSize);
		gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING| GridData.HORIZONTAL_ALIGN_FILL);
//		gridData.horizontalIndent = 5;

		this.eleType="";
		
		editB.setLayoutData(gridData);
		editB.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				eleType = entityList.getItem(entityList.getSelectionIndex());		
				shell.close();
				shell.dispose();
			}
		});
			
		Button cancelB = new Button(shell, SWT.PUSH);
		cancelB.setText("Cancel");
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
	 * Get the type that user has been selected.
	 * @return the selected element type
	 */
	public String getType()
	{
		return this.eleType;
	}
	
	
	/**
	 * Get the item that has been selected.
	 * @return the selected rationale element
	 */
	public RationaleElement getNewItem()
	{
		return ourItem;
	}
	
}
