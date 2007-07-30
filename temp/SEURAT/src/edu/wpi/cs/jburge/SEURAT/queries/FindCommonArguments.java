/*
 * Created on May 15, 2004
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package edu.wpi.cs.jburge.SEURAT.queries;

import java.util.Vector;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;

import edu.wpi.cs.jburge.SEURAT.inference.ArgumentInferences;
import edu.wpi.cs.jburge.SEURAT.rationaleData.RationaleElementType;

/**
 * This display lets the user select which type of argument they want to display
 * a list of most common arguments for.
 * @author jburge
 */
public class FindCommonArguments {
	
	/**
	 * Points to the shell
	 */
	private Shell shell;
	
	/**
	 * Combo box to select the argument
	 */
	private Combo argumentType;
	/**
	 * Our selection button
	 */
	private Button selAltB;
	/**
	 * The constructor that builds the display
	 * @param display points back to the display
	 */
	public FindCommonArguments(Display display) {
		
		shell = new Shell(display, SWT.DIALOG_TRIM | SWT.PRIMARY_MODAL);
		shell.setText("Find Common Arguments");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.makeColumnsEqualWidth = true;
		shell.setLayout(gridLayout);
		
		new Label(shell, SWT.NONE).setText("Argument Type:");
		argumentType = new Combo(shell, SWT.NONE);
		argumentType.add(RationaleElementType.ASSUMPTION.toString());
		argumentType.add(RationaleElementType.CLAIM.toString());
		argumentType.add(RationaleElementType.ONTENTRY.toString());

		argumentType.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		selAltB = new Button(shell, SWT.CHECK);
		selAltB.setText("Selected Alternatives Only");
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		selAltB.setLayoutData(gridData);
		
		Button findB = new Button(shell, SWT.PUSH);
		findB.setText(" Find ");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING);
//		gridData.horizontalIndent = 5;
		findB.setLayoutData(gridData);
		findB.addSelectionListener(new SelectionAdapter() {

		   public void widgetSelected(SelectionEvent event) {
			Vector ourArguments;		
			int selI = argumentType.getSelectionIndex();
			RationaleElementType ourEle = RationaleElementType.fromString(argumentType.getItem(selI));
			ArgumentInferences inf = new ArgumentInferences();
			ourArguments = inf.argumentStatistics(ourEle, selAltB.getSelection());
			@SuppressWarnings("unused") CommonArgumentDisplay cd = new CommonArgumentDisplay(shell, ourEle.toString(), ourArguments);
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
