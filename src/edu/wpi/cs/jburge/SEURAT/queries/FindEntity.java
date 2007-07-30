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

import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;

/**
 * Used to select which kind of entity the user wants to see a list of.
 * @author jburge
 */
public class FindEntity {

	/**
	 * Points back to the display
	 */
	private Display ourDisplay;
	/**
	 * The shell
	 */
	private Shell shell;
	
	/**
	 * A combo box for choosing the entity type
	 */
	private Combo entityType;

	/**
	 * Our constructor.
	 * @param display our display
	 */
	public FindEntity(Display display) {
		
		ourDisplay = display;
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Find Entity");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		new Label(shell, SWT.NONE).setText("Entity Type:");
		entityType = new Combo(shell, SWT.NONE);
		
		Enumeration typeEnum = RationaleElementType.elements();
		RationaleElementType rtype;
		while (typeEnum.hasMoreElements())
		{
			rtype = (RationaleElementType) typeEnum.nextElement();
			if ((rtype != RationaleElementType.NONE) &&
				(rtype != RationaleElementType.RATIONALE))
				{
					entityType.add(rtype.toString());
				}
			
		}
		entityType.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		Button findB = new Button(shell, SWT.PUSH);
		findB.setText(" Find ");
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//		gridData.horizontalIndent = 5;
		findB.setLayoutData(gridData);
		findB.addSelectionListener(new SelectionAdapter() {

		   public void widgetSelected(SelectionEvent event) {
			int selI = entityType.getSelectionIndex();
			RationaleElementType ourEle = RationaleElementType.fromString(entityType.getItem(selI));
			@SuppressWarnings("unused") EditEntity editEntity = new EditEntity(ourDisplay, ourEle);
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
