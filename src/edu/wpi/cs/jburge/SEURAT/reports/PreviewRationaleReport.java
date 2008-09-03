/**
 * Team 1, Release 1- October 30, 2007
 */
package edu.wpi.cs.jburge.SEURAT.reports;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;


/**
 * Constructs a preview of the rationale tree report that the user can view.
 * Called from the Generate Rationale Report dialog.
 * 
 * @author molerjc
 */
public class PreviewRationaleReport {
	
	/**
	 *  Our shell
	 */
	private Shell shell;
	/**
	 * The Text object that will store the report
	 */
	Text previewPane;

	/**
	 * Constructs and displays the rationale report preview window.
	 * 
	 * @param parent - The parent composite
	 * @param report - The string version of the report to be previewed
	 * 
	 */
	public PreviewRationaleReport(Composite parent, String report) {
		shell = new Shell();
		shell.setText("Rationale Report Preview");
		
		GridLayout gridLayout = new GridLayout();
		shell.setLayout(gridLayout);
		previewPane = new Text (shell, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY);
		GridData gridData = new GridData(400, 600);
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		previewPane.setText(report);
		previewPane.setLayoutData(gridData);
		
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Close");
		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		cancel.setLayoutData(gridData2);
		cancel.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent event) {
				shell.close();
				shell.dispose();
				
			}
		});
		
		shell.pack();
		shell.open();
		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		
	}
	
}
